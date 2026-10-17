package com.rcp.commerce.platform.gateway;

import static org.assertj.core.api.Assertions.assertThat;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
            "eureka.client.enabled=false",
            "spring.cloud.discovery.enabled=false",
            "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:1/jwks"
        })
@Import(TestJwtDecoderConfiguration.class)
class ApiGatewayRouteSecurityTest {

    private static final String CORRELATION_HEADER = "X-Correlation-ID";
    private static final AtomicReference<String> LAST_AUTHORIZATION = new AtomicReference<>();
    private static final AtomicReference<String> LAST_CORRELATION_ID = new AtomicReference<>();
    private static final ExecutorService DOWNSTREAM_EXECUTOR = Executors.newSingleThreadExecutor();
    private static final HttpServer DOWNSTREAM = startDownstream();

    @LocalServerPort
    private int port;

    @DynamicPropertySource
    static void gatewayProperties(DynamicPropertyRegistry registry) {
        String downstreamUri = "http://localhost:" + DOWNSTREAM.getAddress().getPort();
        registry.add("commerce.gateway.routes.catalog-uri", () -> downstreamUri);
        registry.add("commerce.gateway.routes.customer-uri", () -> downstreamUri);
    }

    @AfterAll
    static void stopDownstream() {
        DOWNSTREAM.stop(0);
        DOWNSTREAM_EXECUTOR.shutdownNow();
    }

    @Test
    void publicCatalogRouteIsAllowedAnonymouslyAndGetsCorrelationId() {
        webTestClient()
                .get()
                .uri("/api/v1/categories")
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .exists(CORRELATION_HEADER);
    }

    @Test
    void protectedCustomerRouteRequiresAuthentication() {
        webTestClient()
                .get()
                .uri("/api/v1/customers/me")
                .exchange()
                .expectStatus()
                .isUnauthorized()
                .expectBody()
                .jsonPath("$.errorCode")
                .isEqualTo("AUTHENTICATION_REQUIRED");
    }

    @Test
    void invalidTokenIsRejectedOnProtectedRoute() {
        webTestClient()
                .get()
                .uri("/api/v1/customers/me")
                .header("Authorization", "Bearer invalid-token")
                .exchange()
                .expectStatus()
                .isUnauthorized()
                .expectBody()
                .jsonPath("$.errorCode")
                .isEqualTo("AUTHENTICATION_REQUIRED");
    }

    @Test
    void customerRoleCannotAccessCatalogAdminRoute() {
        webTestClient()
                .post()
                .uri("/api/v1/admin/categories")
                .header("Authorization", "Bearer customer-token")
                .exchange()
                .expectStatus()
                .isForbidden()
                .expectBody()
                .jsonPath("$.errorCode")
                .isEqualTo("ACCESS_DENIED");
    }

    @Test
    void adminRoleCanAccessCatalogAdminRoute() {
        webTestClient()
                .post()
                .uri("/api/v1/admin/categories")
                .header("Authorization", "Bearer admin-token")
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void bearerTokenAndCorrelationIdAreForwardedDownstream() {
        String correlationId = "test-correlation-123";

        webTestClient()
                .get()
                .uri("/api/v1/customers/me")
                .header("Authorization", "Bearer original-token")
                .header(CORRELATION_HEADER, correlationId)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .values(CORRELATION_HEADER, values -> assertThat(values).containsExactly(correlationId));

        assertThat(LAST_AUTHORIZATION.get()).isEqualTo("Bearer original-token");
        assertThat(LAST_CORRELATION_ID.get()).isEqualTo(correlationId);
    }

    @Test
    void allowedCorsPreflightReturnsCorsHeaders() {
        webTestClient()
                .options()
                .uri("/api/v1/products")
                .header("Origin", "http://localhost:4200")
                .header("Access-Control-Request-Method", "GET")
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .valueEquals("Access-Control-Allow-Origin", "http://localhost:4200");
    }

    @Test
    void disallowedCorsOriginDoesNotReceivePermissiveHeaders() {
        webTestClient()
                .options()
                .uri("/api/v1/products")
                .header("Origin", "http://malicious.local")
                .header("Access-Control-Request-Method", "GET")
                .exchange()
                .expectHeader()
                .doesNotExist("Access-Control-Allow-Origin");
    }

    @Test
    void unknownRouteReturnsStandardNotFoundProblem() {
        webTestClient()
                .get()
                .uri("/api/v1/unknown")
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath("$.errorCode")
                .isEqualTo("ROUTE_NOT_FOUND");
    }

    @Test
    void downstreamNotFoundPassesThrough() {
        webTestClient()
                .get()
                .uri("/api/v1/products/missing")
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody(String.class)
                .value(body -> assertThat(body).contains("downstream-not-found"));
    }

    private static HttpServer startDownstream() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
            server.createContext("/", ApiGatewayRouteSecurityTest::handleDownstream);
            server.setExecutor(DOWNSTREAM_EXECUTOR);
            server.start();
            return server;
        } catch (IOException exception) {
            throw new IllegalStateException("Could not start test downstream server", exception);
        }
    }

    private WebTestClient webTestClient() {
        return WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .responseTimeout(Duration.ofSeconds(15))
                .build();
    }

    private static void handleDownstream(HttpExchange exchange) throws IOException {
        LAST_AUTHORIZATION.set(exchange.getRequestHeaders().getFirst("Authorization"));
        LAST_CORRELATION_ID.set(exchange.getRequestHeaders().getFirst(CORRELATION_HEADER));
        String path = exchange.getRequestURI().getPath();
        int status = path.equals("/api/v1/products/missing") ? 404 : 200;
        String body = status == 404 ? "{\"message\":\"downstream-not-found\"}" : "{\"ok\":true}";
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set(CORRELATION_HEADER, "downstream-correlation");
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }
}
