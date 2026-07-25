package com.rcp.commerce.catalog.adapter.in.web;

import com.rcp.commerce.catalog.domain.Product;
import com.rcp.commerce.catalog.domain.ProductStatus;
import java.time.Instant;
import java.util.UUID;

public record AdminProductResponse(
        UUID id,
        String name,
        String slug,
        String description,
        UUID categoryId,
        String brand,
        ProductStatus status,
        Instant createdAt,
        Instant updatedAt,
        long version) {

    static AdminProductResponse from(Product product) {
        return new AdminProductResponse(
                product.id().value(),
                product.name(),
                product.slug(),
                product.description(),
                product.categoryId().value(),
                product.brand(),
                product.status(),
                product.createdAt(),
                product.updatedAt(),
                product.version());
    }
}
