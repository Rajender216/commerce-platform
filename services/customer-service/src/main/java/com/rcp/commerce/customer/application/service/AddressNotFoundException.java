package com.rcp.commerce.customer.application.service;

import com.rcp.commerce.customer.domain.AddressId;

public class AddressNotFoundException extends RuntimeException {

    private final AddressId addressId;

    public AddressNotFoundException(AddressId addressId) {
        super("Shipping address was not found.");
        this.addressId = addressId;
    }

    public AddressId addressId() {
        return addressId;
    }
}
