# Commerce Platform Agent Instructions

## Project mission

Build the commerce platform incrementally using the requirements in
`PROJECT_BRIEF.md` and the active MVP roadmap.

Do not attempt to build the complete platform in one task.

Current priority: complete the showcaseable MVP customer journey first.
Treat production hardening, broader platform services, and advanced reliability
work as post-MVP unless the active task explicitly says otherwise.

## Working protocol

Before modifying files:

1. Read `PROJECT_BRIEF.md`.
2. Inspect the existing repository.
3. Restate the requested task.
4. List the files and modules likely to change.
5. Present a small implementation plan.

During implementation:

- Work only within the requested scope.
- Do not implement future phases.
- Do not add an external dependency without explaining its purpose.
- Do not introduce a major architectural decision without an ADR.
- Do not commit secrets or real credentials.
- Keep code maintainable, readable, and testable.

After implementation:

1. Run all relevant tests.
2. Run formatting and static-analysis checks.
3. Review the resulting diff.
4. Summarize changed files.
5. Report commands executed and their results.
6. Report remaining risks or incomplete items.
7. Do not commit or push unless explicitly requested.

## Architecture rules

- Use a Maven multi-module monorepo.
- Centralize dependency and plugin versions.
- Business logic must not directly depend on provider SDKs.
- Each service owns its data.
- Services must not query another service's database.
- Use REST for immediate synchronous operations.
- Use events for asynchronous state propagation.
- Event consumers must be idempotent.
- Cross-service database transactions are forbidden.
- Controllers must not return JPA entities.
- Database changes require Flyway migrations.
- Use Testcontainers for infrastructure integration tests.
- Keep the MVP boundaries explicit: Catalog owns categories, products,
  variants, SKU, price, stock, and product image metadata during the MVP.
- Do not extract Inventory Service or Media Service before the MVP customer
  journey is complete.
- Do not extract Payment Service before the MVP customer journey is complete.
- OpenFeign may be used only as an outbound adapter; business logic must still
  depend on business-specific ports.

## Git rules

- Never push directly to `main`.
- Use a short-lived branch for every task.
- Use conventional commit messages.
- Keep commits focused.
- Never force-push.
- Never merge a pull request automatically.
- Do not commit or push without explicit approval.

## Scope rule

When a task is too large, break it into smaller tasks and implement only the
first approved task.
