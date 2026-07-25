package com.rcp.commerce.catalog.application.service;

import com.rcp.commerce.catalog.domain.ProductId;

public class ProductNotFoundException extends RuntimeException {

    private final ProductId productId;
    private final String lookup;

    public ProductNotFoundException(ProductId productId) {
        super("Product was not found.");
        this.productId = productId;
        this.lookup = productId.value().toString();
    }

    public ProductNotFoundException(String lookup) {
        super("Product was not found.");
        this.productId = null;
        this.lookup = lookup;
    }

    public ProductId productId() {
        return productId;
    }

    public String lookup() {
        return lookup;
    }
}
