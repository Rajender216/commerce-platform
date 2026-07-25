package com.rcp.commerce.catalog.adapter.out.persistence;

import com.rcp.commerce.catalog.domain.Product;
import com.rcp.commerce.catalog.domain.ProductStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "products")
class JpaProductEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = Product.NAME_MAX_LENGTH)
    private String name;

    @Column(name = "slug", nullable = false, length = Product.SLUG_MAX_LENGTH)
    private String slug;

    @Column(name = "description", nullable = false, length = Product.DESCRIPTION_MAX_LENGTH)
    private String description;

    @Column(name = "category_id", nullable = false)
    private UUID categoryId;

    @Column(name = "brand", nullable = false, length = Product.BRAND_MAX_LENGTH)
    private String brand;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private ProductStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    protected JpaProductEntity() {}

    JpaProductEntity(
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
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.categoryId = categoryId;
        this.brand = brand;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    UUID getId() {
        return id;
    }

    String getName() {
        return name;
    }

    String getSlug() {
        return slug;
    }

    String getDescription() {
        return description;
    }

    UUID getCategoryId() {
        return categoryId;
    }

    String getBrand() {
        return brand;
    }

    ProductStatus getStatus() {
        return status;
    }

    Instant getCreatedAt() {
        return createdAt;
    }

    Instant getUpdatedAt() {
        return updatedAt;
    }

    long getVersion() {
        return version;
    }
}
