# API Guidelines

## Scope

These guidelines apply to synchronous HTTP APIs exposed by platform and business services.

## Style

- Use REST for immediate synchronous operations.
- Use JSON request and response bodies.
- Use DTOs at API boundaries.
- Do not expose JPA entities from controllers.
- Document public service APIs with OpenAPI.

## Resource Naming

Use plural nouns for resource collections:

- `/products`
- `/customers`
- `/carts`
- `/orders`

Use nested routes only when the child resource does not make sense outside the parent context.

## HTTP Methods

- `GET` reads resources.
- `POST` creates resources or starts command-style operations.
- `PUT` replaces resources when full replacement is intended.
- `PATCH` modifies part of a resource.
- `DELETE` removes resources or requests removal.

## Status Codes

Use common HTTP status codes consistently:

- `200 OK` for successful reads or updates with response bodies.
- `201 Created` for successful creation.
- `202 Accepted` for asynchronous operations.
- `204 No Content` for successful operations without response bodies.
- `400 Bad Request` for malformed or invalid requests.
- `401 Unauthorized` for missing or invalid authentication.
- `403 Forbidden` for authenticated users without permission.
- `404 Not Found` for missing resources.
- `409 Conflict` for business conflicts or version conflicts.
- `422 Unprocessable Entity` may be used for semantic validation failures if adopted consistently.

## Error Responses

Use a consistent error response format.

Recommended fields:

- `code`
- `message`
- `details`
- `traceId`
- `timestamp`

Do not leak stack traces or sensitive internal details in API responses.

## Validation

Validate request bodies at API boundaries and enforce business invariants in the domain or application layer.

Validation messages should be clear enough for API consumers to correct their request.

## Pagination

Collection APIs should use explicit pagination once collections can grow.

Recommended query parameters:

- `page`
- `size`
- `sort`

## Versioning

Start without URL versioning unless a concrete compatibility need appears. Prefer backward-compatible changes where possible.

If versioning becomes necessary, document the adopted strategy before implementing it.

Before formal versioning exists, breaking API changes require explicit review notes in the pull request and documentation updates for affected clients.

## Idempotency

Command APIs that can be retried by clients should support idempotency keys where duplicate execution would be harmful.

Candidate operations:

- Checkout.
- Payment confirmation.
- Inventory reservation.
- Order creation.

When idempotency keys are introduced, document:

- Header or field name.
- Storage owner.
- Expiration policy.
- Behavior when the same key is replayed with a different request body.
- Whether the original response is replayed.

## Current Catalog API

The golden catalog service currently exposes a temporary unsecured draft-product API:

- `POST /api/v1/products`
- `GET /api/v1/products/{productId}`

This API is administrator-facing by intent, but authentication and
authorization are not integrated until the security phase.

The MVP roadmap expands Catalog Service beyond this temporary foundation to
cover categories, products, variants, SKU, price, stock, image URLs,
activation status, browsing, filtering, pagination, and basic search.

Create request:

- `name`: required, trimmed, maximum 160 characters, blank values rejected.
- `description`: optional, trimmed, maximum 2000 characters.

Product response:

- `id`
- `name`
- `description`
- `status`
- `createdAt`
- `updatedAt`
- `version`

Current status values:

- `DRAFT`

Current explicit non-goals:

- categories, brands, variants, SKUs, prices, inventory, media, publication,
  search, and pagination are not part of the temporary draft-product
  foundation. They become part of the MVP Catalog phase.

Catalog service responses include `X-Correlation-ID`. Incoming values are accepted when valid and generated when absent or invalid.
