package com.rcp.commerce.catalog.adapter.in.web;

import java.math.BigDecimal;

public record PriceRangeResponse(BigDecimal min, BigDecimal max, String currency) {}
