package com.rcp.commerce.catalog.domain;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public record ProductVariant(
        VariantId id,
        ProductId productId,
        String sku,
        String name,
        BigDecimal price,
        int stockQuantity,
        boolean active,
        Instant createdAt,
        Instant updatedAt,
        long version) {

    public static final int SKU_MAX_LENGTH = 80;
    public static final int NAME_MAX_LENGTH = 160;

    public ProductVariant {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(productId, "productId must not be null");
        sku = normalizeRequired(sku, "SKU is required.");
        name = normalizeRequired(name, "Variant name is required.");
        price = Objects.requireNonNull(price, "price must not be null").stripTrailingZeros();
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");
        validateLength(sku, SKU_MAX_LENGTH, "SKU");
        validateLength(name, NAME_MAX_LENGTH, "Variant name");
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Variant price must be greater than zero.");
        }
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative.");
        }
        if (version < 0) {
            throw new IllegalArgumentException("version must not be negative.");
        }
    }

    public static ProductVariant create(
            ProductId productId,
            String sku,
            String name,
            BigDecimal price,
            int stockQuantity,
            boolean active,
            Clock clock) {
        Instant now = Instant.now(Objects.requireNonNull(clock, "clock must not be null"));
        return new ProductVariant(VariantId.newId(), productId, sku, name, price, stockQuantity, active, now, now, 0);
    }

    public ProductVariant update(
            String sku, String name, BigDecimal price, int stockQuantity, boolean active, Clock clock) {
        return new ProductVariant(
                id, productId, sku, name, price, stockQuantity, active, createdAt, Instant.now(clock), version);
    }

    public ProductVariant updateStock(int stockQuantity, Clock clock) {
        return new ProductVariant(
                id, productId, sku, name, price, stockQuantity, active, createdAt, Instant.now(clock), version);
    }

    private static String normalizeRequired(String value, String message) {
        String normalized = Objects.requireNonNullElse(value, "").trim();
        if (normalized.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return normalized;
    }

    private static void validateLength(String value, int maxLength, String field) {
        if (value.length() > maxLength) {
            throw new IllegalArgumentException(field + " must be at most " + maxLength + " characters.");
        }
    }
}
