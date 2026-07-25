package com.rcp.commerce.catalog.adapter.in.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
record ProblemResponse(
        String type,
        String title,
        int status,
        String detail,
        String instance,
        Instant timestamp,
        String code,
        String correlationId,
        List<ValidationError> validationErrors) {}
