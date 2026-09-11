import axios from 'axios'
import type {
  AuthUser,
  Cart,
  Category,
  Order,
  OrderStatus,
  Payment,
  PaymentMethod,
  Product,
} from './types'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  headers: { 'Content-Type': 'application/json' },
})

api.interceptors.request.use((config) => {
  const raw = localStorage.getItem('oms_user')

  if (raw) {
    try {
      const user = JSON.parse(raw) as AuthUser
      if (user.accessToken) {
        config.headers.Authorization = `Bearer ${user.accessToken}`
      }
    } catch {
      localStorage.removeItem('oms_user')
    }
  }

  return config
})

function unwrap<T>(response: { data: T | { data: T } }): T {
  const body = response.data
  if (body && typeof body === 'object' && 'data' in body) {
    return (body as { data: T }).data
  }
  return body as T
}

export async function login(email: string, password: string): Promise<AuthUser> {
  const response = await api.post<AuthUser>('/api/v1/auth/login', { email, password })
  return unwrap(response)
}

export async function register(payload: {
  email: string
  password: string
  firstName: string
  lastName: string
  phoneNumber?: string
}) {
  const response = await api.post('/api/v1/auth/register', payload)
  return unwrap(response)
}

export async function getCategories(): Promise<Category[]> {
  const response = await api.get<Category[] | { data: Category[] }>('/api/v1/categories')
  return unwrap(response)
}

export async function getAdminProducts(): Promise<Product[]> {
  const response = await api.get<Product[] | { data: Product[] }>('/api/v1/products/admin/all')
  return unwrap(response)
}

export async function getProducts(): Promise<Product[]> {
  const response = await api.get<Product[] | { data: Product[] }>('/api/v1/products')
  return unwrap(response)
}

export async function getProductsByCategory(categoryId: number): Promise<Product[]> {
  const response = await api.get<Product[] | { data: Product[] }>(`/api/v1/products/category/${categoryId}`)
  return unwrap(response)
}

export async function searchProducts(keyword: string): Promise<Product[]> {
  const response = await api.get<Product[] | { data: Product[] }>('/api/v1/products/search', { params: { keyword } })
  return unwrap(response)
}

export async function getCart(): Promise<Cart> {
  const response = await api.get<Cart | { data: Cart }>('/api/v1/cart')
  return unwrap(response)
}

export async function addToCart(productId: number, quantity: number): Promise<Cart> {
  const response = await api.post<Cart | { data: Cart }>('/api/v1/cart', { productId, quantity })
  return unwrap(response)
}

export async function updateCartItem(cartItemId: number, quantity: number): Promise<Cart> {
  const response = await api.put<Cart | { data: Cart }>(`/api/v1/cart/${cartItemId}`, { quantity })
  return unwrap(response)
}

export async function removeCartItem(cartItemId: number): Promise<void> {
  await api.delete(`/api/v1/cart/${cartItemId}`)
}

export async function clearCart(): Promise<void> {
  await api.delete('/api/v1/cart')
}

export async function placeOrder(): Promise<Order> {
  const response = await api.post<Order | { data: Order }>('/api/v1/orders')
  return unwrap(response)
}

export async function getOrders(): Promise<Order[]> {
  const response = await api.get<Order[] | { data: Order[] }>('/api/v1/orders')
  return unwrap(response)
}

export async function getOrder(orderId: number): Promise<Order> {
  const response = await api.get<Order | { data: Order }>(`/api/v1/orders/${orderId}`)
  return unwrap(response)
}

export async function cancelOrder(orderId: number): Promise<Order> {
  const response = await api.put<Order | { data: Order }>(`/api/v1/orders/${orderId}/cancel`)
  return unwrap(response)
}

export async function updateOrderStatus(orderId: number, status: OrderStatus): Promise<Order> {
  const response = await api.patch<Order | { data: Order }>(`/api/v1/orders/${orderId}/status`, { status })
  return unwrap(response)
}

export async function makePayment(orderId: number, paymentMethod: PaymentMethod): Promise<Payment> {
  const response = await api.post<Payment | { data: Payment }>('/api/v1/payments', { orderId, paymentMethod })
  return unwrap(response)
}

export async function createCategory(name: string, description: string): Promise<Category> {
  const response = await api.post<Category | { data: Category }>('/api/v1/categories', { name, description })
  return unwrap(response)
}

export async function createProduct(payload: {
  name: string
  description: string
  sku: string
  price: number
  stockQuantity: number
  imageUrl: string
  categoryId: number
}) {
  const response = await api.post<Product | { data: Product }>('/api/v1/products', payload)
  return unwrap(response)
}

export async function updateProduct(id: number, payload: {
  name: string
  description: string
  sku: string
  price: number
  stockQuantity: number
  imageUrl: string
  categoryId: number
  active: boolean
}) {
  const response = await api.put<Product | { data: Product }>(`/api/v1/products/${id}`, payload)
  return unwrap(response)
}

export async function deleteProduct(id: number): Promise<void> {
  await api.delete(`/api/v1/products/${id}`)
}

export async function importProductsCsv(file: File): Promise<{ importedCount: number; rejectedCount: number; errors: string[] }> {
  const form = new FormData()
  form.append('file', file)
  const response = await api.post<{ importedCount: number; rejectedCount: number; errors: string[] } | { data: { importedCount: number; rejectedCount: number; errors: string[] } }>('/api/v1/products/admin/bulk-import', form, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
  return unwrap(response)
}

export default api
