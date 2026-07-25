package com.rcp.commerce.catalog.application.port.out;

import com.rcp.commerce.catalog.application.port.in.BrowseProductsQuery;
import com.rcp.commerce.catalog.application.port.in.PageResult;
import com.rcp.commerce.catalog.application.port.in.ProductCatalogView;
import com.rcp.commerce.catalog.domain.Category;
import com.rcp.commerce.catalog.domain.CategoryId;
import com.rcp.commerce.catalog.domain.Product;
import com.rcp.commerce.catalog.domain.ProductId;
import com.rcp.commerce.catalog.domain.ProductImage;
import com.rcp.commerce.catalog.domain.ProductImageId;
import com.rcp.commerce.catalog.domain.ProductVariant;
import com.rcp.commerce.catalog.domain.VariantId;
import java.util.List;
import java.util.Optional;

public interface CatalogRepository {

    Category saveCategory(Category category);

    Optional<Category> findCategoryById(CategoryId categoryId);

    List<Category> findAllCategories();

    List<Category> findActiveCategories();

    boolean categorySlugExists(String slug, CategoryId excludedCategoryId);

    Product saveProduct(Product product);

    Optional<Product> findProductById(ProductId productId);

    List<Product> findAllProducts();

    boolean productSlugExists(String slug, ProductId excludedProductId);

    ProductVariant saveVariant(ProductVariant variant);

    Optional<ProductVariant> findVariantById(ProductId productId, VariantId variantId);

    boolean skuExists(String sku, VariantId excludedVariantId);

    boolean productHasActiveVariant(ProductId productId);

    void deleteVariant(ProductVariant variant);

    ProductImage saveImage(ProductImage image);

    Optional<ProductImage> findImageById(ProductId productId, ProductImageId imageId);

    void clearPrimaryImages(ProductId productId, ProductImageId excludedImageId);

    void deleteImage(ProductImage image);

    ProductCatalogView getProductView(ProductId productId);

    Optional<ProductCatalogView> findPublicProduct(String productIdOrSlug);

    PageResult<ProductCatalogView> browsePublicProducts(BrowseProductsQuery query);
}
