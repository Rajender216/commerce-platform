package com.rcp.commerce.customer.domain;

import java.util.Objects;
import java.util.UUID;

public record AddressId(UUID value) {

    public AddressId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static AddressId newId() {
        return new AddressId(UUID.randomUUID());
    }

    public static AddressId from(UUID value) {
        return new AddressId(value);
    }
}
