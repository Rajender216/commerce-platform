# Event Guidelines

## Scope

These guidelines apply to asynchronous events published between services.

Kafka is the planned initial event backbone for the MVP. Initially it should
support one meaningful asynchronous flow: `OrderConfirmed` to Notification
Service.

## Event Purpose

Events should communicate facts that have already happened inside a service boundary.

Examples:

- `ProductCreated`
- `InventoryReserved`
- `OrderConfirmed`
- `PaymentSucceeded`
- `NotificationRequested`

Avoid using events as remote procedure calls.

## Ownership

The service that owns the state change owns the event.

Consumers must not infer ownership of another service's data. They may maintain local projections when needed.

## Naming

Use past-tense event names for facts:

- `order.confirmed`
- `inventory.reserved`
- `payment.succeeded`

Topic naming should be confirmed before implementation.

## Event Shape

Recommended fields:

- `eventId`
- `eventType`
- `occurredAt`
- `producer`
- `schemaVersion`
- `correlationId`
- `payload`

## Idempotency

Consumers must be idempotent. A consumer should safely handle duplicate event delivery.

Recommended techniques:

- Store processed event IDs.
- Use natural business keys.
- Use unique constraints for side effects.
- Make state transitions explicit and repeat-safe.

## Transactional Outbox

Use a transactional outbox when a service must persist state and publish an
event as one reliable operation.

The outbox record should be written in the same local database transaction as the business state change.

Do not introduce a transactional outbox before the MVP actually requires it.

## Compatibility

Events should evolve in a backward-compatible way when possible:

- Add optional fields.
- Do not rename fields without a migration plan.
- Do not change field meaning.
- Version schemas when compatibility cannot be preserved.

Before a schema registry exists, event compatibility is enforced through documentation and tests. Event-producing changes must document the schema version, required fields, optional fields, and known consumers.

## Open Decisions

- Topic naming convention.
- Event schema format.
- Whether to add a schema registry.
- Dead-letter topic strategy.
- Retry and backoff standards.
