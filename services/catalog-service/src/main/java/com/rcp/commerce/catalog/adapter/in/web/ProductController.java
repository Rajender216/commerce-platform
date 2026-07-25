package com.rcp.commerce.catalog.adapter.in.web;

import com.rcp.commerce.catalog.application.port.in.CreateProductCommand;
import com.rcp.commerce.catalog.application.port.in.CreateProductUseCase;
import com.rcp.commerce.catalog.application.port.in.GetProductUseCase;
import com.rcp.commerce.catalog.domain.Product;
import com.rcp.commerce.catalog.domain.ProductDescription;
import com.rcp.commerce.catalog.domain.ProductId;
import com.rcp.commerce.catalog.domain.ProductName;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/products")
class ProductController {

    private final CreateProductUseCase createProductUseCase;
    private final GetProductUseCase getProductUseCase;

    ProductController(CreateProductUseCase createProductUseCase, GetProductUseCase getProductUseCase) {
        this.createProductUseCase = createProductUseCase;
        this.getProductUseCase = getProductUseCase;
    }

    @PostMapping
    ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        Product product = createProductUseCase.createProduct(new CreateProductCommand(
                new ProductName(request.name()), new ProductDescription(request.description())));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{productId}")
                .buildAndExpand(product.id().value())
                .toUri();
        return ResponseEntity.created(location).body(ProductResponse.from(product));
    }

    @GetMapping("/{productId}")
    ResponseEntity<ProductResponse> getProduct(@PathVariable UUID productId) {
        Product product = getProductUseCase.getProduct(ProductId.from(productId));
        return ResponseEntity.ok(ProductResponse.from(product));
    }
}
