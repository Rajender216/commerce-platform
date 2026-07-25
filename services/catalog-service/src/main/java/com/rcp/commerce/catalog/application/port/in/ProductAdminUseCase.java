package com.rcp.commerce.catalog.application.port.in;

import com.rcp.commerce.catalog.domain.Product;
import com.rcp.commerce.catalog.domain.ProductId;
import com.rcp.commerce.catalog.domain.ProductStatus;
import java.util.List;

public interface ProductAdminUseCase {

    Product createProduct(ProductCommand command);

    Product updateProduct(ProductId productId, ProductCommand command);

    Product getProduct(ProductId productId);

    List<Product> listProducts();

    Product changeProductStatus(ProductId productId, ProductStatus status);
}
