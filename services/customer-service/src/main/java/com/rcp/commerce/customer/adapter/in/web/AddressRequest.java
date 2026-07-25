package com.rcp.commerce.customer.adapter.in.web;

import com.rcp.commerce.customer.application.port.in.AddressCommand;
import com.rcp.commerce.customer.domain.ShippingAddress;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressRequest(
        @NotBlank @Size(max = ShippingAddress.NAME_MAX_LENGTH) String recipientName,
        @Size(max = ShippingAddress.PHONE_MAX_LENGTH) String phoneNumber,
        @NotBlank @Size(max = ShippingAddress.LINE_MAX_LENGTH) String addressLine1,
        @Size(max = ShippingAddress.LINE_MAX_LENGTH) String addressLine2,
        @NotBlank @Size(max = ShippingAddress.CITY_MAX_LENGTH) String city,
        @NotBlank @Size(max = ShippingAddress.STATE_MAX_LENGTH) String state,
        @NotBlank @Size(max = ShippingAddress.POSTAL_CODE_MAX_LENGTH) String postalCode,
        @NotBlank @Size(max = ShippingAddress.COUNTRY_MAX_LENGTH) String country,
        boolean defaultAddress) {

    AddressCommand toCommand() {
        return new AddressCommand(
                recipientName,
                phoneNumber,
                addressLine1,
                addressLine2,
                city,
                state,
                postalCode,
                country,
                defaultAddress);
    }
}
