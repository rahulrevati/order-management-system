import { useEffect, useMemo, useState, type FormEvent, type ReactNode } from 'react'
import { Link, Navigate, Route, Routes, useLocation, useNavigate, useParams } from 'react-router-dom'
import {
  addToCart, cancelOrder, clearCart, createCategory, createProduct, deleteProduct,
  getCart, getCategories, getOrder, getOrders, getAdminProducts, getProducts, importProductsCsv, login, makePayment, placeOrder,
  register, removeCartItem, searchProducts, updateCartItem, updateOrderStatus, updateProduct
} from './api'
import type { AuthUser, Cart, Category, Order, OrderStatus, PaymentMethod, Product } from './types'

const money = (n: number) => new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 0 }).format(n)
const date = (s: string) => new Date(s).toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' })

function useAuth() {
  const [user, setUser] = useState<AuthUser | null>(() => {
    const raw = localStorage.getItem('oms_user')
    return raw ? JSON.parse(raw) : null
  })
  const save = (next: AuthUser | null) => {
    setUser(next)
    if (next) localStorage.setItem('oms_user', JSON.stringify(next))
    else localStorage.removeItem('oms_user')
  }
  return { user, save }
}

function App() {
  const auth = useAuth()
  return (
    <div className="app-shell">
      <Navbar user={auth.user} logout={() => auth.save(null)} />
      <main>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/shop" element={<Shop />} />
          <Route path="/login" element={<Login onLogin={auth.save} />} />
          <Route path="/register" element={<Register />} />
          <Route path="/cart" element={auth.user ? <CartPage /> : <Navigate to="/login" />} />
          <Route path="/orders" element={auth.user ? <Orders /> : <Navigate to="/login" />} />
          <Route path="/orders/:id" element={auth.user ? <OrderDetails /> : <Navigate to="/login" />} />
          <Route path="/admin" element={auth.user?.role === 'ADMIN' ? <Admin /> : <Navigate to="/" />} />
          <Route path="*" element={<Navigate to="/" />} />
        </Routes>
      </main>
      <Footer />
    </div>
  )
}

function Navbar({ user, logout }: { user: AuthUser | null; logout: () => void }) {
  const location = useLocation()
  const navigate = useNavigate()
  const [cartCount, setCartCount] = useState(0)
  const [menuOpen, setMenuOpen] = useState(false)

  useEffect(() => {
    setMenuOpen(false)
    if (!user) { setCartCount(0); return }
    getCart().then(c => setCartCount(c.items?.reduce((s, i) => s + i.quantity, 0) || 0)).catch(() => setCartCount(0))
  }, [user, location.pathname])

  const nav = (
    <>
      <Link className={location.pathname === '/' ? 'active' : ''} to="/">Home</Link>
      <Link className={location.pathname.startsWith('/shop') ? 'active' : ''} to="/shop">Shop</Link>
      {user && <Link className={location.pathname.startsWith('/orders') ? 'active' : ''} to="/orders">Orders</Link>}
      {user?.role === 'ADMIN' && <Link className={location.pathname === '/admin' ? 'active' : ''} to="/admin">Admin</Link>}
    </>
  )

  return (
    <header className="nav">
      <div className="container nav-inner">
        <Link to="/" className="brand"><span className="brand-mark">N</span><span>Nova<span>Cart</span></span></Link>
        <nav className="nav-links">{nav}</nav>
        <div className="nav-actions">
          {user ? (
            <>
              <Link className="icon-button cart-button" to="/cart" title="Cart" aria-label={`Cart, ${cartCount} items`}>🛒<b>{cartCount}</b></Link>
              <button className="user-pill" onClick={() => { if (confirm('Sign out?')) { logout(); navigate('/') } }}>
                <span>{user.firstName?.[0] || 'U'}</span><span className="user-name">{user.firstName}</span>
              </button>
            </>
          ) : (
            <>
              <Link className="text-button" to="/login">Sign in</Link>
              <Link className="primary-button small" to="/register">Create account</Link>
            </>
          )}
          <button className="menu-button" aria-label="Open menu" aria-expanded={menuOpen} onClick={() => setMenuOpen(v => !v)}>☰</button>
        </div>
      </div>
      {menuOpen && <nav className="mobile-menu">{nav}</nav>}
    </header>
  )
}

function Home() {
  const navigate = useNavigate()
  return (
    <>
      <section className="hero">
        <div className="container hero-grid">
          <div>
            <span className="eyebrow">NEXT-GEN COMMERCE</span>
            <h1>Everything you need.<br /><em>One beautiful store.</em></h1>
            <p>Discover quality products, effortless checkout and real-time order updates — powered by a production-ready order management platform.</p>
            <div className="hero-actions">
              <button className="primary-button" onClick={() => navigate('/shop')}>Explore products <span>→</span></button>
              <a className="ghost-button" href="#why">Why NovaCart</a>
            </div>
            <div className="trust-row"><span>✓ Secure checkout</span><span>✓ Live order tracking</span><span>✓ Fast support</span></div>
          </div>
          <div className="hero-art">
            <div className="orb orb-one" /><div className="orb orb-two" />
            <div className="hero-card main-card">
              <div className="product-visual">⌁</div>
              <div><small>Featured collection</small><strong>Smart essentials</strong><span>Curated for modern life</span></div>
            </div>
            <div className="float-card"><span className="check">✓</span><div><b>Order confirmed</b><small>Just now · #NC-2048</small></div></div>
            <div className="float-price"><small>Starting from</small><b>₹999</b></div>
          </div>
        </div>
      </section>
      <section id="why" className="section">
        <div className="container">
          <div className="section-heading center"><span className="eyebrow">BUILT AROUND YOU</span><h2>A better way to shop online.</h2><p>Simple, fast and transparent from the first click to delivery.</p></div>
          <div className="feature-grid">
            {[
              ['⚡','Fast & focused','Find what you need with smart search and clean product discovery.'],
              ['🔐','Secure by design','JWT authentication, protected resources and safe payment flows.'],
              ['📦','Order visibility','Follow your order lifecycle from pending to delivered.']
            ].map(([icon,title,desc]) => <div className="feature-card" key={title}><div className="feature-icon">{icon}</div><h3>{title}</h3><p>{desc}</p></div>)}
          </div>
        </div>
      </section>
    </>
  )
}

function Shop() {
  const [products, setProducts] = useState<Product[]>([])
  const [categories, setCategories] = useState<Category[]>([])
  const [query, setQuery] = useState('')
  const [category, setCategory] = useState('all')
  const [sort, setSort] = useState('featured')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [notice, setNotice] = useState('')

  useEffect(() => {
    setLoading(true)
    setError('')
    Promise.all([getProducts(), getCategories()])
      .then(([p, c]) => { setProducts(p.filter(x => x.active)); setCategories(c.filter(x => x.active !== false)) })
      .catch(() => setError('We could not load the store right now. Please try again.'))
      .finally(() => setLoading(false))
  }, [])

  const filtered = useMemo(() => {
    const result = products.filter(p => {
      const text = `${p.name} ${p.description || ''} ${p.sku}`.toLowerCase()
      return (!query || text.includes(query.toLowerCase().trim())) &&
        (category === 'all' || String(p.categoryId) === category)
    })
    return [...result].sort((a, b) => {
      if (sort === 'price-low') return a.price - b.price
      if (sort === 'price-high') return b.price - a.price
      if (sort === 'name') return a.name.localeCompare(b.name)
      return a.id - b.id
    })
  }, [products, query, category, sort])

  const add = async (p: Product) => {
    try {
      await addToCart(p.id, 1)
      setNotice(`${p.name} added to cart`)
      setTimeout(() => setNotice(''), 2200)
    } catch {
      setNotice('Please sign in to add items to your cart')
      setTimeout(() => setNotice(''), 2200)
    }
  }

  return (
    <section className="section shop-page">
      <div className="container">
        <div className="shop-head">
          <div>
            <span className="eyebrow">THE COLLECTION</span>
            <h1>Shop essentials.</h1>
            <p>Thoughtfully selected products, ready when you are.</p>
          </div>
          <div className="search-wrap">⌕<input value={query} onChange={e => setQuery(e.target.value)} placeholder="Search products..." aria-label="Search products" /></div>
        </div>

        <div className="shop-controls">
          <div className="filter-row">
            <button className={category === 'all' ? 'filter active' : 'filter'} onClick={() => setCategory('all')}>All</button>
            {categories.map(c => <button key={c.id} className={category === String(c.id) ? 'filter active' : 'filter'} onClick={() => setCategory(String(c.id))}>{c.name}</button>)}
          </div>
          <label className="sort-control"><span>Sort</span><select value={sort} onChange={e => setSort(e.target.value)} aria-label="Sort products">
            <option value="featured">Featured</option>
            <option value="name">Name A–Z</option>
            <option value="price-low">Price: Low to high</option>
            <option value="price-high">Price: High to low</option>
          </select></label>
        </div>

        {notice && <div className="toast" role="status">{notice}</div>}
        {loading ? <div className="product-grid">{Array.from({ length: 6 }).map((_, i) => <div className="product-skeleton" key={i}><div /><span /><span /><b /></div>)}</div> :
          error ? <div className="empty"><div className="empty-icon">!</div><h2>Store unavailable</h2><p>{error}</p><button className="primary-button" onClick={() => window.location.reload()}>Try again</button></div> :
          filtered.length === 0 ? <Empty title="No products found" text="Try another search or category." /> :
          <>
            <div className="results-bar"><span><b>{filtered.length}</b> product{filtered.length === 1 ? '' : 's'}</span>{query && <button className="text-button" onClick={() => setQuery('')}>Clear search ×</button>}</div>
            <div className="product-grid">{filtered.map(p => <ProductCard key={p.id} product={p} onAdd={() => add(p)} />)}</div>
          </>}
      </div>
    </section>
  )
}

function ProductCard({ product, onAdd }: { product: Product; onAdd: () => void }) {
  const image = product.imageUrl
  return <article className="product-card">
    <div className="product-image">{image ? <img src={image} alt={product.name} onError={e => { e.currentTarget.style.display = "none" }} /> : <div className="placeholder-art">{product.name.slice(0,1)}</div>}
      {product.stockQuantity <= 0 ? <span className="stock-badge sold">Out of stock</span> : product.stockQuantity < 5 ? <span className="stock-badge">Only {product.stockQuantity} left</span> : null}
    </div>
    <div className="product-body"><span className="product-category">{product.categoryName || 'Featured'}</span><h3>{product.name}</h3><p>{product.description || 'Premium everyday essential.'}</p><div className="product-bottom"><strong>{money(product.price)}</strong><button className="add-button" disabled={product.stockQuantity <= 0} onClick={onAdd}>+ Add</button></div></div>
  </article>
}

function Login({ onLogin }: { onLogin: (u: AuthUser) => void }) {
  const navigate = useNavigate()
  const [email,setEmail]=useState(''); const [password,setPassword]=useState(''); const [error,setError]=useState(''); const [busy,setBusy]=useState(false)
  const submit = async (e: FormEvent) => { e.preventDefault(); setBusy(true); setError(''); try { const u=await login(email,password); onLogin(u); navigate(u.role==='ADMIN'?'/admin':'/shop') } catch(err:any) { setError(err?.response?.data?.message || 'Unable to sign in. Check your credentials.') } finally { setBusy(false) } }
  return <AuthLayout title="Welcome back." subtitle="Sign in to continue your NovaCart journey."><form className="auth-form" onSubmit={submit}><Field label="Email" type="email" value={email} onChange={setEmail} placeholder="you@example.com" /><Field label="Password" type="password" value={password} onChange={setPassword} placeholder="••••••••" />{error&&<div className="form-error">{error}</div>}<button disabled={busy} className="primary-button full">{busy?'Signing in…':'Sign in →'}</button><p className="auth-switch">New here? <Link to="/register">Create an account</Link></p></form></AuthLayout>
}

function Register() {
  const navigate=useNavigate(); const [form,setForm]=useState({firstName:'',lastName:'',email:'',password:'',phoneNumber:''}); const [error,setError]=useState(''); const [busy,setBusy]=useState(false)
  const set=(k:string,v:string)=>setForm({...form,[k]:v})
  const submit=async(e:FormEvent)=>{e.preventDefault();setBusy(true);setError('');try{await register(form);navigate('/login')}catch(err:any){setError(err?.response?.data?.message||'Registration failed. Please check your details.')}finally{setBusy(false)}}
  return <AuthLayout title="Create your account." subtitle="Join NovaCart and make every order effortless."><form className="auth-form two-col" onSubmit={submit}><Field label="First name" value={form.firstName} onChange={v=>set('firstName',v)} placeholder="Rahul"/><Field label="Last name" value={form.lastName} onChange={v=>set('lastName',v)} placeholder="Revati"/><div className="span-2"><Field label="Email" type="email" value={form.email} onChange={v=>set('email',v)} placeholder="you@example.com"/></div><div className="span-2"><Field label="Password" type="password" value={form.password} onChange={v=>set('password',v)} placeholder="At least 6 characters"/></div><div className="span-2"><Field label="Phone (optional)" value={form.phoneNumber} onChange={v=>set('phoneNumber',v)} placeholder="10-digit mobile number"/></div>{error&&<div className="form-error span-2">{error}</div>}<button disabled={busy} className="primary-button full span-2">{busy?'Creating…':'Create account →'}</button><p className="auth-switch span-2">Already registered? <Link to="/login">Sign in</Link></p></form></AuthLayout>
}

function AuthLayout({title,subtitle,children}:{title:string;subtitle:string;children:ReactNode}) {
  return <section className="auth-page"><div className="auth-visual"><Link to="/" className="brand light"><span className="brand-mark">N</span><span>Nova<span>Cart</span></span></Link><div><span className="eyebrow">SHOP WITH CONFIDENCE</span><h2>Commerce that feels<br/><em>effortless.</em></h2><p>Secure accounts, transparent orders and a storefront designed around clarity.</p></div><small>© {new Date().getFullYear()} NovaCart</small></div><div className="auth-panel"><div className="auth-box"><span className="eyebrow">YOUR ACCOUNT</span><h1>{title}</h1><p>{subtitle}</p>{children}</div></div></section>
}

function Field({label,type='text',value,onChange,placeholder}:{label:string;type?:string;value:string;onChange:(v:string)=>void;placeholder?:string}) {
  return <label className="field"><span>{label}</span><input required={label!=='Phone (optional)'} type={type} value={value} onChange={e=>onChange(e.target.value)} placeholder={placeholder}/></label>
}

function CartPage() {
  const [cart, setCart] = useState<Cart | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [busyItem, setBusyItem] = useState<number | null>(null)
  const [clearing, setClearing] = useState(false)
  const [placing, setPlacing] = useState(false)
  const navigate = useNavigate()

  const loadCart = async () => {
    try {
      setError('')
      const result = await getCart()
      setCart(result)
    } catch (err: any) {
      console.error('Cart loading error:', err)
      setError(
        err?.response?.data?.message ||
        err?.response?.data?.error ||
        err?.message ||
        'Unable to load your cart.'
      )
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadCart()
  }, [])

  const changeQuantity = async (cartItemId: number, quantity: number) => {
    if (quantity < 1) return
    try {
      setBusyItem(cartItemId)
      await updateCartItem(cartItemId, quantity)
      await loadCart()
    } catch (err: any) {
      console.error('Cart quantity update error:', err)
      setError(err?.response?.data?.message || err?.response?.data?.error || 'Unable to update cart quantity.')
    } finally {
      setBusyItem(null)
    }
  }

  const removeItem = async (cartItemId: number) => {
    try {
      setBusyItem(cartItemId)
      await removeCartItem(cartItemId)
      await loadCart()
    } catch (err: any) {
      console.error('Cart remove error:', err)
      setError(err?.response?.data?.message || err?.response?.data?.error || 'Unable to remove this item.')
    } finally {
      setBusyItem(null)
    }
  }

  const handleClearCart = async () => {
    if (!window.confirm('Remove all items from your cart?')) return
    try {
      setClearing(true)
      await clearCart()
      await loadCart()
    } catch (err: any) {
      console.error('Clear cart error:', err)
      setError(err?.response?.data?.message || err?.response?.data?.error || 'Unable to clear your cart.')
    } finally {
      setClearing(false)
    }
  }

  const handlePlaceOrder = async () => {
    try {
      setPlacing(true)
      setError('')
      const order = await placeOrder()
      navigate(`/orders/${order.orderId}`)
    } catch (err: any) {
      console.error('Place order error:', err)
      setError(
        err?.response?.data?.message ||
        err?.response?.data?.error ||
        'Unable to place your order.'
      )
    } finally {
      setPlacing(false)
    }
  }

  if (loading) {
    return <section className="section"><div className="container"><div className="loading">Loading your cart…</div></div></section>
  }

  if (error && !cart) {
    return (
      <section className="section">
        <div className="container">
          <div className="empty">
            <div className="empty-icon">⚠</div>
            <h2>Cart unavailable</h2>
            <p>{error}</p>
            <div style={{ display: 'flex', gap: '12px', justifyContent: 'center', marginTop: '20px' }}>
              <button className="primary-button" onClick={() => { setLoading(true); loadCart() }}>Try again</button>
              <Link className="ghost-button" to="/shop">Browse products</Link>
            </div>
          </div>
        </div>
      </section>
    )
  }

  if (!cart?.items?.length) {
    return <section className="section"><div className="container"><Empty title="Your cart is empty" text="Add something beautiful from the store." action="Browse products" href="/shop" /></div></section>
  }

  const subtotal = cart.grandTotal ?? cart.totalAmount ?? cart.items.reduce((sum, item) => sum + Number(item.totalPrice || 0), 0)

  return (
    <section className="section">
      <div className="container">
        <div className="section-heading">
          <span className="eyebrow">YOUR BAG</span>
          <h1>Shopping cart.</h1>
          <p>Review your items before placing your order.</p>
        </div>

        {error && <div className="form-error" style={{ marginBottom: '20px' }}>{error}</div>}

        <div className="cart-layout">
          <div className="cart-list">
            {cart.items.map(item => (
              <div className="cart-item" key={item.cartItemId}>
                <div className="mini-image">
                  {item.productImage ? <img src={item.productImage} alt={item.productName} /> : <span>{item.productName?.charAt(0).toUpperCase() || 'P'}</span>}
                </div>
                <div className="cart-info">
                  <h3>{item.productName}</h3>
                  <small>{money(Number(item.unitPrice))} each</small>
                  <div className="qty">
                    <button disabled={busyItem === item.cartItemId || item.quantity <= 1} onClick={() => changeQuantity(item.cartItemId, item.quantity - 1)}>−</button>
                    <b>{item.quantity}</b>
                    <button disabled={busyItem === item.cartItemId} onClick={() => changeQuantity(item.cartItemId, item.quantity + 1)}>+</button>
                  </div>
                </div>
                <strong>{money(Number(item.totalPrice))}</strong>
                <button className="remove" disabled={busyItem === item.cartItemId} onClick={() => removeItem(item.cartItemId)} title="Remove item">×</button>
              </div>
            ))}
            <button className="text-button" disabled={clearing} onClick={handleClearCart}>{clearing ? 'Clearing…' : 'Clear cart'}</button>
          </div>

          <div className="summary-card">
            <span className="eyebrow">ORDER SUMMARY</span>
            <div className="summary-line"><span>Items</span><b>{cart.items.reduce((sum, item) => sum + item.quantity, 0)}</b></div>
            <div className="summary-line"><span>Subtotal</span><b>{money(Number(subtotal))}</b></div>
            <div className="summary-line"><span>Delivery</span><b>Free</b></div>
            <hr />
            <div className="summary-total"><span>Total</span><b>{money(Number(subtotal))}</b></div>
            <button className="primary-button full" disabled={placing} onClick={handlePlaceOrder}>{placing ? 'Placing order…' : 'Place order →'}</button>
            <Link className="ghost-button full" to="/shop" style={{ marginTop: '10px' }}>Continue shopping</Link>
            <small className="secure-note">🔒 Secure checkout powered by your OMS</small>
          </div>
        </div>
      </div>
    </section>
  )
}
function Orders() {
  const [orders,setOrders]=useState<Order[]>([]); const [loading,setLoading]=useState(true)
  useEffect(()=>{getOrders().then(setOrders).finally(()=>setLoading(false))},[])
  return <section className="section"><div className="container"><div className="section-heading"><span className="eyebrow">YOUR ACTIVITY</span><h1>Orders.</h1><p>Everything you've bought, in one place.</p></div>{loading?<div className="loading">Loading orders…</div>:orders.length===0?<Empty title="No orders yet" text="Your next order will appear here." action="Start shopping" href="/shop"/>:<div className="orders-list">{orders.map(o=><Link to={`/orders/${o.orderId}`} className="order-row" key={o.orderId}><div><span className="order-number">{o.orderNumber}</span><small>{date(o.orderDate)} · {o.items?.length||0} item(s)</small></div><div><Status status={o.status}/></div><strong>{money(o.totalAmount)}</strong><span>→</span></Link>)}</div>}</div></section>
}

function OrderDetails() {
  const { id: idParam } = useParams<{ id: string }>();
  const id = Number(idParam); const [order,setOrder]=useState<Order|null>(null); const [loading,setLoading]=useState(true); const [payment,setPayment]=useState<PaymentMethod>('UPI'); const [message,setMessage]=useState('')
  const load = async () => {
    try {
      const data = await getOrder(id)
      setOrder(data)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load()
  }, [id])
  const pay=async()=>{try{await makePayment(id,payment);setMessage('Payment completed successfully.')}catch(e:any){setMessage(e?.response?.data?.message||'Payment could not be completed.')} }
  const cancel=async()=>{try{await cancelOrder(id);setMessage('Order cancelled.');load()}catch(e:any){setMessage(e?.response?.data?.message||'Order cannot be cancelled.')} }
  if(loading)return <div className="loading section">Loading order…</div>
  if(!order)return <div className="section"><div className="container"><Empty title="Order not found" text="This order is unavailable."/></div></div>
  return <section className="section"><div className="container narrow"><Link className="back-link" to="/orders">← Back to orders</Link><div className="order-detail-head"><div><span className="eyebrow">ORDER DETAILS</span><h1>{order.orderNumber}</h1><p>Placed {date(order.orderDate)}</p></div><Status status={order.status}/></div><div className="timeline">{(['PENDING','CONFIRMED','SHIPPED','DELIVERED'] as OrderStatus[]).map((s,i)=><div className={`timeline-step ${order.status===s?'current':''} ${['PENDING','CONFIRMED','SHIPPED','DELIVERED'].indexOf(order.status)>=i?'done':''}`} key={s}><span>{i+1}</span><b>{s}</b></div>)}</div><div className="detail-card"><h2>Items</h2>{order.items?.map(i=><div className="detail-item" key={i.productId}><span>{i.productName} × {i.quantity}</span><b>{money(i.totalPrice)}</b></div>)}<hr/><div className="summary-total"><span>Total</span><b>{money(order.totalAmount)}</b></div></div>{message&&<div className="toast inline">{message}</div>}{order.status!=='CANCELLED'&&order.status!=='DELIVERED'&&<div className="detail-actions"><button className="danger-button" onClick={cancel}>Cancel order</button>{order.status==='PENDING'&&<div className="pay-box"><select value={payment} onChange={e=>setPayment(e.target.value as PaymentMethod)}><option value="UPI">UPI</option><option value="CARD">Card</option><option value="NET_BANKING">Net Banking</option><option value="WALLET">Wallet</option><option value="COD">Cash on Delivery</option></select><button className="primary-button" onClick={pay}>Pay {money(order.totalAmount)} →</button></div>}</div>}</div></section>
}

function Status({status}:{status:OrderStatus}) { return <span className={`status ${status.toLowerCase()}`}>{status.replace('_',' ')}</span> }

function Admin() {
  const [tab,setTab]=useState<'overview'|'products'|'categories'|'orders'>('overview'); const [products,setProducts]=useState<Product[]>([]); const [categories,setCategories]=useState<Category[]>([]); const [orders,setOrders]=useState<Order[]>([]); const [notice,setNotice]=useState('')
  const refresh=async()=>{const [p,c,o]=await Promise.all([getAdminProducts(),getCategories(),getOrders()]);setProducts(p);setCategories(c);setOrders(o)}
  useEffect(()=>{refresh().catch(()=>{})},[])
  const active=products.filter(p=>p.active).length; const revenue=orders.reduce((s,o)=>s+Number(o.totalAmount||0),0)
  return <section className="section admin-page"><div className="container"><div className="admin-head"><div><span className="eyebrow">CONTROL CENTER</span><h1>Admin dashboard.</h1><p>Manage your catalogue and keep orders moving.</p></div><span className="admin-badge">ADMIN ACCESS</span></div><div className="admin-tabs">{[['overview','Overview'],['products','Products'],['categories','Categories'],['orders','Orders']].map(([v,l])=><button className={tab===v?'active':''} onClick={()=>setTab(v as any)} key={v}>{l}</button>)}</div>{notice&&<div className="toast inline">{notice}</div>}{tab==='overview'&&<div className="stats-grid"><Stat label="Active products" value={active}/><Stat label="Categories" value={categories.length}/><Stat label="Orders" value={orders.length}/><Stat label="Order value" value={money(revenue)}/></div>}{tab==='products'&&<AdminProducts products={products} categories={categories} refresh={refresh} notify={setNotice}/>} {tab==='categories'&&<AdminCategories categories={categories} refresh={refresh} notify={setNotice}/>} {tab==='orders'&&<AdminOrders orders={orders} refresh={refresh} notify={setNotice}/>}</div></section>
}

function Stat({label,value}:{label:string;value:string|number}){return <div className="stat-card"><small>{label}</small><strong>{value}</strong><span>Live data</span></div>}

function AdminProducts({products,categories,refresh,notify}:{products:Product[];categories:Category[];refresh:()=>Promise<void>;notify:(s:string)=>void}) {
  const [editing,setEditing]=useState<Product|null>(null); const [importing,setImporting]=useState(false); const [form,setForm]=useState<any>({name:'',description:'',sku:'',price:0,stockQuantity:0,imageUrl:'',categoryId:categories[0]?.id||0,active:true})
  const downloadTemplate=()=>{
    const csv='name,description,sku,price,stockQuantity,imageUrl,categoryId\nSamsung Galaxy S25,Flagship Android smartphone,SAM-S25-001,79999,25,https://example.com/s25.jpg,1\nOnePlus 13,Premium Android smartphone,ONE-13-001,69999,30,https://example.com/oneplus13.jpg,1'
    const url=URL.createObjectURL(new Blob([csv],{type:'text/csv;charset=utf-8'})); const a=document.createElement('a'); a.href=url; a.download='novacart-products-template.csv'; a.click(); URL.revokeObjectURL(url)
  }
  const importFile=async(file:File)=>{setImporting(true); try { const result=await importProductsCsv(file); if(result.rejectedCount>0){notify(`Import rejected: ${result.rejectedCount} row(s). ${result.errors.slice(0,2).join(' | ')}`)} else {notify(`✓ ${result.importedCount} products imported successfully.`); await refresh()} } catch(e:any){notify(e?.response?.data?.message||'Unable to import CSV.')} finally {setImporting(false)}}
  const start=(p?:Product)=>{setEditing(p||null);setForm(p?{...p}:{name:'',description:'',sku:'',price:0,stockQuantity:0,imageUrl:'',categoryId:categories[0]?.id||0,active:true})}
  const save=async()=>{try{editing?await updateProduct(editing.id,form):await createProduct(form);notify('Product saved.');start();await refresh()}catch(e:any){notify(e?.response?.data?.message||'Unable to save product.')}}
  return <div><div className="admin-toolbar"><div><h2>Products</h2><small>Bulk import supports CSV files with validation.</small></div><div style={{display:'flex',gap:8,flexWrap:'wrap'}}><button className="ghost-button small" onClick={downloadTemplate}>Download CSV template</button><label className="ghost-button small" style={{cursor:'pointer'}}>{importing?'Importing…':'Import CSV'}<input type="file" accept=".csv,text/csv" hidden disabled={importing} onChange={e=>{const file=e.target.files?.[0]; if(file) importFile(file); e.currentTarget.value='' }}/></label><button className="primary-button small" onClick={()=>start()}>+ Add product</button></div></div>{editing!==null||form.name!==''?<div className="admin-form"><div className="form-grid">{['name','sku','description','imageUrl'].map(k=><label className={k==='description'?'span-2 field':'field'} key={k}><span>{k}</span><input value={form[k]} onChange={e=>setForm({...form,[k]:e.target.value})}/></label>)}<label className="field"><span>Price</span><input type="number" value={form.price} onChange={e=>setForm({...form,price:Number(e.target.value)})}/></label><label className="field"><span>Stock</span><input type="number" value={form.stockQuantity} onChange={e=>setForm({...form,stockQuantity:Number(e.target.value)})}/></label><label className="field"><span>Category</span><select value={form.categoryId} onChange={e=>setForm({...form,categoryId:Number(e.target.value)})}>{categories.map(c=><option key={c.id} value={c.id}>{c.name}</option>)}</select></label>{editing&&<label className="check-field"><input type="checkbox" checked={form.active} onChange={e=>setForm({...form,active:e.target.checked})}/> Active</label>}</div><div className="form-actions"><button className="ghost-button" onClick={()=>start()}>Close</button><button className="primary-button" onClick={save}>Save product</button></div></div>:null}<div className="table-card"><table><thead><tr><th>Product</th><th>SKU</th><th>Category</th><th>Price</th><th>Stock</th><th>Status</th><th></th></tr></thead><tbody>{products.map(p=><tr key={p.id}><td><b>{p.name}</b><small>{p.description}</small></td><td>{p.sku}</td><td>{p.categoryName}</td><td>{money(p.price)}</td><td>{p.stockQuantity}</td><td><span className={`status ${p.active?'confirmed':'cancelled'}`}>{p.active?'Active':'Inactive'}</span></td><td><button
      className="table-action"
      onClick={() => start(p)}
  >
    Edit
  </button>

    <button
        className="table-action"
        onClick={async () => {
          try {
            await updateProduct(p.id, {
              name: p.name,
              description: p.description || '',
              sku: p.sku,
              price: p.price,
              stockQuantity: p.stockQuantity,
              imageUrl: p.imageUrl || '',
              categoryId: p.categoryId || 0,
              active: !p.active,
            })

            notify(p.active ? 'Product deactivated.' : 'Product activated.')
            await refresh()
          } catch (e: any) {
            notify(
                e?.response?.data?.message ||
                'Unable to change product status.'
            )
          }
        }}
    >
      {p.active ? 'Deactivate' : 'Activate'}
    </button></td></tr>)}</tbody></table></div></div>
}

function AdminCategories({categories,refresh,notify}:{categories:Category[];refresh:()=>Promise<void>;notify:(s:string)=>void}) {
  const [name,setName]=useState('');const [description,setDescription]=useState('')
  const save=async()=>{if(!name.trim())return;try{await createCategory(name,description);setName('');setDescription('');notify('Category created.');await refresh()}catch(e:any){notify(e?.response?.data?.message||'Unable to create category.')}}
  return <div><div className="admin-toolbar"><h2>Categories</h2></div><div className="inline-form"><input value={name} onChange={e=>setName(e.target.value)} placeholder="Category name"/><input value={description} onChange={e=>setDescription(e.target.value)} placeholder="Description"/><button className="primary-button" onClick={save}>Create</button></div><div className="category-admin-grid">{categories.map(c=><div className="category-admin-card" key={c.id}><span className="feature-icon">◈</span><h3>{c.name}</h3><p>{c.description||'No description'}</p></div>)}</div></div>
}

function AdminOrders({orders,refresh,notify}:{orders:Order[];refresh:()=>Promise<void>;notify:(s:string)=>void}) {
  const statuses:OrderStatus[]=['PENDING','CONFIRMED','SHIPPED','DELIVERED','CANCELLED']
  const change=async(id:number,status:OrderStatus)=>{try{await updateOrderStatus(id,status);notify('Order status updated.');await refresh()}catch(e:any){notify(e?.response?.data?.message||'Unable to update status.')}}
  return <div><div className="admin-toolbar"><h2>Orders</h2></div><div className="table-card"><table><thead><tr><th>Order</th><th>Date</th><th>Amount</th><th>Status</th><th>Update</th></tr></thead><tbody>{orders.map(o=><tr key={o.orderId}><td><b>{o.orderNumber}</b><small>{o.items?.length||0} item(s)</small></td><td>{date(o.orderDate)}</td><td>{money(o.totalAmount)}</td><td><Status status={o.status}/></td><td><select value={o.status} onChange={e=>change(o.orderId,e.target.value as OrderStatus)}>{statuses.map(s=><option key={s}>{s}</option>)}</select></td></tr>)}</tbody></table></div></div>
}

function Empty({title,text,action,href}:{title:string;text:string;action?:string;href?:string}){return <div className="empty"><div className="empty-icon">◌</div><h2>{title}</h2><p>{text}</p>{action&&href&&<Link className="primary-button" to={href}>{action} →</Link>}</div>}
function Footer(){return <footer><div className="container footer-inner"><div><Link to="/" className="brand"><span className="brand-mark">N</span><span>Nova<span>Cart</span></span></Link><p>Modern commerce, thoughtfully managed.</p></div><div><small>Order Management System</small><small>Spring Boot · React · MySQL · Redis · Kafka</small></div><div><small>© {new Date().getFullYear()} NovaCart</small></div></div></footer>}

export default App
