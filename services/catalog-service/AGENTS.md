# Catalog Service Agent Instructions

## Ownership

`catalog-service` owns catalog data for the commerce platform.

Current approved capability:

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

Current product statuses:

- `DRAFT`
- `ACTIVE`
- `INACTIVE`

MVP limitations:

- Admin endpoints require a valid Keycloak JWT with the `ADMIN` role.
- Product image upload is not owned here yet; administrators provide image URLs.
- Search uses PostgreSQL queries rather than Elasticsearch.
- Variant hard delete is allowed only while no cart/order capability references
  variants.

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

The service validates Keycloak JWTs directly. The API Gateway may also validate
tokens, but it must not become this service's only authorization boundary.

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

`catalog-service` owns these tables:

- `categories`
- `products`
- `product_variants`
- `product_images`

Rules:

- Schema creation is owned by Flyway migrations.
- Hibernate `ddl-auto` must remain `validate`.
- PostgreSQL is the database target.
- Do not use H2 as a substitute for PostgreSQL integration tests.
- No other service may read or write catalog tables.

## Required Tests

Required test coverage for catalog changes:

- Domain unit tests for category, product, variant, stock, status, and image rules.
- Application service unit tests for use-case behavior and business conflicts.
- Web tests for admin APIs, public browsing, validation, missing resources,
  duplicate resources, error shape, and correlation ID.
- Security tests for anonymous public browsing, admin authorization, and
  customer-token denial on admin APIs.
- PostgreSQL Testcontainers integration tests for Flyway, persistence, mapping,
  optimistic locking, constraints, filtering, and pagination.

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
- Config Server.
- Cloudinary.
- Elasticsearch.
- Angular.
