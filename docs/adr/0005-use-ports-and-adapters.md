# ADR 0005: Use Ports and Adapters

## Status

Accepted

## Context

The platform will integrate with infrastructure and external providers such as Kafka, object storage, email providers, and payment providers. The project brief requires business logic to avoid direct dependency on provider SDKs.

## Decision

Use ports and adapters inside each service.

Domain and application logic will define or depend on ports. Infrastructure-specific code will live in adapters.

Required ports include:

- `DomainEventPublisher`
- `ObjectStorage`
- `NotificationChannel`
- `PaymentProvider`

## Consequences

Benefits:

- Business logic stays testable.
- Provider implementations can be replaced.
- Fake adapters can support local development and tests.
- Framework and SDK details remain at the edge of each service.

Tradeoffs:

- Adds structure and mapping code.
- Requires discipline to avoid leaking adapter models inward.
- Small services may initially feel more layered than necessary, but consistency matters as the platform grows.
