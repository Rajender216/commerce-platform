package com.rcp.commerce.customer.adapter.in.web;

import com.rcp.commerce.customer.domain.ShippingAddress;
import java.time.Instant;
import java.util.UUID;

public record AddressResponse(
        UUID id,
        UUID customerId,
        String recipientName,
        String phoneNumber,
        String addressLine1,
        String addressLine2,
        String city,
        String state,
        String postalCode,
        String country,
        boolean defaultAddress,
        Instant createdAt,
        Instant updatedAt,
        long version) {

    static AddressResponse from(ShippingAddress address) {
        return new AddressResponse(
                address.id().value(),
                address.customerId().value(),
                address.recipientName(),
                address.phoneNumber(),
                address.addressLine1(),
                address.addressLine2(),
                address.city(),
                address.state(),
                address.postalCode(),
                address.country(),
                address.defaultAddress(),
                address.createdAt(),
                address.updatedAt(),
                address.version());
    }
}
