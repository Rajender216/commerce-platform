package com.rcp.commerce.customer.application.port.out;

import com.rcp.commerce.customer.domain.AddressId;
import com.rcp.commerce.customer.domain.CustomerId;
import com.rcp.commerce.customer.domain.CustomerProfile;
import com.rcp.commerce.customer.domain.ShippingAddress;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository {

    CustomerProfile saveProfile(CustomerProfile profile);

    Optional<CustomerProfile> findProfileByIdentitySubject(String identitySubject);

    Optional<CustomerProfile> findProfileById(CustomerId customerId);

    ShippingAddress saveAddress(ShippingAddress address);

    List<ShippingAddress> findAddressesByCustomerId(CustomerId customerId);

    Optional<ShippingAddress> findAddressByCustomerIdAndId(CustomerId customerId, AddressId addressId);

    void clearDefaultAddresses(CustomerId customerId, AddressId excludedAddressId);

    void deleteAddress(ShippingAddress address);
}
