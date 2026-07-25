package com.rcp.commerce.catalog.application.port.in;

import com.rcp.commerce.catalog.domain.Category;
import com.rcp.commerce.catalog.domain.Product;
import com.rcp.commerce.catalog.domain.ProductImage;
import com.rcp.commerce.catalog.domain.ProductVariant;
import java.math.BigDecimal;
import java.util.List;

public record ProductCatalogView(
        Product product,
        Category category,
        List<ProductVariant> variants,
        List<ProductImage> images,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        boolean inStock) {}
