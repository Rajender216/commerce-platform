package com.rcp.commerce.catalog.application.port.in;

import com.rcp.commerce.catalog.domain.ProductDescription;
import com.rcp.commerce.catalog.domain.ProductName;
import java.util.Objects;

public record CreateProductCommand(ProductName name, ProductDescription description) {

    public CreateProductCommand {
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(description, "description must not be null");
    }
}
