package com.rcp.commerce.platform.gateway.configuration;

import com.rcp.commerce.platform.gateway.adapter.in.web.GatewayErrorCode;
import com.rcp.commerce.platform.gateway.adapter.in.web.ProblemResponseWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
class GatewaySecurityConfiguration {

    @Bean
    SecurityWebFilterChain gatewaySecurityFilterChain(
            ServerHttpSecurity http, ProblemResponseWriter problemResponseWriter) {
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(Customizer.withDefaults())
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.OPTIONS, "/**")
                        .permitAll()
                        .pathMatchers("/actuator/health/**", "/actuator/info")
                        .permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/v1/categories", "/api/v1/products", "/api/v1/products/**")
                        .permitAll()
                        .pathMatchers("/api/v1/admin/categories", "/api/v1/admin/categories/**")
                        .hasRole("ADMIN")
                        .pathMatchers("/api/v1/admin/products", "/api/v1/admin/products/**")
                        .hasRole("ADMIN")
                        .pathMatchers("/api/v1/customers", "/api/v1/customers/**")
                        .authenticated()
                        .anyExchange()
                        .permitAll())
                .oauth2ResourceServer(
                        oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((exchange, exception) -> problemResponseWriter.write(
                                exchange,
                                HttpStatus.UNAUTHORIZED,
                                GatewayErrorCode.AUTHENTICATION_REQUIRED,
                                "Authentication required",
                                "A valid bearer token is required for this route."))
                        .accessDeniedHandler((exchange, exception) -> problemResponseWriter.write(
                                exchange,
                                HttpStatus.FORBIDDEN,
                                GatewayErrorCode.ACCESS_DENIED,
                                "Access denied",
                                "The authenticated user is not allowed to access this route.")))
                .build();
    }

    @Bean
    Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        return jwt -> Mono.just(new JwtAuthenticationToken(jwt, extractAuthorities(jwt)));
    }

    private static Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        addRoles(jwt.getClaim("realm_access"), authorities);
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess != null) {
            resourceAccess.values().forEach(access -> addRoles(access, authorities));
        }
        return authorities;
    }

    @SuppressWarnings("unchecked")
    private static void addRoles(Object access, Set<GrantedAuthority> authorities) {
        if (!(access instanceof Map<?, ?> accessMap)) {
            return;
        }
        Object roles = accessMap.get("roles");
        if (!(roles instanceof Collection<?> roleValues)) {
            return;
        }
        roleValues.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .forEach(authorities::add);
    }
}
