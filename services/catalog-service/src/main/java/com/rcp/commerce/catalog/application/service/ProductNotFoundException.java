package com.rcp.commerce.catalog.application.service;

import com.rcp.commerce.catalog.domain.ProductId;

public class ProductNotFoundException extends RuntimeException {

    private final ProductId productId;

    public ProductNotFoundException(ProductId productId) {
        super("Product was not found.");
        this.productId = productId;
    }

    public ProductId productId() {
        return productId;
    }
}
