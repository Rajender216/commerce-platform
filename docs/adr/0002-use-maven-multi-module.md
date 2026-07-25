# ADR 0002: Use Maven Multi-Module

## Status

Accepted

## Context

The backend will be implemented with Java, Spring Boot, and Spring Cloud. The project needs consistent dependency versions, plugin versions, build behavior, testing conventions, and module organization across services.

## Decision

Use Maven multi-module builds with a root parent POM.

The root parent will centralize:

- Dependency versions.
- Maven plugin versions.
- Java version.
- Test plugin configuration.
- Build conventions.

Business and platform services will be added as modules incrementally.

## Consequences

Benefits:

- Consistent builds across services.
- Simpler dependency upgrades.
- Easier CI setup.
- Clear module boundaries.

Tradeoffs:

- Build time can grow as services are added.
- The parent POM can become overloaded if not kept disciplined.
- Module introduction order must remain phase-driven.
