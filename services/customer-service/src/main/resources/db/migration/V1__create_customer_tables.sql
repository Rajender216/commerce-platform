CREATE TABLE customer_profiles (
    id UUID PRIMARY KEY,
    identity_subject VARCHAR(128) NOT NULL,
    email VARCHAR(320) NOT NULL,
    first_name VARCHAR(120) NOT NULL,
    last_name VARCHAR(120) NOT NULL,
    phone_number VARCHAR(40) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    version BIGINT NOT NULL,
    CONSTRAINT uk_customer_profiles_identity_subject UNIQUE (identity_subject),
    CONSTRAINT chk_customer_profiles_subject_not_blank CHECK (length(btrim(identity_subject)) > 0),
    CONSTRAINT chk_customer_profiles_email_not_blank CHECK (length(btrim(email)) > 0),
    CONSTRAINT chk_customer_profiles_first_name_not_blank CHECK (length(btrim(first_name)) > 0),
    CONSTRAINT chk_customer_profiles_last_name_not_blank CHECK (length(btrim(last_name)) > 0),
    CONSTRAINT chk_customer_profiles_version_non_negative CHECK (version >= 0)
);

CREATE TABLE shipping_addresses (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    recipient_name VARCHAR(160) NOT NULL,
    phone_number VARCHAR(40) NOT NULL,
    address_line1 VARCHAR(255) NOT NULL,
    address_line2 VARCHAR(255) NOT NULL,
    city VARCHAR(120) NOT NULL,
    state VARCHAR(120) NOT NULL,
    postal_code VARCHAR(32) NOT NULL,
    country VARCHAR(80) NOT NULL,
    default_address BOOLEAN NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    version BIGINT NOT NULL,
    CONSTRAINT fk_shipping_addresses_customer
        FOREIGN KEY (customer_id) REFERENCES customer_profiles (id) ON DELETE CASCADE,
    CONSTRAINT chk_shipping_addresses_recipient_not_blank CHECK (length(btrim(recipient_name)) > 0),
    CONSTRAINT chk_shipping_addresses_line1_not_blank CHECK (length(btrim(address_line1)) > 0),
    CONSTRAINT chk_shipping_addresses_city_not_blank CHECK (length(btrim(city)) > 0),
    CONSTRAINT chk_shipping_addresses_state_not_blank CHECK (length(btrim(state)) > 0),
    CONSTRAINT chk_shipping_addresses_postal_not_blank CHECK (length(btrim(postal_code)) > 0),
    CONSTRAINT chk_shipping_addresses_country_not_blank CHECK (length(btrim(country)) > 0),
    CONSTRAINT chk_shipping_addresses_version_non_negative CHECK (version >= 0)
);

CREATE INDEX idx_customer_profiles_identity_subject ON customer_profiles (identity_subject);
CREATE INDEX idx_shipping_addresses_customer ON shipping_addresses (customer_id, created_at);
CREATE UNIQUE INDEX uk_shipping_addresses_one_default
    ON shipping_addresses (customer_id)
    WHERE default_address = true;
