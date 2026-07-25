package com.rcp.commerce.catalog.domain;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public record ProductImage(
        ProductImageId id,
        ProductId productId,
        String url,
        String altText,
        int displayOrder,
        boolean primaryImage,
        Instant createdAt) {

    public static final int URL_MAX_LENGTH = 2_048;
    public static final int ALT_TEXT_MAX_LENGTH = 255;

    public ProductImage {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(productId, "productId must not be null");
        url = normalizeRequired(url, "Image URL is required.");
        altText = Objects.requireNonNullElse(altText, "").trim();
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        validateLength(url, URL_MAX_LENGTH, "Image URL");
        validateLength(altText, ALT_TEXT_MAX_LENGTH, "Image alt text");
        if (displayOrder < 0) {
            throw new IllegalArgumentException("Image display order cannot be negative.");
        }
    }

    public static ProductImage create(
            ProductId productId, String url, String altText, int displayOrder, boolean primaryImage, Clock clock) {
        return new ProductImage(
                ProductImageId.newId(), productId, url, altText, displayOrder, primaryImage, Instant.now(clock));
    }

    public ProductImage update(String url, String altText, int displayOrder, boolean primaryImage) {
        return new ProductImage(id, productId, url, altText, displayOrder, primaryImage, createdAt);
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
