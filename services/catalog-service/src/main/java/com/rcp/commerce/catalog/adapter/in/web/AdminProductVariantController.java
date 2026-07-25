package com.rcp.commerce.catalog.adapter.in.web;

import com.rcp.commerce.catalog.application.port.in.ProductVariantAdminUseCase;
import com.rcp.commerce.catalog.domain.ProductId;
import com.rcp.commerce.catalog.domain.VariantId;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/admin/products/{productId}/variants")
class AdminProductVariantController {

    private final ProductVariantAdminUseCase variants;

    AdminProductVariantController(ProductVariantAdminUseCase variants) {
        this.variants = variants;
    }

    @PostMapping
    ResponseEntity<VariantResponse> create(@PathVariable UUID productId, @Valid @RequestBody VariantRequest request) {
        VariantResponse response =
                VariantResponse.from(variants.addVariant(ProductId.from(productId), request.toCommand()));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{variantId}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{variantId}")
    VariantResponse update(
            @PathVariable UUID productId, @PathVariable UUID variantId, @Valid @RequestBody VariantRequest request) {
        return VariantResponse.from(
                variants.updateVariant(ProductId.from(productId), VariantId.from(variantId), request.toCommand()));
    }

    @PatchMapping("/{variantId}/stock")
    VariantResponse updateStock(
            @PathVariable UUID productId, @PathVariable UUID variantId, @Valid @RequestBody StockRequest request) {
        return VariantResponse.from(
                variants.updateStock(ProductId.from(productId), VariantId.from(variantId), request.toCommand()));
    }

    @DeleteMapping("/{variantId}")
    ResponseEntity<Void> delete(@PathVariable UUID productId, @PathVariable UUID variantId) {
        variants.deleteVariant(ProductId.from(productId), VariantId.from(variantId));
        return ResponseEntity.noContent().build();
    }
}
