package com.rcp.commerce.catalog.adapter.out.persistence;

import com.rcp.commerce.catalog.domain.ProductImage;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "product_images")
class JpaProductImageEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "url", nullable = false, length = ProductImage.URL_MAX_LENGTH)
    private String url;

    @Column(name = "alt_text", nullable = false, length = ProductImage.ALT_TEXT_MAX_LENGTH)
    private String altText;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Column(name = "primary_image", nullable = false)
    private boolean primaryImage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected JpaProductImageEntity() {}

    JpaProductImageEntity(
            UUID id,
            UUID productId,
            String url,
            String altText,
            int displayOrder,
            boolean primaryImage,
            Instant createdAt) {
        this.id = id;
        this.productId = productId;
        this.url = url;
        this.altText = altText;
        this.displayOrder = displayOrder;
        this.primaryImage = primaryImage;
        this.createdAt = createdAt;
    }

    UUID getId() {
        return id;
    }

    UUID getProductId() {
        return productId;
    }

    String getUrl() {
        return url;
    }

    String getAltText() {
        return altText;
    }

    int getDisplayOrder() {
        return displayOrder;
    }

    boolean isPrimaryImage() {
        return primaryImage;
    }

    Instant getCreatedAt() {
        return createdAt;
    }

    void setPrimaryImage(boolean primaryImage) {
        this.primaryImage = primaryImage;
    }
}
