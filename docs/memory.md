# Project Memory

## Stable Decisions

- Use a Maven multi-module monorepo.
- Backend implementation comes before frontend implementation.
- The top priority is a complete MVP customer journey before production
  maturity work.
- Use Spring Boot and Spring Cloud for backend services and platform components.
- Use PostgreSQL as the initial source of truth.
- Use Kafka for asynchronous events.
- Use Keycloak for OAuth 2.0 and OpenID Connect.
- Use ports and adapters inside services.
- Use Flyway for database migrations.
- Use Testcontainers for infrastructure integration tests.
- Use OpenAPI for REST API documentation.
- Use Docker Compose for local infrastructure.
- Use GitHub Actions for CI.
- Use Java release `21` for compilation.
- Use Spring Boot `4.1.0`.
- Use Spring Cloud `2025.1.2`.
- Use Apache Maven Wrapper with Maven `3.9.16`.
- Catalog Service owns categories, products, variants, SKU, price, stock, and
  product image metadata during the MVP.
- Payment remains inside Order Service during the MVP.

## Deferred Decisions

- Dedicated database versus schema-per-service for local development.
- API gateway implementation details.
- Service discovery timing.
- Kafka topic naming convention.
- Event schema format and schema registry timing.
- Observability stack selection.
- Inventory Service extraction after the MVP.
- Media Service extraction after the MVP.
- Payment Service extraction after the MVP.
- Transactional outbox, advanced saga orchestration, and advanced messaging
  reliability.
- Elasticsearch, Redis, Kubernetes, service mesh, and full production
  hardening.

## Constraints To Preserve

- Do not generate all services at once.
- Do not add Angular until backend workflows are stable.
- Do not let business logic depend directly on provider SDKs.
- Do not let services query another service's database.
- Do not use cross-service database transactions.
- Do not return JPA entities from controllers.
- Do not commit secrets or real credentials.
- Do not commit or push without explicit approval.
- Do not create platform service skeletons unless the active task explicitly approves them.
- Keep customer profile ownership separate from identity credential ownership.
- Keep Inventory and Media inside Catalog Service until the MVP is complete.
- Keep payment inside Order Service until the MVP is complete.

## Phase Notes

Phase 0 is documentation only.

Phase 1 established Maven build and dependency management. CI foundation and local infrastructure remain separate follow-up work unless explicitly approved.

Phase 2 should add one golden service to prove patterns before creating additional services.

The active roadmap now continues with MVP phases documented in
`docs/roadmap/mvp-roadmap.md`.
