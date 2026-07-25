package com.rcp.commerce.catalog.application.port.in;

import com.rcp.commerce.catalog.domain.Category;
import java.util.List;

public interface PublicCatalogUseCase {

    List<Category> listActiveCategories();

    PageResult<ProductCatalogView> browseProducts(BrowseProductsQuery query);

    ProductCatalogView getPublicProduct(String productIdOrSlug);
}
