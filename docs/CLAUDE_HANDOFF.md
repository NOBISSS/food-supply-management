# Food Supply Management — Project Status and Handoff

**Report date:** 2026-10-10  
**Stack:** Java 21, Spring Boot 4.1.1, Maven multi-module, PostgreSQL, Flyway

## Current status

The repository has been restructured from one Spring Boot application into independently buildable/runnable service modules. Existing controller paths and domain behavior were moved without adding business workflows. New Java classes are limited to Spring Boot launch entry points; Maven POMs and per-service configuration provide module/build/runtime wiring. The multi-module package build completed successfully using `mvnw.cmd -q -DskipTests package`. Tests and live database startup were not run.

## Modules

| Module | Port | Current code |
|---|---:|---|
| `auth-service` | 8081 | Existing registration/login API, user and role persistence, token issuance |
| `beneficiary-service` | 8082 | Beneficiary CRUD |
| `food-catalog-service` | 8083 | Food item CRUD (moved from `foodinventory`) |
| `procurement-service` | 8084 | Supplier and purchase order header CRUD |
| `inventory-service` | 8085 | Startup/build shell and stock migrations; no domain API/entities yet |
| `distribution-service` | 8086 | Distribution plan CRUD |
| `nutrition-programme-service` | 8087 | Startup/build shell and nutrition programme migrations; no domain API/entities yet |
| `quality-wastage-service` | 8088 | Quality inspection CRUD |
| `reporting-service` | 8089 | Existing dashboard summary code |
| `common-library` | — | Existing common exception handling and JWT/security classes shared across modules |

Each service has a Maven module, Spring Boot main class, `application.properties`, and owned migration directory. Root `pom.xml` aggregates all modules. Build the modules from repository root with `mvnw.cmd -DskipTests package`; executable service jars use the `-exec.jar` classifier. The API gateway and web UI in `docs/ARCHITECTURE.md` have no implementation and were not added.

## Existing API routes

- Auth: `POST /api/auth/register`, `POST /api/auth/login`
- Beneficiary: `/api/beneficiaries` and `/{id}` (GET/POST/PUT/DELETE)
- Food catalog: `/api/food-items` and `/{id}` (GET/POST/PUT/DELETE)
- Procurement: `/api/suppliers`, `/api/purchase-orders` and `/{id}` (GET/POST/PUT/DELETE)
- Distribution: `/api/distribution-plans` and `/{id}` (GET/POST/PUT/DELETE)
- Quality: `/api/quality-inspections` and `/{id}` (GET/POST/PUT/DELETE)
- Dashboard: `GET /api/dashboard/summary`

JWT security configuration is shared from `common-library`; domain endpoints require a valid bearer token. The dashboard currently depends on the domain modules and reads their repositories against the shared database. That preserves its existing behavior, but is not yet the REST-based reporting service shown in the target design.

## Persistence and configuration

Services connect to the Compose PostgreSQL database `food_supply_db` on host port `5433`, with `DB_USER` and `DB_PASSWORD` supplied to each Spring process. Compose currently sets PostgreSQL user `postgres`, so set `DB_USER=postgres`. Compose can interpolate `.env`; Spring Boot and IntelliJ do not automatically load that file.

Auth retains the existing `flyway_schema_history` table to recognize the prior V1 migration. Other persistence-owning services use distinct Flyway history table names so their versioned migrations can coexist in the shared schema. SQL from the previous combined V2/V3 migrations has been divided among service migration directories by table ownership. The prior combined migration files are retained under `src/main/resources/db/migration/*.monolith-backup` for reference and are not active migration resources.

If a database has already been migrated by the monolith, service-specific Flyway histories may cause migrations to attempt creating tables that already exist. Reconcile migration history before starting services against a database with important data; do not reset it casually. The current quality migration has no foreign key to inventory; inventory does not need to start first.

## Target architecture versus current implementation

`docs/ARCHITECTURE.md` is the target design. The project now has a multi-module service structure, but does not yet implement the API gateway, web UI, inter-service REST calls, event messaging, full inventory APIs, nutrition programme APIs, or HTTP-based dashboard aggregation. `inventory-service` and `nutrition-programme-service` are shells with their migration schemas only. Continue with those gaps only when asked; do not mistake module scaffolding for completed domain functionality.

## Known issues to address in later development

1. Public registration currently accepts a caller-supplied role, including `ADMIN`; restrict this before non-local use.
2. The V1 admin seed password is a placeholder BCrypt value.
3. JWT secret has a local fallback in service properties; production must supply `JWT_SECRET` securely and rotate any exposed/shared secret.
4. Request validation, consistent auth errors, role-based authorization, API documentation, and domain behavior tests are missing.
5. Cross-service reference IDs are not checked through REST calls. Do not introduce cross-service database foreign keys.
6. There is no gateway or UI yet. Reporting still reads repositories directly and is not isolated from domain persistence.
7. Database startup/migrations and endpoint flows have not been exercised. Only Maven packaging with tests skipped has been verified.

## Git and continuation notes

The branch was observed at `5dac5ae` (`db username changed`) before these local changes. Preserve existing user modifications to `compose.yml`, `.gitignore`, `application.properties`, V1 SQL, V3 SQL, and the edited architecture document. The prior monolith source/config and combined migrations were relocated or retained as backups during extraction; do not remove those backups until the new service migration state is reconciled. `bin/` is tracked legacy project output; `target/` and module targets are generated build output.

Start follow-up work by inspecting `git status` and diffs. Do not expose `.env` values. Keep migrations forward-only and report the exact verification commands run.
