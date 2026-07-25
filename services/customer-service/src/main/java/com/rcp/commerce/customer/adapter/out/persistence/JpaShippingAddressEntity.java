package com.rcp.commerce.customer.adapter.out.persistence;

import com.rcp.commerce.customer.domain.ShippingAddress;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "shipping_addresses")
class JpaShippingAddressEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "recipient_name", nullable = false, length = ShippingAddress.NAME_MAX_LENGTH)
    private String recipientName;

    @Column(name = "phone_number", nullable = false, length = ShippingAddress.PHONE_MAX_LENGTH)
    private String phoneNumber;

    @Column(name = "address_line1", nullable = false, length = ShippingAddress.LINE_MAX_LENGTH)
    private String addressLine1;

    @Column(name = "address_line2", nullable = false, length = ShippingAddress.LINE_MAX_LENGTH)
    private String addressLine2;

    @Column(name = "city", nullable = false, length = ShippingAddress.CITY_MAX_LENGTH)
    private String city;

    @Column(name = "state", nullable = false, length = ShippingAddress.STATE_MAX_LENGTH)
    private String state;

    @Column(name = "postal_code", nullable = false, length = ShippingAddress.POSTAL_CODE_MAX_LENGTH)
    private String postalCode;

    @Column(name = "country", nullable = false, length = ShippingAddress.COUNTRY_MAX_LENGTH)
    private String country;

    @Column(name = "default_address", nullable = false)
    private boolean defaultAddress;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    protected JpaShippingAddressEntity() {}

    JpaShippingAddressEntity(
            UUID id,
            UUID customerId,
            String recipientName,
            String phoneNumber,
            String addressLine1,
            String addressLine2,
            String city,
            String state,
            String postalCode,
            String country,
            boolean defaultAddress,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        this.id = id;
        this.customerId = customerId;
        this.recipientName = recipientName;
        this.phoneNumber = phoneNumber;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.country = country;
        this.defaultAddress = defaultAddress;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    UUID getId() {
        return id;
    }

    UUID getCustomerId() {
        return customerId;
    }

    String getRecipientName() {
        return recipientName;
    }

    String getPhoneNumber() {
        return phoneNumber;
    }

    String getAddressLine1() {
        return addressLine1;
    }

    String getAddressLine2() {
        return addressLine2;
    }

    String getCity() {
        return city;
    }

    String getState() {
        return state;
    }

    String getPostalCode() {
        return postalCode;
    }

    String getCountry() {
        return country;
    }

    boolean isDefaultAddress() {
        return defaultAddress;
    }

    Instant getCreatedAt() {
        return createdAt;
    }

    Instant getUpdatedAt() {
        return updatedAt;
    }

    long getVersion() {
        return version;
    }

    void setDefaultAddress(boolean defaultAddress) {
        this.defaultAddress = defaultAddress;
    }
}
