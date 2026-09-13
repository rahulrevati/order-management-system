# NovaCart Frontend

Modern React + TypeScript storefront for the Spring Boot Order Management System.

## Features

- Responsive e-commerce storefront
- JWT login and registration
- Product catalogue and search
- Category filters
- Cart management
- Order history and order details
- Payment-method selection
- Customer order cancellation
- Admin dashboard
- Product and category administration
- Admin order-status management
- API error/loading/empty states
- Production-ready environment variable for the backend URL

## Run locally

```bash
npm install
npm run dev
```

Open `http://localhost:5173`.

The Vite development server proxies `/api` requests to `http://localhost:8080`.

For another backend URL, create `.env`:

```env
VITE_API_BASE_URL=https://your-backend.example.com
```

## Build

```bash
npm run build
npm run preview
```

## Backend contract

The frontend consumes the existing Spring Boot endpoints:

- `/api/v1/auth`
- `/api/v1/categories`
- `/api/v1/products`
- `/api/v1/cart`
- `/api/v1/orders`
- `/api/v1/payments`

The backend must be running and configured with its own MySQL, Redis, Kafka and mail environment.

## Production

Recommended deployment:

- Frontend: Vercel or another static hosting provider
- Backend: Railway, Render or container hosting
- Database: managed MySQL
- Redis: managed Redis
- Kafka: managed Kafka
- Email: production SMTP/transactional email provider

Set `VITE_API_BASE_URL` to the deployed backend origin and configure backend CORS to allow the deployed frontend origin.

## Notes

This UI intentionally uses the current backend API without changing business functionality. Backend authentication, authorization and ownership rules remain authoritative.
