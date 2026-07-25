package com.rcp.commerce.customer.adapter.out.persistence;

import com.rcp.commerce.customer.application.port.out.CustomerRepository;
import com.rcp.commerce.customer.domain.AddressId;
import com.rcp.commerce.customer.domain.CustomerId;
import com.rcp.commerce.customer.domain.CustomerProfile;
import com.rcp.commerce.customer.domain.ShippingAddress;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
class CustomerPersistenceAdapter implements CustomerRepository {

    private final JpaCustomerProfileRepository profileRepository;
    private final JpaShippingAddressRepository addressRepository;
    private final CustomerPersistenceMapper mapper = new CustomerPersistenceMapper();

    CustomerPersistenceAdapter(
            JpaCustomerProfileRepository profileRepository, JpaShippingAddressRepository addressRepository) {
        this.profileRepository = profileRepository;
        this.addressRepository = addressRepository;
    }

    @Override
    public CustomerProfile saveProfile(CustomerProfile profile) {
        return mapper.toDomain(profileRepository.saveAndFlush(mapper.toEntity(profile)));
    }

    @Override
    public Optional<CustomerProfile> findProfileByIdentitySubject(String identitySubject) {
        return profileRepository.findByIdentitySubject(identitySubject).map(mapper::toDomain);
    }

    @Override
    public Optional<CustomerProfile> findProfileById(CustomerId customerId) {
        return profileRepository.findById(customerId.value()).map(mapper::toDomain);
    }

    @Override
    public ShippingAddress saveAddress(ShippingAddress address) {
        return mapper.toDomain(addressRepository.saveAndFlush(mapper.toEntity(address)));
    }

    @Override
    public List<ShippingAddress> findAddressesByCustomerId(CustomerId customerId) {
        return addressRepository.findByCustomerIdOrderByCreatedAtAsc(customerId.value()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<ShippingAddress> findAddressByCustomerIdAndId(CustomerId customerId, AddressId addressId) {
        return addressRepository
                .findByCustomerIdAndId(customerId.value(), addressId.value())
                .map(mapper::toDomain);
    }

    @Override
    public void clearDefaultAddresses(CustomerId customerId, AddressId excludedAddressId) {
        List<JpaShippingAddressEntity> addresses =
                addressRepository.findByCustomerIdOrderByCreatedAtAsc(customerId.value());
        addresses.stream()
                .filter(JpaShippingAddressEntity::isDefaultAddress)
                .filter(address -> excludedAddressId == null || !address.getId().equals(excludedAddressId.value()))
                .forEach(address -> address.setDefaultAddress(false));
        addressRepository.saveAllAndFlush(addresses);
    }

    @Override
    public void deleteAddress(ShippingAddress address) {
        addressRepository.deleteById(address.id().value());
        addressRepository.flush();
    }
}
