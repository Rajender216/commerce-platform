package com.rcp.commerce.customer.adapter.in.web;

import com.rcp.commerce.customer.application.port.in.UpdateProfileCommand;
import com.rcp.commerce.customer.domain.CustomerProfile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @NotBlank @Size(max = CustomerProfile.NAME_MAX_LENGTH) String firstName,
        @NotBlank @Size(max = CustomerProfile.NAME_MAX_LENGTH) String lastName,
        @Size(max = CustomerProfile.PHONE_MAX_LENGTH) String phoneNumber) {

    UpdateProfileCommand toCommand() {
        return new UpdateProfileCommand(firstName, lastName, phoneNumber);
    }
}
