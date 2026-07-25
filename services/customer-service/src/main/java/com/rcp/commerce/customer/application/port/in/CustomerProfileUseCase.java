package com.rcp.commerce.customer.application.port.in;

import com.rcp.commerce.customer.domain.CustomerProfile;

public interface CustomerProfileUseCase {

    CustomerProfile getOrCreateProfile(AuthenticatedCustomer authenticatedCustomer);

    CustomerProfile updateProfile(AuthenticatedCustomer authenticatedCustomer, UpdateProfileCommand command);
}
