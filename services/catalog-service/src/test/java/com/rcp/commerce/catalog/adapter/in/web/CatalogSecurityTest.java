package com.rcp.commerce.catalog.adapter.in.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.rcp.commerce.catalog.application.port.in.CategoryAdminUseCase;
import com.rcp.commerce.catalog.application.port.in.ProductAdminUseCase;
import com.rcp.commerce.catalog.application.port.in.PublicCatalogUseCase;
import com.rcp.commerce.catalog.configuration.CatalogServiceProperties;
import com.rcp.commerce.catalog.domain.Category;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({AdminCategoryController.class, PublicCatalogController.class})
@AutoConfigureMockMvc
@ImportAutoConfiguration({
    SecurityAutoConfiguration.class,
    SecurityFilterAutoConfiguration.class,
    ServletWebSecurityAutoConfiguration.class,
    OAuth2ResourceServerAutoConfiguration.class
})
@Import({
    CatalogSecurityConfiguration.class,
    GlobalExceptionHandler.class,
    CorrelationIdFilter.class,
    CatalogSecurityTest.TestConfig.class
})
@EnableConfigurationProperties(CatalogServiceProperties.class)
class CatalogSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryAdminUseCase categories;

    @MockitoBean
    private ProductAdminUseCase products;

    @MockitoBean
    private PublicCatalogUseCase publicCatalog;

    @Test
    void publicCatalogBrowsingRemainsAnonymous() throws Exception {
        when(publicCatalog.listActiveCategories()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/categories")).andExpect(status().isOk());
    }

    @Test
    void missingTokenOnAdminCatalogReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/v1/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryJson()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void customerTokenCannotAccessAdminCatalog() throws Exception {
        mockMvc.perform(post("/api/v1/admin/categories")
                        .with(jwt().jwt(jwt ->
                                jwt.subject("customer").claim("realm_access", Map.of("roles", List.of("CUSTOMER")))))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryJson()))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminTokenCanAccessAdminCatalog() throws Exception {
        when(categories.createCategory(any()))
                .thenReturn(Category.create("Phones", "phones", "", true, TestConfig.CLOCK));

        mockMvc.perform(post("/api/v1/admin/categories")
                        .with(jwt().jwt(jwt ->
                                        jwt.subject("admin").claim("realm_access", Map.of("roles", List.of("ADMIN"))))
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryJson()))
                .andExpect(status().isCreated());
    }

    private static String categoryJson() {
        return """
                {"name":"Phones","slug":"phones","description":"","active":true}
                """;
    }

    static class TestConfig {

        private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-07-25T00:00:00Z"), ZoneOffset.UTC);

        @Bean
        Clock utcClock() {
            return CLOCK;
        }
    }
}
