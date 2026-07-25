package com.rcp.commerce.catalog.application.service;

import com.rcp.commerce.catalog.domain.VariantId;

public class VariantNotFoundException extends RuntimeException {

    private final VariantId variantId;

    public VariantNotFoundException(VariantId variantId) {
        super("Variant was not found.");
        this.variantId = variantId;
    }

    public VariantId variantId() {
        return variantId;
    }
}
