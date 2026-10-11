# Integrated Food Supply & Nutrition Management

**Team18 · Enterprise Java Hackathon (Spring Boot & Microservices) · VIT**

A microservices-based system for managing food supply, beneficiary records, nutrition
programmes, procurement, inventory, distribution, and quality/wastage tracking. Built with
Spring Boot, Spring Data JPA, Spring Security (JWT), Flyway, and a shared PostgreSQL database.

## Services

| Service | Port | Responsibility |
|---|---|---|
| `auth-service` | 8081 | User registration/login, JWT issuance and verification |
| `beneficiary-service` | 8082 | Beneficiary profiles, eligibility, nutrition programmes & participation |
| `supply-service` | 8083 | Food catalog, procurement, inventory, distribution |
| `quality-report-service` | 8084 | Quality inspections, wastage records, corrective actions |

All services share a single PostgreSQL database (`food_supply_db`). Each service owns and
migrates only its own tables — there are no cross-service foreign keys. See
[`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) for the full design notes and target architecture.

## Tech stack

- Java 21
- Spring Boot (Web, Data JPA, Security, Validation, Thymeleaf)
- PostgreSQL + Flyway migrations
- JWT (`jjwt`) for authentication
- Maven
- Docker Compose (for the shared database)

## Prerequisites

- JDK 21
- Maven (or use the wrapper in `auth-service`: `mvnw` / `mvnw.cmd`)
- Docker Desktop (for the PostgreSQL container)

## Setup

1. **Copy the environment template** and fill in real values:

   ```bash
   cp .env.example .env
   ```

   `.env` holds the shared Postgres credentials and port (`POSTGRES_DB`, `POSTGRES_USER`,
   `POSTGRES_PASSWORD`, `POSTGRES_PORT`, `DB_PORT`). It is gitignored — never commit it.

   > If port 5432/5433 is already taken by another local PostgreSQL installation, change
   > `POSTGRES_PORT` and `DB_PORT` in `.env` to a free port (e.g. `5434`) before continuing.

2. **Start the shared database:**

   ```bash
   docker compose up -d
   ```

3. **Bootstrap the schema (first run only).** `auth-service`'s Flyway migrations
   (`V1__initial_schema.sql`, `V2__second_schema.sql`) create the tables for *all four*
   services, not just auth. Flyway is disabled by default in `auth-service`'s
   `application.properties`, so for a brand-new database, enable it for one run:

   ```bash
   cd auth-service
   SPRING_FLYWAY_ENABLED=true ./mvnw.cmd spring-boot:run   # Windows
   # SPRING_FLYWAY_ENABLED=true ./mvnw spring-boot:run     # macOS/Linux
   ```

   Once you see `Started FoodSupplyManagementApplication`, stop it (Ctrl+C) — the schema is
   now in place and future runs don't need the flag (Flyway re-running is harmless either way,
   since it only applies pending migrations).

4. **Run each service** (one per terminal). Each service reads its own
   `application.properties`, which is also gitignored locally — make sure `DB_PORT` matches
   the port you set in `.env`:

   ```bash
   cd auth-service              && ./mvnw.cmd spring-boot:run
   cd beneficiary-service       && mvn spring-boot:run
   cd supply-service            && mvn spring-boot:run
   cd quality-report-service    && mvn spring-boot:run
   ```

   ### Overriding `DB_PORT` per run

   Each service's `application.properties` hardcodes its own fallback port
   (`${DB_PORT:5432}` or `${DB_PORT:5433}` depending on the service), so a mismatch between
   that fallback and your actual Postgres port is common — especially after changing
   `POSTGRES_PORT` in `.env`. Spring Boot reads `DB_PORT` from the environment at startup, so
   you can override it per run without editing any properties file.

   **Note:** `.env` is only auto-loaded by Docker Compose, *not* by Maven. Setting values in
   `.env` has no effect on `mvn`/`mvnw` runs unless you export them into the shell yourself or
   pass them inline as shown below.

   - **bash / macOS / Linux / Git Bash** — prefix the command with the variable:

     ```bash
     DB_PORT=5434 mvn spring-boot:run
     ```

     To load every value from `.env` into the current shell instead of typing them out:

     ```bash
     set -a && source .env && set +a && mvn spring-boot:run
     ```

   - **Windows PowerShell** — set it as a session variable first (PowerShell has no inline
     `VAR=value cmd` syntax):

     ```powershell
     $env:DB_PORT = "5434"
     mvn spring-boot:run
     ```

   - **Windows cmd.exe**:

     ```bat
     set DB_PORT=5434 && mvn spring-boot:run
     ```

   If you need to override more than the port — e.g. a different host, username, or
   database name — skip `DB_PORT` and override the full connection string instead, since it
   takes precedence over everything in `application.properties`:

   ```bash
   SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5434/food_supply_db mvn spring-boot:run
   ```

   This override applies per terminal session only — close the terminal (or open a new one)
   and you're back to the properties file defaults, so repeat it each time you start a service
   until the fallback ports in `application.properties` are updated to match your `.env`.

## API reference

### auth-service (`:8081`)

| Method | Path | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Authenticate and receive a JWT |
| GET | `/api/auth/me` | Get the current authenticated user |

### beneficiary-service (`:8082`)

| Method | Path | Description |
|---|---|---|
| POST | `/api/beneficiaries` | Create a beneficiary |
| GET | `/api/beneficiaries` | List beneficiaries |
| GET | `/api/beneficiaries/{id}` | Get a beneficiary |
| PUT | `/api/beneficiaries/{id}` | Update a beneficiary |
| DELETE | `/api/beneficiaries/{id}` | Deactivate a beneficiary |
| PATCH | `/api/beneficiaries/{id}/reactivate` | Reactivate a beneficiary |
| POST | `/api/nutrition-programmes` | Create a nutrition programme |
| GET | `/api/nutrition-programmes` | List nutrition programmes |
| GET | `/api/nutrition-programmes/{id}` | Get a nutrition programme |
| PUT | `/api/nutrition-programmes/{id}` | Update a nutrition programme |
| DELETE | `/api/nutrition-programmes/{id}` | Deactivate a nutrition programme |
| PATCH | `/api/nutrition-programmes/{id}/reactivate` | Reactivate a nutrition programme |
| POST | `/api/nutrition-programmes/{id}/enroll` | Enroll a beneficiary |
| GET | `/api/nutrition-programmes/{id}/participants` | List participants |
| PATCH | `/api/nutrition-programmes/participations/{participationId}/drop` | Drop a participant |
| PATCH | `/api/nutrition-programmes/participations/{participationId}/complete` | Complete a participation |

### supply-service (`:8083`)

| Method | Path | Description |
|---|---|---|
| GET | `/api/catalog/categories` | List food categories |
| GET | `/api/catalog/items` | List food items |
| GET | `/api/catalog/items/by-category/{categoryId}` | List food items by category |
| GET | `/api/inventory/warehouses` | List warehouses |
| GET | `/api/inventory/batches` | List stock batches |
| GET | `/api/inventory/batches/by-item/{foodItemId}` | List batches for an item (FEFO order) |
| GET | `/api/inventory/available/{foodItemId}` | Get available quantity for an item |
| POST | `/api/inventory/receive` | Receive new stock |
| POST | `/api/inventory/deduct` | Deduct stock (FEFO logic) |
| GET | `/api/procurement/suppliers` | List suppliers |
| POST | `/api/procurement/suppliers` | Create a supplier |
| POST | `/api/procurement/requests` | Create a procurement request |
| GET | `/api/procurement/requests` | List procurement requests |
| POST | `/api/procurement/orders` | Approve request, create purchase order |
| POST | `/api/procurement/orders/{orderId}/items` | Add item to a purchase order |
| POST | `/api/procurement/orders/{orderId}/deliver` | Mark order delivered, receive stock |
| GET | `/api/procurement/orders` | List purchase orders |
| GET | `/api/distribution/centres` | List distribution centres |
| POST | `/api/distribution/centres` | Create a distribution centre |
| POST | `/api/distribution/plans` | Create a distribution plan |
| GET | `/api/distribution/plans` | List distribution plans |
| GET | `/api/distribution/plans/{planId}/items` | List items in a plan |
| POST | `/api/distribution/plan-items/{planItemId}/confirm-delivery` | Confirm delivery, deduct stock |

### quality-report-service (`:8084`)

| Method | Path | Description |
|---|---|---|
| GET | `/api/quality/inspections` | List quality inspections |
| GET | `/api/quality/inspections/batch/{batchId}` | List inspections for a batch |
| POST | `/api/quality/inspections` | Create a quality inspection |
| GET | `/api/quality/wastage` | List wastage records |
| GET | `/api/quality/wastage/batch/{batchId}` | List wastage records for a batch |
| POST | `/api/quality/wastage` | Create a wastage record |
| GET | `/api/quality/corrective-actions` | List corrective actions |
| GET | `/api/quality/corrective-actions/wastage/{wastageId}` | List corrective actions for a wastage record |
| POST | `/api/quality/corrective-actions` | Create a corrective action |

## Project structure

```
food-supply-management/
├── compose.yml                  (shared PostgreSQL container)
├── .env.example                 (environment variable template)
├── auth-service/                (port 8081)
├── beneficiary-service/         (port 8082)
├── supply-service/              (port 8083)
├── quality-report-service/      (port 8084)
└── docs/
    └── ARCHITECTURE.md          (full design notes)
```

## Notes

- Each service validates its schema against Flyway-managed tables
  (`spring.jpa.hibernate.ddl-auto=validate`) — it will not auto-create tables.
- `application.properties` files are intentionally gitignored per service; copy the example
  file where provided and fill in local values, or set the required properties via environment
  variables when running.
- See `docs/ARCHITECTURE.md` for the full target design, including components not yet built
  (API gateway, web UI, inter-service REST calls, and the remaining domain services).
