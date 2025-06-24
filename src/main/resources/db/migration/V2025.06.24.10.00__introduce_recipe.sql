ALTER TABLE IF EXISTS cart_items
    RENAME TO cart_products;

CREATE TABLE IF NOT EXISTS recipes (
    id   uuid NOT NULL,
    name varchar,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS cart_recipes (
    id        uuid NOT NULL,
    cart_id   uuid REFERENCES carts (id),
    recipe_id uuid REFERENCES recipes (id),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS recipe_items (
    id         uuid NOT NULL,
    recipe_id  uuid REFERENCES recipes (id),
    product_id uuid REFERENCES products (id)
)


