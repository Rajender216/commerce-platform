package com.rcp.commerce.catalog.application.service;

import com.rcp.commerce.catalog.application.port.in.CreateProductCommand;
import com.rcp.commerce.catalog.application.port.in.CreateProductUseCase;
import com.rcp.commerce.catalog.application.port.in.GetProductUseCase;
import com.rcp.commerce.catalog.application.port.out.ProductRepository;
import com.rcp.commerce.catalog.domain.Product;
import com.rcp.commerce.catalog.domain.ProductId;
import java.time.Clock;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductApplicationService implements CreateProductUseCase, GetProductUseCase {

    private final ProductRepository productRepository;
    private final Clock clock;

    public ProductApplicationService(ProductRepository productRepository, Clock clock) {
        this.productRepository = Objects.requireNonNull(productRepository, "productRepository must not be null");
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    @Override
    @Transactional
    public Product createProduct(CreateProductCommand command) {
        Product product = Product.createDraft(command.name(), command.description(), clock);
        return productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProduct(ProductId productId) {
        return productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));
    }
}
