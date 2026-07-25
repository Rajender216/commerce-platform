package com.rcp.commerce.customer.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.rcp.commerce.customer.application.port.out.CustomerRepository;
import com.rcp.commerce.customer.domain.CustomerProfile;
import com.rcp.commerce.customer.domain.ShippingAddress;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class CustomerPersistenceAdapterIT {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:17-alpine");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    private Flyway flyway;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CustomerRepository customerRepository;

    private final Clock clock = Clock.fixed(Instant.parse("2026-07-25T00:00:00Z"), ZoneOffset.UTC);

    @Test
    void flywayMigrationSucceeds() {
        assertThat(flyway.info().current().getVersion().getVersion()).isEqualTo("1");
        assertThat(tableExists("customer_profiles")).isOne();
        assertThat(tableExists("shipping_addresses")).isOne();
    }

    @Test
    void profileAndAddressCanBeSavedAndRetrievedByOwner() {
        CustomerProfile profile = customerRepository.saveProfile(
                CustomerProfile.create("subject-1", "user@example.test", "Ada", "Lovelace", "", clock));
        ShippingAddress address = customerRepository.saveAddress(address(profile, "Ada", true));

        assertThat(customerRepository.findProfileByIdentitySubject("subject-1")).contains(profile);
        assertThat(customerRepository.findAddressesByCustomerId(profile.id())).contains(address);
        assertThat(customerRepository.findAddressByCustomerIdAndId(profile.id(), address.id()))
                .contains(address);
    }

    @Test
    void databaseRejectsDuplicateIdentitySubjectAndDuplicateDefaultAddress() {
        CustomerProfile profile = customerRepository.saveProfile(
                CustomerProfile.create("subject-unique", "unique@example.test", "Ada", "Lovelace", "", clock));
        customerRepository.saveAddress(address(profile, "Ada", true));

        assertThatThrownBy(() -> customerRepository.saveProfile(
                        CustomerProfile.create("subject-unique", "other@example.test", "Grace", "Hopper", "", clock)))
                .isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> customerRepository.saveAddress(address(profile, "Grace", true)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void clearingDefaultAddressLeavesOnlyOneDefault() {
        CustomerProfile profile = customerRepository.saveProfile(
                CustomerProfile.create("subject-default", "default@example.test", "Ada", "Lovelace", "", clock));
        ShippingAddress first = customerRepository.saveAddress(address(profile, "Ada", true));
        customerRepository.clearDefaultAddresses(profile.id(), null);
        ShippingAddress second = customerRepository.saveAddress(address(profile, "Grace", true));

        assertThat(customerRepository
                        .findAddressByCustomerIdAndId(profile.id(), first.id())
                        .orElseThrow()
                        .defaultAddress())
                .isFalse();
        assertThat(customerRepository
                        .findAddressByCustomerIdAndId(profile.id(), second.id())
                        .orElseThrow()
                        .defaultAddress())
                .isTrue();
    }

    @Test
    void optimisticLockingIsConfiguredForProfiles() {
        CustomerProfile profile = customerRepository.saveProfile(
                CustomerProfile.create("subject-lock", "lock@example.test", "Ada", "Lovelace", "", clock));

        customerRepository.saveProfile(profile.update("Grace", "Hopper", "", clock));

        assertThatThrownBy(() -> customerRepository.saveProfile(profile.update("Katherine", "Johnson", "", clock)))
                .isInstanceOf(OptimisticLockingFailureException.class);
    }

    private ShippingAddress address(CustomerProfile profile, String recipient, boolean defaultAddress) {
        return ShippingAddress.create(
                profile.id(), recipient, "", "Line 1", "", "City", "State", "12345", "US", defaultAddress, clock);
    }

    private Long tableExists(String tableName) {
        return jdbcTemplate.queryForObject(
                "select count(*) from information_schema.tables where table_name = ?", Long.class, tableName);
    }
}
