# Customer Service Agent Instructions

## Ownership

Customer Service owns MVP customer profile data and shipping addresses.

It owns:

- customer profile records linked to Keycloak `sub`
- first name, last name, email, optional phone number
- shipping-address CRUD
- one-default-address behavior
- address ownership checks

## Non-Owned Responsibilities

Do not add these responsibilities to Customer Service:

- passwords or password hashes
- refresh tokens, Google tokens, OTP values, or credential validation
- carts, orders, payments, notifications, product data, inventory, or media
- Keycloak administration APIs beyond reading validated JWT claims

## Identity Boundary

Keycloak owns authentication, email verification, Google sign-in, roles, access tokens, and refresh tokens. Customer Service trusts only a validated JWT from Spring Security and uses the `sub` claim as `identitySubject`.

## Package Rules

- Controllers depend on inbound application ports.
- Application services depend on domain types and outbound ports.
- JPA entities and Spring Data repositories stay inside `adapter.out.persistence`.
- Domain and application packages must not import web or persistence adapters.

## Database Rules

Customer Service owns its own PostgreSQL schema. Changes require Flyway migrations. Hibernate schema generation must remain disabled with `ddl-auto: validate`.

## Required Tests

Run focused unit, security, and PostgreSQL integration tests for profile creation, idempotent profile lookup, address ownership, default-address behavior, optimistic locking, and JWT access rules.

## Commands

From the repository root:

```powershell
.\mvnw.cmd -pl services/customer-service -am clean verify
.\mvnw.cmd clean verify
```
