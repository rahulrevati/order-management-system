# 🛒 Order Management System

A production-oriented **E-Commerce Order Management System** built with Spring Boot, MySQL, Redis, Kafka, JWT-based authentication, email notifications, and application monitoring using Prometheus and Grafana.

The system provides secure APIs for authentication, category and product management, shopping cart operations, order processing, payments, asynchronous Kafka-based notifications, and monitoring.

---

## 🚀 Features

### Authentication & Authorization
- User registration
- User login
- JWT-based authentication
- BCrypt password hashing
- Role-based authorization
- Admin and Customer roles
- Protected API endpoints
- Resource ownership validation

### Category Management
- Create category
- Get all categories
- Get category by ID
- Update category
- Delete category
- Duplicate category validation

### Product Management
- Create product
- Get products
- Get product by ID
- Update product
- Delete/deactivate product
- Product search
- Filtering
- Pagination
- Sorting
- Stock management
- Category association

### Shopping Cart
- Add products to cart
- Update cart quantity
- Remove cart items
- Clear cart
- Automatic quantity merging
- Stock validation
- Product availability validation
- Cart total calculation

### Order Management
- Place orders
- Generate unique order numbers
- Calculate order totals
- Automatic stock deduction
- View orders
- Order ownership protection
- Order status management
- Order cancellation
- Stock restoration after cancellation

### Payment Management
- Process payments
- Payment status tracking
- Transaction ID generation
- Duplicate payment protection
- Cancelled-order payment protection
- Payment ownership authorization

### Kafka & Email Notifications
- Publish order-created events using Kafka
- Consume order events asynchronously
- Send email notifications after order creation
- Kafka producer and consumer integration

### Monitoring
- Spring Boot Actuator
- Health monitoring
- Application information
- Prometheus metrics
- Grafana dashboards
- Business metrics for orders, Kafka messages, and emails

### Docker
- Redis container
- Kafka container
- Application container
- Prometheus container
- Grafana container
- Docker health checks
- Separate Docker Kafka configuration

---

## 🏗️ Architecture

```text
                         ┌─────────────────────┐
                         │      Client         │
                         │ Swagger / REST API  │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │    Spring Boot      │
                         │   REST Application  │
                         └──────────┬──────────┘
                                    │
              ┌─────────────────────┼─────────────────────┐
              │                     │                     │
              ▼                     ▼                     ▼
        ┌───────────┐        ┌────────────┐        ┌───────────┐
        │   MySQL   │        │   Redis    │        │   Kafka   │
        │ Database  │        │   Caching  │        │ Messaging │
        └───────────┘        └────────────┘        └─────┬─────┘
                                                         │
                                                         ▼
                                                  ┌─────────────┐
                                                  │   Email     │
                                                  │ Notification │
                                                  └─────────────┘

                         Monitoring
                              │
                    ┌─────────┴─────────┐
                    ▼                   ▼
               Prometheus           Grafana


---

## 🧩 Application Modules

### Authentication
Handles registration, login, JWT creation/validation, user details, roles, and password encoding.

### Category
Provides category CRUD operations and duplicate-name validation.

### Product
Provides product CRUD operations, category association, stock management, search, filtering, pagination, sorting, and active/inactive handling.

### Cart
Provides authenticated-user cart operations with stock checks, quantity merging, totals, and ownership protection.

### Order
Creates orders from the authenticated user's cart, calculates totals, deducts stock, manages status transitions, supports cancellation, restores stock when appropriate, and publishes order events.

### Payment
Processes payments against authorized orders, prevents duplicate payments, validates order state, and records transaction information.

### Email
Sends asynchronous order notifications after Kafka events are consumed.

### Monitoring
Provides business and infrastructure metrics through Micrometer, Prometheus, and Grafana.

---

## 🔌 API Summary

The complete API contract is available through Swagger/OpenAPI.

### Authentication

```text
POST /api/v1/auth/register
POST /api/v1/auth/login
```

### Categories

```text
POST   /api/v1/categories
GET    /api/v1/categories
GET    /api/v1/categories/{id}
PUT    /api/v1/categories/{id}
DELETE /api/v1/categories/{id}
```

### Products

```text
POST   /api/v1/products
GET    /api/v1/products
GET    /api/v1/products/{id}
PUT    /api/v1/products/{id}
DELETE /api/v1/products/{id}
```

### Cart

```text
POST   /api/v1/cart
GET    /api/v1/cart
PUT    /api/v1/cart/{cartItemId}
DELETE /api/v1/cart/{cartItemId}
DELETE /api/v1/cart
```

### Orders

```text
POST   /api/v1/orders
GET    /api/v1/orders
GET    /api/v1/orders/{orderId}
PATCH  /api/v1/orders/{orderId}/status
POST   /api/v1/orders/{orderId}/cancel
```

### Payments

```text
POST /api/v1/payments
GET  /api/v1/payments/{paymentId}
GET  /api/v1/payments/order/{orderId}
```

> Exact request and response schemas should be treated as defined by the OpenAPI specification exposed by the running application.

---

## 🛡️ Error Handling

The application uses centralized exception handling to return consistent HTTP responses.

Examples of handled conditions include:

| Condition | Typical HTTP status |
|---|---:|
| Invalid credentials | 401 |
| Missing/invalid authentication | 401 |
| Forbidden resource access | 403 |
| Resource not found | 404 |
| Validation error | 400 |
| Insufficient stock | 400 |
| Inactive product | 400 |
| Invalid order cancellation | 400 |
| Duplicate resource/payment | 409 |
| Invalid order status transition | 409 |
| Invalid application state | 409 |

The API also handles malformed JSON and invalid enum values with a client-facing `400 Bad Request` response.

---

## 🔐 Security Model

The application uses Spring Security with JWT authentication.

```text
Client
  │
  │ Login
  ▼
Authentication API
  │
  ▼
JWT Token
  │
  ▼
Authorization: Bearer <token>
  │
  ▼
JWT Filter
  │
  ▼
Spring Security
  │
  ├── Role check
  └── Resource ownership check
          │
          ▼
       Controller
```

Administrative operations are restricted to users with the `ADMIN` role.

Customer resources are protected by ownership checks so that one customer cannot access another customer's cart, orders, or payments.

---

## 📈 Business Metrics

The application records business-level metrics including:

```text
orders_total
emails_sent_total
kafka_messages_total
```

These metrics can be queried from Prometheus and visualized in Grafana.

Example PromQL:

```promql
orders_total
```

```promql
emails_sent_total
```

```promql
kafka_messages_total
```

Application availability:

```promql
up
```

---

## 🔍 Observability Endpoints

### Health

```text
GET /actuator/health
```

### Info

```text
GET /actuator/info
```

### Prometheus

```text
GET /actuator/prometheus
```

The hardened configuration limits exposed Actuator endpoints to the required monitoring endpoints.

---

## 🧪 Integration Verification

The Kafka/email integration has been verified using a real order flow.

Example verified sequence:

```text
POST /api/v1/orders
        ↓
Order created
        ↓
OrderPlacedEvent published
        ↓
Kafka order-created topic
        ↓
NotificationConsumer
        ↓
EmailService
        ↓
Customer receives notification
```

The verified order flow produced:

```text
Order Number: ORD-1789129544280
Amount:       79999.00
Status:       PENDING
```

The corresponding Kafka event was received by the consumer and the email service reported successful delivery to the configured test recipient.

---

## 🧰 Build

Build the application with Maven:

### Windows

```powershell
.\mvnw.cmd clean package
```

### Linux/macOS

```bash
./mvnw clean package
```

Run tests:

### Windows

```powershell
.\mvnw.cmd test
```

### Linux/macOS

```bash
./mvnw test
```

> The manual functional, integration, security, Kafka, Actuator, Prometheus, and Grafana validation described in this README was performed against the tested development environment.

---

## 🧹 Git Hygiene

The repository intentionally excludes development secrets and generated artifacts.

Do not commit:

```text
.env
target/
.idea/
*.iml
```

Commit the template:

```text
.env.example
```

The repository also contains:

```text
PRODUCTION_SETUP.md
```

for additional production setup guidance.

---

## 🚀 Recommended Deployment Flow

```text
Developer
   ↓
Git
   ↓
CI/CD
   ↓
Build & Test
   ↓
Container Image
   ↓
Deployment Environment
   ↓
Spring Boot Application
   ├── MySQL
   ├── Redis
   ├── Kafka
   └── SMTP/Email Provider
          ↓
   Prometheus
          ↓
      Grafana
```

For production, keep application secrets outside source control and inject them through the deployment environment or a dedicated secret-management system.

---

## 📋 Final Validation Checklist

Before considering the application ready for deployment:

- [x] Authentication tested
- [x] JWT authentication tested
- [x] Role-based authorization tested
- [x] Category APIs tested
- [x] Product APIs tested
- [x] Cart APIs tested
- [x] Order APIs tested
- [x] Order ownership tested
- [x] Order cancellation tested
- [x] Stock restoration tested
- [x] Payment APIs tested
- [x] Duplicate payment protection tested
- [x] Payment ownership tested
- [x] Kafka producer tested
- [x] Kafka consumer tested
- [x] Email notification tested
- [x] Redis tested
- [x] MySQL connection tested
- [x] Actuator health tested
- [x] Actuator info tested
- [x] Prometheus metrics tested
- [x] Prometheus target tested
- [x] Grafana datasource tested
- [x] Grafana metrics tested
- [x] Externalized secrets configured
- [x] Production configuration hardened
- [x] `.env` excluded from Git

---

## 👤 Author

**Rahul Revati**

GitHub:

https://github.com/rahulrevati

---

## 📄 License

No open-source license has currently been specified for this project.

If the repository is intended to be distributed publicly as open-source software, add an appropriate license file such as `LICENSE` and update this section accordingly.
