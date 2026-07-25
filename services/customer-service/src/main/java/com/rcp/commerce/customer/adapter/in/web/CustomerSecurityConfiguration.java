package com.rcp.commerce.customer.adapter.in.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
class CustomerSecurityConfiguration {

    @Bean
    SecurityFilterChain customerSecurityFilterChain(HttpSecurity http, Clock clock) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/actuator/health/**", "/actuator/info")
                        .permitAll()
                        .requestMatchers("/api/v1/customers/**")
                        .authenticated()
                        .anyRequest()
                        .authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {}))
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
