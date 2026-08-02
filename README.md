# Commerce Platform

Production-oriented full-stack e-commerce platform built incrementally for learning, portfolio development, and practical distributed systems experience.

The backend will be implemented first with Java, Spring Boot, Spring Cloud, Maven, PostgreSQL, Kafka, Keycloak, Docker Compose, Flyway, OpenAPI, Testcontainers, and GitHub Actions. The Angular frontend starts only after core backend APIs and workflows are stable.

## Current Phase

MVP Phase 3 - API Gateway and Eureka Service Discovery.

The Maven build foundation is preserved. Catalog Service supports
administrator catalog management and public product browsing, Customer Service
supports authenticated customer profiles and shipping addresses, and API
Gateway is the public backend entry point for the current MVP services.

## Build Foundation

Implemented Maven modules:

```text
.
├── pom.xml
├── build-platform
│   └── pom.xml
└── services
    ├── catalog-service
    │   └── pom.xml
    └── customer-service
        └── pom.xml
```

The root POM is the aggregator and parent. `build-platform` is a POM-only module used to validate inherited build policy without adding runtime code. `discovery-server` and `api-gateway` are platform modules. `catalog-service` and `customer-service` are the current MVP service modules.

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
.\mvnw.cmd -pl platform/discovery-server -am clean verify
.\mvnw.cmd -pl platform/api-gateway -am clean verify
.\mvnw.cmd -pl services/catalog-service -am clean verify
.\mvnw.cmd -pl services/customer-service -am clean verify
```

```sh
./mvnw clean verify
./scripts/verify.sh
./mvnw -pl platform/discovery-server -am clean verify
./mvnw -pl platform/api-gateway -am clean verify
./mvnw -pl services/catalog-service -am clean verify
./mvnw -pl services/customer-service -am clean verify
```

## Catalog Service

`services/catalog-service` implements the MVP Catalog Service using ports and
adapters, Spring Boot, PostgreSQL, Flyway, JPA, and Testcontainers.

Implemented admin APIs require a valid Keycloak JWT with the `ADMIN` role:

- `POST /api/v1/admin/categories`
- `PUT /api/v1/admin/categories/{categoryId}`
- `GET /api/v1/admin/categories`
- `GET /api/v1/admin/categories/{categoryId}`
- `POST /api/v1/admin/products`
- `PUT /api/v1/admin/products/{productId}`
- `GET /api/v1/admin/products`
- `GET /api/v1/admin/products/{productId}`
- `PATCH /api/v1/admin/products/{productId}/status`
- `POST /api/v1/admin/products/{productId}/variants`
- `PUT /api/v1/admin/products/{productId}/variants/{variantId}`
- `PATCH /api/v1/admin/products/{productId}/variants/{variantId}/stock`
- `DELETE /api/v1/admin/products/{productId}/variants/{variantId}`
- `POST /api/v1/admin/products/{productId}/images`
- `PUT /api/v1/admin/products/{productId}/images/{imageId}`
- `DELETE /api/v1/admin/products/{productId}/images/{imageId}`

Implemented public APIs:

- `GET /api/v1/categories`
- `GET /api/v1/products`
- `GET /api/v1/products/{productIdOrSlug}`

Public product listing supports page, size, sort, name search, category,
brand, price range, and in-stock filters using PostgreSQL queries. The MVP
currency is `USD`. Product image binary upload and external media SDKs are out
of scope; administrators provide image URLs.

## Authentication and Customer Service

Local authentication uses Keycloak realm `commerce-mvp`.

Current local endpoints:

- API Gateway: `http://localhost:8080`
- Eureka Dashboard: `http://localhost:8761`
- Keycloak: `http://localhost:8081`
- Mailpit UI: `http://localhost:8025`
- Catalog Service: `http://localhost:8083`
- Customer Service: `http://localhost:8082`

The Gateway is the preferred public backend entry point. Direct service ports
remain exposed locally for development and troubleshooting.

Customer APIs require authentication:

- `GET /api/v1/customers/me`
- `PUT /api/v1/customers/me`
- `POST /api/v1/customers/me/addresses`
- `GET /api/v1/customers/me/addresses`
- `GET /api/v1/customers/me/addresses/{addressId}`
- `PUT /api/v1/customers/me/addresses/{addressId}`
- `DELETE /api/v1/customers/me/addresses/{addressId}`
- `PATCH /api/v1/customers/me/addresses/{addressId}/default`

Demo users for local development:

- Admin: `admin@example.test` / `admin-local-pass`
- Customer: `customer@example.test` / `customer-local-pass`

Start local authentication and databases:

```powershell
docker compose -f infrastructure/compose.yaml up -d keycloak keycloak-db mailpit catalog-db customer-db
```

Build and start the current local services through Compose:

```powershell
docker compose -f infrastructure/compose.yaml up -d --build discovery-server catalog-service customer-service api-gateway
```

If host port `8080` is already occupied, override the Gateway host port:

```powershell
$env:API_GATEWAY_HOST_PORT = "8084"
docker compose -f infrastructure/compose.yaml up -d --build api-gateway
```

Gateway route table:

- `GET /api/v1/categories` -> Catalog Service, anonymous.
- `GET /api/v1/products` and `GET /api/v1/products/**` -> Catalog Service,
  anonymous.
- `/api/v1/admin/categories/**` and `/api/v1/admin/products/**` -> Catalog
  Service, `ADMIN` required.
- `/api/v1/customers/**` -> Customer Service, authentication required.

Run the local Gateway smoke test after the Compose stack is healthy:

```powershell
.\scripts\smoke-gateway.ps1
```

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
