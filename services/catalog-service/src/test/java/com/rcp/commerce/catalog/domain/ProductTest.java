package com.rcp.commerce.catalog.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class ProductTest {

    private final Clock clock = Clock.fixed(Instant.parse("2026-07-22T00:00:00Z"), ZoneOffset.UTC);

    @Test
    void createsDraftProductWithNormalizedFields() {
        Product product = Product.createDraft(
                new ProductName("  Draft product  "), new ProductDescription("  Description  "), clock);

        assertThat(product.id()).isNotNull();
        assertThat(product.name().value()).isEqualTo("Draft product");
        assertThat(product.description().value()).isEqualTo("Description");
        assertThat(product.status()).isEqualTo(ProductStatus.DRAFT);
        assertThat(product.createdAt()).isEqualTo(Instant.parse("2026-07-22T00:00:00Z"));
        assertThat(product.updatedAt()).isEqualTo(product.createdAt());
        assertThat(product.version()).isZero();
    }

    @Test
    void rejectsBlankProductName() {
        assertThatThrownBy(() -> new ProductName("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product name is required.");
    }

    @Test
    void rejectsProductNameLongerThanMaximum() {
        assertThatThrownBy(() -> new ProductName("a".repeat(ProductName.MAX_LENGTH + 1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product name must be at most 160 characters.");
    }

    @Test
    void rejectsDescriptionLongerThanMaximum() {
        assertThatThrownBy(() -> new ProductDescription("a".repeat(ProductDescription.MAX_LENGTH + 1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product description must be at most 2000 characters.");
    }
}
