CREATE TABLE products (
    id UUID PRIMARY KEY,
    name VARCHAR(160) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    version BIGINT NOT NULL,
    CONSTRAINT chk_products_name_not_blank CHECK (length(btrim(name)) > 0),
    CONSTRAINT chk_products_status CHECK (status IN ('DRAFT')),
    CONSTRAINT chk_products_version_non_negative CHECK (version >= 0)
);
