CREATE TABLE IF NOT EXISTS carts (
    id             uuid NOT NULL,
    total_in_cents INT,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS products (
    id             uuid NOT NULL,
    name           varchar,
    price_in_cents INT,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS cart_items (
    id         uuid NOT NULL,
    cart_id    uuid REFERENCES carts (id),
    product_id uuid REFERENCES products (id),
    PRIMARY KEY (id)
);

