package com.rcp.commerce.catalog.adapter.out.persistence;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface JpaProductRepository extends JpaRepository<JpaProductEntity, UUID> {}
