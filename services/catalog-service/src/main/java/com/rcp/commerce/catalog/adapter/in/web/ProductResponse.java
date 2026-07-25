package com.rcp.commerce.catalog.adapter.in.web;

import com.rcp.commerce.catalog.domain.Product;
import java.time.Instant;
import java.util.UUID;

record ProductResponse(
        UUID id, String name, String description, String status, Instant createdAt, Instant updatedAt, long version) {

    static ProductResponse from(Product product) {
        return new ProductResponse(
                product.id().value(),
                product.name().value(),
                product.description().value(),
                product.status().name(),
                product.createdAt(),
                product.updatedAt(),
                product.version());
    }
}
