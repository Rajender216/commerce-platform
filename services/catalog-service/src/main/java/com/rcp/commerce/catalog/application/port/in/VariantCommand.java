package com.rcp.commerce.catalog.application.port.in;

import java.math.BigDecimal;

public record VariantCommand(String sku, String name, BigDecimal price, int stockQuantity, boolean active) {}
