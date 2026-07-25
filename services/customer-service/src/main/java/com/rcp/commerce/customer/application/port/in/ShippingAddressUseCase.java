package com.rcp.commerce.customer.application.port.in;

import com.rcp.commerce.customer.domain.AddressId;
import com.rcp.commerce.customer.domain.ShippingAddress;
import java.util.List;

public interface ShippingAddressUseCase {

    ShippingAddress addAddress(AuthenticatedCustomer authenticatedCustomer, AddressCommand command);

    List<ShippingAddress> listAddresses(AuthenticatedCustomer authenticatedCustomer);

    ShippingAddress getAddress(AuthenticatedCustomer authenticatedCustomer, AddressId addressId);

    ShippingAddress updateAddress(
            AuthenticatedCustomer authenticatedCustomer, AddressId addressId, AddressCommand command);

    void deleteAddress(AuthenticatedCustomer authenticatedCustomer, AddressId addressId);

    ShippingAddress setDefaultAddress(AuthenticatedCustomer authenticatedCustomer, AddressId addressId);
}
