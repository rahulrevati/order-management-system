export type Role = 'ADMIN' | 'CUSTOMER' | string

export type OrderStatus =
  | 'PENDING'
  | 'CONFIRMED'
  | 'SHIPPED'
  | 'DELIVERED'
  | 'CANCELLED'

export type PaymentMethod =
  | 'CARD'
  | 'UPI'
  | 'NET_BANKING'
  | 'WALLET'
  | 'COD'

export interface AuthUser {
  firstName: string
  lastName: string
  userId: number
  email: string
  role: Role
  accessToken: string
  tokenType: string
}

export interface Category {
  id: number
  name: string
  description?: string
  active?: boolean
  createdAt?: string
  updatedAt?: string
}

export interface Product {
  id: number
  name: string
  description?: string
  price: number
  stockQuantity: number
  imageUrl?: string
  categoryId?: number
  categoryName?: string
  active: boolean
  sku: string
  createdAt?: string
  updatedAt?: string
}

export interface CartItem {
  cartItemId: number
  productId: number
  productName: string
  productImage?: string
  quantity: number
  unitPrice: number
  totalPrice: number
}

export interface Cart {
  items: CartItem[]
  totalAmount?: number
  totalItems?: number
  grandTotal?: number
}

export interface OrderItem {
  productId: number
  productName: string
  quantity: number
  unitPrice: number
  totalPrice: number
}

export interface Order {
  orderId: number
  id?: number
  orderNumber: string
  totalAmount: number
  status: OrderStatus
  orderDate: string
  items: OrderItem[]
}

export interface Payment {
  id?: number
  paymentId: string | number
  orderId: number
  orderNumber?: string
  amount: number
  paymentMethod: PaymentMethod
  paymentStatus?: string
  status?: string
  transactionId?: string
  paymentDate?: string
}
