# Architecture — Integrated Food Supply & Nutrition Management

**Team18 · Enterprise Java Hackathon (Spring Boot & Microservices) · VIT**

> Note: IntelliJ's built-in Markdown preview does not render Mermaid diagrams unless you install the **Mermaid** plugin (`Settings → Plugins → search "Mermaid"`). This version uses a plain-text diagram instead, so it reads correctly in IntelliJ with no extra setup. GitHub will still render it fine as a code block.

## Overview

An enterprise-style microservices system built with Spring Boot (MVC), REST APIs between services, and a shared PostgreSQL instance. Seven domain microservices, each owning its own tables, plus supporting infrastructure (gateway, reporting aggregator, web UI).

| # | Service | Owns | Maps to requirement |
|---|---|---|---|
| 1 | `beneficiary-service` | Beneficiary profiles, demographics, eligibility, nutrition needs | Beneficiary Management |
| 2 | `food-catalog-service` | Food items, nutrition values, categories, units | Food Item & Nutrition Management |
| 3 | `procurement-service` | Suppliers, purchase orders, procurement status | Procurement Management |
| 4 | `inventory-service` | Stock batches, expiry, movement, min-stock levels | Inventory & Stock Management |
| 5 | `distribution-service` | Distribution plans, distribution centres | Food Distribution Management |
| 6 | `nutrition-programme-service` | Programmes, meal plans, dietary requirements, participation | Nutrition Programme Management |
| 7 | `quality-wastage-service` | Quality inspections, wastage, corrective actions | Food Quality & Wastage Management |

**Supporting components:**

| Component | Role |
|---|---|
| `api-gateway` | Single entry point; routes requests to the right service; central auth/rate-limiting |
| `reporting-service` | Read-only aggregator for Dashboard & Reporting — queries the 7 services, owns no primary data |
| `web-ui` | Thymeleaf frontend; talks only to the gateway, never directly to a service |

## Architecture Diagram

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
                              +----------+----------+
         +----------+----------+--------+--------+----------+------------+------------+
         |          |          |        |        |          |            |            |
         v          v          v        v        v          v            v            v
   +--------+  +--------+  +------+  +-----+  +------+  +--------+  +---------+  +----------+
   |benefic.|  |food-   |  |procure|  |inven|  |distri|  |nutri.  |  |quality- |  |reporting |
   |-service|  |catalog |  |-ment  |  |tory |  |bution|  |program.|  |wastage  |  |-service  |
   +----+---+  +---+----+  +--+----+  +-+---+  +--+---+  +---+----+  +----+----+  +----+-----+
        |          |          |          |         |          |           |            |
        |          |          |          |         |          |           |            |
        |          |          |          |  distribution-service also calls:           |
        |          |          |          |   -> inventory-service   (check stock)       |
        |          |          |          |   -> beneficiary-service (verify eligibility) |
        |          |          |  procurement-service also calls:                        |
        |          |          |   -> food-catalog-service (resolve item)                 |
        |          |          |                                                          |
        |          |          |   reporting-service queries all 7 services (read-only)   |
        |          |          |                                                          |
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

Each service connects to the **same** Postgres instance but only migrates and touches its own tables — no cross-service foreign keys, ever. Consistency across services is enforced through REST calls (and, in the full enterprise version, async events), not the database.

## Communication Patterns

**Synchronous (REST)** — for calls that need an answer right away:
- `web-ui` → `api-gateway` → any service (page reads)
- `distribution-service` → `inventory-service` (check & reserve stock before confirming a plan)
- `distribution-service` → `beneficiary-service` (verify eligibility)
- `procurement-service` → `food-catalog-service` (resolve a food item on a purchase order)
- `reporting-service` → all 7 services (read-only aggregation for the dashboard)

**Asynchronous (events — Kafka/RabbitMQ, if time allows)** — for state changes other services react to:
- `inventory-service` publishes `StockBelowMinimum` → `procurement-service` auto-creates a purchase request
- `procurement-service` publishes `PurchaseOrderReceived` → `inventory-service` creates a new stock batch
- `quality-wastage-service` publishes `BatchRejected` → `inventory-service` writes the batch off
- `distribution-service` publishes `DistributionCompleted` → `nutrition-programme-service` updates participation

## Database Schema

One shared PostgreSQL instance (`foodsupply`); each service's Flyway migration (`db/migration/V1__<service>.sql`) creates **only its own tables**.

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

## Project Structure in IntelliJ

Open the **repo root** (not an individual service folder) so IntelliJ's Maven tool window picks up every module:

```
food-supply-management/          <- open THIS in IntelliJ
├── pom.xml                      <- parent pom, packaging=pom, lists all modules
├── docker-compose.yml
├── beneficiary-service/
│   ├── pom.xml
│   └── src/main/resources/
│       ├── application.properties   (server.port=8081)
│       └── db/migration/V1__beneficiary.sql
├── food-catalog-service/        (server.port=8082)
├── procurement-service/         (server.port=8083)
├── inventory-service/           (server.port=8084)
├── distribution-service/        (server.port=8085)
├── nutrition-programme-service/ (server.port=8086)
├── quality-wastage-service/     (server.port=8087)
├── reporting-service/           (server.port=8088)
└── web-ui/                      (server.port=8080)
```

If a module doesn't show up in the Maven tool window: right-click its `pom.xml` → **Add as Maven Project**.

## IntelliJ Setup Steps

1. **Start the shared database** — open the Terminal tab (bottom) and run:
   ```
   docker compose up -d
   ```

2. **Connect the Database tool** (once, for the whole project):
   `View → Tool Windows → Database` → `+` → **Data Source → PostgreSQL** → host `localhost`, port `5432`, db `foodsupply` → **Download missing driver files** → **Test Connection** → OK.

3. **Create a Run Configuration per service**:
   `Run → Edit Configurations → +` → **Spring Boot** → set Main class and Module for each of the 9 services (7 domain services + gateway + web-ui; add `reporting-service` as the 10th if you build it).

4. **Group them into a Compound config** to start everything with one click:
   `+` → **Compound** → add all service configs → name it `All Services` → Run ▶.

5. **Startup order**: `docker compose up -d` first → then run services (Flyway migrates each service's own tables on boot) → run `web-ui` last.

## Design Principles

- **One schema, many owners** — all services share one Postgres instance for simpler local setup, but each service migrates and writes only its own tables. No cross-service foreign keys.
- **No direct service-to-service DB reads** — a service never queries another service's tables directly, even though they're in the same database; it calls that service's REST API.
- **Controller → Service → Repository** (MVC) inside every service; cross-service calls happen in the service layer via `RestTemplate`/`WebClient`, never in a controller.
- **No static/dummy data** — every screen reads from a service's own database, per the hackathon rules.
