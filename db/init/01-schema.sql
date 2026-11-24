-- db/init/01-schema.sql
-- Runs once, on first startup of the postgres container.
-- Changed something? Run: docker compose down -v  then start again.

-- === product-service (owner: Marianne's A2) ===
CREATE TABLE products (
    id        BIGSERIAL PRIMARY KEY,
    name      VARCHAR(255)   NOT NULL,
    category  VARCHAR(255)   NOT NULL,
    price     NUMERIC(10, 2) NOT NULL,
    stock_quantity INTEGER        NOT NULL,
    image_Url VARCHAR(255)
    
);

-- === Inventory Service (owner:) ===
CREATE TABLE inventory (
    id          BIGSERIAL PRIMARY KEY,
    product_id  BIGINT  NOT NULL UNIQUE,
    available   INTEGER NOT NULL
);


-- === Cart Service (owner:) ===
CREATE TABLE carts (
    id BIGSERIAL PRIMARY KEY,
    status VARCHAR(50) NOT NULL
);

CREATE TABLE cart_items (
    id BIGSERIAL PRIMARY KEY,
    cart_id BIGINT NOT NULL REFERENCES carts(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    UNIQUE (cart_id, product_id)
);

CREATE INDEX idx_cart_items_cart_id ON cart_items(cart_id);

-- === Order Service (owner: Marianne's A3) ===
CREATE TABLE orders (
    id         BIGSERIAL PRIMARY KEY,
    product_id BIGINT         NOT NULL,
    quantity   INTEGER        NOT NULL,
    total_price NUMERIC(10, 2) NOT NULL,
    status     VARCHAR(50)    NOT NULL
);
