# E-Commerce Backend (Spring Boot + MongoDB)

## Project Overview
A production-style backend for an E-Commerce platform handling Products, Carts, Orders, and Mock Payments with Webhook support.

## Tech Stack
* **Java 17**
* **Spring Boot 3.x** (Web, Data MongoDB)
* **MongoDB** (Database)
* **Lombok** (Boilerplate reduction)

## Architecture
1.  **Controllers**: REST endpoints.
2.  **Services**: Business logic (Stock management, Cart conversion).
3.  **Webhook**: A local endpoint (`/api/webhooks/payment`) simulates a 3rd party payment gateway callback.
4.  **Database**: MongoDB documents with relationships handled logically.

## How to Run
1.  Ensure MongoDB is running locally on port `27017`.
2.  Clone the repository.
3.  Run `mvn spring-boot:run`.
4.  App starts on `http://localhost:8080`.

## Testing Steps (Postman)
1.  **Create Product**: Use `POST /api/products`. Copy the returned `id`.
2.  **Add to Cart**: Use `POST /api/cart/add` with `userId` and `productId`.
3.  **Create Order**: Use `POST /api/orders`. This reduces stock and returns an `orderId`. Status is `CREATED`.
4.  **Pay**: Use `POST /api/payments/create` with `orderId`.
    * Returns status `PENDING`.
    * Wait 3 seconds.
    * Check Console: You will see "Webhook Received".
5.  **Verify**: Use `GET /api/orders/{orderId}`. Status should be `PAID`.
6.  **Bonus**: Test Cancellation `POST /api/orders/{id}/cancel` to see stock restored.
