package com.rcp.commerce.catalog.domain;

import java.util.Objects;

public record ProductName(String value) {

    public static final int MAX_LENGTH = 160;

    public ProductName {
        value = normalize(value);
        if (value.isBlank()) {
            throw new IllegalArgumentException("Product name is required.");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Product name must be at most " + MAX_LENGTH + " characters.");
        }
    }

    private static String normalize(String value) {
        return Objects.requireNonNullElse(value, "").trim();
    }
}
