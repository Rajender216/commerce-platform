package com.rcp.commerce.catalog.domain;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public record Product(
        ProductId id,
        ProductName name,
        ProductDescription description,
        ProductStatus status,
        Instant createdAt,
        Instant updatedAt,
        long version) {

    public Product {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(description, "description must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");
        if (updatedAt.isBefore(createdAt)) {
            throw new IllegalArgumentException("updatedAt must not be before createdAt.");
        }
        if (version < 0) {
            throw new IllegalArgumentException("version must not be negative.");
        }
    }

    public static Product createDraft(ProductName name, ProductDescription description, Clock clock) {
        Instant now = Instant.now(Objects.requireNonNull(clock, "clock must not be null"));
        return new Product(ProductId.newId(), name, description, ProductStatus.DRAFT, now, now, 0);
    }
}
