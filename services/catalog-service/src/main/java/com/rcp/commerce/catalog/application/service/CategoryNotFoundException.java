package com.rcp.commerce.catalog.application.service;

import com.rcp.commerce.catalog.domain.CategoryId;

public class CategoryNotFoundException extends RuntimeException {

    private final CategoryId categoryId;

    public CategoryNotFoundException(CategoryId categoryId) {
        super("Category was not found.");
        this.categoryId = categoryId;
    }

    public CategoryId categoryId() {
        return categoryId;
    }
}
