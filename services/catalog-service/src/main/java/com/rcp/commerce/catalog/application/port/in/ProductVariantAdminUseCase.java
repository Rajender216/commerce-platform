package com.rcp.commerce.catalog.application.port.in;

import com.rcp.commerce.catalog.domain.ProductId;
import com.rcp.commerce.catalog.domain.ProductVariant;
import com.rcp.commerce.catalog.domain.VariantId;

public interface ProductVariantAdminUseCase {

    ProductVariant addVariant(ProductId productId, VariantCommand command);

    ProductVariant updateVariant(ProductId productId, VariantId variantId, VariantCommand command);

    ProductVariant updateStock(ProductId productId, VariantId variantId, StockCommand command);

    void deleteVariant(ProductId productId, VariantId variantId);
}
