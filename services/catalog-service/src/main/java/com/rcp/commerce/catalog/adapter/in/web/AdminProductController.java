package com.rcp.commerce.catalog.adapter.in.web;

import com.rcp.commerce.catalog.application.port.in.ProductAdminUseCase;
import com.rcp.commerce.catalog.domain.ProductId;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/admin/products")
class AdminProductController {

    private final ProductAdminUseCase products;

    AdminProductController(ProductAdminUseCase products) {
        this.products = products;
    }

    @PostMapping
    ResponseEntity<AdminProductResponse> create(@Valid @RequestBody ProductRequest request) {
        AdminProductResponse response = AdminProductResponse.from(products.createProduct(request.toCommand()));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{productId}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{productId}")
    AdminProductResponse update(@PathVariable UUID productId, @Valid @RequestBody ProductRequest request) {
        return AdminProductResponse.from(products.updateProduct(ProductId.from(productId), request.toCommand()));
    }

    @GetMapping("/{productId}")
    AdminProductResponse get(@PathVariable UUID productId) {
        return AdminProductResponse.from(products.getProduct(ProductId.from(productId)));
    }

    @GetMapping
    List<AdminProductResponse> list() {
        return products.listProducts().stream().map(AdminProductResponse::from).toList();
    }

    @PatchMapping("/{productId}/status")
    AdminProductResponse changeStatus(@PathVariable UUID productId, @Valid @RequestBody ProductStatusRequest request) {
        return AdminProductResponse.from(products.changeProductStatus(ProductId.from(productId), request.status()));
    }
}
