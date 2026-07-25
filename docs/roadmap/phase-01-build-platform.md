# Phase 01: Build Platform

## Goal

Create the technical foundation needed to build services consistently without creating business services.

This phase is complete. It is retained as a foundation record. The active
product roadmap now continues in `docs/roadmap/mvp-roadmap.md`.

## Proposed Scope

- Root Maven parent POM.
- Maven module grouping.
- Central dependency management.
- Central plugin management.
- Basic build validation.
- Docker Compose infrastructure only if explicitly approved for the task.
- GitHub Actions CI skeleton.
- Local development documentation updates.

Implemented in the Maven build foundation task:

- Maven Wrapper.
- Root aggregator and parent POM.
- `build-platform` POM-only module.
- Central Spring Boot, Spring Cloud, and Testcontainers BOM imports.
- Central Maven plugin versions.
- Maven Enforcer rules.
- Compiler configuration for Java release `21`.
- Surefire and Failsafe configuration.
- JaCoCo configuration.
- Spotless style validation.
- `scripts/verify.ps1` and `scripts/verify.sh`.
- `.editorconfig`, `.gitignore`, and `.gitattributes`.

Not implemented in this task:

- Business services.
- Infrastructure services.
- Docker Compose.
- GitHub Actions.
- Kafka, Keycloak, Eureka, OpenFeign, Cloudinary, or Angular.

## Maven Foundation

The root POM should:

- Use packaging `pom`.
- Define modules only as they are introduced.
- Import Spring Boot, Spring Cloud, and Testcontainers BOMs.
- Centralize plugin versions.
- Configure Java compiler behavior.
- Configure Surefire and Failsafe for unit and integration tests.

## Docker Compose Plan

When Docker Compose is included in the approved Phase 1 task, initial local infrastructure should include:

- PostgreSQL
- Kafka
- Keycloak
- Mailpit

Optional developer tools:

- Kafka UI
- PostgreSQL administration UI

Docker Compose files should use development-only credentials and must not include real secrets.

## CI Pipeline Plan

Initial GitHub Actions pipeline:

- Trigger on pull requests and pushes to non-main branches as appropriate.
- Set up Java.
- Cache Maven dependencies.
- Run Maven validation.
- Run unit tests.
- Run integration tests once modules exist.
- Publish test reports when useful.

## Non-Goals

- Do not build every service.
- Do not implement business workflows.
- Do not add frontend code.
- Do not introduce Elasticsearch.
- Do not create platform service skeletons unless the active task explicitly approves them.
- Do not create service discovery until its timing and implementation are confirmed.

## Exit Criteria

- The repository has a reproducible Maven build foundation.
- Local infrastructure can start through Docker Compose if Docker Compose implementation was approved for the task.
- CI can validate the build.
- Documentation reflects setup and commands.

Current verification command:

```powershell
.\mvnw.cmd clean verify
```
