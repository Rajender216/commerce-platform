package com.rcp.commerce.catalog.application.service;

public class DuplicateSlugException extends RuntimeException {

    private final String owner;
    private final String slug;

    public DuplicateSlugException(String owner, String slug) {
        super(owner + " slug already exists.");
        this.owner = owner;
        this.slug = slug;
    }

    public String owner() {
        return owner;
    }

    public String slug() {
        return slug;
    }
}
