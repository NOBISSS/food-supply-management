# Architecture — Integrated Food Supply & Nutrition Management

**Team18 · Enterprise Java Hackathon (Spring Boot & Microservices) · VIT**

> Note: IntelliJ's built-in Markdown preview does not render Mermaid diagrams unless you install the **Mermaid** plugin (`Settings → Plugins → search "Mermaid"`). This version uses a plain-text diagram instead, so it reads correctly in IntelliJ with no extra setup. GitHub will still render it fine as a code block.

## 0. Current state vs. this document

The repository is now a Maven multi-module project. Existing authentication and implemented domain code have been separated into independently runnable Spring Boot modules, with shared infrastructure in `common-library`. The package names and API paths are preserved. This extraction is structural: it does not implement an API gateway, web UI, service-to-service REST calls, or missing domain workflows.

Modules with endpoint implementations are `auth-service`, `beneficiary-service`, `food-catalog-service`, `procurement-service`, `distribution-service`, `quality-wastage-service`, and `reporting-service`. `inventory-service` and `nutrition-programme-service` currently contain startup/build structure and migrations only; their domain entities and APIs remain unimplemented. `reporting-service` currently reuses domain repository code against the shared database, so it is a transitional local dashboard, not yet the REST-based reporting aggregator described below. The API gateway and web UI remain planned components with no code in this repository.

## 1. Overview

An enterprise-style microservices system built with Spring Boot (MVC), REST APIs between services, and a shared PostgreSQL instance. Eight domain microservices (seven business domains + auth), each owning its own tables, plus supporting infrastructure (gateway, reporting aggregator, web UI).

| # | Service | Port | Owns | Maps to requirement |
|---|---|---|---|---|
| 0 | `auth-service` | 8081 | App users, roles, JWT issuance | Enterprise Integration — security |
| 1 | `beneficiary-service` | 8082 | Beneficiary profiles, demographics, eligibility, nutrition needs | Beneficiary Management |
| 2 | `food-catalog-service` | 8083 | Food items, nutrition values, categories, units | Food Item & Nutrition Management |
| 3 | `procurement-service` | 8084 | Suppliers, purchase orders, purchase order items, procurement status | Procurement Management |
| 4 | `inventory-service` | 8085 | Stock batches, stock movement, expiry, min-stock levels | Inventory & Stock Management |
| 5 | `distribution-service` | 8086 | Distribution plans, distribution centres | Food Distribution Management |
| 6 | `nutrition-programme-service` | 8087 | Programmes, meal plans, dietary requirements, participation | Nutrition Programme Management |
| 7 | `quality-wastage-service` | 8088 | Quality inspections, wastage, corrective actions | Food Quality & Wastage Management |

**Supporting components:**

| Component | Port | Role |
|---|---|---|
| `api-gateway` | 8080 | Single entry point; routes requests to the right service; validates JWT once, forwards identity downstream |
| `reporting-service` | 8089 | Read-only aggregator for Dashboard & Reporting — queries the 8 services, owns no primary data |
| `web-ui` | 8090 | Thymeleaf frontend; talks only to the gateway, never directly to a service |

Moving auth into its own service (rather than leaving it embedded, as it is in the current monolith) matters because every other service needs to validate the same JWT independently — that's the thing that actually makes this "microservices" instead of "one app with packages."

## 2. Architecture Diagram

```
                              +-------------------+
                              |      Web UI        |
                              | Thymeleaf + Spring  |
                              |        MVC          |
                              +----------+----------+
                                         |
                                         v
                              +----------+----------+
                              |     API Gateway      |
                              |  (JWT validation)     |
                              +----------+----------+
    +----------+----------+----------+---------+----------+------------+------------+------------+
    |          |          |          |         |          |            |            |            |
    v          v          v          v         v          v            v            v            v
+------+  +--------+  +--------+  +------+  +-----+  +------+  +--------+  +---------+  +----------+
| auth |  |benefic.|  |food-   |  |procure|  |inven|  |distri|  |nutri.  |  |quality- |  |reporting |
|-svc  |  |-service|  |catalog |  |-ment  |  |tory |  |bution|  |program.|  |wastage  |  |-service  |
+------+  +----+---+  +---+----+  +--+----+  +-+---+  +--+---+  +---+----+  +----+----+  +----+-----+
               |          |          |          |         |          |           |            |
               |          |          |          |         |          |           |            |
               |          |          |          |  distribution-service also calls:           |
               |          |          |          |   -> inventory-service   (check & reserve)   |
               |          |          |          |   -> beneficiary-service (verify eligibility)|
               |          |          |  procurement-service also calls:                        |
               |          |          |   -> food-catalog-service (resolve item on PO line)     |
               |          |                                                                    |
               |          |   nutrition-programme-service also calls:                          |
               |          |    -> beneficiary-service (resolve participant)                    |
               |          |                                                                    |
               |          |   reporting-service queries all 8 services (read-only)              |
               |          |                                                                    |
               +----------+----------+----------+---------+----------+-----------+------------+
                                         |
                                         v
                              +----------+----------+
                              |   Shared PostgreSQL   |
                              |   (foodsupply db)     |
                              |  each service owns    |
                              |  only its own tables  |
                              +------------------------+
```

Every service connects to the **same** Postgres instance but only migrates and touches its own tables — no cross-service foreign keys, ever. Consistency across services is enforced through REST calls (and, in the full enterprise version, async events), not the database.

## 3. Communication Patterns

**Synchronous (REST)** — for calls that need an answer right away:
- `web-ui` → `api-gateway` → any service (page reads)
- `api-gateway` → `auth-service` (token validation, or local JWT verification using a shared signing secret — see 3.1)
- `distribution-service` → `inventory-service` (check & reserve stock before confirming a plan)
- `distribution-service` → `beneficiary-service` (verify eligibility)
- `procurement-service` → `food-catalog-service` (resolve a food item on a purchase order line)
- `nutrition-programme-service` → `beneficiary-service` (resolve a participant)
- `reporting-service` → all 8 services (read-only aggregation for the dashboard)

**Asynchronous (events — Kafka/RabbitMQ, if time allows)** — for state changes other services react to:
- `inventory-service` publishes `StockBelowMinimum` → `procurement-service` auto-creates a purchase request
- `procurement-service` publishes `PurchaseOrderReceived` → `inventory-service` creates a new stock batch
- `quality-wastage-service` publishes `BatchRejected` → `inventory-service` writes the batch off
- `distribution-service` publishes `DistributionCompleted` → `nutrition-programme-service` updates participation

If there's no time for a broker, implement the above as direct REST calls instead (e.g. `quality-wastage-service` calls `PATCH /api/stock-batches/{id}/reject` on `inventory-service`) and note in the README that the event-driven version is the stretch goal. A working synchronous version beats a half-wired broker.

### 3.1 Auth across services

Each service needs to know who's calling without re-hitting `auth-service` on every request. The practical approach for a hackathon:
- `auth-service` signs JWTs with a shared secret (`jwt.secret`, from environment, never hard-coded).
- Every other service — including `api-gateway` — carries a small JWT-verification filter that checks the signature and expiry locally, and reads role claims off the token. No network call needed per request.
- `api-gateway` additionally forwards `Authorization` unchanged to downstream services so each service can still enforce its own role rules (e.g. only `ADMIN`/`PROCUREMENT_OFFICER` can create a purchase order).

This is what the current monolith's `auth` package already does internally (bearer token filter + JWT utility) — extracting it means copying that filter class into every other service's `security` package, not rebuilding it.

## 4. Database Schema

One shared PostgreSQL instance (`foodsupply`); each service's Flyway migration (`db/migration/V1__<service>.sql`) creates **only its own tables**.

### auth-service
```sql
CREATE TABLE roles (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE app_users (
    id            BIGSERIAL PRIMARY KEY,
    username      VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role_id       BIGINT NOT NULL REFERENCES roles(id),
    created_at    TIMESTAMP NOT NULL DEFAULT now()
);
```

### beneficiary-service
```sql
CREATE TABLE beneficiary (
    id                  BIGSERIAL PRIMARY KEY,
    name                VARCHAR(150) NOT NULL,
    date_of_birth       DATE,
    gender              VARCHAR(20),
    address             VARCHAR(255),
    phone               VARCHAR(20),
    eligibility_status  VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at          TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE nutrition_requirement (
    id              BIGSERIAL PRIMARY KEY,
    beneficiary_id  BIGINT NOT NULL REFERENCES beneficiary(id),
    nutrient        VARCHAR(50) NOT NULL,
    recommended_qty NUMERIC(10,2) NOT NULL,
    unit            VARCHAR(20) NOT NULL
);
```

### food-catalog-service
```sql
CREATE TABLE food_item (
    id                 BIGSERIAL PRIMARY KEY,
    name               VARCHAR(100) NOT NULL,
    category           VARCHAR(50) NOT NULL,
    unit               VARCHAR(20) NOT NULL,
    calories_per_unit  NUMERIC(10,2),
    protein_per_unit   NUMERIC(10,2),
    created_at         TIMESTAMP NOT NULL DEFAULT now()
);
```

### procurement-service
```sql
CREATE TABLE supplier (
    id             BIGSERIAL PRIMARY KEY,
    name           VARCHAR(150) NOT NULL,
    contact_person VARCHAR(100),
    phone          VARCHAR(20),
    email          VARCHAR(100)
);

CREATE TABLE purchase_order (
    id                BIGSERIAL PRIMARY KEY,
    supplier_id       BIGINT NOT NULL REFERENCES supplier(id),
    status            VARCHAR(20) NOT NULL DEFAULT 'REQUESTED',
    order_date        DATE NOT NULL DEFAULT CURRENT_DATE,
    expected_delivery DATE,
    created_at        TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE purchase_order_item (
    id                BIGSERIAL PRIMARY KEY,
    purchase_order_id BIGINT NOT NULL REFERENCES purchase_order(id),
    food_item_id      BIGINT NOT NULL, -- owned by food-catalog-service, no FK
    quantity          NUMERIC(10,2) NOT NULL,
    unit_price        NUMERIC(10,2)
);
```

### inventory-service
```sql
CREATE TABLE stock_batch (
    id                  BIGSERIAL PRIMARY KEY,
    food_item_id        BIGINT NOT NULL, -- owned by food-catalog-service, no FK
    batch_no            VARCHAR(50) NOT NULL,
    quantity            NUMERIC(10,2) NOT NULL,
    received_date       DATE NOT NULL DEFAULT CURRENT_DATE,
    expiry_date         DATE,
    min_stock_level     NUMERIC(10,2) NOT NULL DEFAULT 0,
    warehouse_location  VARCHAR(100)
);

CREATE TABLE stock_movement (
    id             BIGSERIAL PRIMARY KEY,
    stock_batch_id BIGINT NOT NULL REFERENCES stock_batch(id),
    movement_type  VARCHAR(10) NOT NULL, -- IN / OUT
    quantity       NUMERIC(10,2) NOT NULL,
    reason         VARCHAR(100),
    moved_at       TIMESTAMP NOT NULL DEFAULT now()
);
```

### distribution-service
```sql
CREATE TABLE distribution_centre (
    id        BIGSERIAL PRIMARY KEY,
    name      VARCHAR(100) NOT NULL,
    location  VARCHAR(150),
    capacity  INT
);

CREATE TABLE distribution_plan (
    id                      BIGSERIAL PRIMARY KEY,
    beneficiary_id          BIGINT NOT NULL, -- owned by beneficiary-service
    food_item_id            BIGINT NOT NULL, -- owned by food-catalog-service
    quantity                NUMERIC(10,2) NOT NULL,
    distribution_centre_id  BIGINT NOT NULL REFERENCES distribution_centre(id),
    planned_date            DATE NOT NULL,
    status                  VARCHAR(20) NOT NULL DEFAULT 'PLANNED',
    created_at              TIMESTAMP NOT NULL DEFAULT now()
);
```

### nutrition-programme-service
```sql
CREATE TABLE nutrition_programme (
    id                   BIGSERIAL PRIMARY KEY,
    name                 VARCHAR(150) NOT NULL,
    dietary_requirements VARCHAR(255),
    start_date           DATE NOT NULL,
    end_date             DATE,
    status               VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
);

CREATE TABLE programme_participation (
    id                      BIGSERIAL PRIMARY KEY,
    nutrition_programme_id  BIGINT NOT NULL REFERENCES nutrition_programme(id),
    beneficiary_id          BIGINT NOT NULL, -- owned by beneficiary-service
    enrolled_date           DATE NOT NULL DEFAULT CURRENT_DATE,
    status                  VARCHAR(20) NOT NULL DEFAULT 'ENROLLED'
);
```

### quality-wastage-service
```sql
CREATE TABLE quality_inspection (
    id                BIGSERIAL PRIMARY KEY,
    stock_batch_id    BIGINT NOT NULL, -- owned by inventory-service
    inspection_date   DATE NOT NULL DEFAULT CURRENT_DATE,
    result            VARCHAR(20) NOT NULL, -- PASS / FAIL / DAMAGED / EXPIRED
    wastage_qty       NUMERIC(10,2) DEFAULT 0,
    corrective_action VARCHAR(255),
    inspected_by      VARCHAR(100)
);
```

## 5. REST API surface per service

Each service exposes only its own CRUD plus any cross-service read endpoints other services depend on.

| Service | Endpoints |
|---|---|
| `auth-service` | `POST /api/auth/register`, `POST /api/auth/login` |
| `beneficiary-service` | `GET/POST/PUT/DELETE /api/beneficiaries[/{id}]`, `GET /api/beneficiaries/{id}/eligibility` (used by distribution-service) |
| `food-catalog-service` | `GET/POST/PUT/DELETE /api/food-items[/{id}]` |
| `procurement-service` | `GET/POST/PUT/DELETE /api/suppliers[/{id}]`, `GET/POST/PUT/DELETE /api/purchase-orders[/{id}]`, `GET/POST /api/purchase-orders/{id}/items` |
| `inventory-service` | `GET/POST/PUT/DELETE /api/stock-batches[/{id}]`, `POST /api/stock-batches/{id}/movements`, `GET /api/stock-batches/available?foodItemId=` (used by distribution-service), `PATCH /api/stock-batches/{id}/reject` (used by quality-wastage-service) |
| `distribution-service` | `GET/POST/PUT/DELETE /api/distribution-centres[/{id}]`, `GET/POST/PUT/DELETE /api/distribution-plans[/{id}]` |
| `nutrition-programme-service` | `GET/POST/PUT/DELETE /api/nutrition-programmes[/{id}]`, `GET/POST /api/nutrition-programmes/{id}/participants` |
| `quality-wastage-service` | `GET/POST/PUT/DELETE /api/quality-inspections[/{id}]` |
| `reporting-service` | `GET /api/dashboard/summary`, `GET /api/dashboard/stock-shortages`, `GET /api/dashboard/wastage` |

## 6. Project Structure in IntelliJ

Open the repository root as a Maven multi-module project:

```
food-supply-management/
├── pom.xml                     (Maven aggregator)
├── common-library/             (shared exception and JWT security classes)
├── auth-service/               (port 8081)
├── beneficiary-service/        (port 8082)
├── food-catalog-service/       (port 8083)
├── procurement-service/        (port 8084)
├── inventory-service/          (port 8085; schema/startup scaffold only)
├── distribution-service/      (port 8086)
├── nutrition-programme-service/ (port 8087; schema/startup scaffold only)
├── quality-wastage-service/    (port 8088)
└── reporting-service/          (port 8089; current DB-backed dashboard)
```

Each service has its own Maven module, Spring Boot entry point, `application.properties`, and migration directory. Root `mvnw.cmd` builds all modules. The API gateway and web UI from the target design have not been created.
## 7. IntelliJ Setup Steps

1. Start the shared PostgreSQL container from the repository root:
   ```
   docker compose up -d
   ```
2. Ensure `DB_USER`, `DB_PASSWORD`, and `JWT_SECRET` are available to the service processes. Compose uses PostgreSQL user `postgres`; Spring Boot does not automatically import `.env` into IntelliJ run configurations.
3. Import the repository root as a Maven project. Create Spring Boot run configurations by selecting a service module and its main class; module ports are listed in Section 6.
4. Optionally group the current service configurations in a Compound configuration. The API gateway and web UI are not part of the current build.
5. Services use separate Flyway history tables. Review the migration note in Section 9 before starting against a database previously migrated by the monolith.
## 8. Design Principles

- **One schema, many owners** — all services share one Postgres instance for simpler local setup, but each service migrates and writes only its own tables. No cross-service foreign keys.
- **No direct service-to-service DB reads** — a service never queries another service's tables directly, even though they're in the same database; it calls that service's REST API.
- **Controller → Service → Repository** (MVC) inside every service; cross-service calls happen in the service layer via `RestTemplate`/`WebClient`, never in a controller.
- **No static/dummy data** — every screen reads from a service's own database, per the hackathon rules.
- **One JWT, verified everywhere** — `auth-service` issues it; every other service verifies it locally with the shared secret rather than calling back to `auth-service` per request.

## 9. Current extraction status and remaining work

The existing application code has been split into Maven modules while preserving controller routes and business code. Small Spring Boot entry points, module POMs, and per-service runtime configuration were added so the modules can be built and launched separately. Existing JWT security classes are in `common-library` and are included by the current services.

The following parts of the target design remain unimplemented: API gateway, web UI, REST calls between services, reporting over HTTP, inventory and nutrition-programme domain APIs, and role-specific authorization. Reporting currently depends on domain modules and reads repositories directly, so it must be replaced with HTTP aggregation before it is an independently owned reporting service.

A shared PostgreSQL database is still configured. Each domain service has a distinct Flyway history table, while auth retains the previous Flyway history table for compatibility with its existing V1 migration. A database previously migrated by the monolith may need migration-history reconciliation before the extracted service migrations are started; do not reset a database containing needed data.

Build from the repository root with:

```
./mvnw -DskipTests package
```

On Windows PowerShell:

```
.\mvnw.cmd -DskipTests package
```

The standalone executable artifacts use the `-exec.jar` classifier. `spring-boot:run` can also be run with the desired Maven module selected.