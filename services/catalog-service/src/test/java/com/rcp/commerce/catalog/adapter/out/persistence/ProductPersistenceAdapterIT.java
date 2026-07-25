package com.rcp.commerce.catalog.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.rcp.commerce.catalog.application.port.out.ProductRepository;
import com.rcp.commerce.catalog.domain.Product;
import com.rcp.commerce.catalog.domain.ProductDescription;
import com.rcp.commerce.catalog.domain.ProductName;
import jakarta.persistence.EntityManager;
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
class ProductPersistenceAdapterIT {

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
    private ProductRepository productRepository;

    @Autowired
    private JpaProductRepository jpaProductRepository;

    @Autowired
    private EntityManager entityManager;

    private final Clock clock = Clock.fixed(Instant.parse("2026-07-22T00:00:00Z"), ZoneOffset.UTC);

    @Test
    void flywayMigrationSucceeds() {
        assertThat(flyway.info().current().getVersion().getVersion()).isEqualTo("1");
        assertThat(jdbcTemplate.queryForObject(
                        "select count(*) from information_schema.tables where table_name = 'products'", Long.class))
                .isOne();
    }

    @Test
    void productCanBeSavedAndRetrieved() {
        Product product = Product.createDraft(new ProductName("Draft"), new ProductDescription("Description"), clock);

        Product saved = productRepository.save(product);

        assertThat(productRepository.findById(saved.id())).contains(saved);
        assertThat(saved.name().value()).isEqualTo("Draft");
        assertThat(saved.description().value()).isEqualTo("Description");
    }

    @Test
    void domainJpaMappingPreservesProductFields() {
        Product product =
                Product.createDraft(new ProductName("  Draft  "), new ProductDescription("  Description  "), clock);

        Product saved = productRepository.save(product);
        JpaProductEntity entity =
                jpaProductRepository.findById(saved.id().value()).orElseThrow();

        assertThat(entity.getId()).isEqualTo(saved.id().value());
        assertThat(entity.getName()).isEqualTo("Draft");
        assertThat(entity.getDescription()).isEqualTo("Description");
        assertThat(entity.getStatus()).isEqualTo(saved.status());
        assertThat(entity.getCreatedAt()).isEqualTo(saved.createdAt());
        assertThat(entity.getUpdatedAt()).isEqualTo(saved.updatedAt());
        assertThat(entity.getVersion()).isEqualTo(saved.version());
    }

    @Test
    void optimisticLockingIsConfigured() {
        Product product = productRepository.save(
                Product.createDraft(new ProductName("Draft"), ProductDescription.empty(), clock));

        JpaProductEntity first =
                jpaProductRepository.findById(product.id().value()).orElseThrow();
        entityManager.detach(first);
        JpaProductEntity second =
                jpaProductRepository.findById(product.id().value()).orElseThrow();
        entityManager.detach(second);

        first.setName("First update");
        jpaProductRepository.saveAndFlush(first);

        second.setName("Second update");
        assertThatThrownBy(() -> jpaProductRepository.saveAndFlush(second))
                .isInstanceOf(OptimisticLockingFailureException.class);
    }

    @Test
    void databaseRejectsBlankProductName() {
        assertThatThrownBy(
                        () -> jdbcTemplate.update(
                                """
                        INSERT INTO products (id, name, description, status, created_at, updated_at, version)
                        VALUES (gen_random_uuid(), '   ', '', 'DRAFT', now(), now(), 0)
                        """))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
