package com.rcp.commerce.customer.adapter.in.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.rcp.commerce.customer.application.port.in.CustomerProfileUseCase;
import com.rcp.commerce.customer.application.port.in.ShippingAddressUseCase;
import com.rcp.commerce.customer.configuration.CustomerServiceProperties;
import com.rcp.commerce.customer.domain.CustomerId;
import com.rcp.commerce.customer.domain.CustomerProfile;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CustomerController.class)
@AutoConfigureMockMvc
@ImportAutoConfiguration({
    SecurityAutoConfiguration.class,
    SecurityFilterAutoConfiguration.class,
    ServletWebSecurityAutoConfiguration.class,
    OAuth2ResourceServerAutoConfiguration.class
})
@Import({
    CustomerSecurityConfiguration.class,
    GlobalExceptionHandler.class,
    CorrelationIdFilter.class,
    CustomerSecurityTest.TestConfig.class
})
@EnableConfigurationProperties(CustomerServiceProperties.class)
class CustomerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerProfileUseCase profiles;

    @MockitoBean
    private ShippingAddressUseCase addresses;

    @Test
    void anonymousCustomerEndpointReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/customers/me")).andExpect(status().isUnauthorized());
    }

    @Test
    void customerTokenCanAccessProfile() throws Exception {
        when(profiles.getOrCreateProfile(any()))
                .thenReturn(new CustomerProfile(
                        CustomerId.newId(),
                        "subject-1",
                        "customer@example.test",
                        "Ada",
                        "Lovelace",
                        "",
                        Instant.parse("2026-07-25T00:00:00Z"),
                        Instant.parse("2026-07-25T00:00:00Z"),
                        0));

        mockMvc.perform(get("/api/v1/customers/me").with(jwt().jwt(jwt -> jwt.subject("subject-1")
                        .claim("email", "customer@example.test")
                        .claim("given_name", "Ada")
                        .claim("family_name", "Lovelace"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("customer@example.test"));
    }

    @Test
    void invalidAddressRequestReturnsValidationProblem() throws Exception {
        mockMvc.perform(
                        post("/api/v1/customers/me/addresses")
                                .with(jwt().jwt(jwt ->
                                        jwt.subject("subject-1").claim("email", "customer@example.test")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                {"recipientName":"","phoneNumber":"","addressLine1":"","addressLine2":"","city":"","state":"","postalCode":"","country":"","defaultAddress":false}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));
    }

    static class TestConfig {

        @Bean
        Clock utcClock() {
            return Clock.fixed(Instant.parse("2026-07-25T00:00:00Z"), ZoneOffset.UTC);
        }
    }
}
