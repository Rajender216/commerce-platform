# Project Status

## Current Phase

MVP Phase 3 - API Gateway and Eureka Service Discovery.

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
- `platform/discovery-server` Eureka registry
- `platform/api-gateway` reactive edge gateway
- Keycloak, Mailpit, and PostgreSQL local infrastructure in
  `infrastructure/compose.yaml`
- Keycloak realm import in `infrastructure/keycloak/realm-commerce-mvp.json`
- Active MVP roadmap documentation

Application code exists in `services/catalog-service`,
`services/customer-service`, `platform/discovery-server`, and
`platform/api-gateway`.

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
- Eureka Discovery Server added to the Maven reactor.
- API Gateway added to the Maven reactor.
- Catalog Service, Customer Service, and API Gateway register with Eureka.
- Gateway routes current Catalog and Customer APIs through service discovery.
- Gateway validates JWTs, enforces public/authenticated/admin route rules, and
  forwards bearer tokens downstream.
- Gateway CORS, correlation-ID propagation, and safe edge Problem responses
  implemented.
- Gateway route/security tests and Discovery context/configuration tests added.
- Docker Compose updated for Discovery Server and API Gateway.
- Gateway smoke-test script added.
- Repository roadmap alignment updated for the MVP customer journey.

## Not Started

- Cart, Order, and Notification services.
- Angular frontend.
- Full local showcase sample data and walkthrough.

## Next Recommended Phase

Next recommended work is MVP Phase 4: Cart.

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
- Do not create config-server modules unless the active task explicitly approves them.
- Do not add Cart, Order, Notification, Kafka, Angular,
  Elasticsearch, Redis, Cloudinary, or payment capabilities without an explicit
  active task.
