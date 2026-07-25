package com.rcp.commerce.catalog.application.port.in;

public record ProductImageCommand(String url, String altText, int displayOrder, boolean primaryImage) {}
