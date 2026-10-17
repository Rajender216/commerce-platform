package com.rcp.commerce.platform.gateway.adapter.in.web;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
class GatewayErrorHandlingFilter implements GlobalFilter, Ordered {

    private final ProblemResponseWriter problemResponseWriter;

    GatewayErrorHandlingFilter(ProblemResponseWriter problemResponseWriter) {
        this.problemResponseWriter = problemResponseWriter;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).then(Mono.defer(() -> {
            boolean routeMatched = exchange.getAttribute(GATEWAY_ROUTE_ATTR) != null;
            HttpStatus status =
                    HttpStatus.resolve(exchange.getResponse().getStatusCode().value());
            if (!routeMatched
                    && status == HttpStatus.NOT_FOUND
                    && !exchange.getResponse().isCommitted()) {
                return problemResponseWriter.write(
                        exchange,
                        HttpStatus.NOT_FOUND,
                        GatewayErrorCode.ROUTE_NOT_FOUND,
                        "Route not found",
                        "No gateway route matches this request.");
            }
            if (routeMatched
                    && status == HttpStatus.SERVICE_UNAVAILABLE
                    && !exchange.getResponse().isCommitted()) {
                return problemResponseWriter.write(
                        exchange,
                        HttpStatus.SERVICE_UNAVAILABLE,
                        GatewayErrorCode.SERVICE_UNAVAILABLE,
                        "Service unavailable",
                        "The requested downstream service is not available.");
            }
            return Mono.empty();
        }));
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }
}
