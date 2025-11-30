# E-Commerce Platform

A full-stack **e-commerce platform** built with a microservices backend and Angular frontend. The platform supports user management, product management, media uploads, and seller dashboards, with real-time communication via Kafka.

---

## Table of Contents

* [Project Structure](#project-structure)
* [Backend](#backend)
* [Frontend](#frontend)
* [Tools & Technologies](#tools--technologies)
* [Frontend Routes](#frontend-routes)
* [Getting Started](#getting-started)
* [Future Enhancements](#future-enhancements)

---

## Project Structure

```
backend/
  ├── api-gateway
  ├── user-service
  ├── product-service
  └── media-service

frontend/
  └── angular-app
```

---

## Backend

The backend is built using **microservices architecture**. Each service is independently deployable and communicates via **Kafka** events.

### Services

* **API Gateway**: Routes requests to the appropriate microservices.
* **User Service**: Handles authentication, user profiles, and roles (customer/seller).
* **Product Service**: Manages products, categories, and inventory.
* **Media Service**: Handles uploading, storing, and retrieving media files.

---

## Tools & Technologies

* **Kafka**: For event-driven communication between microservices.
* **MongoDB Atlas**: Cloud database for storing user, product, and media metadata.
* **Firebase**: Storage solution for media uploads.
* **Angular**: Frontend framework for building dynamic user interfaces.

---

## Frontend

The frontend is developed with **Angular** and provides a responsive interface for both buyers and sellers.

### Pages & Routes

```typescript
export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'products', component: ProductsComponent },
  { path: 'products/:id', component: ProductDetailComponent },
  { path: 'login', component: LoginComponent },
  { path: 'signup', component: SignupComponent },
  { path: 'profile', component: ProfileComponent },
  { path: 'edit-profile/:id', component: EditProfileComponent },
  { path: 'profile/:id', component: ProfileComponent },
  { path: 'seller/dashboard', component: SellerDashboardComponent },
  { path: 'seller/dashboard/create-product', component: ProductFormComponent },
  { path: 'seller/dashboard/edit-product/:id', component: ProductFormComponent },
  { path: 'seller/dashboard/media/:id', component: ManageMediaComponent },
  { path: 'bad-request', component: BadRequest },
  { path: 'conflict', component: Conflict },
  { path: 'forbidden', component: Forbidden },
  { path: 'not-found', component: NotFoundComponent },
  { path: 'unauthorized', component: NotAuthorizedComponent },
  { path: 'server-error', component: ServerError },
  { path: '**', component: NotFoundComponent }
];
```

---

## Getting Started

### Prerequisites

* **Docker & Docker Compose**
* **Make** (for running Makefile commands)
* Node.js >= 22 (for frontend)
* Angular CLI >= 16 (for frontend)

---

Great! You already have a **very clear Makefile** for managing all backend services via Docker Compose. We can now integrate it cleanly into your README so users can easily understand how to use it. Here's a polished version of the **Backend section with Makefile instructions**:

---

## Backend

All backend services (`api-gateway`, `user-service`, `product-service`, `media-service`) are fully containerized with Docker and can be managed using the provided **Makefile**.

### Prerequisites

* Docker & Docker Compose installed
* `sudo` access (if required by Docker)

### Common Makefile Commands

Navigate to the backend folder:

```bash
cd backend
```

| Command        | Description                                                |
| -------------- | ---------------------------------------------------------- |
| `make build`   | Build all backend Docker images                            |
| `make up`      | Start all backend services in detached mode                |
| `make down`    | Stop all backend services                                  |
| `make restart` | Restart all backend services                               |
| `make logs`    | Stream logs from all services                              |
| `make remove`  | Remove all containers, volumes, images (with confirmation) |
| `make images`  | List all backend Docker images                             |
| `make status`  | Show running containers status                             |
| `make jar`     | Run `jar-files.sh` to create any required JARs             |

**Example Usage:**

```bash
# Build and start backend
make build
make up

# Follow logs
make logs

# Stop all services
make down

# Remove all containers, volumes, and images
make remove
```

**Note:** Ensure environment variables for **Kafka**, **MongoDB Atlas**, and **Firebase** are properly set before running the backend.

---

### Frontend

1. Navigate to the frontend folder:

   ```bash
   cd frontend/angular-app
   ```

2. Install dependencies:

   ```bash
   npm install
   ```

3. Run the frontend:

   ```bash
   ng serve
   ```

4. Open your browser at `http://localhost:4200`.

---

