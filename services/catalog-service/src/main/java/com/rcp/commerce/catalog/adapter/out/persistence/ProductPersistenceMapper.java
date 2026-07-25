package com.rcp.commerce.catalog.adapter.out.persistence;

import com.rcp.commerce.catalog.domain.Product;
import com.rcp.commerce.catalog.domain.ProductDescription;
import com.rcp.commerce.catalog.domain.ProductId;
import com.rcp.commerce.catalog.domain.ProductName;

class ProductPersistenceMapper {

    JpaProductEntity toEntity(Product product) {
        return new JpaProductEntity(
                product.id().value(),
                product.name().value(),
                product.description().value(),
                product.status(),
                product.createdAt(),
                product.updatedAt(),
                product.version());
    }

    Product toDomain(JpaProductEntity entity) {
        return new Product(
                ProductId.from(entity.getId()),
                new ProductName(entity.getName()),
                new ProductDescription(entity.getDescription()),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion());
    }
}
