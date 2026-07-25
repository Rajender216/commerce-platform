package com.rcp.commerce.catalog.application.port.in;

import com.rcp.commerce.catalog.domain.CategoryId;

public record ProductCommand(String name, String slug, String description, CategoryId categoryId, String brand) {}
