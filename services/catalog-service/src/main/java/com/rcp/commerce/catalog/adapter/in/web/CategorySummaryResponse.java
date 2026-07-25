package com.rcp.commerce.catalog.adapter.in.web;

import com.rcp.commerce.catalog.domain.Category;
import java.util.UUID;

public record CategorySummaryResponse(UUID id, String name, String slug) {

    static CategorySummaryResponse from(Category category) {
        return new CategorySummaryResponse(category.id().value(), category.name(), category.slug());
    }
}
