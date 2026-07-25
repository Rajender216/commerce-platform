package com.rcp.commerce.catalog.application.port.in;

import com.rcp.commerce.catalog.application.service.ProductNotFoundException;
import com.rcp.commerce.catalog.domain.Product;
import com.rcp.commerce.catalog.domain.ProductId;

public interface GetProductUseCase {

    Product getProduct(ProductId productId) throws ProductNotFoundException;
}
