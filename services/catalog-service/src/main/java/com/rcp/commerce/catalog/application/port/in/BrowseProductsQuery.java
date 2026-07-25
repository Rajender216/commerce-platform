package com.rcp.commerce.catalog.application.port.in;

import java.math.BigDecimal;

public record BrowseProductsQuery(
        int page,
        int size,
        String sort,
        String search,
        String category,
        String brand,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        boolean inStockOnly) {}
