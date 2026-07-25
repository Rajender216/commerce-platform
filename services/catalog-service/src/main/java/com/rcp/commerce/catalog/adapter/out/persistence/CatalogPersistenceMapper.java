package com.rcp.commerce.catalog.adapter.out.persistence;

import com.rcp.commerce.catalog.domain.Category;
import com.rcp.commerce.catalog.domain.CategoryId;
import com.rcp.commerce.catalog.domain.Product;
import com.rcp.commerce.catalog.domain.ProductId;
import com.rcp.commerce.catalog.domain.ProductImage;
import com.rcp.commerce.catalog.domain.ProductImageId;
import com.rcp.commerce.catalog.domain.ProductVariant;
import com.rcp.commerce.catalog.domain.VariantId;

class CatalogPersistenceMapper {

    JpaCategoryEntity toEntity(Category category) {
        return new JpaCategoryEntity(
                category.id().value(),
                category.name(),
                category.slug(),
                category.description(),
                category.active(),
                category.createdAt(),
                category.updatedAt());
    }

    Category toDomain(JpaCategoryEntity entity) {
        return new Category(
                CategoryId.from(entity.getId()),
                entity.getName(),
                entity.getSlug(),
                entity.getDescription(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    JpaProductEntity toEntity(Product product) {
        return new JpaProductEntity(
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

    Product toDomain(JpaProductEntity entity) {
        return new Product(
                ProductId.from(entity.getId()),
                entity.getName(),
                entity.getSlug(),
                entity.getDescription(),
                CategoryId.from(entity.getCategoryId()),
                entity.getBrand(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion());
    }

    JpaProductVariantEntity toEntity(ProductVariant variant) {
        return new JpaProductVariantEntity(
                variant.id().value(),
                variant.productId().value(),
                variant.sku(),
                variant.name(),
                variant.price(),
                variant.stockQuantity(),
                variant.active(),
                variant.createdAt(),
                variant.updatedAt(),
                variant.version());
    }

    ProductVariant toDomain(JpaProductVariantEntity entity) {
        return new ProductVariant(
                VariantId.from(entity.getId()),
                ProductId.from(entity.getProductId()),
                entity.getSku(),
                entity.getName(),
                entity.getPrice(),
                entity.getStockQuantity(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion());
    }

    JpaProductImageEntity toEntity(ProductImage image) {
        return new JpaProductImageEntity(
                image.id().value(),
                image.productId().value(),
                image.url(),
                image.altText(),
                image.displayOrder(),
                image.primaryImage(),
                image.createdAt());
    }

    ProductImage toDomain(JpaProductImageEntity entity) {
        return new ProductImage(
                ProductImageId.from(entity.getId()),
                ProductId.from(entity.getProductId()),
                entity.getUrl(),
                entity.getAltText(),
                entity.getDisplayOrder(),
                entity.isPrimaryImage(),
                entity.getCreatedAt());
    }
}
