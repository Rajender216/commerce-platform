package com.rcp.commerce.catalog.domain;

import java.util.Objects;
import java.util.UUID;

public record ProductId(UUID value) {

    public ProductId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static ProductId newId() {
        return new ProductId(UUID.randomUUID());
    }

    public static ProductId from(UUID value) {
        return new ProductId(value);
    }
}
