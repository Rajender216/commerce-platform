package com.rcp.commerce.catalog.adapter.in.web;

import com.rcp.commerce.catalog.application.port.in.BrowseProductsQuery;
import com.rcp.commerce.catalog.application.port.in.PublicCatalogUseCase;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
class PublicCatalogController {

    private static final int DEFAULT_PAGE_SIZE = 20;

    private final PublicCatalogUseCase catalog;

    PublicCatalogController(PublicCatalogUseCase catalog) {
        this.catalog = catalog;
    }

    @GetMapping("/categories")
    List<CategoryResponse> activeCategories() {
        return catalog.listActiveCategories().stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @GetMapping("/products")
    PageResponse<PublicProductResponse> browseProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "" + DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "false") boolean inStockOnly) {
        return PageResponse.from(
                catalog.browseProducts(new BrowseProductsQuery(
                        page, size, sort, search, category, brand, minPrice, maxPrice, inStockOnly)),
                view -> PublicProductResponse.from(view, false));
    }

    @GetMapping("/products/{productIdOrSlug}")
    PublicProductResponse getProduct(@PathVariable String productIdOrSlug) {
        return PublicProductResponse.from(catalog.getPublicProduct(productIdOrSlug), false);
    }
}
