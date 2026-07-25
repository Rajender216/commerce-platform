package com.rcp.commerce.catalog.adapter.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface JpaProductImageRepository extends JpaRepository<JpaProductImageEntity, UUID> {

    List<JpaProductImageEntity> findByProductIdOrderByDisplayOrderAscCreatedAtAsc(UUID productId);

    Optional<JpaProductImageEntity> findByProductIdAndId(UUID productId, UUID id);
}
