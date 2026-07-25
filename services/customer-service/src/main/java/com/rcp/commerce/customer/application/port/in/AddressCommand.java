package com.rcp.commerce.customer.application.port.in;

public record AddressCommand(
        String recipientName,
        String phoneNumber,
        String addressLine1,
        String addressLine2,
        String city,
        String state,
        String postalCode,
        String country,
        boolean defaultAddress) {}
