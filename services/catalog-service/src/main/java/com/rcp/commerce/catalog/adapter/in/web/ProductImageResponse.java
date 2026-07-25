package com.rcp.commerce.catalog.adapter.in.web;

import com.rcp.commerce.catalog.domain.ProductImage;
import java.time.Instant;
import java.util.UUID;

public record ProductImageResponse(
        UUID id,
        UUID productId,
        String url,
        String altText,
        int displayOrder,
        boolean primaryImage,
        Instant createdAt) {

    static ProductImageResponse from(ProductImage image) {
        return new ProductImageResponse(
                image.id().value(),
                image.productId().value(),
                image.url(),
                image.altText(),
                image.displayOrder(),
                image.primaryImage(),
                image.createdAt());
    }
}
