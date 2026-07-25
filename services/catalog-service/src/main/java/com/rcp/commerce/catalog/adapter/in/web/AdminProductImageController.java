package com.rcp.commerce.catalog.adapter.in.web;

import com.rcp.commerce.catalog.application.port.in.ProductImageAdminUseCase;
import com.rcp.commerce.catalog.domain.ProductId;
import com.rcp.commerce.catalog.domain.ProductImageId;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/admin/products/{productId}/images")
class AdminProductImageController {

    private final ProductImageAdminUseCase images;

    AdminProductImageController(ProductImageAdminUseCase images) {
        this.images = images;
    }

    @PostMapping
    ResponseEntity<ProductImageResponse> create(
            @PathVariable UUID productId, @Valid @RequestBody ProductImageRequest request) {
        ProductImageResponse response =
                ProductImageResponse.from(images.addImage(ProductId.from(productId), request.toCommand()));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{imageId}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{imageId}")
    ProductImageResponse update(
            @PathVariable UUID productId, @PathVariable UUID imageId, @Valid @RequestBody ProductImageRequest request) {
        return ProductImageResponse.from(
                images.updateImage(ProductId.from(productId), ProductImageId.from(imageId), request.toCommand()));
    }

    @DeleteMapping("/{imageId}")
    ResponseEntity<Void> delete(@PathVariable UUID productId, @PathVariable UUID imageId) {
        images.deleteImage(ProductId.from(productId), ProductImageId.from(imageId));
        return ResponseEntity.noContent().build();
    }
}
