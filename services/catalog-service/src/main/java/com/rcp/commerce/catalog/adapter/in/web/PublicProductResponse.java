package com.rcp.commerce.catalog.adapter.in.web;

import com.rcp.commerce.catalog.application.port.in.ProductCatalogView;
import com.rcp.commerce.catalog.domain.Product;
import com.rcp.commerce.catalog.domain.ProductImage;
import com.rcp.commerce.catalog.domain.ProductStatus;
import java.util.List;
import java.util.UUID;

public record PublicProductResponse(
        UUID id,
        String name,
        String slug,
        String description,
        CategorySummaryResponse category,
        String brand,
        ProductStatus status,
        List<VariantResponse> variants,
        PriceRangeResponse priceRange,
        boolean inStock,
        List<ProductImageResponse> images,
        ProductImageResponse primaryImage) {

    static PublicProductResponse from(ProductCatalogView view, boolean includeStatus) {
        Product product = view.product();
        ProductImageResponse primaryImage = view.images().stream()
                .filter(ProductImage::primaryImage)
                .findFirst()
                .map(ProductImageResponse::from)
                .orElse(null);
        return new PublicProductResponse(
                product.id().value(),
                product.name(),
                product.slug(),
                product.description(),
                CategorySummaryResponse.from(view.category()),
                product.brand(),
                includeStatus ? product.status() : null,
                view.variants().stream().map(VariantResponse::from).toList(),
                new PriceRangeResponse(view.minPrice(), view.maxPrice(), CatalogWebModels.CURRENCY),
                view.inStock(),
                view.images().stream().map(ProductImageResponse::from).toList(),
                primaryImage);
    }
}
