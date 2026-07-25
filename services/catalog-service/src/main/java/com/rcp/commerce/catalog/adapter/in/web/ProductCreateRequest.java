package com.rcp.commerce.catalog.adapter.in.web;

import com.rcp.commerce.catalog.domain.ProductDescription;
import com.rcp.commerce.catalog.domain.ProductName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

record ProductCreateRequest(
        @NotBlank(message = "Product name is required.")
                @Size(max = ProductName.MAX_LENGTH, message = "Product name must be at most 160 characters.")
                String name,
        @Size(max = ProductDescription.MAX_LENGTH, message = "Product description must be at most 2000 characters.")
                String description) {}
