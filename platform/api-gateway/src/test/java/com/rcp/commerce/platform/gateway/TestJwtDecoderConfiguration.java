package com.rcp.commerce.platform.gateway;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import reactor.core.publisher.Mono;

@TestConfiguration
class TestJwtDecoderConfiguration {

    @Bean
    @Primary
    ReactiveJwtDecoder reactiveJwtDecoder() {
        return token -> switch (token) {
            case "admin-token" -> Mono.just(jwt(token, List.of("ADMIN")));
            case "customer-token", "original-token" -> Mono.just(jwt(token, List.of("CUSTOMER")));
            default -> Mono.error(new JwtException("Invalid token"));
        };
    }

    private Jwt jwt(String token, List<String> roles) {
        Instant now = Instant.now();
        return Jwt.withTokenValue(token)
                .header("alg", "none")
                .subject("subject-" + token)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(300))
                .claim("realm_access", Map.of("roles", roles))
                .build();
    }
}
