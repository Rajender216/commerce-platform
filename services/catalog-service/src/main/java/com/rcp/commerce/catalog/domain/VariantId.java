package com.rcp.commerce.catalog.domain;

import java.util.Objects;
import java.util.UUID;

public record VariantId(UUID value) {

    public VariantId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static VariantId newId() {
        return new VariantId(UUID.randomUUID());
    }

    public static VariantId from(UUID value) {
        return new VariantId(value);
    }
}
