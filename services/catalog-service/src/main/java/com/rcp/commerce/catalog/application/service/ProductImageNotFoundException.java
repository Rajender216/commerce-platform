package com.rcp.commerce.catalog.application.service;

import com.rcp.commerce.catalog.domain.ProductImageId;

public class ProductImageNotFoundException extends RuntimeException {

    private final ProductImageId imageId;

    public ProductImageNotFoundException(ProductImageId imageId) {
        super("Product image was not found.");
        this.imageId = imageId;
    }

    public ProductImageId imageId() {
        return imageId;
    }
}
