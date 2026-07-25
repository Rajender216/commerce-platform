package com.rcp.commerce.catalog.adapter.out.persistence;

import com.rcp.commerce.catalog.domain.ProductVariant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "product_variants")
class JpaProductVariantEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "sku", nullable = false, length = ProductVariant.SKU_MAX_LENGTH)
    private String sku;

    @Column(name = "name", nullable = false, length = ProductVariant.NAME_MAX_LENGTH)
    private String name;

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    protected JpaProductVariantEntity() {}

    JpaProductVariantEntity(
            UUID id,
            UUID productId,
            String sku,
            String name,
            BigDecimal price,
            int stockQuantity,
            boolean active,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        this.id = id;
        this.productId = productId;
        this.sku = sku;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    UUID getId() {
        return id;
    }

    UUID getProductId() {
        return productId;
    }

    String getSku() {
        return sku;
    }

    String getName() {
        return name;
    }

    BigDecimal getPrice() {
        return price;
    }

    int getStockQuantity() {
        return stockQuantity;
    }

    boolean isActive() {
        return active;
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

    void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}
