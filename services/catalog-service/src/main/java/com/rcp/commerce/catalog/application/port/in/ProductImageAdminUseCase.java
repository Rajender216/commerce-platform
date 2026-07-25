package com.rcp.commerce.catalog.application.port.in;

import com.rcp.commerce.catalog.domain.ProductId;
import com.rcp.commerce.catalog.domain.ProductImage;
import com.rcp.commerce.catalog.domain.ProductImageId;

public interface ProductImageAdminUseCase {

    ProductImage addImage(ProductId productId, ProductImageCommand command);

    ProductImage updateImage(ProductId productId, ProductImageId imageId, ProductImageCommand command);

    void deleteImage(ProductId productId, ProductImageId imageId);
}
