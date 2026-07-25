CREATE TABLE categories (
    id UUID PRIMARY KEY,
    name VARCHAR(160) NOT NULL,
    slug VARCHAR(160) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uk_categories_slug UNIQUE (slug),
    CONSTRAINT chk_categories_name_not_blank CHECK (length(btrim(name)) > 0),
    CONSTRAINT chk_categories_slug_not_blank CHECK (length(btrim(slug)) > 0)
);

ALTER TABLE products ADD COLUMN slug VARCHAR(160);
ALTER TABLE products ADD COLUMN category_id UUID;
ALTER TABLE products ADD COLUMN brand VARCHAR(120) NOT NULL DEFAULT '';

INSERT INTO categories (id, name, slug, description, active, created_at, updated_at)
VALUES (
    '00000000-0000-0000-0000-000000000001',
    'Uncategorized',
    'uncategorized',
    'Temporary category for products created before the MVP catalog migration.',
    false,
    now(),
    now()
)
ON CONFLICT (slug) DO NOTHING;

UPDATE products
SET
    slug = lower(regexp_replace(btrim(name), '[^a-zA-Z0-9]+', '-', 'g')) || '-' || substring(id::text, 1, 8),
    category_id = '00000000-0000-0000-0000-000000000001'
WHERE slug IS NULL OR category_id IS NULL;

ALTER TABLE products DROP CONSTRAINT chk_products_status;
ALTER TABLE products ADD CONSTRAINT chk_products_status CHECK (status IN ('DRAFT', 'ACTIVE', 'INACTIVE'));
ALTER TABLE products ADD CONSTRAINT chk_products_slug_not_blank CHECK (length(btrim(slug)) > 0);
ALTER TABLE products ADD CONSTRAINT uk_products_slug UNIQUE (slug);
ALTER TABLE products ADD CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories (id);

ALTER TABLE products ALTER COLUMN slug SET NOT NULL;
ALTER TABLE products ALTER COLUMN category_id SET NOT NULL;

CREATE TABLE product_variants (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL,
    sku VARCHAR(80) NOT NULL,
    name VARCHAR(160) NOT NULL,
    price NUMERIC(12, 2) NOT NULL,
    stock_quantity INTEGER NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    version BIGINT NOT NULL,
    CONSTRAINT fk_product_variants_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE,
    CONSTRAINT uk_product_variants_sku UNIQUE (sku),
    CONSTRAINT chk_product_variants_sku_not_blank CHECK (length(btrim(sku)) > 0),
    CONSTRAINT chk_product_variants_name_not_blank CHECK (length(btrim(name)) > 0),
    CONSTRAINT chk_product_variants_price_positive CHECK (price > 0),
    CONSTRAINT chk_product_variants_stock_non_negative CHECK (stock_quantity >= 0),
    CONSTRAINT chk_product_variants_version_non_negative CHECK (version >= 0)
);

CREATE TABLE product_images (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL,
    url VARCHAR(2048) NOT NULL,
    alt_text VARCHAR(255) NOT NULL,
    display_order INTEGER NOT NULL,
    primary_image BOOLEAN NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_product_images_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE,
    CONSTRAINT chk_product_images_url_not_blank CHECK (length(btrim(url)) > 0),
    CONSTRAINT chk_product_images_display_order_non_negative CHECK (display_order >= 0)
);

CREATE UNIQUE INDEX uk_product_images_primary
    ON product_images (product_id)
    WHERE primary_image = true;

CREATE INDEX idx_categories_active_name ON categories (active, name);
CREATE INDEX idx_products_public_browse ON products (status, category_id, brand, name);
CREATE INDEX idx_products_name_lower ON products (lower(name));
CREATE INDEX idx_product_variants_public_filters ON product_variants (product_id, active, price, stock_quantity);
CREATE INDEX idx_product_images_product_order ON product_images (product_id, display_order, created_at);
