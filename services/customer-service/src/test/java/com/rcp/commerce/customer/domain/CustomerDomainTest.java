package com.rcp.commerce.customer.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class CustomerDomainTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-07-25T00:00:00Z"), ZoneOffset.UTC);

    @Test
    void createsValidProfile() {
        CustomerProfile profile =
                CustomerProfile.create("subject-1", "USER@EXAMPLE.TEST", "Ada", "Lovelace", "", CLOCK);

        assertThat(profile.identitySubject()).isEqualTo("subject-1");
        assertThat(profile.email()).isEqualTo("user@example.test");
    }

    @Test
    void rejectsMissingProfileFields() {
        assertThatThrownBy(() -> CustomerProfile.create("", "user@example.test", "Ada", "Lovelace", "", CLOCK))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> CustomerProfile.create("subject", "not-email", "Ada", "Lovelace", "", CLOCK))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> CustomerProfile.create("subject", "user@example.test", "", "Lovelace", "", CLOCK))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createsValidAddressAndRejectsMissingRequiredFields() {
        CustomerId customerId = CustomerId.newId();
        ShippingAddress address = ShippingAddress.create(
                customerId, "Ada", "", "Line 1", "", "City", "State", "12345", "US", true, CLOCK);

        assertThat(address.defaultAddress()).isTrue();
        assertThatThrownBy(() -> ShippingAddress.create(
                        customerId, "", "", "Line 1", "", "City", "State", "12345", "US", true, CLOCK))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ShippingAddress.create(
                        customerId, "Ada", "", "Line 1", "", "City", "State", "", "US", true, CLOCK))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
