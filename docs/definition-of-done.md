# Definition of Done

## General

A task is done when it is implemented, tested, documented, and reviewed against the requested scope.

For MVP tasks, do not require complete enterprise hardening. Require the
requested business capability, the critical validation and authorization for
the active phase, the important integration paths, and the documented Maven
verification command.

## Required Checks

For implementation tasks:

- Code is scoped to the requested phase.
- Relevant tests pass.
- The documented Maven verification command passes once Maven exists.
- Formatting and static analysis checks pass when configured.
- No unrelated files are changed.
- No secrets or real credentials are committed.
- Documentation is updated when behavior, architecture, setup, or operations change.
- ADRs are added for major architecture decisions.
- Security-sensitive MVP tasks document public and protected endpoints, local
  token testing, and any local-only credentials.

## Backend Requirements

- Business logic is not coupled to provider SDKs.
- Controllers do not return JPA entities.
- Database changes include Flyway migrations.
- Service boundaries are respected.
- Cross-service database transactions are not introduced.
- Events are idempotent where consumed.
- REST APIs use DTOs and consistent error responses.
- Protected services validate JWTs directly. The API Gateway may validate and
  authorize at the edge, but it must not become the only security boundary.

## Testing Requirements

Testing should match the risk of the change.

Expected tests may include:

- Unit tests for domain and application behavior.
- Controller or API tests for request and response behavior.
- Persistence integration tests with Testcontainers.
- Messaging integration tests with Testcontainers.
- Migration validation for database changes.

MVP tasks should not silently skip relevant tests. If a phase does not yet have
the tooling for a test gate, the task summary must say so explicitly.

Testing gates must not be silently skipped. If a gate cannot run because tooling has not been introduced yet, the pull request or task summary must say so explicitly.

## Documentation Requirements

Update relevant files under `docs/` when decisions or workflows change.

Common updates:

- `docs/project-status.md`
- `docs/memory.md`
- roadmap documents
- ADRs
- API or event guidelines

## Pull Request Requirements

Each pull request should explain:

- What changed.
- Why it changed.
- What did not change.
- Tests and checks executed.
- Migration or setup impact.
- Known risks.

## Not Done

A task is not done if:

- It implements future phases without approval.
- It silently changes architecture direction.
- It requires undocumented manual setup.
- Tests were skipped without explanation.
- It creates code that cannot be built or run in the expected environment.
