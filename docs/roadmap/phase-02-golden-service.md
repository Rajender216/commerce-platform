# Phase 02: Golden Service

## Goal

Create one business service that proves the architecture, build, testing, migrations, API style, and adapter boundaries before other services are created.

Selected golden service: `catalog-service`.

This phase is complete. It is retained as a foundation record. The active
product roadmap now continues in `docs/roadmap/mvp-roadmap.md`.

## Candidate Services

Recommended candidates:

- `catalog-service`
- `customer-service`

`catalog-service` is a strong candidate because it can demonstrate admin-only writes, public reads, PostgreSQL persistence, Flyway migrations, OpenAPI documentation, and future search evolution.

`customer-service` is a strong candidate if the team wants to validate customer profile boundaries and token-based authorization first. It must not own password storage, registration credentials, email verification, password reset, or social login linkage.

## Proposed Scope

For the selected golden service:

- Service Maven module.
- Hexagonal package structure.
- Domain model for a narrow use case.
- REST API for the selected use case.
- DTOs and validation.
- PostgreSQL persistence adapter.
- Flyway migration.
- Unit tests.
- Controller tests.
- Persistence integration tests with Testcontainers.
- OpenAPI documentation.

Implemented in the catalog golden service task:

- `services/catalog-service` Maven module.
- Spring Boot application.
- Hexagonal package structure.
- Framework-independent product draft domain model.
- Create/get product inbound ports.
- Product repository outbound port.
- Application service transaction boundary.
- REST controller adapter.
- Service-local Problem Details-style error model and centralized exception handling.
- Correlation ID HTTP filter using `X-Correlation-ID`.
- JPA persistence adapter with separate JPA entity and explicit mapper.
- Flyway migration for the `products` table.
- PostgreSQL configuration with Hibernate `ddl-auto: validate`.
- Actuator health, info, and metrics exposure.
- Multi-stage Dockerfile with non-root runtime user and health check.
- Service-level `AGENTS.md`.
- Unit, web, and PostgreSQL Testcontainers integration tests.

Not implemented:

- OpenAPI generation dependency.
- ArchUnit tests, because ArchUnit is not yet centrally approved in the build platform.
- Authentication or authorization integration.

## Non-Goals

- Do not create all services.
- Do not implement full purchase flow.
- Do not add frontend.
- Do not introduce Elasticsearch.
- Do not add real payment, email, or object storage providers.

## Exit Criteria

- One service demonstrates the agreed structure.
- Tests prove domain, API, persistence, and migration behavior.
- Stable lessons are recorded in project documentation when they establish a reusable convention.
- Any new architecture decisions are captured as ADRs.

Current verification command:

```powershell
.\mvnw.cmd -pl services/catalog-service -am clean verify
```
