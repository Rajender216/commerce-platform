package com.rcp.commerce.customer.adapter.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface JpaShippingAddressRepository extends JpaRepository<JpaShippingAddressEntity, UUID> {

    List<JpaShippingAddressEntity> findByCustomerIdOrderByCreatedAtAsc(UUID customerId);

    Optional<JpaShippingAddressEntity> findByCustomerIdAndId(UUID customerId, UUID id);
}
