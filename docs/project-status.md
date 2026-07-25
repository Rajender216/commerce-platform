# Project Status

## Current Phase

MVP Phase 2 - Authentication and Customer Service.

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
- `services/customer-service` MVP customer implementation
- Keycloak, Mailpit, and PostgreSQL local infrastructure in
  `infrastructure/compose.yaml`
- Keycloak realm import in `infrastructure/keycloak/realm-commerce-mvp.json`
- Active MVP roadmap documentation

Application code exists in `services/catalog-service` and
`services/customer-service`.

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
- Keycloak local infrastructure added with `CUSTOMER` and `ADMIN` roles,
  backend and future frontend clients, demo users, Mailpit SMTP settings, and
  Google identity-provider placeholders.
- Catalog admin APIs protected by JWT validation and `ADMIN` authorization.
- Catalog public browsing APIs remain anonymous.
- Customer service added to the Maven reactor.
- Customer profile and shipping-address APIs implemented.
- Lazy idempotent customer profile creation from JWT claims implemented.
- Customer service PostgreSQL/Flyway/JPA persistence path implemented.
- Customer unit, security/MVC, and PostgreSQL Testcontainers tests added.
- Repository roadmap alignment updated for the MVP customer journey.

## Not Started

- Gateway and discovery.
- Cart, Order, and Notification services.
- Angular frontend.
- Full local showcase sample data and walkthrough.

## Next Recommended Phase

Next recommended work is MVP Phase 3: Gateway and Discovery.

Production-maturity work remains post-MVP unless explicitly approved.

Possible post-MVP follow-ups:

- GitHub Actions CI skeleton.
- Local showcase sample data and walkthrough.
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
- Do not add Cart, Order, Notification, Gateway, Kafka, Angular,
  Elasticsearch, Redis, Cloudinary, or payment capabilities without an explicit
  active task.
