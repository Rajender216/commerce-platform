package com.rcp.commerce.catalog.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.rcp.commerce.catalog.application.port.in.BrowseProductsQuery;
import com.rcp.commerce.catalog.application.port.out.CatalogRepository;
import com.rcp.commerce.catalog.domain.Category;
import com.rcp.commerce.catalog.domain.Product;
import com.rcp.commerce.catalog.domain.ProductImage;
import com.rcp.commerce.catalog.domain.ProductStatus;
import com.rcp.commerce.catalog.domain.ProductVariant;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class CatalogPersistenceAdapterIT {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:17-alpine");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    private Flyway flyway;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CatalogRepository catalogRepository;

    private final Clock clock = Clock.fixed(Instant.parse("2026-07-22T00:00:00Z"), ZoneOffset.UTC);

    @Test
    void flywayMigrationSucceeds() {
        assertThat(flyway.info().current().getVersion().getVersion()).isEqualTo("2");
        assertThat(tableExists("categories")).isOne();
        assertThat(tableExists("products")).isOne();
        assertThat(tableExists("product_variants")).isOne();
        assertThat(tableExists("product_images")).isOne();
    }

    @Test
    void savesAndRetrievesCatalogAggregateParts() {
        Category category = catalogRepository.saveCategory(Category.create("Phones", "phones", "", true, clock));
        Product product =
                catalogRepository.saveProduct(Product.createDraft("Phone", "phone", "", category.id(), "Acme", clock));
        ProductVariant variant = catalogRepository.saveVariant(
                ProductVariant.create(product.id(), "SKU-1", "Default", new BigDecimal("10.99"), 5, true, clock));
        ProductImage image = catalogRepository.saveImage(
                ProductImage.create(product.id(), "https://example.test/phone.jpg", "Phone", 0, true, clock));

        assertThat(catalogRepository.findCategoryById(category.id())).contains(category);
        assertThat(catalogRepository.findProductById(product.id())).contains(product);
        assertThat(catalogRepository.findVariantById(product.id(), variant.id()))
                .contains(variant);
        assertThat(catalogRepository.findImageById(product.id(), image.id())).contains(image);
    }

    @Test
    void databaseConstraintsRejectDuplicateSlugsSkuAndInvalidRelationships() {
        Category category =
                catalogRepository.saveCategory(Category.create("Phones", "phones-constraints", "", true, clock));
        Product product = catalogRepository.saveProduct(
                Product.createDraft("Phone", "phone-constraints", "", category.id(), "Acme", clock));
        catalogRepository.saveVariant(
                ProductVariant.create(product.id(), "SKU-CONSTRAINTS", "Default", BigDecimal.TEN, 5, true, clock));

        assertThatThrownBy(() ->
                        catalogRepository.saveCategory(Category.create("Other", "phones-constraints", "", true, clock)))
                .isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> catalogRepository.saveProduct(
                        Product.createDraft("Other", "phone-constraints", "", category.id(), "Acme", clock)))
                .isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> catalogRepository.saveVariant(ProductVariant.create(
                        product.id(), "SKU-CONSTRAINTS", "Other", BigDecimal.TEN, 1, true, clock)))
                .isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> catalogRepository.saveProduct(Product.createDraft(
                        "Broken", "broken", "", com.rcp.commerce.catalog.domain.CategoryId.newId(), "", clock)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void optimisticLockingIsConfiguredForStockChanges() {
        Category category = catalogRepository.saveCategory(Category.create("Phones", "phones-lock", "", true, clock));
        Product product = catalogRepository.saveProduct(
                Product.createDraft("Phone Lock", "phone-lock", "", category.id(), "Acme", clock));
        ProductVariant variant = catalogRepository.saveVariant(
                ProductVariant.create(product.id(), "SKU-LOCK", "Default", BigDecimal.TEN, 5, true, clock));

        catalogRepository.saveVariant(variant.updateStock(4, clock));

        assertThatThrownBy(() -> catalogRepository.saveVariant(variant.updateStock(3, clock)))
                .isInstanceOf(OptimisticLockingFailureException.class);
    }

    @Test
    void publicFilteringPaginationPriceAndStockQueriesUsePostgresql() {
        Category phones = catalogRepository.saveCategory(Category.create("Phones", "phones-query", "", true, clock));
        Product active = catalogRepository.saveProduct(
                Product.createDraft("Query Phone", "query-phone", "", phones.id(), "Acme", clock)
                        .changeStatus(ProductStatus.ACTIVE, true, true, clock));
        catalogRepository.saveVariant(
                ProductVariant.create(active.id(), "SKU-Q1", "Default", new BigDecimal("15.00"), 3, true, clock));
        Product draft = catalogRepository.saveProduct(
                Product.createDraft("Query Draft", "query-draft", "", phones.id(), "Acme", clock));
        catalogRepository.saveVariant(
                ProductVariant.create(draft.id(), "SKU-Q2", "Default", new BigDecimal("15.00"), 3, true, clock));

        var page = catalogRepository.browsePublicProducts(new BrowseProductsQuery(
                0,
                20,
                "name",
                "query",
                "phones-query",
                "acme",
                new BigDecimal("10.00"),
                new BigDecimal("20.00"),
                true));

        assertThat(page.totalElements()).isEqualTo(1);
        assertThat(page.content()).hasSize(1);
        assertThat(page.content().getFirst().product().slug()).isEqualTo("query-phone");
        assertThat(catalogRepository.findPublicProduct("query-phone")).isPresent();
        assertThat(catalogRepository.findPublicProduct("query-draft")).isEmpty();
    }

    private Long tableExists(String tableName) {
        return jdbcTemplate.queryForObject(
                "select count(*) from information_schema.tables where table_name = ?", Long.class, tableName);
    }
}
