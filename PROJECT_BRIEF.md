# Commerce Platform Project Brief

## Objective

Build a showcaseable e-commerce MVP first. Mature the platform toward full
production quality only after the complete customer shopping journey works
end to end.

The immediate goal is a complete working application with clean, maintainable
code and sensible extension points. Do not spend time on enterprise hardening
or generic reusable frameworks before the MVP journey is complete.

## Backend technology

- Java
- Spring Boot
- Maven
- Spring Cloud
- PostgreSQL
- Kafka
- Keycloak
- Docker Compose
- Testcontainers
- Flyway
- OpenAPI
- GitHub Actions
- Angular

## Architecture

- Microservice architecture
- Maven multi-module monorepo
- Hexagonal architecture inside each service
- Database ownership per service
- REST for synchronous communication
- Kafka for asynchronous communication
- OpenFeign may be used as an outbound adapter, but business logic must depend
  on business-specific ports
- Ports and adapters for replaceable infrastructure providers
- Idempotent event consumers
- Transactional outbox, advanced saga orchestration, and related reliability
  machinery are deferred until the MVP requires them

## Initial platform components

- API Gateway
- Service Discovery
- Configuration Server
- Keycloak
- Kafka
- PostgreSQL
- Local email testing
- Angular frontend

Configuration Server is optional for the MVP and must not block progress.

## Planned business services

- Catalog Service
- Customer Service
- Cart Service
- Order Service
- Notification Service

Catalog Service owns inventory and product image metadata for the MVP. Do not
extract Inventory Service or Media Service before the MVP is complete.

Payment remains inside Order Service for the MVP. Do not extract Payment
Service before the MVP is complete.

Services must be added incrementally. Do not generate all services at once.

## Authentication requirements

- OAuth 2.0 and OpenID Connect
- Keycloak as identity provider
- Email and password registration
- Email verification
- Google sign-in and sign-up
- Password reset
- Role-based authorization
- Customer and administrator roles
- Email OTP or MFA will be evaluated in a later security phase

Business services must never store user passwords.

## Provider abstractions

Business logic must not directly depend on:

- Kafka SDK
- Cloudinary SDK
- AWS S3 SDK
- SMTP provider SDK
- Payment provider SDK

Required application ports include:

- DomainEventPublisher
- ObjectStorage
- NotificationChannel
- PaymentProvider

Initial adapters:

- Kafka messaging adapter
- Cloudinary storage adapter
- SMTP or Mailpit email adapter
- Fake payment adapter

Future adapters may include:

- RabbitMQ
- AWS S3
- AWS SES
- SendGrid
- Stripe
- Razorpay

## Data

PostgreSQL will be the initial source-of-truth database.

Catalog search will initially use PostgreSQL. Elasticsearch is deferred until
the core purchase flow works and a real search need appears.

Each service owns its own database or isolated schema.
No service may read another service's tables.

## Main milestone

The first complete business milestone is the full MVP customer journey:

1. An administrator signs in.
2. The administrator creates categories and products.
3. The administrator adds variants, prices, stock, and product images.
4. A customer registers or signs in.
5. The customer browses and searches products.
6. The customer views product details.
7. The customer adds items to a cart.
8. The customer updates and removes cart items.
9. The customer provides a shipping address.
10. The customer checks out.
11. Product price and stock are validated.
12. A fake payment succeeds or fails.
13. An order is created.
14. Stock is reduced.
15. The cart is cleared.
16. An order confirmation notification is generated.
17. The customer can view order history and order details.
18. An Angular frontend supports the complete flow.
19. The entire system can be started locally using documented commands.

## Development approach

Work in small phases:

1. Make the complete workflow work.
2. Make the code clean and testable.
3. Add essential security and error handling.
4. Add basic integration tests.
5. Complete the frontend.
6. Make local startup simple.
7. Mature reliability, scalability, and observability afterward.

Avoid:

- premature abstractions
- generic internal frameworks
- excessive shared libraries
- generating unused services
- infrastructure without a current use case
- polishing one service indefinitely while the full product remains incomplete

Never build the entire application in one task.
