# Testing Strategy

## Goals

Testing should prove behavior at the right level without making the codebase slow or brittle.

The test strategy should support:

- Fast feedback during development.
- Confidence in domain behavior.
- Confidence in persistence, messaging, and API adapters.
- Reproducible integration tests with real infrastructure through Testcontainers.
- Regression protection for the first end-to-end purchase flow.

## Test Levels

## Unit Tests

Use unit tests for:

- Domain rules.
- Application use cases.
- Validation logic.
- Idempotency decisions.
- Mapping logic when it contains behavior.

Unit tests should avoid Spring context startup unless framework behavior is the subject of the test.

## Slice Tests

Use slice tests for:

- Web controllers.
- Persistence adapters.
- Messaging adapters.
- Security rules.

Controller tests should verify request validation, response shape, status codes, and authorization behavior.

## Integration Tests

Use Testcontainers for:

- PostgreSQL persistence integration.
- Flyway migration validation.
- Kafka producer and consumer behavior.
- Keycloak integration when security flows require it.

Infrastructure integration tests should run in CI once the relevant modules exist.

The catalog golden service establishes the current convention:

- Unit and web tests use `*Test` naming and run under Surefire.
- PostgreSQL integration tests use `*IT` naming and run under Failsafe during `verify`.
- Web adapter tests may use standalone MockMvc when a focused controller/filter/exception-handler test is enough.
- PostgreSQL integration tests must use Testcontainers, Flyway, and the real PostgreSQL driver.

ArchUnit is not currently approved or centrally managed in the build platform. Architecture tests are a required build-platform follow-up before adding ArchUnit rules.

## Contract Tests

REST API compatibility should be protected through OpenAPI review and contract-focused tests.

Event compatibility should be protected through event schema documentation and consumer expectations.

Before a schema registry exists, every event-producing change must document the event name, payload fields, required fields, optional fields, version, and consumer expectations in the relevant event documentation or service README.

## End-to-End Tests

End-to-end tests should be introduced once the relevant backend and local
showcase pieces are stable enough to exercise the MVP journey.

The first end-to-end scenario should cover the complete MVP customer journey:

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
11. Fake payment success or failure.
12. Order creation.
13. Stock reduction.
14. Cart clearing.
15. Order confirmation notification.
16. Order history and order details.
17. Angular frontend support for the flow.
18. Local startup with documented commands.

## CI Expectations

The initial CI pipeline should run:

- Maven validation.
- Unit tests.
- Static checks.
- Integration tests when infrastructure-dependent modules exist.

Once Maven exists, the minimum implementation gate is the repository's documented Maven verification command. If integration tests are split behind a profile, that profile must be named in the pull request and CI documentation.

When database migrations are added, CI should validate that Flyway migrations apply cleanly against a fresh PostgreSQL container.

When messaging is added, CI should include at least one producer or consumer integration test for the changed messaging behavior.

## Test Data

Tests should use builders, fixtures, or factory methods. Avoid sharing mutable test state across tests.

## Coverage Guidance

Coverage numbers are secondary. Important business rules, security boundaries, database migrations, and event idempotency must be tested directly.
