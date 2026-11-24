# Inventory Service

Runs on port 8082 and stores:

- id
- productId
- available

## Docker

```powershell
cd inventory_service
docker compose up --build
```

## Endpoints

- GET /api/inventory
- GET /api/inventory/{id}
- GET /api/inventory/product/{productId}
- POST /api/inventory
- PUT /api/inventory/{id}
- DELETE /api/inventory/{id}
- POST /api/inventory/reserve

# How to run

cd inventory_service
docker compose up --build
