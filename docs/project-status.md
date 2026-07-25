# Project Status

## Current Phase

MVP Phase 1 - Catalog Service.

## Repository State

Current repository contents:

- `AGENTS.md`
- `PROJECT_BRIEF.md`
- Phase 0 documentation
- Maven Wrapper
- Root aggregator POM
- `build-platform` POM-only module
- Build verification scripts
- `services/catalog-service` MVP catalog implementation
- Active MVP roadmap documentation

Application code exists only in `services/catalog-service`.

## Completed

- Project brief defined.
- Agent working instructions defined.
- Phase 0 documentation foundation created.
- Initial ADRs documented.
- Maven build foundation created.
- Central dependency and plugin management configured.
- Maven Enforcer, compiler, Surefire, Failsafe, JaCoCo, and Spotless configured.
- Catalog service added to the Maven reactor.
- Admin category, product, variant, stock, and image URL APIs implemented.
- Public category and product browsing APIs implemented.
- Product filtering, pagination, basic PostgreSQL search, price range, and
  in-stock filters implemented.
- Draft, active, and inactive product statuses implemented.
- Service-local Problem Details-style error handling implemented.
- Correlation ID filter implemented.
- PostgreSQL/Flyway/JPA persistence path implemented.
- Catalog unit, web, and PostgreSQL Testcontainers tests added.
- Repository roadmap alignment updated for the MVP customer journey.

## Not Started

- MVP service rollout beyond Catalog Service.
- Gateway and discovery.
- Customer, Cart, Order, and Notification services.
- Angular frontend.
- Local showcase stack.

## Next Recommended Phase

Next recommended work is MVP Phase 2: Authentication and Customer.

Production-maturity work remains post-MVP unless explicitly approved.

Possible post-MVP follow-ups:

- GitHub Actions CI skeleton.
- Docker Compose local showcase stack.
- Build-platform approval for ArchUnit.
- Advanced monitoring and resilience.
- Elasticsearch, Redis, Kubernetes, and service mesh.

## Current Constraints

- Do not build all services at once.
- Do not initialize Angular yet.
- Do not introduce Elasticsearch yet.
- Do not commit or push without explicit approval.
- Do not create discovery, gateway, or config-server modules unless the active
  task explicitly approves them.
- Do not add Customer, Cart, Order, Notification, Gateway, Keycloak, Kafka,
  Angular, Elasticsearch, or Cloudinary capabilities without an explicit active
  task.
