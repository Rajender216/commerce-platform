package com.rcp.commerce.customer.adapter.out.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface JpaCustomerProfileRepository extends JpaRepository<JpaCustomerProfileEntity, UUID> {

    Optional<JpaCustomerProfileEntity> findByIdentitySubject(String identitySubject);
}
