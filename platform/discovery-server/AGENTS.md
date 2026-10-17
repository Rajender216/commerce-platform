# Discovery Server Agent Instructions

## Ownership

This module owns local MVP service registration through Eureka.

## Boundaries

- Do not add business logic.
- Do not access service databases.
- Do not turn this module into Config Server.
- Do not add routes, authentication flows, or customer-facing APIs here.
- Keep exposed actuator endpoints minimal.

## Configuration

- Default local port: `8761`.
- Self-registration and registry fetching must remain disabled for the server.
- Use environment variables for local overrides.

## Verification

Run:

```powershell
.\mvnw.cmd -pl platform/discovery-server -am clean verify
```
