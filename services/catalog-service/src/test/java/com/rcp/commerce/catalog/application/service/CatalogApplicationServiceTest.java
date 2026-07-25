package com.rcp.commerce.catalog.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.rcp.commerce.catalog.application.port.in.BrowseProductsQuery;
import com.rcp.commerce.catalog.application.port.in.CategoryCommand;
import com.rcp.commerce.catalog.application.port.in.PageResult;
import com.rcp.commerce.catalog.application.port.in.ProductCatalogView;
import com.rcp.commerce.catalog.application.port.in.ProductCommand;
import com.rcp.commerce.catalog.application.port.in.StockCommand;
import com.rcp.commerce.catalog.application.port.in.VariantCommand;
import com.rcp.commerce.catalog.application.port.out.CatalogRepository;
import com.rcp.commerce.catalog.domain.Category;
import com.rcp.commerce.catalog.domain.CategoryId;
import com.rcp.commerce.catalog.domain.Product;
import com.rcp.commerce.catalog.domain.ProductId;
import com.rcp.commerce.catalog.domain.ProductImage;
import com.rcp.commerce.catalog.domain.ProductImageId;
import com.rcp.commerce.catalog.domain.ProductStatus;
import com.rcp.commerce.catalog.domain.ProductVariant;
import com.rcp.commerce.catalog.domain.VariantId;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class CatalogApplicationServiceTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-07-22T00:00:00Z"), ZoneOffset.UTC);

    private final InMemoryCatalogRepository repository = new InMemoryCatalogRepository();
    private final CatalogApplicationService service = new CatalogApplicationService(repository, CLOCK);

    @Test
    void createsProductForExistingCategory() {
        Category category = service.createCategory(new CategoryCommand("Phones", "phones", "", true));

        Product product = service.createProduct(new ProductCommand("Phone", "phone", "", category.id(), "Acme"));

        assertThat(product.status()).isEqualTo(ProductStatus.DRAFT);
        assertThat(product.categoryId()).isEqualTo(category.id());
        assertThat(repository.findProductById(product.id())).contains(product);
    }

    @Test
    void rejectsMissingCategoryAndProduct() {
        assertThatThrownBy(() ->
                        service.createProduct(new ProductCommand("Phone", "phone", "", CategoryId.newId(), "Acme")))
                .isInstanceOf(CategoryNotFoundException.class);
        assertThatThrownBy(() -> service.getProduct(ProductId.newId())).isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void rejectsDuplicateCategoryAndProductSlugs() {
        Category category = service.createCategory(new CategoryCommand("Phones", "phones", "", true));

        assertThatThrownBy(() -> service.createCategory(new CategoryCommand("Other", "phones", "", true)))
                .isInstanceOf(DuplicateSlugException.class);

        service.createProduct(new ProductCommand("Phone", "phone", "", category.id(), "Acme"));
        assertThatThrownBy(
                        () -> service.createProduct(new ProductCommand("Phone 2", "phone", "", category.id(), "Acme")))
                .isInstanceOf(DuplicateSlugException.class);
    }

    @Test
    void rejectsDuplicateSkuAndUpdatesStock() {
        Product product = createDraftProduct();
        ProductVariant variant =
                service.addVariant(product.id(), new VariantCommand("SKU-1", "Default", BigDecimal.TEN, 5, true));

        assertThatThrownBy(() -> service.addVariant(
                        product.id(), new VariantCommand("SKU-1", "Second", BigDecimal.TEN, 1, true)))
                .isInstanceOf(DuplicateSkuException.class);

        ProductVariant updated = service.updateStock(product.id(), variant.id(), new StockCommand(3));

        assertThat(updated.stockQuantity()).isEqualTo(3);
        assertThatThrownBy(() -> service.updateStock(product.id(), variant.id(), new StockCommand(-1)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void activationRequiresVariantAndActiveCategory() {
        Category inactive = service.createCategory(new CategoryCommand("Hidden", "hidden", "", false));
        Product inactiveCategoryProduct =
                service.createProduct(new ProductCommand("Hidden", "hidden-product", "", inactive.id(), "Acme"));

        assertThatThrownBy(() -> service.changeProductStatus(inactiveCategoryProduct.id(), ProductStatus.ACTIVE))
                .isInstanceOf(InvalidProductStatusTransitionException.class);

        Product product = createDraftProduct();
        assertThatThrownBy(() -> service.changeProductStatus(product.id(), ProductStatus.ACTIVE))
                .isInstanceOf(InvalidProductStatusTransitionException.class);

        service.addVariant(product.id(), new VariantCommand("SKU-2", "Default", BigDecimal.TEN, 1, true));
        assertThat(service.changeProductStatus(product.id(), ProductStatus.ACTIVE)
                        .status())
                .isEqualTo(ProductStatus.ACTIVE);
    }

    private Product createDraftProduct() {
        Category category = service.createCategory(
                new CategoryCommand("Phones", "phones-" + repository.categories.size(), "", true));
        return service.createProduct(
                new ProductCommand("Phone", "phone-" + repository.products.size(), "", category.id(), "Acme"));
    }

    private static class InMemoryCatalogRepository implements CatalogRepository {

        private final Map<CategoryId, Category> categories = new LinkedHashMap<>();
        private final Map<ProductId, Product> products = new LinkedHashMap<>();
        private final Map<VariantId, ProductVariant> variants = new LinkedHashMap<>();
        private final Map<ProductImageId, ProductImage> images = new LinkedHashMap<>();

        @Override
        public Category saveCategory(Category category) {
            categories.put(category.id(), category);
            return category;
        }

        @Override
        public Optional<Category> findCategoryById(CategoryId categoryId) {
            return Optional.ofNullable(categories.get(categoryId));
        }

        @Override
        public List<Category> findAllCategories() {
            return List.copyOf(categories.values());
        }

        @Override
        public List<Category> findActiveCategories() {
            return categories.values().stream().filter(Category::active).toList();
        }

        @Override
        public boolean categorySlugExists(String slug, CategoryId excludedCategoryId) {
            return categories.values().stream()
                    .anyMatch(category ->
                            category.slug().equals(slug) && !category.id().equals(excludedCategoryId));
        }

        @Override
        public Product saveProduct(Product product) {
            products.put(product.id(), product);
            return product;
        }

        @Override
        public Optional<Product> findProductById(ProductId productId) {
            return Optional.ofNullable(products.get(productId));
        }

        @Override
        public List<Product> findAllProducts() {
            return List.copyOf(products.values());
        }

        @Override
        public boolean productSlugExists(String slug, ProductId excludedProductId) {
            return products.values().stream()
                    .anyMatch(product ->
                            product.slug().equals(slug) && !product.id().equals(excludedProductId));
        }

        @Override
        public ProductVariant saveVariant(ProductVariant variant) {
            variants.put(variant.id(), variant);
            return variant;
        }

        @Override
        public Optional<ProductVariant> findVariantById(ProductId productId, VariantId variantId) {
            return Optional.ofNullable(variants.get(variantId))
                    .filter(variant -> variant.productId().equals(productId));
        }

        @Override
        public boolean skuExists(String sku, VariantId excludedVariantId) {
            return variants.values().stream()
                    .anyMatch(variant ->
                            variant.sku().equals(sku) && !variant.id().equals(excludedVariantId));
        }

        @Override
        public boolean productHasActiveVariant(ProductId productId) {
            return variants.values().stream()
                    .anyMatch(variant -> variant.productId().equals(productId) && variant.active());
        }

        @Override
        public void deleteVariant(ProductVariant variant) {
            variants.remove(variant.id());
        }

        @Override
        public ProductImage saveImage(ProductImage image) {
            images.put(image.id(), image);
            return image;
        }

        @Override
        public Optional<ProductImage> findImageById(ProductId productId, ProductImageId imageId) {
            return Optional.ofNullable(images.get(imageId))
                    .filter(image -> image.productId().equals(productId));
        }

        @Override
        public void clearPrimaryImages(ProductId productId, ProductImageId excludedImageId) {}

        @Override
        public void deleteImage(ProductImage image) {
            images.remove(image.id());
        }

        @Override
        public ProductCatalogView getProductView(ProductId productId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<ProductCatalogView> findPublicProduct(String productIdOrSlug) {
            return Optional.empty();
        }

        @Override
        public PageResult<ProductCatalogView> browsePublicProducts(BrowseProductsQuery query) {
            return new PageResult<>(List.of(), query.page(), query.size(), 0, 0, true, true);
        }
    }
}
