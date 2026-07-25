package com.rcp.commerce.catalog.adapter.out.persistence;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface JpaCategoryRepository extends JpaRepository<JpaCategoryEntity, UUID> {

    List<JpaCategoryEntity> findByActiveTrueOrderByNameAsc();

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, UUID id);
}
