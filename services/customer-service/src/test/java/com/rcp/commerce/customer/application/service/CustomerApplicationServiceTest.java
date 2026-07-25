package com.rcp.commerce.customer.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.rcp.commerce.customer.application.port.in.AddressCommand;
import com.rcp.commerce.customer.application.port.in.AuthenticatedCustomer;
import com.rcp.commerce.customer.application.port.in.UpdateProfileCommand;
import com.rcp.commerce.customer.application.port.out.CustomerRepository;
import com.rcp.commerce.customer.domain.AddressId;
import com.rcp.commerce.customer.domain.CustomerId;
import com.rcp.commerce.customer.domain.CustomerProfile;
import com.rcp.commerce.customer.domain.ShippingAddress;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class CustomerApplicationServiceTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-07-25T00:00:00Z"), ZoneOffset.UTC);
    private static final AuthenticatedCustomer CUSTOMER =
            new AuthenticatedCustomer("subject-1", "customer@example.test", "Ada", "Lovelace");

    private final InMemoryCustomerRepository repository = new InMemoryCustomerRepository();
    private final CustomerApplicationService service = new CustomerApplicationService(repository, CLOCK);

    @Test
    void profileInitializationIsIdempotentAndCanBeUpdated() {
        CustomerProfile first = service.getOrCreateProfile(CUSTOMER);
        CustomerProfile second = service.getOrCreateProfile(CUSTOMER);

        assertThat(second.id()).isEqualTo(first.id());
        assertThat(repository.profiles).hasSize(1);

        CustomerProfile updated = service.updateProfile(CUSTOMER, new UpdateProfileCommand("Grace", "Hopper", "123"));
        assertThat(updated.firstName()).isEqualTo("Grace");
        assertThat(updated.phoneNumber()).isEqualTo("123");
    }

    @Test
    void missingNameClaimsUsePlaceholderUntilProfileUpdate() {
        CustomerProfile profile =
                service.getOrCreateProfile(new AuthenticatedCustomer("subject-2", "missing@example.test", "", null));

        assertThat(profile.firstName()).isEqualTo("MISSING");
        assertThat(profile.lastName()).isEqualTo("MISSING");
    }

    @Test
    void addressLifecycleAndDefaultReplacementWork() {
        CustomerProfile profile = service.getOrCreateProfile(CUSTOMER);
        ShippingAddress first = service.addAddress(CUSTOMER, address("Ada", true));
        ShippingAddress second = service.addAddress(CUSTOMER, address("Grace", true));

        assertThat(service.listAddresses(CUSTOMER)).hasSize(2);
        assertThat(service.getAddress(CUSTOMER, second.id()).defaultAddress()).isTrue();
        assertThat(repository.addresses.get(first.id()).defaultAddress()).isFalse();

        service.deleteAddress(CUSTOMER, second.id());
        assertThat(service.listAddresses(CUSTOMER)).hasSize(1);
        assertThat(repository.addresses.values().iterator().next().customerId()).isEqualTo(profile.id());
    }

    @Test
    void customerCannotAccessAnotherCustomersAddress() {
        service.getOrCreateProfile(CUSTOMER);
        ShippingAddress address = service.addAddress(CUSTOMER, address("Ada", false));
        AuthenticatedCustomer other = new AuthenticatedCustomer("subject-other", "other@example.test", "Other", "User");
        service.getOrCreateProfile(other);

        assertThatThrownBy(() -> service.getAddress(other, address.id())).isInstanceOf(AddressNotFoundException.class);
    }

    @Test
    void missingProfileOrAddressIsReported() {
        assertThatThrownBy(() -> service.listAddresses(CUSTOMER)).isInstanceOf(CustomerNotFoundException.class);

        service.getOrCreateProfile(CUSTOMER);
        assertThatThrownBy(() -> service.getAddress(CUSTOMER, AddressId.newId()))
                .isInstanceOf(AddressNotFoundException.class);
    }

    private static AddressCommand address(String name, boolean defaultAddress) {
        return new AddressCommand(name, "", "Line 1", "", "City", "State", "12345", "US", defaultAddress);
    }

    private static class InMemoryCustomerRepository implements CustomerRepository {

        private final Map<CustomerId, CustomerProfile> profiles = new LinkedHashMap<>();
        private final Map<AddressId, ShippingAddress> addresses = new LinkedHashMap<>();

        @Override
        public CustomerProfile saveProfile(CustomerProfile profile) {
            profiles.put(profile.id(), profile);
            return profile;
        }

        @Override
        public Optional<CustomerProfile> findProfileByIdentitySubject(String identitySubject) {
            return profiles.values().stream()
                    .filter(profile -> profile.identitySubject().equals(identitySubject))
                    .findFirst();
        }

        @Override
        public Optional<CustomerProfile> findProfileById(CustomerId customerId) {
            return Optional.ofNullable(profiles.get(customerId));
        }

        @Override
        public ShippingAddress saveAddress(ShippingAddress address) {
            addresses.put(address.id(), address);
            return address;
        }

        @Override
        public List<ShippingAddress> findAddressesByCustomerId(CustomerId customerId) {
            return addresses.values().stream()
                    .filter(address -> address.customerId().equals(customerId))
                    .toList();
        }

        @Override
        public Optional<ShippingAddress> findAddressByCustomerIdAndId(CustomerId customerId, AddressId addressId) {
            return Optional.ofNullable(addresses.get(addressId))
                    .filter(address -> address.customerId().equals(customerId));
        }

        @Override
        public void clearDefaultAddresses(CustomerId customerId, AddressId excludedAddressId) {
            findAddressesByCustomerId(customerId).stream()
                    .filter(ShippingAddress::defaultAddress)
                    .filter(address ->
                            excludedAddressId == null || !address.id().equals(excludedAddressId))
                    .map(address -> address.withDefaultAddress(false, CLOCK))
                    .forEach(address -> addresses.put(address.id(), address));
        }

        @Override
        public void deleteAddress(ShippingAddress address) {
            addresses.remove(address.id());
        }
    }
}
