# API Gateway Agent Instructions

## Ownership

This module owns external HTTP routing and MVP edge concerns.

## Boundaries

- Do not add business logic.
- Do not access service databases.
- Do not replace downstream service authorization; services must continue validating JWTs.
- Do not add backend-for-frontend aggregation or fake routes for services that do not exist.
- Do not add Cart, Order, Notification, payment, Kafka, Eureka Server, Config Server, Redis, Elasticsearch, or Angular code here.

## Routing And Security

- Route through Eureka using stable service names.
- Forward the original bearer token downstream.
- Never trust client-provided role or user identity headers.
- Catalog public browsing routes remain anonymous.
- Catalog admin routes require `ADMIN`.
- Customer routes require authentication.

## Correlation And CORS

- Use `X-Correlation-ID` only.
- Generate a correlation ID when absent or invalid.
- Forward and return the correlation ID.
- Keep CORS origins environment-driven; do not use wildcard origins with credentials.

## Verification

Run:

```powershell
.\mvnw.cmd -pl platform/api-gateway -am clean verify
```
