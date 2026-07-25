package com.rcp.commerce.catalog.adapter.in.web;

import com.rcp.commerce.catalog.application.port.in.CategoryCommand;
import com.rcp.commerce.catalog.domain.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotBlank @Size(max = Category.NAME_MAX_LENGTH) String name,
        @NotBlank @Size(max = Category.SLUG_MAX_LENGTH) String slug,
        @Size(max = Category.DESCRIPTION_MAX_LENGTH) String description,
        boolean active) {

    CategoryCommand toCommand() {
        return new CategoryCommand(name, slug, description, active);
    }
}
