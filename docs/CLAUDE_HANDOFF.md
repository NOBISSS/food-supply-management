# Food Supply Management — Project Status Report and Handoff

**Report date:** 2026-10-10  
**Repository:** `food-supply-management`  
**Branch observed:** `main`, tracking `origin/main` at `5dac5ae`

## 1. Executive summary

This repository is a Java 21 / Spring Boot application for an Integrated Food Supply & Nutrition Management system. The project began with PostgreSQL setup, Flyway schema work, and JWT authentication. It has since been extended with the first domain CRUD APIs for beneficiaries, food catalog items, suppliers, purchase orders, distribution plans, and quality inspections, plus a summary dashboard endpoint.

The code is currently a **single Spring Boot application organized into domain packages**. The multi-service design in `docs/ARCHITECTURE.md` remains a proposed target; the repository does not currently contain separate deployable microservices, a gateway, reporting aggregator, or web UI. The new domain APIs are foundational CRUD, not complete end-to-end food supply workflows.

The latest implementation was compiled successfully with `mvnw.cmd -q -DskipTests compile`. Tests and a live database startup were not run.

## 2. Technology and runtime shape

- Java 21; Maven wrapper (`mvnw.cmd` on Windows).
- Spring Boot parent `4.1.1`.
- Spring MVC, Spring Data JPA, Spring Security, Flyway, PostgreSQL JDBC, and JJWT `0.12.6`.
- Application entry point: `com.foodsupply.management.FoodSupplyManagementApplication`.
- HTTP port: `8081`.
- PostgreSQL is configured at `localhost:5433/food_supply_db`.
- Hibernate schema mode is `validate`; Flyway owns schema creation and updates.
- Existing security is stateless JWT. `/api/auth/**` is public; other application routes require authentication.

Local startup is intended to use Docker Compose for PostgreSQL, then `./mvnw spring-boot:run` (or `.\mvnw.cmd spring-boot:run` in PowerShell). The datasource username and password now come from `DB_USER` and `DB_PASSWORD`; these variables must be present in the environment of the Spring Boot process. Docker Compose reads `.env` for interpolation, but Spring Boot does not automatically load a Compose `.env` file.

## 3. Implemented packages and behavior

All package names are rooted at `com.foodsupply.management`.

### Authentication (`auth`)

Existing code was present before the latest domain work and was left unchanged:

- `entity`: `AppUser`, `Role` mapped to `app_users` and `roles`.
- `repository`: Spring Data repositories for users and roles.
- `dto`: registration, login, and auth response payloads.
- `service`: BCrypt password hashing/checking, registration and login, JWT issuance.
- `security`: stateless security filter chain, bearer token filter, and JWT utility.
- `controller`: `POST /api/auth/register` and `POST /api/auth/login`.

Registration defaults to `VIEWER` when no role is supplied, but currently trusts a caller-provided role name. This includes `ADMIN`, which is a security issue to fix before use beyond local development. The V1 migration's seeded admin password is a placeholder hash, so it does not provide usable admin credentials.

### Beneficiaries (`beneficiary`)

CRUD for a beneficiary with name, date of birth, gender, address, phone, eligibility status, and creation timestamp.

- `GET /api/beneficiaries`, `GET /api/beneficiaries/{id}`
- `POST /api/beneficiaries`, `PUT /api/beneficiaries/{id}`, `DELETE /api/beneficiaries/{id}`

### Food items (`foodinventory`)

CRUD for food catalog items with name, category, unit, calories per unit, protein per unit, and creation timestamp.

- `GET /api/food-items`, `GET /api/food-items/{id}`
- `POST /api/food-items`, `PUT /api/food-items/{id}`, `DELETE /api/food-items/{id}`

Despite the package name, stock batches, stock movements, expiry tracking, and stock-level operations are not implemented yet.

### Procurement (`procurement`)

CRUD for suppliers and purchase order headers. Purchase orders currently store supplier ID, status, order date, expected delivery date, and creation timestamp. There are no order line items or external food-catalog checks yet.

- Suppliers: `/api/suppliers` and `/api/suppliers/{id}` (GET/POST/PUT/DELETE)
- Purchase orders: `/api/purchase-orders` and `/api/purchase-orders/{id}` (GET/POST/PUT/DELETE)

### Distribution (`distribution`)

CRUD for distribution plans containing beneficiary ID, food item ID, quantity, distribution centre ID, planned date, status, and creation timestamp.

- `GET /api/distribution-plans`, `GET /api/distribution-plans/{id}`
- `POST /api/distribution-plans`, `PUT /api/distribution-plans/{id}`, `DELETE /api/distribution-plans/{id}`

Distribution centres and checks for beneficiary eligibility or available inventory are not implemented.

### Quality (`quality`)

CRUD for inspections with stock batch ID, inspection date, result, wastage quantity, corrective action, and inspector.

- `GET /api/quality-inspections`, `GET /api/quality-inspections/{id}`
- `POST /api/quality-inspections`, `PUT /api/quality-inspections/{id}`, `DELETE /api/quality-inspections/{id}`

There is no connection to an implemented stock-batch model and no rejection/wastage workflow yet.

### Dashboard (`dashboard`)

- `GET /api/dashboard/summary` returns counts for beneficiaries, food items, purchase orders, distribution plans, and quality inspections.
- It is a direct read of the corresponding repositories inside this monolith, not a microservice aggregator.

### Shared packages (`common`)

- `common.exception.ResourceNotFoundException` and `ApiExceptionHandler` return a JSON 404 for missing resource IDs.
- `common.config.ApiConfig` is a placeholder; it currently adds no substantive behavior.

Except for the auth routes, these API endpoints are protected by the existing JWT security policy. The controller methods return JPA entities directly. DTO classes are request payloads; separate response DTOs, validation, pagination, sorting, and API documentation have not been added.

## 4. Persistence and migrations

Flyway migrations live in `src/main/resources/db/migration`:

- `V1__initial_schema.sql`: roles and app users, role seed rows, and a placeholder admin row. This file has uncommitted local modifications in the current working tree. Inspect its diff before changing it.
- `V2__create_domain_tables.sql`: beneficiaries, food items, suppliers, purchase orders, distribution plans, and quality inspections.
- `V3__tables.sql`: currently present as an uncommitted working-tree migration. It adds nutrition requirements, stock batches and movements, distribution centres, purchase order items, nutrition programmes and participations, plus foreign keys from programme participation to beneficiaries, plans to centres, and inspections to stock batches. The current CRUD code does not yet include these new entities/APIs. Review this migration against the target schema and the database's Flyway history before relying on it; its Git index version was observed empty while its working-tree version contains SQL.

In V2, domain IDs that point to another area (supplier, beneficiary, food item, distribution centre, stock batch) are plain numeric IDs, without cross-domain checks. The uncommitted V3 migration adds selected local database foreign keys as listed above. Nutrition requirement, stock batch/movement, distribution centre, and purchase-order item tables are described in V3, but do not yet have corresponding Java entities or API layers.

For a database where V1 has already run, keep migrations forward-only and add a new versioned migration instead of editing V1 or V2. Only reset a local disposable database when intentionally clearing its data.

## 5. Design intent in `docs/ARCHITECTURE.md`

The architecture document describes seven microservices (beneficiary, catalog, procurement, inventory, distribution, nutrition programme, quality/wastage), an API gateway, reporting service, and Thymeleaf UI. It proposes REST and optional events between services and gives example schemas. These components are **not implemented** in the current code. The current package layout is a monolith and only covers part of that scope. Confirm the desired direction against project requirements before adding service boundaries or infrastructure.

## 6. Git history and current working tree

Recent committed history includes Flyway/configuration iterations, `d47a416` (“Authentication and User Services Created”), and `5dac5ae` (“db username changed”). At the time this report was prepared, the following were already uncommitted or newly created; preserve and inspect them rather than assuming they are disposable:

- Modified: `.gitignore`, `compose.yml`, `src/main/resources/application.properties`, `src/main/resources/db/migration/V1__initial_schema.sql`, and the staged/working-tree state of `V3__tables.sql`.
- New: this report and domain/common/dashboard source packages, plus `V2__create_domain_tables.sql`.
- `bin/` is tracked and contains duplicate project artifacts and compiled classes. `target/` is generated build output.

The report task did not alter those pre-existing modified files. The domain source and V2 migration are uncommitted additions from the current development work.

## 7. Configuration and known gaps

1. **Database environment variables:** `application.properties` now uses `${DB_USER}` and `${DB_PASSWORD}`. Compose currently creates PostgreSQL user `postgres` and takes its password from `DB_PASSWORD`, so use `DB_USER=postgres` for the app. Ensure both variables are exported/set when launching Spring Boot; a `.env` file used by Compose is not automatically loaded by Spring Boot or IntelliJ.
2. **Secrets:** `.env` is ignored by Git and contains local credentials. Never include its values in documentation or commits. `jwt.secret` remains hard-coded in properties and should be provided through a secure environment-specific setting.
3. **Unsafe registration role selection:** public registration permits requesting privileged roles. Restrict self-registration or add a protected administrative user-provisioning flow.
4. **Seeded admin hash:** the V1 SQL hash is a placeholder, not a valid configured password.
5. **Validation and API errors:** request payloads are not validated, generic auth exceptions are not mapped to clear HTTP errors, and domain error handling currently covers only missing IDs.
6. **Authorization:** JWT authentication is enabled, but role-based access rules are not defined for domain routes.
7. **Domain completeness:** V3 now has SQL for stock inventory, nutrition requirements/programmes, distribution centres, and purchase order items, but Java entities, repositories, DTOs, services, controllers, and workflows for those tables are still missing.
8. **Test coverage:** only the generated context-load test exists. No domain/auth behavior tests have been added or run as part of the reported compile.
9. **Environment verification:** compilation passed with tests skipped; live Postgres migration/startup and endpoint behavior remain unverified.

## 8. Suggested continuation order

1. Review `git status` and diffs first; preserve existing user edits.
2. Reconcile Compose and Spring datasource credentials, move JWT secret to environment configuration, and define a safe admin bootstrap.
3. Add input validation, consistent error responses, and role-based authorization.
4. Add tests for auth and each CRUD area; verify Flyway against a disposable PostgreSQL database.
5. Implement missing inventory entities and movements, purchase order items, distribution centres, nutrition requirements/programmes, and cross-domain workflows according to confirmed requirements.
6. Decide monolith versus microservices, then bring `docs/ARCHITECTURE.md` into alignment with the chosen implementation.

## 9. Handoff notes for Claude

Begin by inspecting the current Git status and diffs, especially the pre-existing edits to configuration and V1 SQL. Treat the source as current truth and this report as a guide. Do not expose `.env` values. When making changes, keep Flyway history forward-only and report commands actually run and their results. Current verified result: `mvnw.cmd -q -DskipTests compile` succeeded; no tests or database startup were performed.
