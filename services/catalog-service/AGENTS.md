# Catalog Service Agent Instructions

## Ownership

`catalog-service` owns catalog data for the commerce platform.

Current approved capability:

- Create draft product.
- Retrieve product by ID.

Current product fields:

- `id`
- `name`
- `description`
- `status`
- `createdAt`
- `updatedAt`
- `version`

MVP ownership scope for Catalog Service:

- Categories.
- Products.
- Product variants.
- SKU.
- Product price.
- Stock quantity.
- Product image metadata.
- Product activation status.
- Product browsing.
- Filtering.
- Pagination.
- Basic search.

Current status:

- `DRAFT`

## Non-Owned Responsibilities

Do not add these responsibilities to this service without an explicit approved task:

- Orders.
- Cart.
- Checkout orchestration.
- Payments.
- Media storage as a separate service.
- Authentication or password handling.
- Customer profile data.
- Notifications.
- Payment provider integration.

The product API is temporarily unsecured until the security phase. Do not add Keycloak or other authentication dependencies in this service without explicit approval.

Do not extract Inventory Service or Media Service before the MVP customer
journey is complete. Inventory and image metadata remain in Catalog Service
until then.

## Package Dependency Rules

- `domain` contains framework-independent product rules and value objects.
- `application` contains use-case ports, outbound ports, and application services.
- `application` must not depend on persistence adapter implementation classes.
- `domain` must not depend on application, adapter, Spring, JPA, or web classes.
- `adapter.in.web` contains REST DTOs, controllers, filters, and service-local error handling.
- `adapter.out.persistence` contains JPA entities, Spring Data repositories, persistence mappers, and persistence adapters.
- Controllers must not call Spring Data repositories directly.
- JPA entities must not be returned from controllers.

## Database Ownership

`catalog-service` owns the `products` table.

Rules:

- Schema creation is owned by Flyway migrations.
- Hibernate `ddl-auto` must remain `validate`.
- PostgreSQL is the database target.
- Do not use H2 as a substitute for PostgreSQL integration tests.
- No other service may read or write catalog tables.

## Required Tests

Required test coverage for catalog changes:

- Domain unit tests for product creation rules.
- Application service unit tests for create and get behavior.
- Product-not-found unit tests.
- Web tests for create, validation, get, missing product, error shape, and correlation ID.
- PostgreSQL Testcontainers integration tests for Flyway, persistence, mapping, optimistic locking, and database constraints.

ArchUnit is not currently approved in the build platform. Add architecture tests only after that tooling is centrally approved.

## Commands

Run this module and required upstream modules:

```powershell
.\mvnw.cmd -pl services/catalog-service -am clean verify
```

Run the full repository:

```powershell
.\mvnw.cmd clean verify
```

POSIX equivalents:

```sh
./mvnw -pl services/catalog-service -am clean verify
./mvnw clean verify
```

## Prohibited Technology In This Phase

Do not add:

- Kafka.
- OpenFeign.
- Eureka.
- Spring Cloud Gateway.
- Config Server.
- Keycloak.
- Cloudinary.
- Elasticsearch.
- Angular.
- Docker Compose.
