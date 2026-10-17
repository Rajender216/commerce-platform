package com.rcp.commerce.platform.gateway.adapter.in.web;

import java.time.Instant;

record ProblemResponse(
        String type,
        String title,
        int status,
        String detail,
        String instance,
        Instant timestamp,
        GatewayErrorCode errorCode,
        String correlationId) {}
