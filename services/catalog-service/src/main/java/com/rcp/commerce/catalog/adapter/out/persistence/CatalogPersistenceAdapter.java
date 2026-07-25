package com.rcp.commerce.catalog.adapter.out.persistence;

import com.rcp.commerce.catalog.application.port.in.BrowseProductsQuery;
import com.rcp.commerce.catalog.application.port.in.PageResult;
import com.rcp.commerce.catalog.application.port.in.ProductCatalogView;
import com.rcp.commerce.catalog.application.port.out.CatalogRepository;
import com.rcp.commerce.catalog.domain.Category;
import com.rcp.commerce.catalog.domain.CategoryId;
import com.rcp.commerce.catalog.domain.Product;
import com.rcp.commerce.catalog.domain.ProductId;
import com.rcp.commerce.catalog.domain.ProductImage;
import com.rcp.commerce.catalog.domain.ProductImageId;
import com.rcp.commerce.catalog.domain.ProductVariant;
import com.rcp.commerce.catalog.domain.VariantId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
class CatalogPersistenceAdapter implements CatalogRepository {

    private final JpaCategoryRepository categoryRepository;
    private final JpaProductRepository productRepository;
    private final JpaProductVariantRepository variantRepository;
    private final JpaProductImageRepository imageRepository;
    private final EntityManager entityManager;
    private final CatalogPersistenceMapper mapper = new CatalogPersistenceMapper();

    CatalogPersistenceAdapter(
            JpaCategoryRepository categoryRepository,
            JpaProductRepository productRepository,
            JpaProductVariantRepository variantRepository,
            JpaProductImageRepository imageRepository,
            EntityManager entityManager) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.variantRepository = variantRepository;
        this.imageRepository = imageRepository;
        this.entityManager = entityManager;
    }

    @Override
    public Category saveCategory(Category category) {
        return mapper.toDomain(categoryRepository.saveAndFlush(mapper.toEntity(category)));
    }

    @Override
    public Optional<Category> findCategoryById(CategoryId categoryId) {
        return categoryRepository.findById(categoryId.value()).map(mapper::toDomain);
    }

    @Override
    public List<Category> findAllCategories() {
        return categoryRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Category> findActiveCategories() {
        return categoryRepository.findByActiveTrueOrderByNameAsc().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean categorySlugExists(String slug, CategoryId excludedCategoryId) {
        return excludedCategoryId == null
                ? categoryRepository.existsBySlug(slug)
                : categoryRepository.existsBySlugAndIdNot(slug, excludedCategoryId.value());
    }

    @Override
    public Product saveProduct(Product product) {
        return mapper.toDomain(productRepository.saveAndFlush(mapper.toEntity(product)));
    }

    @Override
    public Optional<Product> findProductById(ProductId productId) {
        return productRepository.findById(productId.value()).map(mapper::toDomain);
    }

    @Override
    public List<Product> findAllProducts() {
        return productRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean productSlugExists(String slug, ProductId excludedProductId) {
        return excludedProductId == null
                ? productRepository.existsBySlug(slug)
                : productRepository.existsBySlugAndIdNot(slug, excludedProductId.value());
    }

    @Override
    public ProductVariant saveVariant(ProductVariant variant) {
        return mapper.toDomain(variantRepository.saveAndFlush(mapper.toEntity(variant)));
    }

    @Override
    public Optional<ProductVariant> findVariantById(ProductId productId, VariantId variantId) {
        return variantRepository
                .findByProductIdAndId(productId.value(), variantId.value())
                .map(mapper::toDomain);
    }

    @Override
    public boolean skuExists(String sku, VariantId excludedVariantId) {
        return excludedVariantId == null
                ? variantRepository.existsBySku(sku)
                : variantRepository.existsBySkuAndIdNot(sku, excludedVariantId.value());
    }

    @Override
    public boolean productHasActiveVariant(ProductId productId) {
        return variantRepository.existsByProductIdAndActiveTrue(productId.value());
    }

    @Override
    public void deleteVariant(ProductVariant variant) {
        variantRepository.deleteById(variant.id().value());
        variantRepository.flush();
    }

    @Override
    public ProductImage saveImage(ProductImage image) {
        return mapper.toDomain(imageRepository.saveAndFlush(mapper.toEntity(image)));
    }

    @Override
    public Optional<ProductImage> findImageById(ProductId productId, ProductImageId imageId) {
        return imageRepository
                .findByProductIdAndId(productId.value(), imageId.value())
                .map(mapper::toDomain);
    }

    @Override
    public void clearPrimaryImages(ProductId productId, ProductImageId excludedImageId) {
        List<JpaProductImageEntity> images =
                imageRepository.findByProductIdOrderByDisplayOrderAscCreatedAtAsc(productId.value());
        images.stream()
                .filter(JpaProductImageEntity::isPrimaryImage)
                .filter(image -> excludedImageId == null || !image.getId().equals(excludedImageId.value()))
                .forEach(image -> image.setPrimaryImage(false));
        imageRepository.saveAllAndFlush(images);
    }

    @Override
    public void deleteImage(ProductImage image) {
        imageRepository.deleteById(image.id().value());
        imageRepository.flush();
    }

    @Override
    public ProductCatalogView getProductView(ProductId productId) {
        JpaProductEntity product = productRepository.findById(productId.value()).orElseThrow();
        JpaCategoryEntity category =
                categoryRepository.findById(product.getCategoryId()).orElseThrow();
        return buildView(product, category, false);
    }

    @Override
    public Optional<ProductCatalogView> findPublicProduct(String productIdOrSlug) {
        Optional<JpaProductEntity> candidate = parseUuid(productIdOrSlug)
                .flatMap(productRepository::findById)
                .or(() -> productRepository.findBySlug(productIdOrSlug));
        return candidate.flatMap(product -> categoryRepository
                .findById(product.getCategoryId())
                .filter(JpaCategoryEntity::isActive)
                .filter(category -> product.getStatus().name().equals("ACTIVE"))
                .map(category -> buildView(product, category, true))
                .filter(view -> !view.variants().isEmpty()));
    }

    @Override
    public PageResult<ProductCatalogView> browsePublicProducts(BrowseProductsQuery query) {
        QueryParts parts = buildBrowseQuery(query);
        long total =
                ((Number) createNativeQuery(parts.countSql(), parts.params()).getSingleResult()).longValue();
        @SuppressWarnings("unchecked")
        List<UUID> ids = createNativeQuery(parts.idSql(), parts.params())
                .setFirstResult(query.page() * query.size())
                .setMaxResults(query.size())
                .getResultList();

        Map<UUID, ProductCatalogView> views = new LinkedHashMap<>();
        for (UUID id : ids) {
            findPublicProduct(id.toString()).ifPresent(view -> views.put(id, view));
        }
        int totalPages = total == 0 ? 0 : (int) Math.ceil((double) total / query.size());
        return new PageResult<>(
                new ArrayList<>(views.values()),
                query.page(),
                query.size(),
                total,
                totalPages,
                query.page() == 0,
                totalPages == 0 || query.page() >= totalPages - 1);
    }

    private ProductCatalogView buildView(JpaProductEntity product, JpaCategoryEntity category, boolean publicOnly) {
        List<ProductVariant> variants = (publicOnly
                        ? variantRepository.findByProductIdAndActiveTrueOrderBySkuAsc(product.getId())
                        : variantRepository.findByProductIdOrderBySkuAsc(product.getId()))
                .stream().map(mapper::toDomain).toList();
        List<ProductImage> images =
                imageRepository.findByProductIdOrderByDisplayOrderAscCreatedAtAsc(product.getId()).stream()
                        .map(mapper::toDomain)
                        .toList();
        BigDecimal minPrice = variants.stream()
                .map(ProductVariant::price)
                .min(BigDecimal::compareTo)
                .orElse(null);
        BigDecimal maxPrice = variants.stream()
                .map(ProductVariant::price)
                .max(BigDecimal::compareTo)
                .orElse(null);
        boolean inStock = variants.stream().anyMatch(variant -> variant.stockQuantity() > 0);
        return new ProductCatalogView(
                mapper.toDomain(product), mapper.toDomain(category), variants, images, minPrice, maxPrice, inStock);
    }

    private Query createNativeQuery(String sql, Map<String, Object> params) {
        Query query = entityManager.createNativeQuery(sql);
        params.forEach(query::setParameter);
        return query;
    }

    private static QueryParts buildBrowseQuery(BrowseProductsQuery query) {
        StringBuilder where = new StringBuilder(
                """
                from products p
                join categories c on c.id = p.category_id
                join product_variants v on v.product_id = p.id
                where p.status = 'ACTIVE' and c.active = true and v.active = true
                """);
        Map<String, Object> params = new LinkedHashMap<>();
        if (query.search() != null && !query.search().isBlank()) {
            where.append(" and lower(p.name) like :search");
            params.put("search", "%" + query.search().trim().toLowerCase() + "%");
        }
        if (query.category() != null && !query.category().isBlank()) {
            where.append(" and (c.slug = :category or cast(c.id as text) = :category)");
            params.put("category", query.category().trim());
        }
        if (query.brand() != null && !query.brand().isBlank()) {
            where.append(" and lower(p.brand) = :brand");
            params.put("brand", query.brand().trim().toLowerCase());
        }
        if (query.minPrice() != null) {
            where.append(" and v.price >= :minPrice");
            params.put("minPrice", query.minPrice());
        }
        if (query.maxPrice() != null) {
            where.append(" and v.price <= :maxPrice");
            params.put("maxPrice", query.maxPrice());
        }
        if (query.inStockOnly()) {
            where.append(" and v.stock_quantity > 0");
        }
        String orderBy =
                switch (query.sort()) {
                    case "newest" -> " order by max(p.created_at) desc";
                    case "priceAsc" -> " order by min(v.price) asc, min(p.name) asc";
                    case "priceDesc" -> " order by max(v.price) desc, min(p.name) asc";
                    default -> " order by min(p.name) asc";
                };
        String idSql = "select p.id " + where + " group by p.id" + orderBy;
        String countSql = "select count(distinct p.id) " + where;
        return new QueryParts(idSql, countSql, params);
    }

    private static Optional<UUID> parseUuid(String value) {
        try {
            return Optional.of(UUID.fromString(value));
        } catch (IllegalArgumentException exception) {
            return Optional.empty();
        }
    }

    private record QueryParts(String idSql, String countSql, Map<String, Object> params) {}
}
