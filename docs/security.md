# Security

## Identity Provider

Keycloak is the identity provider for OAuth 2.0 and OpenID Connect in the MVP.

Required capabilities:

- Email and password registration.
- Email verification.
- Google sign-in and sign-up.
- Password reset.
- Role-based authorization.
- Customer and administrator roles.

Email OTP or MFA will be evaluated in a later security phase.

Identity-provider emails, such as verification and password reset, are separate from business notification emails, such as order confirmation. Business notification services must not take ownership of password or credential flows.

## Password Handling

Business services must never store user passwords.

Password lifecycle concerns belong to Keycloak or a dedicated identity boundary, not to business services.

## Authorization

Services should enforce authorization at API boundaries and application use case boundaries where needed.

Initial roles:

- `CUSTOMER`
- `ADMIN`

Administrative operations, such as category, product, and catalog data
management, require the `ADMIN` role.

Public catalog browsing endpoints remain anonymous.

Customer profile and address endpoints require a valid authenticated customer
token. Customer Service derives the customer identity from the validated JWT
`sub` claim, not from request headers or path parameters.

The API Gateway also validates JWTs for protected routes and applies route
authorization:

- Catalog public browsing routes are anonymous.
- Catalog admin routes require `ADMIN`.
- Customer routes require authentication.

Catalog Service and Customer Service continue validating JWTs independently.
The Gateway must forward the original bearer token and must not replace it with
trusted identity headers.

## Tokens

Services should validate access tokens and use claims for authorization decisions.

Token contents should be treated as security-sensitive. Do not log raw tokens.

Token validation must verify:

- Signature through trusted JWKS or equivalent trusted key material.
- Issuer.
- Audience or authorized party, as appropriate for the client.
- Expiration and not-before timestamps.
- Realm or tenant expectations.
- Role and authority claim mapping.

Services must reject tokens from unexpected realms, clients, issuers, or audiences.

Current local issuer:

- `http://localhost:8081/realms/commerce-mvp`

Services map Keycloak realm/client roles to Spring Security authorities with
the `ROLE_` prefix where service-specific authorization needs it.

Gateway and services use the same role mapping convention for Keycloak
realm/client roles.

## Gateway Edge Security

The Gateway returns sanitized Problem Details-style responses for edge failures
such as missing tokens, forbidden routes, unknown routes, service unavailable,
and gateway timeout. It must not expose stack traces, Java exception names,
Keycloak internals, Reactor internals, or internal service hostnames.

Gateway CORS defaults to `http://localhost:4200` for the future Angular
frontend. Override allowed origins with `API_GATEWAY_CORS_ALLOWED_ORIGINS`.
Do not use wildcard origins with credentials.

The correlation header is `X-Correlation-ID`. The Gateway validates length and
simple characters, generates a UUID when absent or invalid, returns the value,
and forwards exactly one correlation header downstream.

Forwarded-header handling is enabled for the local MVP stack. A production
reverse proxy must define which proxy-provided forwarding headers are trusted.

## Secrets

Real secrets must not be committed.

Local development may use documented placeholder credentials for Docker Compose services. Production-like secrets should be supplied through environment variables or a secret management system.

Development credentials must be clearly marked as local-only. Do not reuse local Keycloak realms, clients, or secrets as production-ready configuration.

Local-only demo users:

- `admin@example.test` / `admin-local-pass`
- `customer@example.test` / `customer-local-pass`

## Service Boundaries

Security boundaries must align with service ownership:

- Services do not access another service's database.
- Services expose behavior through APIs or events.
- Sensitive business operations should be auditable.

Customer profile data is a commerce concern. Passwords, identity credentials, social login links, verification status, and reset flows are identity-provider concerns unless a later ADR defines a different boundary.

Customer Service must never store passwords, password hashes, refresh tokens,
Google access tokens, or OTP values.

## Local Keycloak

Local startup uses `infrastructure/compose.yaml` and imports
`infrastructure/keycloak/realm-commerce-mvp.json`.

The realm includes:

- roles: `CUSTOMER`, `ADMIN`
- public frontend client: `commerce-web`
- backend resource API client: `commerce-backend`
- Mailpit SMTP settings for verification emails
- disabled Google identity provider with placeholder client values

Google Console redirect URI:

```text
http://localhost:8081/realms/commerce-mvp/broker/google/endpoint
```

Do not commit real Google client IDs or secrets. Configure them in Keycloak
locally or through environment-specific configuration before enabling the
Google identity provider.

## API Security

APIs should define:

- CORS policy before browser clients are enabled.
- CSRF posture before cookie-based browser flows are enabled.
- Rate limiting strategy for authentication-sensitive and write-heavy endpoints.
- Audit logging for administrative and payment-related operations.
- PII redaction rules for logs and error responses.

## Provider SDK Isolation

Provider SDKs must stay in outbound adapters. Domain and application logic should depend on ports.

Security-sensitive provider integrations include:

- Payment providers.
- Email providers.
- Object storage providers.
- Messaging infrastructure.

## Initial Risks

- Incorrect role mapping between Keycloak and services.
- Leaking sensitive values in logs.
- Overly broad shared security utilities.
- Missing authorization checks in internal service APIs.
- Treating development credentials as production-ready.
- Mixing customer profile ownership with identity credential ownership.
