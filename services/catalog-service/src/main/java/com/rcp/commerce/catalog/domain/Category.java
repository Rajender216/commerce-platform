package com.rcp.commerce.catalog.domain;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public record Category(
        CategoryId id,
        String name,
        String slug,
        String description,
        boolean active,
        Instant createdAt,
        Instant updatedAt) {

    public static final int NAME_MAX_LENGTH = 160;
    public static final int SLUG_MAX_LENGTH = 160;
    public static final int DESCRIPTION_MAX_LENGTH = 1_000;

    public Category {
        Objects.requireNonNull(id, "id must not be null");
        name = normalizeRequired(name, "Category name is required.");
        slug = normalizeRequired(slug, "Category slug is required.");
        description = normalizeOptional(description);
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");
        validateLength(name, NAME_MAX_LENGTH, "Category name");
        validateLength(slug, SLUG_MAX_LENGTH, "Category slug");
        validateLength(description, DESCRIPTION_MAX_LENGTH, "Category description");
        if (updatedAt.isBefore(createdAt)) {
            throw new IllegalArgumentException("updatedAt must not be before createdAt.");
        }
    }

    public static Category create(String name, String slug, String description, boolean active, Clock clock) {
        Instant now = Instant.now(Objects.requireNonNull(clock, "clock must not be null"));
        return new Category(CategoryId.newId(), name, slug, description, active, now, now);
    }

    public Category update(String name, String slug, String description, boolean active, Clock clock) {
        return new Category(id, name, slug, description, active, createdAt, Instant.now(clock));
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
