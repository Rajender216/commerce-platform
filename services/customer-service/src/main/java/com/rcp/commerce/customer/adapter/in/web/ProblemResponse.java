package com.rcp.commerce.customer.adapter.in.web;

import java.time.Instant;
import java.util.List;

public record ProblemResponse(
        String type,
        String title,
        int status,
        String detail,
        String instance,
        Instant timestamp,
        String code,
        String correlationId,
        List<ValidationError> validationErrors) {}
