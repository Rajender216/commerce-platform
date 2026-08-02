# Architecture

## Target Architecture

The commerce platform will use a backend-first microservice architecture in a
Maven multi-module monorepo.

Services will follow ports and adapters, also known as hexagonal architecture.
Domain and application logic should remain independent from framework adapters,
database details, messaging clients, cloud providers, email providers, and
payment providers.

## Planned Top-Level Structure

```text
.
├── services
│   ├── catalog-service
│   ├── customer-service
│   ├── cart-service
│   ├── order-service
│   └── notification-service
├── platform
│   ├── api-gateway
│   ├── discovery-server
│   └── config-server
└── frontend
```

This is the canonical planned structure. It is not permission to create all
directories or modules immediately. Directories should be introduced only when
needed by the active approved phase.

Shared modules are not part of the MVP baseline. Introduce shared code only
when a convention is proven reusable across multiple services and a dedicated
module is clearly justified.

## Service Internal Structure

Each business service should use a consistent internal package structure:

```text
service
├── domain
├── application
│   ├── port
│   │   ├── in
│   │   └── out
│   └── usecase
├── adapter
│   ├── in
│   │   ├── web
│   │   └── messaging
│   └── out
│       ├── persistence
│       ├── messaging
│       └── provider
└── config
```

## Data Ownership

Each service owns its data. A service may use a dedicated database or isolated
schema, but no service may read or write another service's tables.

PostgreSQL is the initial source of truth. For the MVP, Catalog Service owns
categories, products, variants, SKU, price, stock, image metadata, activation
status, and product browsing/search behavior.

Customer Service owns customer profile records, shipping addresses, and the
mapping from Keycloak `sub` to a local customer profile. Keycloak owns
credentials, email verification, social identity-provider links, access tokens,
refresh tokens, and roles. Customer Service must not store passwords, password
hashes, refresh tokens, Google access tokens, or OTP values.

Elasticsearch is deferred until the core purchase flow works and search needs
justify the added complexity.

## Communication

Synchronous communication:

- REST for direct commands and queries that need immediate responses.
- API contracts documented with OpenAPI.
- DTOs are used at service boundaries.
- OpenFeign may be used for outbound service calls, but the business layer
  must still depend on business-specific ports.

Asynchronous communication:

- Kafka for domain events.
- Kafka initially covers one meaningful asynchronous flow:
  `OrderConfirmed` to Notification Service.
- Transactional outbox is deferred until a real reliability need appears in the
  MVP or a later phase.
- Consumers must be idempotent.

## Provider Abstractions

Business logic must not directly depend on:

- Kafka SDK
- Cloudinary SDK
- AWS S3 SDK
- SMTP provider SDK
- Payment provider SDK

Required ports include:

- `DomainEventPublisher`
- `ObjectStorage`
- `NotificationChannel`
- `PaymentProvider`

Initial adapters:

- Kafka messaging adapter
- Cloudinary storage adapter
- SMTP or Mailpit email adapter
- Fake payment adapter

## Platform Components

Initial platform components:

- API Gateway
- Eureka
- Keycloak
- Kafka
- PostgreSQL
- Local email testing
- Angular frontend

Configuration Server is optional for the MVP and must not block progress.

Observability is planned for a later phase.

During MVP Phase 3, the API Gateway is the external backend entry point and
routes through Eureka. Catalog Service and Customer Service still validate
Keycloak JWTs directly as resource servers; the Gateway is not the only
security boundary.

Current Gateway routes:

- `GET /api/v1/categories` -> `catalog-service`, anonymous.
- `GET /api/v1/products` and `GET /api/v1/products/**` -> `catalog-service`,
  anonymous.
- `/api/v1/admin/categories/**` and `/api/v1/admin/products/**` ->
  `catalog-service`, `ADMIN` required.
- `/api/v1/customers/**` -> `customer-service`, authentication required.

Future service routes must be added only when the target service exists.
Avoid broad `/api/v1/admin/**` routing to a single service because future admin
APIs will belong to different service owners.

The Gateway forwards the original bearer token and the single documented
`X-Correlation-ID` header. Downstream services must not treat custom identity
headers as trusted authorization data.

## Decisions Requiring Confirmation

- Dedicated database versus schema-per-service for local development.
- Kafka schema governance and topic naming.
- Whether schema registry should be added later.
