import { useEffect, useState } from 'react'
import {
  addCartItem,
  checkout,
  createCart,
  getCart,
  getInventory,
  getOrders,
  getProducts,
  removeCartItem,
} from './api.js'

const CART_KEY = 'storeCartId'

// Shown when a product's image file is missing.
const PLACEHOLDER =
  'data:image/svg+xml;utf8,' +
  encodeURIComponent(
    '<svg xmlns="http://www.w3.org/2000/svg" width="300" height="200"><rect width="100%" height="100%" fill="#e8e6e1"/><text x="50%" y="50%" font-family="sans-serif" font-size="16" fill="#8a867d" text-anchor="middle" dominant-baseline="middle">No image</text></svg>',
  )

function money(value) {
  return `$${Number(value ?? 0).toFixed(2)}`
}

function stockLabel(available) {
  if (available === undefined) return { text: 'Stock unknown', css: 'stock unknown' }
  if (available === 0) return { text: 'Out of stock', css: 'stock none' }
  if (available <= 5) return { text: `Only ${available} left`, css: 'stock low' }
  return { text: `${available} in stock`, css: 'stock' }
}

// The cart id is remembered in the browser so a refresh keeps the same cart.
function savedCartId() {
  try {
    return localStorage.getItem(CART_KEY)
  } catch {
    return null
  }
}

function saveCartId(id) {
  try {
    if (id == null) localStorage.removeItem(CART_KEY)
    else localStorage.setItem(CART_KEY, String(id))
  } catch {
    // storage unavailable: the cart just won't survive a refresh
  }
}

// cart-service returns the cart with its lines; accept either field name.
function itemsOf(cart) {
  return (cart && (cart.items || cart.cartItems)) || []
}

function App() {
  const [products, setProducts] = useState([])
  const [stock, setStock] = useState({}) // productId -> available
  const [cart, setCart] = useState(null)
  const [orders, setOrders] = useState([])
  const [quantities, setQuantities] = useState({})
  const [notice, setNotice] = useState(null)
  const [lastOrders, setLastOrders] = useState([])
  const [busy, setBusy] = useState(false)

  // Products from product-service, stock from inventory-service.
  async function loadCatalog() {
    try {
      const [productList, inventoryList] = await Promise.all([getProducts(), getInventory()])
      const stockMap = {}
      inventoryList.forEach((row) => {
        stockMap[row.productId] = row.available
      })
      setProducts(productList)
      setStock(stockMap)
    } catch (error) {
      setNotice({ type: 'error', text: `Could not load products: ${error.message}` })
    }
  }

  async function loadOrders() {
    try {
      setOrders(await getOrders())
    } catch {
      // "My orders" is optional; ignore if order-service is down
    }
  }

  // Reuse the saved cart if it is still OPEN, otherwise ask cart-service for a new one.
  async function ensureCart() {
    const id = savedCartId()
    if (id) {
      try {
        const existing = await getCart(id)
        if (existing.status !== 'CHECKED_OUT') {
          setCart(existing)
          return existing
        }
      } catch {
        // cart no longer exists: fall through and create a new one
      }
    }
    const fresh = await createCart()
    saveCartId(fresh.id)
    setCart(fresh)
    return fresh
  }

  async function refreshCart(cartId) {
    setCart(await getCart(cartId))
  }

  useEffect(() => {
    loadCatalog()
    loadOrders()
    ensureCart().catch((error) =>
      setNotice({ type: 'error', text: `Could not open a cart: ${error.message}` }),
    )
  }, [])

  async function addToCart(product) {
    const quantity = Math.max(1, Number(quantities[product.id] ?? 1) || 1)
    setBusy(true)
    try {
      const current = cart ?? (await ensureCart())
      await addCartItem(current.id, product.id, quantity)
      await refreshCart(current.id)
      setNotice({ type: 'success', text: `Added ${quantity} × ${product.name} to your cart.` })
    } catch (error) {
      setNotice({ type: 'error', text: error.message })
    } finally {
      setBusy(false)
    }
  }

  async function removeFromCart(itemId) {
    setBusy(true)
    try {
      await removeCartItem(cart.id, itemId)
      await refreshCart(cart.id)
    } catch (error) {
      setNotice({ type: 'error', text: error.message })
    } finally {
      setBusy(false)
    }
  }

  async function placeOrder() {
    setBusy(true)
    setLastOrders([])
    try {
      const created = await checkout(cart.id)
      const list = Array.isArray(created) ? created : created ? [created] : []
      setLastOrders(list)
      setNotice({
        type: 'success',
        text: `Order placed! Order number${list.length === 1 ? '' : 's'}: ${list.map((o) => `#${o.id}`).join(', ')}`,
      })
      // The old cart is CHECKED_OUT now, so start a fresh one.
      saveCartId(null)
      setCart(null)
      await ensureCart()
      await Promise.all([loadCatalog(), loadOrders()])
    } catch (error) {
      const text =
        error.status === 409
          ? `Checkout refused: ${error.message} Nothing was ordered, so please lower the quantity and try again.`
          : error.status === 400
            ? `Checkout refused: ${error.message}`
            : `Checkout failed: ${error.message}`
      setNotice({ type: 'error', text })
    } finally {
      setBusy(false)
    }
  }

  function productFor(productId) {
    return products.find((p) => p.id === productId)
  }

  const cartItems = itemsOf(cart)
  const cartTotal = cartItems.reduce((sum, item) => {
    const product = productFor(item.productId)
    return sum + (product ? Number(product.price) * item.quantity : 0)
  }, 0)
  const cartCount = cartItems.reduce((sum, item) => sum + item.quantity, 0)

  return (
    <div className="page">
      <header className="topbar">
        <div>
          <h1>Northfield Store</h1>
          <p>Browse, add to your cart, and check out.</p>
        </div>
        <a className="cart-pill" href="#cart">
          Cart · {cartCount}
        </a>
      </header>

      {notice && (
        <div className={`notice ${notice.type}`}>
          <span>{notice.text}</span>
          <button className="link" onClick={() => setNotice(null)} aria-label="Dismiss">
            ×
          </button>
        </div>
      )}

      <div className="layout">
        <main>
          <section className="grid">
            {products.length === 0 && <p className="muted">No products to show yet.</p>}
            {products.map((product) => {
              const label = stockLabel(stock[product.id])
              return (
                <article className="card" key={product.id}>
                  <img
                    src={product.imageUrl || PLACEHOLDER}
                    alt={product.name}
                    onError={(event) => {
                      event.currentTarget.src = PLACEHOLDER
                    }}
                  />
                  <div className="card-body">
                    <h2>{product.name}</h2>
                    {product.category && <p className="muted">{product.category}</p>}
                    <p className="price">{money(product.price)}</p>
                    <p className={label.css}>{label.text}</p>
                    <div className="actions">
                      <input
                        type="number"
                        min="1"
                        aria-label={`Quantity of ${product.name}`}
                        value={quantities[product.id] ?? 1}
                        onChange={(event) =>
                          setQuantities({ ...quantities, [product.id]: event.target.value })
                        }
                      />
                      <button disabled={busy} onClick={() => addToCart(product)}>
                        Add to cart
                      </button>
                    </div>
                  </div>
                </article>
              )
            })}
          </section>
        </main>

        <aside id="cart" className="cart">
          <h2>Your cart</h2>
          {cartItems.length === 0 ? (
            <p className="muted">Your cart is empty.</p>
          ) : (
            <ul className="cart-lines">
              {cartItems.map((item) => {
                const product = productFor(item.productId)
                return (
                  <li key={item.id}>
                    <div>
                      <strong>{product ? product.name : `Product ${item.productId}`}</strong>
                      <span className="muted">
                        {item.quantity} × {product ? money(product.price) : '?'}
                      </span>
                    </div>
                    <button className="link" disabled={busy} onClick={() => removeFromCart(item.id)}>
                      Remove
                    </button>
                  </li>
                )
              })}
            </ul>
          )}
          <div className="cart-total">
            <span>Total</span>
            <strong>{money(cartTotal)}</strong>
          </div>
          <button
            className="checkout"
            disabled={busy || !cart || cartItems.length === 0}
            onClick={placeOrder}
          >
            Checkout
          </button>

          {lastOrders.length > 0 && (
            <div className="confirmation">
              <h3>Order confirmed</h3>
              <ul>
                {lastOrders.map((order) => (
                  <li key={order.id}>
                    Order #{order.id}: {order.quantity} ×{' '}
                    {productFor(order.productId)?.name ?? `Product ${order.productId}`} ={' '}
                    {money(order.totalPrice)}
                  </li>
                ))}
              </ul>
            </div>
          )}
        </aside>
      </div>

      <section className="orders">
        <h2>My orders</h2>
        {orders.length === 0 ? (
          <p className="muted">No orders yet.</p>
        ) : (
          <table>
            <thead>
              <tr>
                <th>Order</th>
                <th>Product</th>
                <th>Qty</th>
                <th>Total</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {orders.map((order) => (
                <tr key={order.id}>
                  <td>#{order.id}</td>
                  <td>{productFor(order.productId)?.name ?? `Product ${order.productId}`}</td>
                  <td>{order.quantity}</td>
                  <td>{money(order.totalPrice)}</td>
                  <td>{order.status}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </section>
    </div>
  )
}

export default App
