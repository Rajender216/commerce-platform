package com.rcp.commerce.catalog.adapter.in.web;

import com.rcp.commerce.catalog.application.port.in.ProductCommand;
import com.rcp.commerce.catalog.domain.CategoryId;
import com.rcp.commerce.catalog.domain.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record ProductRequest(
        @NotBlank @Size(max = Product.NAME_MAX_LENGTH) String name,
        @NotBlank @Size(max = Product.SLUG_MAX_LENGTH) String slug,
        @Size(max = Product.DESCRIPTION_MAX_LENGTH) String description,
        @NotNull UUID categoryId,
        @Size(max = Product.BRAND_MAX_LENGTH) String brand) {

    ProductCommand toCommand() {
        return new ProductCommand(name, slug, description, CategoryId.from(categoryId), brand);
    }
}
