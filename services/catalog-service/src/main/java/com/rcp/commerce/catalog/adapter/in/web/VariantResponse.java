package com.rcp.commerce.catalog.adapter.in.web;

import com.rcp.commerce.catalog.domain.ProductVariant;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record VariantResponse(
        UUID id,
        UUID productId,
        String sku,
        String name,
        BigDecimal price,
        String currency,
        int stockQuantity,
        boolean active,
        Instant createdAt,
        Instant updatedAt,
        long version) {

    static VariantResponse from(ProductVariant variant) {
        return new VariantResponse(
                variant.id().value(),
                variant.productId().value(),
                variant.sku(),
                variant.name(),
                variant.price(),
                CatalogWebModels.CURRENCY,
                variant.stockQuantity(),
                variant.active(),
                variant.createdAt(),
                variant.updatedAt(),
                variant.version());
    }
}
