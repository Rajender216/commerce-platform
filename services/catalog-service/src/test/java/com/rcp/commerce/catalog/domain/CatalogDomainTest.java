package com.rcp.commerce.catalog.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class CatalogDomainTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-07-22T00:00:00Z"), ZoneOffset.UTC);

    @Test
    void categoryRequiresNameAndSlug() {
        assertThatThrownBy(() -> Category.create(" ", "slug", "", true, CLOCK))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Category name");
        assertThatThrownBy(() -> Category.create("Name", " ", "", true, CLOCK))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Category slug");
    }

    @Test
    void productRequiresNameSlugAndCategory() {
        CategoryId categoryId = CategoryId.newId();

        assertThatThrownBy(() -> Product.createDraft(" ", "slug", "", categoryId, "", CLOCK))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product name");
        assertThatThrownBy(() -> Product.createDraft("Name", " ", "", categoryId, "", CLOCK))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product slug");
        assertThatThrownBy(() -> Product.createDraft("Name", "slug", "", null, "", CLOCK))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void variantRequiresPositivePriceAndNonNegativeStock() {
        ProductId productId = ProductId.newId();

        assertThatThrownBy(() -> ProductVariant.create(productId, "SKU-1", "Variant", BigDecimal.ZERO, 0, true, CLOCK))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("price");
        assertThatThrownBy(() -> ProductVariant.create(productId, "SKU-1", "Variant", BigDecimal.ONE, -1, true, CLOCK))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Stock");
    }

    @Test
    void productActivationRequiresActiveVariantAndActiveCategory() {
        Product product = Product.createDraft("Phone", "phone", "", CategoryId.newId(), "Acme", CLOCK);

        assertThatThrownBy(() -> product.changeStatus(ProductStatus.ACTIVE, true, false, CLOCK))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("variant");
        assertThatThrownBy(() -> product.changeStatus(ProductStatus.ACTIVE, false, true, CLOCK))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Inactive categories");

        assertThat(product.changeStatus(ProductStatus.ACTIVE, true, true, CLOCK).status())
                .isEqualTo(ProductStatus.ACTIVE);
    }

    @Test
    void imageRequiresUrlAndNonNegativeDisplayOrder() {
        ProductId productId = ProductId.newId();

        assertThatThrownBy(() -> ProductImage.create(productId, " ", "", 0, true, CLOCK))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("URL");
        assertThatThrownBy(() -> ProductImage.create(productId, "https://example.test/a.jpg", "", -1, true, CLOCK))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("display order");
    }
}
