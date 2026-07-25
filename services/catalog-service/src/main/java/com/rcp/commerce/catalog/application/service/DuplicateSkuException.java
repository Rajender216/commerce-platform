package com.rcp.commerce.catalog.application.service;

public class DuplicateSkuException extends RuntimeException {

    private final String sku;

    public DuplicateSkuException(String sku) {
        super("SKU already exists.");
        this.sku = sku;
    }

    public String sku() {
        return sku;
    }
}
