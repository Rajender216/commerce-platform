package com.rcp.commerce.catalog.domain;

import java.util.Objects;

public record ProductDescription(String value) {

    public static final int MAX_LENGTH = 2_000;

    public ProductDescription {
        value = normalize(value);
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Product description must be at most " + MAX_LENGTH + " characters.");
        }
    }

    public static ProductDescription empty() {
        return new ProductDescription("");
    }

    private static String normalize(String value) {
        return Objects.requireNonNullElse(value, "").trim();
    }
}
