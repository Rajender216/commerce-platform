package com.rcp.commerce.catalog.adapter.in.web;

import com.rcp.commerce.catalog.domain.Category;
import java.time.Instant;
import java.util.UUID;

public record CategoryResponse(
        UUID id, String name, String slug, String description, boolean active, Instant createdAt, Instant updatedAt) {

    static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.id().value(),
                category.name(),
                category.slug(),
                category.description(),
                category.active(),
                category.createdAt(),
                category.updatedAt());
    }
}
