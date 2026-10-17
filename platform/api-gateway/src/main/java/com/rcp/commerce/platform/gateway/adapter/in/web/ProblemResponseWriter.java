package com.rcp.commerce.platform.gateway.adapter.in.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rcp.commerce.platform.gateway.configuration.GatewayProperties;
import java.net.URI;
import java.time.Instant;
import org.slf4j.MDC;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class ProblemResponseWriter {

    private final ObjectMapper objectMapper;
    private final GatewayProperties properties;

    public ProblemResponseWriter(ObjectMapper objectMapper, GatewayProperties properties) {
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    public Mono<Void> write(
            ServerWebExchange exchange, HttpStatus status, GatewayErrorCode errorCode, String title, String detail) {
        ServerHttpResponse response = exchange.getResponse();
        if (response.isCommitted()) {
            return Mono.empty();
        }
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        String correlationId =
                response.getHeaders().getFirst(properties.getCorrelation().getHeaderName());
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = MDC.get(properties.getCorrelation().getHeaderName());
        }
        ProblemResponse body = new ProblemResponse(
                "about:blank",
                title,
                status.value(),
                detail,
                URI.create(exchange.getRequest().getURI().getRawPath()).toString(),
                Instant.now(),
                errorCode,
                correlationId);
        byte[] bytes = serialize(body);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }

    private byte[] serialize(ProblemResponse body) {
        try {
            return objectMapper.writeValueAsBytes(body);
        } catch (JsonProcessingException exception) {
            return "{}".getBytes();
        }
    }
}
