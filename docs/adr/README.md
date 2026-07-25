# Architecture Decision Records

## Purpose

Architecture Decision Records capture important technical decisions, their context, and their consequences.

Use ADRs when a decision affects:

- Repository structure.
- Service boundaries.
- Frameworks or platform components.
- Data ownership.
- Security architecture.
- Messaging patterns.
- Provider abstraction.
- Testing and deployment strategy.

## Format

Use this structure:

```text
# ADR NNNN: Title

## Status

Accepted | Proposed | Superseded

## Context

What problem or force led to this decision?

## Decision

What decision was made?

## Consequences

What becomes easier, harder, or constrained because of this decision?
```

## Index

- [0001: Use Monorepo](0001-use-monorepo.md)
- [0002: Use Maven Multi-Module](0002-use-maven-multi-module.md)
- [0003: Use Keycloak](0003-use-keycloak.md)
- [0004: Use PostgreSQL Before Elasticsearch](0004-use-postgresql-before-elasticsearch.md)
- [0005: Use Ports and Adapters](0005-use-ports-and-adapters.md)
