# API Guidelines

## Scope

These guidelines apply to synchronous HTTP APIs exposed by platform and business services.

## Style

- Use REST for immediate synchronous operations.
- Use JSON request and response bodies.
- Use DTOs at API boundaries.
- Do not expose JPA entities from controllers.
- Document public service APIs with OpenAPI or repository API documentation.
  Generated OpenAPI tooling should be added only after the build platform
  centrally approves the dependency.

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

Catalog Service exposes MVP catalog APIs for administrators and customers.
Administrator APIs require a valid Keycloak JWT with the `ADMIN` role.

Admin categories:

- `POST /api/v1/admin/categories`
- `PUT /api/v1/admin/categories/{categoryId}`
- `GET /api/v1/admin/categories`
- `GET /api/v1/admin/categories/{categoryId}`

Admin products:

- `POST /api/v1/admin/products`
- `PUT /api/v1/admin/products/{productId}`
- `GET /api/v1/admin/products`
- `GET /api/v1/admin/products/{productId}`
- `PATCH /api/v1/admin/products/{productId}/status`

Admin variants:

- `POST /api/v1/admin/products/{productId}/variants`
- `PUT /api/v1/admin/products/{productId}/variants/{variantId}`
- `PATCH /api/v1/admin/products/{productId}/variants/{variantId}/stock`
- `DELETE /api/v1/admin/products/{productId}/variants/{variantId}`

Admin images:

- `POST /api/v1/admin/products/{productId}/images`
- `PUT /api/v1/admin/products/{productId}/images/{imageId}`
- `DELETE /api/v1/admin/products/{productId}/images/{imageId}`

Public catalog:

- `GET /api/v1/categories`
- `GET /api/v1/products`
- `GET /api/v1/products/{productIdOrSlug}`

Public product listing query parameters:

- `page`: zero-based page index, default `0`.
- `size`: page size, default `20`, maximum `100`.
- `sort`: `name`, `newest`, `priceAsc`, or `priceDesc`.
- `search`: case-insensitive product-name contains search.
- `category`: category ID or slug.
- `brand`: case-insensitive exact brand filter.
- `minPrice` and `maxPrice`: variant price range filters.
- `inStockOnly`: when `true`, requires at least one active variant with stock.

Paginated responses contain:

- `content`
- `page`
- `size`
- `totalElements`
- `totalPages`
- `first`
- `last`

Catalog rules:

- Public APIs return only active categories, active products, and active variants.
- Product statuses are `DRAFT`, `ACTIVE`, and `INACTIVE`.
- A product cannot become `ACTIVE` unless it belongs to an active category and
  has at least one active variant.
- Variant SKU is globally unique.
- Category and product slugs are unique.
- Prices use `BigDecimal` and the single MVP currency `USD`.
- Stock quantity cannot be negative and uses optimistic locking.
- Variant hard delete is an MVP limitation while no cart/order capability
  references variants.
- Image URLs are supplied by administrators. Binary upload and Cloudinary
  integration are out of scope for this phase.
- Basic search uses PostgreSQL `lower(name) like ...`; Elasticsearch remains
  post-MVP.

Create product request example:

```json
{
  "name": "Everyday Backpack",
  "slug": "everyday-backpack",
  "description": "A durable everyday bag.",
  "categoryId": "11111111-1111-1111-1111-111111111111",
  "brand": "RCP"
}
```

Variant request example:

```json
{
  "sku": "BAG-001-BLK",
  "name": "Black",
  "price": 49.99,
  "stockQuantity": 25,
  "active": true
}
```

Catalog service responses include `X-Correlation-ID`. Incoming values are
accepted when valid and generated when absent or invalid.

## Current Customer API

Customer Service exposes authenticated MVP profile and shipping-address APIs.
All routes require a valid Keycloak JWT. Customer identity is resolved from the
validated JWT `sub` claim.

Profile:

- `GET /api/v1/customers/me`
- `PUT /api/v1/customers/me`

Addresses:

- `POST /api/v1/customers/me/addresses`
- `GET /api/v1/customers/me/addresses`
- `GET /api/v1/customers/me/addresses/{addressId}`
- `PUT /api/v1/customers/me/addresses/{addressId}`
- `DELETE /api/v1/customers/me/addresses/{addressId}`
- `PATCH /api/v1/customers/me/addresses/{addressId}/default`

Customer rules:

- Customer Service stores profile and address data only.
- Keycloak owns credentials, verification, Google sign-in, tokens, and roles.
- Profile creation is lazy and idempotent on first authenticated
  `/customers/me` request.
- Missing first or last name claims are stored as MVP placeholder values until
  the customer updates the profile.
- Only one shipping address may be default for a customer.
- Deleting a default address does not automatically promote another address.

Update profile request example:

```json
{
  "firstName": "Ada",
  "lastName": "Lovelace",
  "phoneNumber": "+15550101010"
}
```

Address request example:

```json
{
  "recipientName": "Ada Lovelace",
  "phoneNumber": "+15550101010",
  "addressLine1": "1 Market Street",
  "addressLine2": "Suite 100",
  "city": "San Francisco",
  "state": "CA",
  "postalCode": "94105",
  "country": "US",
  "defaultAddress": true
}
```

Customer service responses include `X-Correlation-ID`. Incoming values are
accepted when valid and generated when absent or invalid.

## Current Gateway Routes

The API Gateway is the preferred public backend entry point for current MVP
services.

Gateway URL:

- `http://localhost:8080`

Routes:

- `GET /api/v1/categories` -> Catalog Service, anonymous.
- `GET /api/v1/products` -> Catalog Service, anonymous.
- `GET /api/v1/products/{productIdOrSlug}` -> Catalog Service, anonymous.
- `/api/v1/admin/categories/**` -> Catalog Service, `ADMIN` required.
- `/api/v1/admin/products/**` -> Catalog Service, `ADMIN` required.
- `/api/v1/customers/**` -> Customer Service, authenticated JWT required.

The Gateway forwards the original bearer token to downstream services. Do not
use trusted identity headers instead of JWT validation.

Gateway responses include `X-Correlation-ID`; incoming values are accepted when
valid and generated when absent or invalid. CORS defaults to
`http://localhost:4200` and can be overridden with
`API_GATEWAY_CORS_ALLOWED_ORIGINS`.
