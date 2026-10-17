package com.rcp.commerce.platform.gateway.adapter.in.web;

import com.rcp.commerce.platform.gateway.configuration.GatewayProperties;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class CorrelationIdFilter implements WebFilter {

    private final GatewayProperties properties;

    CorrelationIdFilter(GatewayProperties properties) {
        this.properties = properties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String headerName = properties.getCorrelation().getHeaderName();
        String correlationId = normalize(exchange.getRequest().getHeaders().getFirst(headerName));
        MDC.put(headerName, correlationId);
        ServerHttpRequest request = exchange.getRequest()
                .mutate()
                .headers(headers -> headers.set(headerName, correlationId))
                .build();
        ServerWebExchange mutated = exchange.mutate().request(request).build();
        mutated.getResponse().beforeCommit(() -> {
            mutated.getResponse().getHeaders().set(headerName, correlationId);
            return Mono.empty();
        });
        return chain.filter(mutated).doFinally(signalType -> MDC.remove(headerName));
    }

    private String normalize(String incoming) {
        int maxLength = properties.getCorrelation().getMaxLength();
        if (incoming == null || incoming.isBlank() || incoming.length() > maxLength) {
            return UUID.randomUUID().toString();
        }
        String trimmed = incoming.trim();
        if (!trimmed.matches("[A-Za-z0-9._:-]+")) {
            return UUID.randomUUID().toString();
        }
        return trimmed;
    }
}
