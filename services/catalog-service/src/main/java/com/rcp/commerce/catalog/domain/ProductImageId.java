package com.rcp.commerce.catalog.domain;

import java.util.Objects;
import java.util.UUID;

public record ProductImageId(UUID value) {

    public ProductImageId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static ProductImageId newId() {
        return new ProductImageId(UUID.randomUUID());
    }

    public static ProductImageId from(UUID value) {
        return new ProductImageId(value);
    }
}
