package com.rcp.commerce.catalog.application.port.in;

import com.rcp.commerce.catalog.domain.Product;

public interface CreateProductUseCase {

    Product createProduct(CreateProductCommand command);
}
