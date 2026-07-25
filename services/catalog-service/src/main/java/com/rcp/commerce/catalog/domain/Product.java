package com.rcp.commerce.catalog.domain;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public record Product(
        ProductId id,
        String name,
        String slug,
        String description,
        CategoryId categoryId,
        String brand,
        ProductStatus status,
        Instant createdAt,
        Instant updatedAt,
        long version) {

    public static final int NAME_MAX_LENGTH = 160;
    public static final int SLUG_MAX_LENGTH = 160;
    public static final int DESCRIPTION_MAX_LENGTH = 2_000;
    public static final int BRAND_MAX_LENGTH = 120;

    public Product {
        Objects.requireNonNull(id, "id must not be null");
        name = normalizeRequired(name, "Product name is required.");
        slug = normalizeRequired(slug, "Product slug is required.");
        description = normalizeOptional(description);
        brand = normalizeOptional(brand);
        Objects.requireNonNull(categoryId, "categoryId must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");
        validateLength(name, NAME_MAX_LENGTH, "Product name");
        validateLength(slug, SLUG_MAX_LENGTH, "Product slug");
        validateLength(description, DESCRIPTION_MAX_LENGTH, "Product description");
        validateLength(brand, BRAND_MAX_LENGTH, "Product brand");
        if (updatedAt.isBefore(createdAt)) {
            throw new IllegalArgumentException("updatedAt must not be before createdAt.");
        }
        if (version < 0) {
            throw new IllegalArgumentException("version must not be negative.");
        }
    }

    public static Product createDraft(
            String name, String slug, String description, CategoryId categoryId, String brand, Clock clock) {
        Instant now = Instant.now(Objects.requireNonNull(clock, "clock must not be null"));
        return new Product(
                ProductId.newId(), name, slug, description, categoryId, brand, ProductStatus.DRAFT, now, now, 0);
    }

    public Product update(
            String name, String slug, String description, CategoryId categoryId, String brand, Clock clock) {
        return new Product(
                id,
                name,
                slug,
                description,
                categoryId,
                brand,
                status,
                createdAt,
                Instant.now(Objects.requireNonNull(clock, "clock must not be null")),
                version);
    }

    public Product changeStatus(
            ProductStatus newStatus, boolean categoryActive, boolean hasActiveVariant, Clock clock) {
        Objects.requireNonNull(newStatus, "newStatus must not be null");
        if (newStatus == ProductStatus.ACTIVE && !hasActiveVariant) {
            throw new IllegalStateException("Product activation requires at least one active variant.");
        }
        if (newStatus == ProductStatus.ACTIVE && !categoryActive) {
            throw new IllegalStateException("Inactive categories cannot receive active products.");
        }
        return new Product(
                id,
                name,
                slug,
                description,
                categoryId,
                brand,
                newStatus,
                createdAt,
                Instant.now(Objects.requireNonNull(clock, "clock must not be null")),
                version);
    }

    private static String normalizeRequired(String value, String message) {
        String normalized = normalizeOptional(value);
        if (normalized.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return normalized;
    }

    private static String normalizeOptional(String value) {
        return Objects.requireNonNullElse(value, "").trim();
    }

    private static void validateLength(String value, int maxLength, String field) {
        if (value.length() > maxLength) {
            throw new IllegalArgumentException(field + " must be at most " + maxLength + " characters.");
        }
    }
}
