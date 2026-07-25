package com.rcp.commerce.customer.adapter.out.persistence;

import com.rcp.commerce.customer.domain.AddressId;
import com.rcp.commerce.customer.domain.CustomerId;
import com.rcp.commerce.customer.domain.CustomerProfile;
import com.rcp.commerce.customer.domain.ShippingAddress;

class CustomerPersistenceMapper {

    JpaCustomerProfileEntity toEntity(CustomerProfile profile) {
        return new JpaCustomerProfileEntity(
                profile.id().value(),
                profile.identitySubject(),
                profile.email(),
                profile.firstName(),
                profile.lastName(),
                profile.phoneNumber(),
                profile.createdAt(),
                profile.updatedAt(),
                profile.version());
    }

    CustomerProfile toDomain(JpaCustomerProfileEntity entity) {
        return new CustomerProfile(
                CustomerId.from(entity.getId()),
                entity.getIdentitySubject(),
                entity.getEmail(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getPhoneNumber(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion());
    }

    JpaShippingAddressEntity toEntity(ShippingAddress address) {
        return new JpaShippingAddressEntity(
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

    ShippingAddress toDomain(JpaShippingAddressEntity entity) {
        return new ShippingAddress(
                AddressId.from(entity.getId()),
                CustomerId.from(entity.getCustomerId()),
                entity.getRecipientName(),
                entity.getPhoneNumber(),
                entity.getAddressLine1(),
                entity.getAddressLine2(),
                entity.getCity(),
                entity.getState(),
                entity.getPostalCode(),
                entity.getCountry(),
                entity.isDefaultAddress(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion());
    }
}
