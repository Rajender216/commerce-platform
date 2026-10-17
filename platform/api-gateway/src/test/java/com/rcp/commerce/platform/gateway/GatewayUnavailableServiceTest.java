package com.rcp.commerce.platform.gateway;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
            "eureka.client.enabled=false",
            "spring.cloud.discovery.enabled=false",
            "commerce.gateway.routes.customer-uri=lb://missing-service",
            "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:1/jwks"
        })
@Import(TestJwtDecoderConfiguration.class)
class GatewayUnavailableServiceTest {

    @LocalServerPort
    private int port;

    @Test
    void unavailableDownstreamServiceReturnsSafeProblemResponse() {
        WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .responseTimeout(Duration.ofSeconds(15))
                .build()
                .get()
                .uri("/api/v1/customers/me")
                .header("Authorization", "Bearer customer-token")
                .exchange()
                .expectStatus()
                .is5xxServerError()
                .expectBody()
                .jsonPath("$.errorCode")
                .isEqualTo("SERVICE_UNAVAILABLE");
    }
}
