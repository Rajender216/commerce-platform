package com.rcp.commerce.platform.gateway;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Mono;

@SpringBootTest(
        properties = {
            "eureka.client.enabled=false",
            "spring.cloud.discovery.enabled=false",
            "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:1/jwks"
        })
class GatewayRoleMappingTest {

    @Autowired
    private Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter;

    @Test
    void mapsKeycloakRealmAndClientRolesToSpringAuthorities() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject("subject-1")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300))
                .claim("realm_access", Map.of("roles", List.of("CUSTOMER")))
                .claim("resource_access", Map.of("commerce-api", Map.of("roles", List.of("ADMIN"))))
                .build();

        AbstractAuthenticationToken authentication =
                jwtAuthenticationConverter.convert(jwt).block();

        assertThat(authentication.getAuthorities()).extracting("authority").contains("ROLE_CUSTOMER", "ROLE_ADMIN");
    }
}
