# Development Guide

## Development Principles

- Work in small, reviewable phases.
- Keep every phase focused on one coherent outcome.
- Do not generate all services at once.
- Prefer existing project conventions over new patterns.
- Add ADRs for major architectural decisions.
- Do not add external dependencies without a clear reason.
- Do not commit secrets or real credentials.

## Planned Maven Strategy

The repository should use a Maven multi-module structure with a root parent POM.

Recommended parent responsibilities:

- Define Java version.
- Centralize dependency versions in `dependencyManagement`.
- Centralize plugin versions in `pluginManagement`.
- Configure common test, formatting, static analysis, and build behavior.
- Avoid application-specific code in the root module.

Recommended module groups:

- `services/*` for business services.
- `platform/*` for API gateway, discovery, and optional config server once the
  MVP phase requires them.
- `shared/*` only after a convention has proven reusable and worth extracting.

Shared modules must stay small. They should not become a dumping ground for domain logic.

Allowed shared module contents:

- Generic error response types and request tracing helpers.
- Test fixtures and Testcontainers utilities that are not service-specific.
- Security helpers for token and authority mapping, without business authorization rules.
- Messaging abstractions and common envelope types, without service event ownership.

Forbidden shared module contents:

- Domain entities or business rules.
- JPA entities or repositories.
- Service-specific DTOs.
- Provider SDK clients.
- Cross-service orchestration logic.

## Dependency Management

Use central dependency management for:

- Spring Boot BOM
- Spring Cloud BOM
- Testcontainers BOM
- OpenAPI tooling
- Flyway
- Maven plugins

Service modules should declare dependencies without versions when those versions are centrally managed.

## Implemented Build Foundation

Current Maven modules:

- Root `pom.xml`: aggregator and parent build.
- `build-platform`: POM-only build module with no application code.
- `services/catalog-service`: current service foundation with draft product API.

The root POM currently centralizes:

- Java release configuration.
- Spring Boot dependency BOM.
- Spring Cloud dependency BOM.
- Testcontainers dependency BOM.
- Maven plugin versions.
- Maven Enforcer rules.
- Compiler configuration.
- Surefire and Failsafe configuration.
- JaCoCo configuration.
- Spotless style validation.

## Selected Versions

Current build versions:

- Java release: `21`
- Maven Wrapper runtime: Apache Maven `3.9.16`
- Maven Wrapper: `3.3.4`
- Spring Boot: `4.1.0`
- Spring Cloud: `2025.1.2`
- Testcontainers BOM: `1.21.3`

Reasoning:

- Java `21` is an LTS target and compiles on the local JDK `22.0.2`.
- Spring Boot `4.1.0` is a stable release. Its official system requirements state Java `17` through `26` compatibility and Maven `3.6.3` or later.
- Spring Cloud `2025.1.2` is the compatible release train for Spring Boot `4.1.x`, according to the official Spring Cloud compatibility table.
- Apache Maven `3.9.16` is the current stable Maven 3 release recommended by Apache Maven.
- The Maven Wrapper uses Apache Maven Wrapper `3.3.4` with `only-script` distribution to avoid committing a wrapper jar.

References:

- Spring Boot system requirements: https://docs.spring.io/spring-boot/system-requirements.html
- Spring Cloud compatibility: https://spring.io/projects/spring-cloud
- Apache Maven download: https://maven.apache.org/download.cgi
- Apache Maven Wrapper: https://maven.apache.org/tools/wrapper/

## Plugin Management

Centralize plugin versions for:

- `maven-compiler-plugin`
- `maven-surefire-plugin`
- `maven-failsafe-plugin`
- `spring-boot-maven-plugin`
- `flyway-maven-plugin`
- static analysis and formatting plugins once selected

## Local Development

The local showcase phase may add Docker Compose when the approved MVP roadmap
reaches that stage. Expected infrastructure services:

- PostgreSQL
- Kafka
- Keycloak
- Mailpit
- Optional Kafka UI
- Optional database administration UI

Local credentials must be development-only and documented. Real secrets must not be committed.

## Branching

Use short-lived branches for every task.

Recommended branch naming:

- `docs/phase-00-foundation`
- `feature/catalog-service-skeleton`
- `fix/order-reservation-idempotency`
- `chore/update-build-plugins`

## Commit Messages

Use Conventional Commits:

- `docs: add phase 0 foundation`
- `build: add parent maven module`
- `test: add catalog repository integration tests`
- `feat: add catalog product creation`
- `fix: make inventory reservation idempotent`

## Pull Requests

Every pull request should include:

- Summary of changes.
- Scope and non-goals.
- Tests run.
- Migration notes if applicable.
- ADR links for architecture decisions.
- Known risks or follow-up items.

## Required Build Gates

Once Maven exists, implementation pull requests should run the documented project command for:

- Maven verification.
- Unit tests.
- Integration tests when integration-test modules or profiles exist.
- Formatting checks when configured.
- Static analysis when configured.
- Flyway migration validation when migrations are added.

Current verification command:

```powershell
.\mvnw.cmd clean verify
```

Equivalent script:

```powershell
.\scripts\verify.ps1
```

POSIX equivalents:

```sh
./mvnw clean verify
./scripts/verify.sh
```

Catalog service verification:

```powershell
.\mvnw.cmd -pl services/catalog-service -am clean verify
```

```sh
./mvnw -pl services/catalog-service -am clean verify
```

The catalog service verification runs unit tests, standalone MockMvc web tests, and PostgreSQL Testcontainers integration tests.

## Prohibited In Early Phases

- Do not create all services at once.
- Do not add Angular before backend workflows stabilize.
- Do not introduce Elasticsearch before PostgreSQL search is proven insufficient.
- Do not put provider SDK usage inside domain or application logic.
- Do not return persistence entities from controllers.
- Do not create platform service skeletons unless the active task explicitly approves them.
- Do not create service discovery until the active MVP phase explicitly requires it.
- Do not extract shared libraries prematurely.
- Do not spend time on production hardening before the MVP customer journey is complete.
