# ADR 0003: Use Keycloak

## Status

Accepted

## Context

The platform requires OAuth 2.0, OpenID Connect, email and password registration, email verification, Google sign-in and sign-up, password reset, role-based authorization, and customer and administrator roles.

Business services must never store user passwords.

## Decision

Use Keycloak as the identity provider.

Business services will validate tokens and make authorization decisions from trusted claims and roles. Password storage, registration credentials, email verification, password reset, social login linkage, and identity lifecycle features stay outside business services.

Customer profile data may be owned by commerce services, but identity credentials and password lifecycle remain Keycloak responsibilities.

## Consequences

Benefits:

- Avoids building custom password and identity management.
- Supports OAuth 2.0 and OpenID Connect.
- Provides role and client configuration suitable for local and later production-like environments.
- Enables Google identity provider integration.

Tradeoffs:

- Requires Keycloak realm and client configuration management.
- Requires clear role mapping into services.
- Integration tests involving authentication may need additional setup.
