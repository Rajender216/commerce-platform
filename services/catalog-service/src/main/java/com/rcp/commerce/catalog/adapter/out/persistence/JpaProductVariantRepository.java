package com.rcp.commerce.catalog.adapter.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface JpaProductVariantRepository extends JpaRepository<JpaProductVariantEntity, UUID> {

    List<JpaProductVariantEntity> findByProductIdOrderBySkuAsc(UUID productId);

    List<JpaProductVariantEntity> findByProductIdAndActiveTrueOrderBySkuAsc(UUID productId);

    Optional<JpaProductVariantEntity> findByProductIdAndId(UUID productId, UUID id);

    boolean existsByProductIdAndActiveTrue(UUID productId);

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, UUID id);
}
