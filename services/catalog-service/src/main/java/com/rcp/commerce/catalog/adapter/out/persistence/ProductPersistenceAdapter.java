package com.rcp.commerce.catalog.adapter.out.persistence;

import com.rcp.commerce.catalog.application.port.out.ProductRepository;
import com.rcp.commerce.catalog.domain.Product;
import com.rcp.commerce.catalog.domain.ProductId;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
class ProductPersistenceAdapter implements ProductRepository {

    private final JpaProductRepository jpaProductRepository;
    private final ProductPersistenceMapper mapper = new ProductPersistenceMapper();

    ProductPersistenceAdapter(JpaProductRepository jpaProductRepository) {
        this.jpaProductRepository = jpaProductRepository;
    }

    @Override
    public Product save(Product product) {
        return mapper.toDomain(jpaProductRepository.saveAndFlush(mapper.toEntity(product)));
    }

    @Override
    public Optional<Product> findById(ProductId productId) {
        return jpaProductRepository.findById(productId.value()).map(mapper::toDomain);
    }
}
