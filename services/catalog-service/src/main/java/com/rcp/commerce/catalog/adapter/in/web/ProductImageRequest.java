package com.rcp.commerce.catalog.adapter.in.web;

import com.rcp.commerce.catalog.application.port.in.ProductImageCommand;
import com.rcp.commerce.catalog.domain.ProductImage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ProductImageRequest(
        @NotBlank @Size(max = ProductImage.URL_MAX_LENGTH) String url,
        @Size(max = ProductImage.ALT_TEXT_MAX_LENGTH) String altText,
        @PositiveOrZero int displayOrder,
        boolean primaryImage) {

    ProductImageCommand toCommand() {
        return new ProductImageCommand(url, altText, displayOrder, primaryImage);
    }
}
