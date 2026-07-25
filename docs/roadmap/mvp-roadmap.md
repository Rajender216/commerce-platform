# MVP Roadmap

## Goal

Build the complete showcaseable customer journey first. Mature the platform
toward production quality after the MVP works end to end.

## MVP Phases

### MVP Phase 1 - Catalog

Status: implemented for the backend Catalog Service.

- Categories.
- Product CRUD.
- Product variants.
- SKU.
- Price.
- Stock.
- Image URLs.
- Activation status.
- Public product APIs.
- Admin product APIs.
- Filtering.
- Pagination.
- Basic search.

Catalog Service owns inventory and image metadata for the MVP. Do not extract
Inventory Service or Media Service before the MVP is complete.

Current limitations:

- Admin endpoints are temporarily unsecured until MVP Phase 2.
- Image binary upload is not implemented; administrators provide image URLs.
- Search uses PostgreSQL name matching; Elasticsearch remains post-MVP.
- Variant hard delete is allowed until another MVP capability references
  variants.

### MVP Phase 2 - Authentication and Customer

- Keycloak.
- Email and password authentication.
- Google sign-in.
- `ADMIN` and `CUSTOMER` roles.
- Customer profile.
- Shipping addresses.

### MVP Phase 3 - Gateway and Discovery

- API Gateway.
- Eureka.
- Routing.
- JWT validation.
- CORS.

Configuration Server is optional for the MVP and must not block progress.

### MVP Phase 4 - Cart

- Create or get active cart.
- Add item.
- Update quantity.
- Remove item.
- Calculate total.
- Clear cart.
- Validate product and stock through Catalog Service.

### MVP Phase 5 - Checkout and Orders

- Checkout request.
- Shipping-address snapshot.
- Product and price snapshot.
- Stock validation and reduction.
- Fake payment.
- Order creation.
- Order status.
- Order history.
- Order details.
- Cart clearing.
- Idempotent checkout request where practical.

Payment remains inside Order Service for the MVP. Do not extract Payment
Service before the MVP is complete.

### MVP Phase 6 - Notification

- `OrderConfirmed` event.
- Kafka.
- Notification consumer.
- Email template.
- Mailpit or SMTP.
- Delivery status.

Use Kafka initially for one meaningful asynchronous workflow:
`OrderConfirmed` -> Notification Service.

### MVP Phase 7 - Angular Frontend

- Authentication.
- Product listing.
- Product details.
- Cart.
- Checkout.
- Order confirmation.
- Order history.
- Customer profile.
- Admin product management.

### MVP Phase 8 - Local Showcase

- Docker Compose.
- Sample data.
- Demo users.
- Local startup documentation.
- Basic CI.
- Screenshots.
- Architecture diagram.
- Demo walkthrough.

## Post-MVP Improvements

The following items are deferred until after the MVP:

- Elasticsearch.
- Redis.
- Kubernetes.
- Service mesh.
- Transactional outbox.
- Advanced saga orchestration.
- Distributed tracing.
- Advanced metrics dashboards.
- Multiple warehouses.
- Multiple payment providers.
- Real payment integration.
- Returns and refunds.
- Recommendation engine.
- Promotion engine.
- Multiple notification providers.
- Advanced event schema governance.
- Comprehensive performance optimization.
- Full production hardening.
