package com.rcp.commerce.catalog.adapter.in.web;

import com.rcp.commerce.catalog.application.port.in.VariantCommand;
import com.rcp.commerce.catalog.domain.ProductVariant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record VariantRequest(
        @NotBlank @Size(max = ProductVariant.SKU_MAX_LENGTH) String sku,
        @NotBlank @Size(max = ProductVariant.NAME_MAX_LENGTH) String name,
        @NotNull @Positive BigDecimal price,
        @PositiveOrZero int stockQuantity,
        boolean active) {

    VariantCommand toCommand() {
        return new VariantCommand(sku, name, price, stockQuantity, active);
    }
}
