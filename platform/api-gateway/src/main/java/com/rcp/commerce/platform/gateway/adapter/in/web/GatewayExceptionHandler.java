package com.rcp.commerce.platform.gateway.adapter.in.web;

import java.net.ConnectException;
import java.util.concurrent.TimeoutException;
import org.springframework.boot.webflux.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Order(-2)
class GatewayExceptionHandler implements ErrorWebExceptionHandler {

    private final ProblemResponseWriter problemResponseWriter;

    GatewayExceptionHandler(ProblemResponseWriter problemResponseWriter) {
        this.problemResponseWriter = problemResponseWriter;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable exception) {
        if (isConnectionFailure(exception)) {
            return problemResponseWriter.write(
                    exchange,
                    HttpStatus.SERVICE_UNAVAILABLE,
                    GatewayErrorCode.SERVICE_UNAVAILABLE,
                    "Service unavailable",
                    "The requested downstream service is not available.");
        }
        if (isTimeout(exception)) {
            return problemResponseWriter.write(
                    exchange,
                    HttpStatus.GATEWAY_TIMEOUT,
                    GatewayErrorCode.GATEWAY_TIMEOUT,
                    "Gateway timeout",
                    "The requested downstream service did not respond in time.");
        }
        Throwable root = rootCause(exception);
        if (root instanceof JwtException) {
            return problemResponseWriter.write(
                    exchange,
                    HttpStatus.UNAUTHORIZED,
                    GatewayErrorCode.AUTHENTICATION_REQUIRED,
                    "Authentication required",
                    "A valid bearer token is required for this route.");
        }
        if (exception instanceof ResponseStatusException responseStatusException
                && responseStatusException.getStatusCode().value() == HttpStatus.SERVICE_UNAVAILABLE.value()) {
            return problemResponseWriter.write(
                    exchange,
                    HttpStatus.SERVICE_UNAVAILABLE,
                    GatewayErrorCode.SERVICE_UNAVAILABLE,
                    "Service unavailable",
                    "The requested downstream service is not available.");
        }
        if (exception instanceof ResponseStatusException responseStatusException
                && responseStatusException.getStatusCode().value() == HttpStatus.NOT_FOUND.value()) {
            return problemResponseWriter.write(
                    exchange,
                    HttpStatus.NOT_FOUND,
                    GatewayErrorCode.ROUTE_NOT_FOUND,
                    "Route not found",
                    "No gateway route matches this request.");
        }
        return problemResponseWriter.write(
                exchange,
                HttpStatus.INTERNAL_SERVER_ERROR,
                GatewayErrorCode.GATEWAY_REQUEST_REJECTED,
                "Gateway request rejected",
                "The gateway could not process this request.");
    }

    private boolean isConnectionFailure(Throwable exception) {
        Throwable current = exception;
        while (current != null) {
            String className = current.getClass().getName();
            String message = current.getMessage();
            if (current instanceof ConnectException
                    || className.contains("ConnectException")
                    || containsIgnoreCase(message, "connection refused")
                    || containsIgnoreCase(message, "finishconnect")) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private boolean isTimeout(Throwable exception) {
        Throwable current = exception;
        while (current != null) {
            String className = current.getClass().getName();
            String message = current.getMessage();
            if (current instanceof TimeoutException
                    || className.contains("TimeoutException")
                    || containsIgnoreCase(message, "timeout")) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private boolean containsIgnoreCase(String value, String pattern) {
        return value != null && value.toLowerCase().contains(pattern);
    }

    private Throwable rootCause(Throwable exception) {
        Throwable current = exception;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        return current;
    }
}
