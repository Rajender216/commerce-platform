package com.rcp.commerce.catalog.adapter.in.web;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.rcp.commerce.catalog.application.port.in.BrowseProductsQuery;
import com.rcp.commerce.catalog.application.port.in.PageResult;
import com.rcp.commerce.catalog.application.port.in.ProductCatalogView;
import com.rcp.commerce.catalog.application.port.out.CatalogRepository;
import com.rcp.commerce.catalog.application.service.CatalogApplicationService;
import com.rcp.commerce.catalog.configuration.CatalogServiceProperties;
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
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

class CatalogControllerTest {

    private static final String CORRELATION_HEADER = "X-Correlation-ID";
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-07-22T00:00:00Z"), ZoneOffset.UTC);

    private MockMvc mockMvc;
    private TestCatalogRepository repository;

    @BeforeEach
    void setUp() {
        repository = new TestCatalogRepository();
        CatalogApplicationService service = new CatalogApplicationService(repository, CLOCK);
        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler(CLOCK);
        CorrelationIdFilter filter = new CorrelationIdFilter(
                new CatalogServiceProperties(new CatalogServiceProperties.Correlation(CORRELATION_HEADER, 128)));

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(
                        new AdminCategoryController(service),
                        new AdminProductController(service),
                        new AdminProductVariantController(service),
                        new AdminProductImageController(service),
                        new PublicCatalogController(service))
                .setControllerAdvice(exceptionHandler)
                .addFilters(filter)
                .setValidator(validator)
                .setMessageConverters(new JacksonJsonHttpMessageConverter())
                .build();
    }

    @Test
    void validCategoryCreationReturnsCreated() throws Exception {
        mockMvc.perform(post("/api/v1/admin/categories")
                        .header(CORRELATION_HEADER, "catalog-test-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryJson("Phones", "phones", true)))
                .andExpect(status().isCreated())
                .andExpect(header().string(CORRELATION_HEADER, "catalog-test-1"))
                .andExpect(header().string(
                                "Location",
                                endsWith("/api/v1/admin/categories/"
                                        + repository.lastCategory.id().value())))
                .andExpect(jsonPath("$.name").value("Phones"));
    }

    @Test
    void invalidCategoryReturnsValidationProblem() throws Exception {
        mockMvc.perform(post("/api/v1/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryJson(" ", "phones", true)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.validationErrors[0].field").value("name"));
    }

    @Test
    void productVariantAndActivationFlowWorks() throws Exception {
        Category category = repository.saveCategory(Category.create("Phones", "phones", "", true, CLOCK));

        mockMvc.perform(post("/api/v1/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson("Phone", "phone", category.id())))
                .andExpect(status().isCreated())
                .andExpect(header().string(
                                "Location",
                                endsWith("/api/v1/admin/products/"
                                        + repository.lastProduct.id().value())))
                .andExpect(jsonPath("$.status").value("DRAFT"));

        Product product = repository.lastProduct;
        mockMvc.perform(post(
                                "/api/v1/admin/products/{productId}/variants",
                                product.id().value())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(variantJson("SKU-1", "Default", "10.99", 5, true)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.currency").value("USD"));

        mockMvc.perform(patch(
                                "/api/v1/admin/products/{productId}/status",
                                product.id().value())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"ACTIVE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void invalidProductVariantAndActivationFailuresReturnSafeProblems() throws Exception {
        Category category = repository.saveCategory(Category.create("Phones", "phones", "", true, CLOCK));
        Product product =
                repository.saveProduct(Product.createDraft("Phone", "phone", "", category.id(), "Acme", CLOCK));

        mockMvc.perform(post("/api/v1/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson(" ", "bad", category.id())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));

        mockMvc.perform(post(
                                "/api/v1/admin/products/{productId}/variants",
                                product.id().value())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(variantJson("SKU-1", "Default", "0", -1, true)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));

        mockMvc.perform(patch(
                                "/api/v1/admin/products/{productId}/status",
                                product.id().value())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"ACTIVE\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("INVALID_PRODUCT_STATUS_TRANSITION"));
    }

    @Test
    void publicListingFiltersAndPaginatesActiveProductsOnly() throws Exception {
        Category phones = repository.saveCategory(Category.create("Phones", "phones", "", true, CLOCK));
        Product active = repository.saveProduct(Product.createDraft("Phone", "phone", "", phones.id(), "Acme", CLOCK)
                .changeStatus(ProductStatus.ACTIVE, true, true, CLOCK));
        repository.saveVariant(
                ProductVariant.create(active.id(), "SKU-1", "Default", new BigDecimal("10.00"), 5, true, CLOCK));
        Product draft = repository.saveProduct(Product.createDraft("Draft", "draft", "", phones.id(), "Acme", CLOCK));
        repository.saveVariant(
                ProductVariant.create(draft.id(), "SKU-2", "Default", new BigDecimal("10.00"), 5, true, CLOCK));

        mockMvc.perform(get("/api/v1/products")
                        .param("search", "pho")
                        .param("brand", "acme")
                        .param("category", "phones")
                        .param("minPrice", "5")
                        .param("maxPrice", "20")
                        .param("inStockOnly", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].slug").value("phone"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    void publicMissingDuplicateAndCorrelationErrorsAreStandard() throws Exception {
        Category category = repository.saveCategory(Category.create("Phones", "phones", "", true, CLOCK));
        repository.saveProduct(Product.createDraft("Phone", "phone", "", category.id(), "Acme", CLOCK));

        mockMvc.perform(get("/api/v1/products/missing").header(CORRELATION_HEADER, "lookup-123"))
                .andExpect(status().isNotFound())
                .andExpect(header().string(CORRELATION_HEADER, "lookup-123"))
                .andExpect(jsonPath("$.code").value("PRODUCT_NOT_FOUND"));

        mockMvc.perform(post("/api/v1/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson("Other", "phone", category.id())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_PRODUCT_SLUG"));

        mockMvc.perform(get("/api/v1/products").header(CORRELATION_HEADER, "x".repeat(129)))
                .andExpect(status().isOk())
                .andExpect(header().string(CORRELATION_HEADER, matchesPattern("[0-9a-fA-F-]{36}")));
    }

    private static String categoryJson(String name, String slug, boolean active) {
        return """
                {"name":"%s","slug":"%s","description":"","active":%s}
                """
                .formatted(name, slug, active);
    }

    private static String productJson(String name, String slug, CategoryId categoryId) {
        return """
                {"name":"%s","slug":"%s","description":"","categoryId":"%s","brand":"Acme"}
                """
                .formatted(name, slug, categoryId.value());
    }

    private static String variantJson(String sku, String name, String price, int stock, boolean active) {
        return """
                {"sku":"%s","name":"%s","price":%s,"stockQuantity":%d,"active":%s}
                """
                .formatted(sku, name, price, stock, active);
    }

    private static class TestCatalogRepository implements CatalogRepository {

        private final Map<CategoryId, Category> categories = new LinkedHashMap<>();
        private final Map<ProductId, Product> products = new LinkedHashMap<>();
        private final Map<VariantId, ProductVariant> variants = new LinkedHashMap<>();
        private final Map<ProductImageId, ProductImage> images = new LinkedHashMap<>();
        private Category lastCategory;
        private Product lastProduct;

        @Override
        public Category saveCategory(Category category) {
            categories.put(category.id(), category);
            lastCategory = category;
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
            lastProduct = product;
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
            return buildView(products.get(productId), false).orElseThrow();
        }

        @Override
        public Optional<ProductCatalogView> findPublicProduct(String productIdOrSlug) {
            return products.values().stream()
                    .filter(product -> product.id().value().toString().equals(productIdOrSlug)
                            || product.slug().equals(productIdOrSlug))
                    .findFirst()
                    .flatMap(product -> buildView(product, true))
                    .filter(view -> !view.variants().isEmpty());
        }

        @Override
        public PageResult<ProductCatalogView> browsePublicProducts(BrowseProductsQuery query) {
            List<ProductCatalogView> filtered = products.values().stream()
                    .sorted(Comparator.comparing(Product::name))
                    .map(product -> buildView(product, true))
                    .flatMap(Optional::stream)
                    .filter(view -> !view.variants().isEmpty())
                    .filter(view -> query.search() == null
                            || view.product()
                                    .name()
                                    .toLowerCase()
                                    .contains(query.search().toLowerCase()))
                    .filter(view -> query.category() == null
                            || view.category().slug().equals(query.category())
                            || view.category().id().value().toString().equals(query.category()))
                    .filter(view ->
                            query.brand() == null || view.product().brand().equalsIgnoreCase(query.brand()))
                    .filter(view -> query.minPrice() == null || view.maxPrice().compareTo(query.minPrice()) >= 0)
                    .filter(view -> query.maxPrice() == null || view.minPrice().compareTo(query.maxPrice()) <= 0)
                    .filter(view -> !query.inStockOnly() || view.inStock())
                    .toList();
            return new PageResult<>(
                    filtered, query.page(), query.size(), filtered.size(), filtered.isEmpty() ? 0 : 1, true, true);
        }

        private Optional<ProductCatalogView> buildView(Product product, boolean publicOnly) {
            Category category = categories.get(product.categoryId());
            if (product == null || category == null) {
                return Optional.empty();
            }
            if (publicOnly && (product.status() != ProductStatus.ACTIVE || !category.active())) {
                return Optional.empty();
            }
            List<ProductVariant> viewVariants = variants.values().stream()
                    .filter(variant -> variant.productId().equals(product.id()))
                    .filter(variant -> !publicOnly || variant.active())
                    .toList();
            BigDecimal minPrice = viewVariants.stream()
                    .map(ProductVariant::price)
                    .min(BigDecimal::compareTo)
                    .orElse(null);
            BigDecimal maxPrice = viewVariants.stream()
                    .map(ProductVariant::price)
                    .max(BigDecimal::compareTo)
                    .orElse(null);
            boolean inStock = viewVariants.stream().anyMatch(variant -> variant.stockQuantity() > 0);
            return Optional.of(new ProductCatalogView(
                    product, category, viewVariants, List.copyOf(images.values()), minPrice, maxPrice, inStock));
        }
    }
}
