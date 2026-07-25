package com.rcp.commerce.catalog.adapter.in.web;

import com.rcp.commerce.catalog.domain.ProductStatus;
import jakarta.validation.constraints.NotNull;

public record ProductStatusRequest(@NotNull ProductStatus status) {}
