# Project Context

## Objective

Build a showcaseable e-commerce MVP first, then mature the platform toward
production quality after the complete customer journey works end to end.

The application should be developed incrementally. Each phase should leave the
repository in a coherent, reviewable state.

## Product Scope

The MVP business domain includes:

- Category, product, variant, SKU, price, stock, and image metadata
  management.
- Customer profile data and shipping addresses.
- Shopping cart.
- Checkout and order confirmation.
- Fake payment processing.
- Local email notification.

Catalog Service owns inventory and product image metadata during the MVP. Do
not extract Inventory Service or Media Service before the MVP is complete.

Payment remains inside Order Service during the MVP. Do not extract Payment
Service before the MVP is complete.

Identity lifecycle is a separate identity-provider concern. Registration, password storage, email verification, password reset, social sign-in, and identity credentials belong to Keycloak or the identity boundary, not to business services.

## Technical Scope

The backend is the first priority. The frontend starts only after core backend
APIs and workflows are stable.

Primary backend technologies:

- Java
- Spring Boot
- Spring Cloud
- Maven
- PostgreSQL
- Kafka
- Keycloak
- Flyway
- Testcontainers
- Docker Compose
- OpenAPI
- GitHub Actions

## Architectural Direction

The platform will use a microservice architecture inside a Maven multi-module
monorepo. Each service owns its data and exposes behavior through APIs and
events.

Core rules:

- Services do not query another service's database.
- Cross-service database transactions are forbidden.
- REST handles immediate synchronous interactions.
- Kafka events handle the first meaningful asynchronous workflow:
  `OrderConfirmed` to Notification Service.
- Event consumers are idempotent.
- Business logic depends on ports, not provider SDKs.
- Controllers do not return JPA entities.
- Database changes are versioned with Flyway.
- Identity-provider emails and business notification emails are separate responsibilities.

## First End-to-End Milestone

The first meaningful business path is the complete MVP customer journey:

1. Administrator signs in.
2. The administrator creates categories and products.
3. The administrator adds variants, prices, stock, and product images.
4. Customer registers or signs in.
5. Customer browses and searches products.
6. Customer views product details.
7. Customer adds items to a cart.
8. Customer updates and removes cart items.
9. Customer provides a shipping address.
10. Customer checks out.
11. Product price and stock are validated.
12. Fake payment succeeds or fails.
13. Order is created.
14. Stock is reduced.
15. Cart is cleared.
16. Order confirmation notification is generated.
17. Customer views order history and order details.
18. Angular frontend supports the flow.
19. The entire system can be started locally with documented commands.

## Phase 0 Boundary

Phase 0 creates documentation only. It does not create Java application code, microservices, Angular frontend code, Docker infrastructure files, or CI workflows.
