package com.rcp.commerce.catalog.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.rcp.commerce.catalog.application.port.in.CreateProductCommand;
import com.rcp.commerce.catalog.application.port.out.ProductRepository;
import com.rcp.commerce.catalog.domain.Product;
import com.rcp.commerce.catalog.domain.ProductDescription;
import com.rcp.commerce.catalog.domain.ProductId;
import com.rcp.commerce.catalog.domain.ProductName;
import com.rcp.commerce.catalog.domain.ProductStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ProductApplicationServiceTest {

    private final InMemoryProductRepository repository = new InMemoryProductRepository();
    private final ProductApplicationService service = new ProductApplicationService(
            repository, Clock.fixed(Instant.parse("2026-07-22T00:00:00Z"), ZoneOffset.UTC));

    @Test
    void createsDraftProduct() {
        Product product = service.createProduct(new CreateProductCommand(
                new ProductName("Draft product"), new ProductDescription("Draft description")));

        assertThat(product.id()).isNotNull();
        assertThat(product.status()).isEqualTo(ProductStatus.DRAFT);
        assertThat(product.name().value()).isEqualTo("Draft product");
        assertThat(product.description().value()).isEqualTo("Draft description");
        assertThat(repository.findById(product.id())).contains(product);
    }

    @Test
    void getsExistingProduct() {
        Product product =
                service.createProduct(new CreateProductCommand(new ProductName("Draft"), ProductDescription.empty()));

        Product found = service.getProduct(product.id());

        assertThat(found).isEqualTo(product);
    }

    @Test
    void throwsWhenProductIsMissing() {
        ProductId productId = ProductId.newId();

        assertThatThrownBy(() -> service.getProduct(productId))
                .isInstanceOf(ProductNotFoundException.class)
                .extracting("productId")
                .isEqualTo(productId);
    }

    private static class InMemoryProductRepository implements ProductRepository {

        private final Map<ProductId, Product> products = new LinkedHashMap<>();

        @Override
        public Product save(Product product) {
            products.put(product.id(), product);
            return product;
        }

        @Override
        public Optional<Product> findById(ProductId productId) {
            return Optional.ofNullable(products.get(productId));
        }
    }
}
