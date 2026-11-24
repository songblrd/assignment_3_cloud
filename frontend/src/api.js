// Every call the storefront makes to the backend lives in this file.
// The browser calls each service directly (the services allow CORS from http://localhost:3000).
// These URLs are localhost because the code runs in the shopper's browser, not inside Docker.

const PRODUCT_URL = 'http://localhost:8080'
const ORDER_URL = 'http://localhost:8081'
const INVENTORY_URL = 'http://localhost:8082'
const CART_URL = 'http://localhost:8083'

// Shared helper: sends the request, reads the JSON body (if any),
// and turns any non-2xx response into an Error carrying the service's "message".
async function request(url, options) {
  let response
  try {
    response = await fetch(url, options)
  } catch {
    throw new Error(`Could not reach ${url}. Is that service running?`)
  }

  const text = await response.text()
  const body = text ? JSON.parse(text) : null

  if (!response.ok) {
    const error = new Error((body && body.message) || `Request failed with status ${response.status}`)
    error.status = response.status
    throw error
  }
  return body
}

const jsonHeaders = { 'Content-Type': 'application/json' }

// ----- product-service (browsing) -----
export function getProducts() {
  return request(`${PRODUCT_URL}/api/products`)
}

// ----- inventory-service (browsing: stock badges) -----
export function getInventory() {
  return request(`${INVENTORY_URL}/api/inventory`)
}

// ----- cart-service -----
export function createCart() {
  return request(`${CART_URL}/api/carts`, { method: 'POST' })
}

export function getCart(cartId) {
  return request(`${CART_URL}/api/carts/${cartId}`)
}

export function addCartItem(cartId, productId, quantity) {
  return request(`${CART_URL}/api/carts/${cartId}/items`, {
    method: 'POST',
    headers: jsonHeaders,
    body: JSON.stringify({ productId, quantity }),
  })
}

export function removeCartItem(cartId, itemId) {
  return request(`${CART_URL}/api/carts/${cartId}/items/${itemId}`, { method: 'DELETE' })
}

// Checkout: cart-service -> order-service -> product-service + inventory-service.
// Returns the list of created orders (201), or throws with the 400/409 message.
export function checkout(cartId) {
  return request(`${CART_URL}/api/carts/${cartId}/checkout`, { method: 'POST' })
}

// ----- order-service ("My orders") -----
export function getOrders() {
  return request(`${ORDER_URL}/api/orders`)
}
