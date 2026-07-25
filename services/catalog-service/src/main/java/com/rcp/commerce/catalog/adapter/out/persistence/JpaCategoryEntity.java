package com.rcp.commerce.catalog.adapter.out.persistence;

import com.rcp.commerce.catalog.domain.Category;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "categories")
class JpaCategoryEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = Category.NAME_MAX_LENGTH)
    private String name;

    @Column(name = "slug", nullable = false, length = Category.SLUG_MAX_LENGTH)
    private String slug;

    @Column(name = "description", nullable = false, length = Category.DESCRIPTION_MAX_LENGTH)
    private String description;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected JpaCategoryEntity() {}

    JpaCategoryEntity(
            UUID id,
            String name,
            String slug,
            String description,
            boolean active,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    boolean isActive() {
        return active;
    }

    Instant getCreatedAt() {
        return createdAt;
    }

    Instant getUpdatedAt() {
        return updatedAt;
    }
}
