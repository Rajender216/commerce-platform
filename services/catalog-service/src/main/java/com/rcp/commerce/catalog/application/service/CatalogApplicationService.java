package com.rcp.commerce.catalog.application.service;

import com.rcp.commerce.catalog.application.port.in.BrowseProductsQuery;
import com.rcp.commerce.catalog.application.port.in.CategoryAdminUseCase;
import com.rcp.commerce.catalog.application.port.in.CategoryCommand;
import com.rcp.commerce.catalog.application.port.in.PageResult;
import com.rcp.commerce.catalog.application.port.in.ProductAdminUseCase;
import com.rcp.commerce.catalog.application.port.in.ProductCatalogView;
import com.rcp.commerce.catalog.application.port.in.ProductCommand;
import com.rcp.commerce.catalog.application.port.in.ProductImageAdminUseCase;
import com.rcp.commerce.catalog.application.port.in.ProductImageCommand;
import com.rcp.commerce.catalog.application.port.in.ProductVariantAdminUseCase;
import com.rcp.commerce.catalog.application.port.in.PublicCatalogUseCase;
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
import java.time.Clock;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CatalogApplicationService
        implements CategoryAdminUseCase,
                ProductAdminUseCase,
                ProductVariantAdminUseCase,
                ProductImageAdminUseCase,
                PublicCatalogUseCase {

    public static final int MAX_PAGE_SIZE = 100;

    private final CatalogRepository catalogRepository;
    private final Clock clock;

    public CatalogApplicationService(CatalogRepository catalogRepository, Clock clock) {
        this.catalogRepository = Objects.requireNonNull(catalogRepository, "catalogRepository must not be null");
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    @Override
    @Transactional
    public Category createCategory(CategoryCommand command) {
        Category category =
                Category.create(command.name(), command.slug(), command.description(), command.active(), clock);
        rejectDuplicateCategorySlug(category.slug(), null);
        return catalogRepository.saveCategory(category);
    }

    @Override
    @Transactional
    public Category updateCategory(CategoryId categoryId, CategoryCommand command) {
        Category existing = getCategory(categoryId);
        Category updated =
                existing.update(command.name(), command.slug(), command.description(), command.active(), clock);
        rejectDuplicateCategorySlug(updated.slug(), categoryId);
        return catalogRepository.saveCategory(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> listCategories() {
        return catalogRepository.findAllCategories();
    }

    @Override
    @Transactional(readOnly = true)
    public Category getCategory(CategoryId categoryId) {
        return catalogRepository
                .findCategoryById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
    }

    @Override
    @Transactional
    public Product createProduct(ProductCommand command) {
        Category category = getCategory(command.categoryId());
        rejectDuplicateProductSlug(command.slug(), null);
        Product product = Product.createDraft(
                command.name(), command.slug(), command.description(), category.id(), command.brand(), clock);
        return catalogRepository.saveProduct(product);
    }

    @Override
    @Transactional
    public Product updateProduct(ProductId productId, ProductCommand command) {
        Product existing = getProduct(productId);
        Category category = getCategory(command.categoryId());
        rejectDuplicateProductSlug(command.slug(), productId);
        Product updated = existing.update(
                command.name(), command.slug(), command.description(), category.id(), command.brand(), clock);
        return catalogRepository.saveProduct(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProduct(ProductId productId) {
        return catalogRepository.findProductById(productId).orElseThrow(() -> new ProductNotFoundException(productId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> listProducts() {
        return catalogRepository.findAllProducts();
    }

    @Override
    @Transactional
    public Product changeProductStatus(ProductId productId, ProductStatus status) {
        Product product = getProduct(productId);
        Category category = getCategory(product.categoryId());
        boolean hasActiveVariant = catalogRepository.productHasActiveVariant(productId);
        try {
            return catalogRepository.saveProduct(
                    product.changeStatus(status, category.active(), hasActiveVariant, clock));
        } catch (IllegalStateException exception) {
            throw new InvalidProductStatusTransitionException(exception.getMessage());
        }
    }

    @Override
    @Transactional
    public ProductVariant addVariant(ProductId productId, VariantCommand command) {
        getProduct(productId);
        rejectDuplicateSku(command.sku(), null);
        return catalogRepository.saveVariant(ProductVariant.create(
                productId,
                command.sku(),
                command.name(),
                command.price(),
                command.stockQuantity(),
                command.active(),
                clock));
    }

    @Override
    @Transactional
    public ProductVariant updateVariant(ProductId productId, VariantId variantId, VariantCommand command) {
        ProductVariant existing = findVariant(productId, variantId);
        rejectDuplicateSku(command.sku(), variantId);
        return catalogRepository.saveVariant(existing.update(
                command.sku(), command.name(), command.price(), command.stockQuantity(), command.active(), clock));
    }

    @Override
    @Transactional
    public ProductVariant updateStock(ProductId productId, VariantId variantId, StockCommand command) {
        return catalogRepository.saveVariant(
                findVariant(productId, variantId).updateStock(command.stockQuantity(), clock));
    }

    @Override
    @Transactional
    public void deleteVariant(ProductId productId, VariantId variantId) {
        catalogRepository.deleteVariant(findVariant(productId, variantId));
    }

    @Override
    @Transactional
    public ProductImage addImage(ProductId productId, ProductImageCommand command) {
        getProduct(productId);
        ProductImage image = ProductImage.create(
                productId, command.url(), command.altText(), command.displayOrder(), command.primaryImage(), clock);
        if (image.primaryImage()) {
            catalogRepository.clearPrimaryImages(productId, null);
        }
        return catalogRepository.saveImage(image);
    }

    @Override
    @Transactional
    public ProductImage updateImage(ProductId productId, ProductImageId imageId, ProductImageCommand command) {
        ProductImage existing = findImage(productId, imageId);
        ProductImage updated =
                existing.update(command.url(), command.altText(), command.displayOrder(), command.primaryImage());
        if (updated.primaryImage()) {
            catalogRepository.clearPrimaryImages(productId, imageId);
        }
        return catalogRepository.saveImage(updated);
    }

    @Override
    @Transactional
    public void deleteImage(ProductId productId, ProductImageId imageId) {
        catalogRepository.deleteImage(findImage(productId, imageId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> listActiveCategories() {
        return catalogRepository.findActiveCategories();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<ProductCatalogView> browseProducts(BrowseProductsQuery query) {
        validatePagination(query);
        return catalogRepository.browsePublicProducts(query);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductCatalogView getPublicProduct(String productIdOrSlug) {
        return catalogRepository
                .findPublicProduct(productIdOrSlug)
                .orElseThrow(() -> new ProductNotFoundException(productIdOrSlug));
    }

    private ProductVariant findVariant(ProductId productId, VariantId variantId) {
        getProduct(productId);
        return catalogRepository
                .findVariantById(productId, variantId)
                .orElseThrow(() -> new VariantNotFoundException(variantId));
    }

    private ProductImage findImage(ProductId productId, ProductImageId imageId) {
        getProduct(productId);
        return catalogRepository
                .findImageById(productId, imageId)
                .orElseThrow(() -> new ProductImageNotFoundException(imageId));
    }

    private void rejectDuplicateCategorySlug(String slug, CategoryId excludedCategoryId) {
        if (catalogRepository.categorySlugExists(slug, excludedCategoryId)) {
            throw new DuplicateSlugException("Category", slug);
        }
    }

    private void rejectDuplicateProductSlug(String slug, ProductId excludedProductId) {
        if (catalogRepository.productSlugExists(slug, excludedProductId)) {
            throw new DuplicateSlugException("Product", slug);
        }
    }

    private void rejectDuplicateSku(String sku, VariantId excludedVariantId) {
        if (catalogRepository.skuExists(sku, excludedVariantId)) {
            throw new DuplicateSkuException(sku);
        }
    }

    private static void validatePagination(BrowseProductsQuery query) {
        if (query.page() < 0) {
            throw new InvalidPaginationException("Page must be greater than or equal to zero.");
        }
        if (query.size() < 1 || query.size() > MAX_PAGE_SIZE) {
            throw new InvalidPaginationException("Page size must be between 1 and " + MAX_PAGE_SIZE + ".");
        }
        if (!List.of("name", "newest", "priceAsc", "priceDesc").contains(query.sort())) {
            throw new InvalidPaginationException("Unsupported sort value.");
        }
    }
}
