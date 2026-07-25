package com.rcp.commerce.catalog.adapter.in.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
class CatalogSecurityConfiguration {

    @Bean
    SecurityFilterChain catalogSecurityFilterChain(HttpSecurity http, Clock clock) throws Exception {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(CatalogSecurityConfiguration::authoritiesFrom);

        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/actuator/health/**", "/actuator/info")
                        .permitAll()
                        .requestMatchers("/api/v1/categories", "/api/v1/products/**")
                        .permitAll()
                        .requestMatchers("/api/v1/admin/**")
                        .hasAuthority("ROLE_ADMIN")
                        .anyRequest()
                        .authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(converter)))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, exception) -> writeProblem(
                                clock,
                                request,
                                response,
                                HttpStatus.UNAUTHORIZED,
                                ErrorCode.UNAUTHORIZED,
                                "Unauthorized",
                                "Authentication is required."))
                        .accessDeniedHandler((request, response, exception) -> writeProblem(
                                clock,
                                request,
                                response,
                                HttpStatus.FORBIDDEN,
                                ErrorCode.FORBIDDEN,
                                "Forbidden",
                                "You do not have permission to access this resource.")));
        return http.build();
    }

    private static Collection<GrantedAuthority> authoritiesFrom(Jwt jwt) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        addRoles(authorities, realmAccess);
        Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
        if (resourceAccess != null) {
            resourceAccess.values().stream()
                    .filter(Map.class::isInstance)
                    .map(Map.class::cast)
                    .forEach(access -> addRoles(authorities, access));
        }
        return authorities;
    }

    private static void addRoles(List<GrantedAuthority> authorities, Map<?, ?> access) {
        if (access == null) {
            return;
        }
        Object roles = access.get("roles");
        if (roles instanceof Collection<?> roleValues) {
            roleValues.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                    .map(SimpleGrantedAuthority::new)
                    .forEach(authorities::add);
        }
    }

    private static void writeProblem(
            Clock clock,
            HttpServletRequest request,
            HttpServletResponse response,
            HttpStatus status,
            ErrorCode code,
            String title,
            String detail)
            throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter()
                .write(
                        """
                        {"type":"%s","title":"%s","status":%d,"detail":"%s","instance":"%s","timestamp":"%s","code":"%s","correlationId":"%s","validationErrors":[]}
                        """
                                .formatted(
                                        "https://api.rcp-commerce.local/problems/"
                                                + code.name().toLowerCase().replace('_', '-'),
                                        escape(title),
                                        status.value(),
                                        escape(detail),
                                        escape(request.getRequestURI()),
                                        Instant.now(clock),
                                        code.name(),
                                        escape(MDC.get(CorrelationIdFilter.MDC_KEY))));
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
