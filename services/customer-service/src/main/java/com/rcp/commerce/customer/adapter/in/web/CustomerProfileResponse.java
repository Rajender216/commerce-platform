package com.rcp.commerce.customer.adapter.in.web;

import com.rcp.commerce.customer.domain.CustomerProfile;
import java.time.Instant;
import java.util.UUID;

public record CustomerProfileResponse(
        UUID id,
        String identitySubject,
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        Instant createdAt,
        Instant updatedAt,
        long version) {

    static CustomerProfileResponse from(CustomerProfile profile) {
        return new CustomerProfileResponse(
                profile.id().value(),
                profile.identitySubject(),
                profile.email(),
                profile.firstName(),
                profile.lastName(),
                profile.phoneNumber(),
                profile.createdAt(),
                profile.updatedAt(),
                profile.version());
    }
}
