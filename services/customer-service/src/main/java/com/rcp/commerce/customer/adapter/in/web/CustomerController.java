package com.rcp.commerce.customer.adapter.in.web;

import com.rcp.commerce.customer.application.port.in.AuthenticatedCustomer;
import com.rcp.commerce.customer.application.port.in.CustomerProfileUseCase;
import com.rcp.commerce.customer.application.port.in.ShippingAddressUseCase;
import com.rcp.commerce.customer.domain.AddressId;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/customers/me")
class CustomerController {

    private final CustomerProfileUseCase profiles;
    private final ShippingAddressUseCase addresses;

    CustomerController(CustomerProfileUseCase profiles, ShippingAddressUseCase addresses) {
        this.profiles = profiles;
        this.addresses = addresses;
    }

    @GetMapping
    CustomerProfileResponse me(JwtAuthenticationToken authentication) {
        return CustomerProfileResponse.from(profiles.getOrCreateProfile(authenticatedCustomer(authentication)));
    }

    @PutMapping
    CustomerProfileResponse updateProfile(
            JwtAuthenticationToken authentication, @Valid @RequestBody UpdateProfileRequest request) {
        return CustomerProfileResponse.from(
                profiles.updateProfile(authenticatedCustomer(authentication), request.toCommand()));
    }

    @PostMapping("/addresses")
    ResponseEntity<AddressResponse> addAddress(
            JwtAuthenticationToken authentication, @Valid @RequestBody AddressRequest request) {
        AddressResponse response =
                AddressResponse.from(addresses.addAddress(authenticatedCustomer(authentication), request.toCommand()));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{addressId}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/addresses")
    List<AddressResponse> listAddresses(JwtAuthenticationToken authentication) {
        return addresses.listAddresses(authenticatedCustomer(authentication)).stream()
                .map(AddressResponse::from)
                .toList();
    }

    @GetMapping("/addresses/{addressId}")
    AddressResponse getAddress(JwtAuthenticationToken authentication, @PathVariable UUID addressId) {
        return AddressResponse.from(
                addresses.getAddress(authenticatedCustomer(authentication), AddressId.from(addressId)));
    }

    @PutMapping("/addresses/{addressId}")
    AddressResponse updateAddress(
            JwtAuthenticationToken authentication,
            @PathVariable UUID addressId,
            @Valid @RequestBody AddressRequest request) {
        return AddressResponse.from(addresses.updateAddress(
                authenticatedCustomer(authentication), AddressId.from(addressId), request.toCommand()));
    }

    @DeleteMapping("/addresses/{addressId}")
    ResponseEntity<Void> deleteAddress(JwtAuthenticationToken authentication, @PathVariable UUID addressId) {
        addresses.deleteAddress(authenticatedCustomer(authentication), AddressId.from(addressId));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/addresses/{addressId}/default")
    AddressResponse setDefaultAddress(JwtAuthenticationToken authentication, @PathVariable UUID addressId) {
        return AddressResponse.from(
                addresses.setDefaultAddress(authenticatedCustomer(authentication), AddressId.from(addressId)));
    }

    private static AuthenticatedCustomer authenticatedCustomer(JwtAuthenticationToken authentication) {
        Jwt jwt = authentication.getToken();
        return new AuthenticatedCustomer(
                jwt.getSubject(),
                jwt.getClaimAsString("email"),
                firstPresent(jwt.getClaimAsString("given_name"), jwt.getClaimAsString("first_name")),
                firstPresent(jwt.getClaimAsString("family_name"), jwt.getClaimAsString("last_name")));
    }

    private static String firstPresent(String first, String second) {
        return first == null || first.isBlank() ? second : first;
    }
}
