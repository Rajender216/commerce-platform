package com.rcp.commerce.customer.adapter.out.persistence;

import com.rcp.commerce.customer.domain.CustomerProfile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "customer_profiles")
class JpaCustomerProfileEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "identity_subject", nullable = false, length = CustomerProfile.SUBJECT_MAX_LENGTH)
    private String identitySubject;

    @Column(name = "email", nullable = false, length = CustomerProfile.EMAIL_MAX_LENGTH)
    private String email;

    @Column(name = "first_name", nullable = false, length = CustomerProfile.NAME_MAX_LENGTH)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = CustomerProfile.NAME_MAX_LENGTH)
    private String lastName;

    @Column(name = "phone_number", nullable = false, length = CustomerProfile.PHONE_MAX_LENGTH)
    private String phoneNumber;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    protected JpaCustomerProfileEntity() {}

    JpaCustomerProfileEntity(
            UUID id,
            String identitySubject,
            String email,
            String firstName,
            String lastName,
            String phoneNumber,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        this.id = id;
        this.identitySubject = identitySubject;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    UUID getId() {
        return id;
    }

    String getIdentitySubject() {
        return identitySubject;
    }

    String getEmail() {
        return email;
    }

    String getFirstName() {
        return firstName;
    }

    String getLastName() {
        return lastName;
    }

    String getPhoneNumber() {
        return phoneNumber;
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
}
