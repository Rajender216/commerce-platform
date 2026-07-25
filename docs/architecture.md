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

Service discovery is part of the MVP roadmap and may be introduced when its
phase is active. Do not create platform modules before they are approved.

Observability is planned for a later phase.

## Decisions Requiring Confirmation

- Dedicated database versus schema-per-service for local development.
- API Gateway implementation details.
- Service discovery timing and implementation.
- Kafka schema governance and topic naming.
- Whether schema registry should be added later.
