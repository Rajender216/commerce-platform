package com.rcp.commerce.customer.domain;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public record ShippingAddress(
        AddressId id,
        CustomerId customerId,
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

    public static final int NAME_MAX_LENGTH = 160;
    public static final int PHONE_MAX_LENGTH = 40;
    public static final int LINE_MAX_LENGTH = 255;
    public static final int CITY_MAX_LENGTH = 120;
    public static final int STATE_MAX_LENGTH = 120;
    public static final int POSTAL_CODE_MAX_LENGTH = 32;
    public static final int COUNTRY_MAX_LENGTH = 80;

    public ShippingAddress {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(customerId, "customerId must not be null");
        recipientName = required(recipientName, "Recipient name is required.");
        phoneNumber = optional(phoneNumber);
        addressLine1 = required(addressLine1, "Address line 1 is required.");
        addressLine2 = optional(addressLine2);
        city = required(city, "City is required.");
        state = required(state, "State is required.");
        postalCode = required(postalCode, "Postal code is required.");
        country = required(country, "Country is required.");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");
        max(recipientName, NAME_MAX_LENGTH, "Recipient name");
        max(phoneNumber, PHONE_MAX_LENGTH, "Phone number");
        max(addressLine1, LINE_MAX_LENGTH, "Address line 1");
        max(addressLine2, LINE_MAX_LENGTH, "Address line 2");
        max(city, CITY_MAX_LENGTH, "City");
        max(state, STATE_MAX_LENGTH, "State");
        max(postalCode, POSTAL_CODE_MAX_LENGTH, "Postal code");
        max(country, COUNTRY_MAX_LENGTH, "Country");
        if (version < 0) {
            throw new IllegalArgumentException("version must not be negative.");
        }
    }

    public static ShippingAddress create(
            CustomerId customerId,
            String recipientName,
            String phoneNumber,
            String addressLine1,
            String addressLine2,
            String city,
            String state,
            String postalCode,
            String country,
            boolean defaultAddress,
            Clock clock) {
        Instant now = Instant.now(clock);
        return new ShippingAddress(
                AddressId.newId(),
                customerId,
                recipientName,
                phoneNumber,
                addressLine1,
                addressLine2,
                city,
                state,
                postalCode,
                country,
                defaultAddress,
                now,
                now,
                0);
    }

    public ShippingAddress update(
            String recipientName,
            String phoneNumber,
            String addressLine1,
            String addressLine2,
            String city,
            String state,
            String postalCode,
            String country,
            boolean defaultAddress,
            Clock clock) {
        return new ShippingAddress(
                id,
                customerId,
                recipientName,
                phoneNumber,
                addressLine1,
                addressLine2,
                city,
                state,
                postalCode,
                country,
                defaultAddress,
                createdAt,
                Instant.now(clock),
                version);
    }

    public ShippingAddress withDefaultAddress(boolean defaultAddress, Clock clock) {
        return new ShippingAddress(
                id,
                customerId,
                recipientName,
                phoneNumber,
                addressLine1,
                addressLine2,
                city,
                state,
                postalCode,
                country,
                defaultAddress,
                createdAt,
                Instant.now(clock),
                version);
    }

    private static String required(String value, String message) {
        String normalized = optional(value);
        if (normalized.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return normalized;
    }

    private static String optional(String value) {
        return Objects.requireNonNullElse(value, "").trim();
    }

    private static void max(String value, int maxLength, String field) {
        if (value.length() > maxLength) {
            throw new IllegalArgumentException(field + " must be at most " + maxLength + " characters.");
        }
    }
}
