package com.rcp.commerce.catalog.application.port.in;

public record CategoryCommand(String name, String slug, String description, boolean active) {}
