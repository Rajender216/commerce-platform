package com.rcp.commerce.catalog.adapter.in.web;

import com.rcp.commerce.catalog.application.port.in.CategoryAdminUseCase;
import com.rcp.commerce.catalog.domain.CategoryId;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/admin/categories")
class AdminCategoryController {

    private final CategoryAdminUseCase categories;

    AdminCategoryController(CategoryAdminUseCase categories) {
        this.categories = categories;
    }

    @PostMapping
    ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = CategoryResponse.from(categories.createCategory(request.toCommand()));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{categoryId}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{categoryId}")
    CategoryResponse update(@PathVariable UUID categoryId, @Valid @RequestBody CategoryRequest request) {
        return CategoryResponse.from(categories.updateCategory(CategoryId.from(categoryId), request.toCommand()));
    }

    @GetMapping
    List<CategoryResponse> list() {
        return categories.listCategories().stream().map(CategoryResponse::from).toList();
    }

    @GetMapping("/{categoryId}")
    CategoryResponse get(@PathVariable UUID categoryId) {
        return CategoryResponse.from(categories.getCategory(CategoryId.from(categoryId)));
    }
}
