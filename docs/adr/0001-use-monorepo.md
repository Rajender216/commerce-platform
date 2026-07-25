# ADR 0001: Use Monorepo

## Status

Accepted

## Context

The platform will include multiple backend services, platform components, shared conventions, infrastructure definitions, and eventually a frontend. The project is being built incrementally for learning and portfolio development.

Managing the early system in separate repositories would add coordination overhead before the architecture is stable.

## Decision

Use a single repository for the platform.

The repository will contain backend services, platform services, shared modules, infrastructure, documentation, and eventually frontend code.

## Consequences

Benefits:

- Easier cross-service refactoring during early development.
- One place for documentation, ADRs, infrastructure, and CI.
- Centralized dependency and plugin management.
- Consistent testing and review workflow.

Tradeoffs:

- CI must be designed to avoid unnecessary work as the repository grows.
- Module boundaries must be enforced by convention and tests.
- Shared modules must stay small to avoid coupling services accidentally. They must not contain domain entities, business rules, service-specific DTOs, repositories, provider SDK clients, or cross-service orchestration.
