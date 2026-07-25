package com.rcp.commerce.customer.application.service;

import com.rcp.commerce.customer.application.port.in.AddressCommand;
import com.rcp.commerce.customer.application.port.in.AuthenticatedCustomer;
import com.rcp.commerce.customer.application.port.in.CustomerProfileUseCase;
import com.rcp.commerce.customer.application.port.in.ShippingAddressUseCase;
import com.rcp.commerce.customer.application.port.in.UpdateProfileCommand;
import com.rcp.commerce.customer.application.port.out.CustomerRepository;
import com.rcp.commerce.customer.domain.AddressId;
import com.rcp.commerce.customer.domain.CustomerProfile;
import com.rcp.commerce.customer.domain.ShippingAddress;
import java.time.Clock;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerApplicationService implements CustomerProfileUseCase, ShippingAddressUseCase {

    private static final String MISSING_NAME_PLACEHOLDER = "MISSING";

    private final CustomerRepository customerRepository;
    private final Clock clock;

    public CustomerApplicationService(CustomerRepository customerRepository, Clock clock) {
        this.customerRepository = Objects.requireNonNull(customerRepository, "customerRepository must not be null");
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    @Override
    @Transactional
    public CustomerProfile getOrCreateProfile(AuthenticatedCustomer authenticatedCustomer) {
        return customerRepository
                .findProfileByIdentitySubject(requiredSubject(authenticatedCustomer))
                .orElseGet(() -> customerRepository.saveProfile(CustomerProfile.create(
                        authenticatedCustomer.subject(),
                        authenticatedCustomer.email(),
                        nameOrPlaceholder(authenticatedCustomer.firstName()),
                        nameOrPlaceholder(authenticatedCustomer.lastName()),
                        "",
                        clock)));
    }

    @Override
    @Transactional
    public CustomerProfile updateProfile(AuthenticatedCustomer authenticatedCustomer, UpdateProfileCommand command) {
        CustomerProfile profile = getExistingProfile(authenticatedCustomer);
        return customerRepository.saveProfile(
                profile.update(command.firstName(), command.lastName(), command.phoneNumber(), clock));
    }

    @Override
    @Transactional
    public ShippingAddress addAddress(AuthenticatedCustomer authenticatedCustomer, AddressCommand command) {
        CustomerProfile profile = getExistingProfile(authenticatedCustomer);
        ShippingAddress address = ShippingAddress.create(
                profile.id(),
                command.recipientName(),
                command.phoneNumber(),
                command.addressLine1(),
                command.addressLine2(),
                command.city(),
                command.state(),
                command.postalCode(),
                command.country(),
                command.defaultAddress(),
                clock);
        if (address.defaultAddress()) {
            customerRepository.clearDefaultAddresses(profile.id(), null);
        }
        return customerRepository.saveAddress(address);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShippingAddress> listAddresses(AuthenticatedCustomer authenticatedCustomer) {
        CustomerProfile profile = getExistingProfile(authenticatedCustomer);
        return customerRepository.findAddressesByCustomerId(profile.id());
    }

    @Override
    @Transactional(readOnly = true)
    public ShippingAddress getAddress(AuthenticatedCustomer authenticatedCustomer, AddressId addressId) {
        CustomerProfile profile = getExistingProfile(authenticatedCustomer);
        return customerRepository
                .findAddressByCustomerIdAndId(profile.id(), addressId)
                .orElseThrow(() -> new AddressNotFoundException(addressId));
    }

    @Override
    @Transactional
    public ShippingAddress updateAddress(
            AuthenticatedCustomer authenticatedCustomer, AddressId addressId, AddressCommand command) {
        ShippingAddress existing = getAddress(authenticatedCustomer, addressId);
        ShippingAddress updated = existing.update(
                command.recipientName(),
                command.phoneNumber(),
                command.addressLine1(),
                command.addressLine2(),
                command.city(),
                command.state(),
                command.postalCode(),
                command.country(),
                command.defaultAddress(),
                clock);
        if (updated.defaultAddress()) {
            customerRepository.clearDefaultAddresses(existing.customerId(), addressId);
        }
        return customerRepository.saveAddress(updated);
    }

    @Override
    @Transactional
    public void deleteAddress(AuthenticatedCustomer authenticatedCustomer, AddressId addressId) {
        customerRepository.deleteAddress(getAddress(authenticatedCustomer, addressId));
    }

    @Override
    @Transactional
    public ShippingAddress setDefaultAddress(AuthenticatedCustomer authenticatedCustomer, AddressId addressId) {
        ShippingAddress address = getAddress(authenticatedCustomer, addressId);
        customerRepository.clearDefaultAddresses(address.customerId(), addressId);
        return customerRepository.saveAddress(address.withDefaultAddress(true, clock));
    }

    private CustomerProfile getExistingProfile(AuthenticatedCustomer authenticatedCustomer) {
        return customerRepository
                .findProfileByIdentitySubject(requiredSubject(authenticatedCustomer))
                .orElseThrow(CustomerNotFoundException::new);
    }

    private static String requiredSubject(AuthenticatedCustomer authenticatedCustomer) {
        String subject = Objects.requireNonNull(authenticatedCustomer, "authenticatedCustomer must not be null")
                .subject();
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("Authenticated subject is required.");
        }
        return subject.trim();
    }

    private static String nameOrPlaceholder(String value) {
        return value == null || value.isBlank() ? MISSING_NAME_PLACEHOLDER : value;
    }
}
