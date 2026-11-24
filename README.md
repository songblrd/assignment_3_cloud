# Assignment 3: Cloud-Native E-Commerce Store

A React storefront backed by four Spring Boot microservices and one PostgreSQL database, all running in Docker.

## Run it

Only Docker Desktop is needed (no local JDK, Maven, Node or PostgreSQL).

```
docker compose up --build
```

Then open **http://localhost:3000**.

The first build takes a few minutes (Maven and npm download dependencies inside Docker). If a service starts before the database is ready, it restarts on its own (`restart: on-failure`).

## Ports

| Component         | Port | URL                            |
|-------------------|------|--------------------------------|
| frontend (React)  | 3000 | http://localhost:3000          |
| product-service   | 8080 | http://localhost:8080/api/products  |
| order-service     | 8081 | http://localhost:8081/api/orders    |
| inventory-service | 8082 | http://localhost:8082/api/inventory |
| cart-service      | 8083 | http://localhost:8083/api/carts     |
| postgres          | 5433 | `localhost:5433` → 5432 in the container (db `inventory_db`, user `postgres`, password `password`) |

## Reset the database

The schema and seed data in `db/init/` only run the first time the database volume is created. To start over with fresh data:

```
docker compose down -v
docker compose up --build
```

## Checkout flow

1. The frontend calls `POST /api/carts/{id}/checkout` on cart-service.
2. cart-service sends the cart's lines to order-service: `POST /api/orders`.
3. order-service calls product-service (`GET /api/products/{id}`) for each line to confirm it exists and get its price.
4. order-service calls inventory-service (`POST /api/inventory/reserve`). If any line is short, inventory-service returns 409 and reserves nothing.
5. order-service saves one Order per line (status `CONFIRMED`, `totalPrice = price × quantity`).
6. cart-service marks the cart `CHECKED_OUT` and returns 201 with the orders.

Seed data includes a product with 0 stock (Game Controller) and a low-stock product (Laptop Stand, 2 left) to demo the out-of-stock case.

## Team and service ownership

| Service           | Owner |
|-------------------|-------|
| product-service   | Marianne Moyo |
| order-service     | Marianne Moyo |
| inventory-service | _(name)_ |
| cart-service      | Aaron Sieunarine |
| frontend          | _(name)_ (built with AI assistance, as the assignment allows) |
