package com.rcp.commerce.catalog.adapter.in.web;

import com.rcp.commerce.catalog.application.port.in.CategoryCommand;
import com.rcp.commerce.catalog.application.port.in.PageResult;
import com.rcp.commerce.catalog.application.port.in.ProductCatalogView;
import com.rcp.commerce.catalog.application.port.in.ProductCommand;
import com.rcp.commerce.catalog.application.port.in.ProductImageCommand;
import com.rcp.commerce.catalog.application.port.in.StockCommand;
import com.rcp.commerce.catalog.application.port.in.VariantCommand;
import com.rcp.commerce.catalog.domain.Category;
import com.rcp.commerce.catalog.domain.Product;
import com.rcp.commerce.catalog.domain.ProductImage;
import com.rcp.commerce.catalog.domain.ProductStatus;
import com.rcp.commerce.catalog.domain.ProductVariant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

final class CatalogWebModels {

    static final String CURRENCY = "USD";

    private CatalogWebModels() {}
}

record CategoryRequest(
        @NotBlank @Size(max = Category.NAME_MAX_LENGTH) String name,
        @NotBlank @Size(max = Category.SLUG_MAX_LENGTH) String slug,
        @Size(max = Category.DESCRIPTION_MAX_LENGTH) String description,
        boolean active) {

    CategoryCommand toCommand() {
        return new CategoryCommand(name, slug, description, active);
    }
}

record CategoryResponse(
        UUID id, String name, String slug, String description, boolean active, Instant createdAt, Instant updatedAt) {

    static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.id().value(),
                category.name(),
                category.slug(),
                category.description(),
                category.active(),
                category.createdAt(),
                category.updatedAt());
    }
}

record ProductRequest(
        @NotBlank @Size(max = Product.NAME_MAX_LENGTH) String name,
        @NotBlank @Size(max = Product.SLUG_MAX_LENGTH) String slug,
        @Size(max = Product.DESCRIPTION_MAX_LENGTH) String description,
        @NotNull UUID categoryId,
        @Size(max = Product.BRAND_MAX_LENGTH) String brand) {

    ProductCommand toCommand() {
        return new ProductCommand(
                name, slug, description, com.rcp.commerce.catalog.domain.CategoryId.from(categoryId), brand);
    }
}

record ProductStatusRequest(@NotNull ProductStatus status) {}

record AdminProductResponse(
        UUID id,
        String name,
        String slug,
        String description,
        UUID categoryId,
        String brand,
        ProductStatus status,
        Instant createdAt,
        Instant updatedAt,
        long version) {

    static AdminProductResponse from(Product product) {
        return new AdminProductResponse(
                product.id().value(),
                product.name(),
                product.slug(),
                product.description(),
                product.categoryId().value(),
                product.brand(),
                product.status(),
                product.createdAt(),
                product.updatedAt(),
                product.version());
    }
}

record VariantRequest(
        @NotBlank @Size(max = ProductVariant.SKU_MAX_LENGTH) String sku,
        @NotBlank @Size(max = ProductVariant.NAME_MAX_LENGTH) String name,
        @NotNull @Positive BigDecimal price,
        @PositiveOrZero int stockQuantity,
        boolean active) {

    VariantCommand toCommand() {
        return new VariantCommand(sku, name, price, stockQuantity, active);
    }
}

record StockRequest(@PositiveOrZero int stockQuantity) {

    StockCommand toCommand() {
        return new StockCommand(stockQuantity);
    }
}

record VariantResponse(
        UUID id,
        UUID productId,
        String sku,
        String name,
        BigDecimal price,
        String currency,
        int stockQuantity,
        boolean active,
        Instant createdAt,
        Instant updatedAt,
        long version) {

    static VariantResponse from(ProductVariant variant) {
        return new VariantResponse(
                variant.id().value(),
                variant.productId().value(),
                variant.sku(),
                variant.name(),
                variant.price(),
                CatalogWebModels.CURRENCY,
                variant.stockQuantity(),
                variant.active(),
                variant.createdAt(),
                variant.updatedAt(),
                variant.version());
    }
}

record ProductImageRequest(
        @NotBlank @Size(max = ProductImage.URL_MAX_LENGTH) String url,
        @Size(max = ProductImage.ALT_TEXT_MAX_LENGTH) String altText,
        @PositiveOrZero int displayOrder,
        boolean primaryImage) {

    ProductImageCommand toCommand() {
        return new ProductImageCommand(url, altText, displayOrder, primaryImage);
    }
}

record ProductImageResponse(
        UUID id,
        UUID productId,
        String url,
        String altText,
        int displayOrder,
        boolean primaryImage,
        Instant createdAt) {

    static ProductImageResponse from(ProductImage image) {
        return new ProductImageResponse(
                image.id().value(),
                image.productId().value(),
                image.url(),
                image.altText(),
                image.displayOrder(),
                image.primaryImage(),
                image.createdAt());
    }
}

record CategorySummaryResponse(UUID id, String name, String slug) {

    static CategorySummaryResponse from(Category category) {
        return new CategorySummaryResponse(category.id().value(), category.name(), category.slug());
    }
}

record PriceRangeResponse(BigDecimal min, BigDecimal max, String currency) {}

record PublicProductResponse(
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

record PageResponse<T>(
        List<T> content, int page, int size, long totalElements, int totalPages, boolean first, boolean last) {

    static <T, R> PageResponse<R> from(PageResult<T> page, java.util.function.Function<T, R> mapper) {
        return new PageResponse<>(
                page.content().stream().map(mapper).toList(),
                page.page(),
                page.size(),
                page.totalElements(),
                page.totalPages(),
                page.first(),
                page.last());
    }
}
