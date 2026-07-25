package com.rcp.commerce.catalog.adapter.out.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface JpaProductRepository extends JpaRepository<JpaProductEntity, UUID> {

    Optional<JpaProductEntity> findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, UUID id);
}
