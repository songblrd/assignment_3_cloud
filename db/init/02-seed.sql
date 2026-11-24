-- db/init/02-seed.sql
-- Runs once, right after 01-schema.sql, on first startup of the postgres container.
-- Changed something? Run: docker compose down -v  then start again.

-- === Products (product-service) ===
-- stock_quantity is kept from A1/A2 but is NOT used by the store anymore:
-- inventory-service (the inventory table below) is the source of truth for stock.
INSERT INTO products (name, category, price, stock_quantity, image_url) VALUES
    ('Wireless Headphones', 'Audio',       89.99, 25, '/images/headphones.jpg'),
    ('Mechanical Keyboard', 'Peripherals', 119.99, 15, '/images/keyboard.jpg'),
    ('Gaming Mouse',        'Peripherals', 59.99, 30, '/images/mouse.jpg'),
    ('27in Monitor',        'Displays',   279.99, 8,  '/images/monitor.jpg'),
    ('USB-C Hub',           'Accessories', 39.99, 40, '/images/usb-hub.jpg'),
    ('HD Webcam',           'Video',       69.99, 12, '/images/webcam.jpg'),
    ('XL Mouse Pad',        'Accessories', 24.99, 50, '/images/mousepad.jpg'),
    ('Game Controller',     'Gaming',      64.99, 0,  '/images/controller.jpg'),
    ('LED Desk Lamp',       'Office',      34.99, 20, '/images/desk-lamp.jpg'),
    ('Laptop Stand',        'Office',      44.99, 2,  '/images/laptop-stand.jpg'),
    ('USB Microphone',      'Audio',       99.99, 10, '/images/microphone.jpg'),
    ('Portable SSD 1TB',    'Storage',    129.99, 18, '/images/ssd.jpg');

-- === Inventory (inventory-service) ===
-- One row per product. Game Controller (8) has 0 stock for the out-of-stock demo,
-- Laptop Stand (10) is low stock (2), so asking for 3+ gets a 409 from inventory-service.
INSERT INTO inventory (product_id, available) VALUES
    (1, 25),
    (2, 15),
    (3, 30),
    (4, 8),
    (5, 40),
    (6, 12),
    (7, 50),
    (8, 0),
    (9, 20),
    (10, 2),
    (11, 10),
    (12, 18);
