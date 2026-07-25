# Commerce Platform

Production-oriented full-stack e-commerce platform built incrementally for learning, portfolio development, and practical distributed systems experience.

The backend will be implemented first with Java, Spring Boot, Spring Cloud, Maven, PostgreSQL, Kafka, Keycloak, Docker Compose, Flyway, OpenAPI, Testcontainers, and GitHub Actions. The Angular frontend starts only after core backend APIs and workflows are stable.

## Current Phase

MVP roadmap alignment.

Java application code currently exists only in `services/catalog-service`.
The Maven build foundation and catalog-service foundation are preserved while
the repository roadmap is aligned around the complete MVP customer journey.

## Build Foundation

Implemented Maven modules:

```text
.
├── pom.xml
├── build-platform
│   └── pom.xml
└── services
    └── catalog-service
        └── pom.xml
```

The root POM is the aggregator and parent. `build-platform` is a POM-only module used to validate inherited build policy without adding runtime code. `catalog-service` is the current service foundation for the MVP roadmap.

Selected versions:

- Java release: `21`
- Apache Maven Wrapper distribution: `3.9.16`
- Maven Wrapper: `3.3.4`
- Spring Boot BOM: `4.1.0`
- Spring Cloud BOM: `2025.1.2`
- Testcontainers BOM: `1.21.3`

Verify the build:

```powershell
.\mvnw.cmd clean verify
.\scripts\verify.ps1
.\mvnw.cmd -pl services/catalog-service -am clean verify
```

```sh
./mvnw clean verify
./scripts/verify.sh
./mvnw -pl services/catalog-service -am clean verify
```

## Catalog Service

`services/catalog-service` currently implements only a draft product capability:

- `POST /api/v1/products`
- `GET /api/v1/products/{productId}`

The endpoints are temporarily unsecured until the security phase. They exist to prove service structure, validation, error handling, persistence, Flyway migrations, testing, Docker packaging, and correlation ID behavior.

The MVP roadmap expands Catalog Service next to cover categories, products,
variants, SKU, pricing, stock, image metadata, activation status, browsing,
filtering, pagination, and basic search.

## Planned Repository Shape

The canonical planned repository structure is documented in [Architecture](docs/architecture.md).

That structure is not permission to create all directories or modules immediately. Directories and modules should be introduced only when their approved phase needs them.

## Documentation

- [Project Context](docs/context.md)
- [Architecture](docs/architecture.md)
- [Development Guide](docs/development.md)
- [Testing Strategy](docs/testing-strategy.md)
- [Security](docs/security.md)
- [API Guidelines](docs/api-guidelines.md)
- [Event Guidelines](docs/event-guidelines.md)
- [Definition of Done](docs/definition-of-done.md)
- [Project Status](docs/project-status.md)
- [Project Memory](docs/memory.md)
- [MVP Roadmap](docs/roadmap/mvp-roadmap.md)
- [Architecture Decision Records](docs/adr/README.md)

## Main Business Milestone

The first complete milestone is the MVP customer journey:

1. Administrator sign-in.
2. Category and product administration.
3. Variant, price, stock, and image administration.
4. Customer registration or sign-in.
5. Product browsing and search.
6. Product detail view.
7. Cart management.
8. Shipping address capture.
9. Checkout.
10. Price and stock validation.
11. Fake payment.
12. Order creation.
13. Stock reduction.
14. Cart clearing.
15. Order confirmation notification.
16. Order history and order details.
17. Angular frontend for the full flow.
18. Local startup with documented commands.

## Working Rules

- Work in small phases.
- Do not generate all services at once.
- Do not push directly to `main`.
- Do not commit or push without explicit approval.
- Add major architectural decisions through ADRs.
- Keep business logic independent of provider SDKs.
- Use REST for synchronous operations and events for asynchronous propagation.
- Use Flyway for database changes.
- Use Testcontainers for infrastructure integration tests.
- Complete the MVP journey before spending time on advanced observability,
  resilience, or production hardening.
