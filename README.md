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

## 🛠️ CI/CD & Quality Assurance

The project employs a robust automation pipeline to ensure code quality, security, and seamless integration.

* **Jenkins**: Orchestrates the entire CI/CD lifecycle, automated via a `Jenkinsfile`.
* **SonarQube**: Performs static code analysis to detect bugs, vulnerabilities, and code smells across all microservices and the frontend.
* **ngrok**: Provides a secure tunnel to expose the local Jenkins instance, enabling **GitHub Webhooks** to trigger builds in real-time on every push.

---

## 🏗️ Infrastructure & Local Setup

### DevOps Stack (Dockerized)

The entire development and monitoring stack is containerized. You can spin up the DevOps environment using the `docker-compose` file located in the root/tools directory:

```bash
# Start Jenkins, SonarQube and Ngrok
make up

# stop them
make down

```

| Service | Port | Description |
| --- | --- | --- |
| **Jenkins** | `8080` | Pipeline orchestration & GitHub integration. |
| **SonarQube** | `9000` | Code quality dashboard & static analysis. |
| **Postgres** | `5432` | Database backend for SonarQube. |
| **Ngrok** | '4040' | Connect local Jenkins server with Github |

### Setting up the Webhook Tunnel

Since Jenkins is running inside a local Docker container, **ngrok** is required to bridge the connection from GitHub:

1. **Start the tunnel**:
```bash
ngrok http 8080

```


2. **Update GitHub**: Use the generated URL (e.g., `https://your-id.ngrok-free.app/github-webhook/`) in your Repository Webhook settings.

---

## 🛡️ Quality Gate & Branch Protection

To maintain high standards, the `main` and `production` branches utilize **Mandatory Code Reviews** and **Status Checks**.

* **Review and Approval**: Establishes a mandatory review process before merging pull requests.
* **Issue Resolution**: Requires all identified bugs or vulnerabilities to be resolved or justified before approval.
* **SonarQube Tracking**: Improvements and technical debt are tracked over time via SonarQube reports to ensure long-term maintainability.

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

Adding a **License** section is a crucial final step for a public repository. It clearly defines how others can use, modify, and distribute your e-commerce platform code.

Since this is a full-stack project, the **MIT License** is the most common choice—it is short, simple, and allows for both personal and commercial use while protecting you from liability.

Here is the section to add to the bottom of your **README.md**:

---

## 📄 License

This project is licensed under the **MIT License**.

### Summary:

* **Permissions**: You can use, copy, modify, merge, publish, and even sell copies of the software.
* **Conditions**: The original copyright notice and this permission notice must be included in all copies or substantial portions of the software.
* **Liability**: The software is provided "as is," without warranty of any kind.

For the full legal text, please refer to the [LICENSE](https://github.com/husainkarim/e-commerce-platform/blob/main/LICENSE) file in the root of this repository.

---
