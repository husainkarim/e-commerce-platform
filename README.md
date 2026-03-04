# E-Commerce Platform

<div align="center">

A full-stack, production-ready **microservices-based e-commerce platform** built with Spring Boot, Angular, and modern DevOps practices.

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-green.svg)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-20.3.7-red.svg)](https://angular.io/)

[Features](#features) • [Architecture](#-architecture) • [Quick Start](#-quick-start) • [Development](#-development) • [Deployment](#-deployment)

</div>

---

## Overview

This e-commerce platform demonstrates modern software architecture principles with a **microservices backend**, **event-driven communication**, and a **responsive Angular frontend**. It supports user management, product management, media uploads, seller dashboards, and includes enterprise-grade CI/CD pipelines with code quality gates.

## Table of Contents

- [Features](#features)
- [Architecture](#-architecture)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Quick Start](#-quick-start)
- [Backend Services](#backend-services)
- [Frontend](#frontend)
- [Development](#-development)
- [CI/CD & DevOps](#-cicd--devops)
- [Deployment](#-deployment)
- [Troubleshooting](#-troubleshooting)
- [License](#-license)

---

## Features

✨ **Core Features**
- 🔐 User authentication with role-based access control (Customer/Seller)
- 📦 Product management with categories and inventory tracking
- 🖼️ Media upload and management service
- 🛒 Shopping cart and checkout functionality
- 📊 Seller dashboard for product and order management
- 📬 Real-time event-driven communication via Kafka

🏗️ **Enterprise Features**
- Microservices architecture with API Gateway pattern
- Docker containerization for all services
- CI/CD pipeline with Jenkins and GitHub Webhooks
- Static code analysis with SonarQube
- Mandatory code reviews and status checks on protected branches
- Comprehensive error handling with custom error pages

---

## 🏗️ Architecture

### System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                      Client Layer (Browser)                 │
│                   Angular 20.3.7 Frontend                   │
└────────────────────────┬────────────────────────────────────┘
                         │ HTTP/HTTPS
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                      API Gateway                            │
│            (Spring Boot 3.5.6, Java 21)                     │
└────┬────────────┬──────────────┬─────────────────┬──────────┘
     │            │              │                 │
     ▼            ▼              ▼                 ▼
  ┌──────────┐ ┌──────────┐ ┌────────────┐  ┌──────────┐
  │  User    │ │ Product  │ │   Media    │  │  Order   │
  │ Service  │ │ Service  │ │  Service   │  │ Service  │
  └──────────┘ └──────────┘ └────────────┘  └──────────┘
     │            │              │                 │
     └────────────┼──────────────┼─────────────────┘
                  │ Events (Kafka)
                  ▼
          ┌─────────────────┐
          │  MongoDB Atlas  │
          │   (Databases)   │
          └─────────────────┘
                  │
          ┌─────────────────┐
          │  Firebase       │
          │  (Media Store)  │
          └─────────────────┘
```

### Microservices

| Service | Port | Responsibility |
|---------|------|-----------------|
| **API Gateway** | 8443 | Request routing, load balancing, authentication |
| **User Service** | 8100 | User authentication, profiles, role management |
| **Product Service** | 8200 | Product catalog, categories, inventory |
| **Media Service** | 8300 | File upload/download, image processing |
| **Order Service** | 8400 | Order management, payment processing |

---

## Tech Stack

### Backend
- **Framework**: Spring Boot 3.5.6
- **Language**: Java 21
- **Database**: MongoDB Atlas (NoSQL)
- **Message Broker**: Kafka (Event streaming)
- **Cloud Storage**: Firebase (Media files)
- **Build Tool**: Maven
- **Containerization**: Docker & Docker Compose

### Frontend
- **Framework**: Angular 20.3.7
- **Language**: TypeScript
- **Styling**: CSS3
- **HTTP Client**: Angular HttpClient
- **Routing**: Angular Router
- **Containerization**: Docker & Docker Compose

### DevOps & CI/CD
- **CI/CD**: Jenkins
- **Code Quality**: SonarQube
- **Container Registry**: Docker Hub
- **Tunneling**: ngrok (for GitHub webhook integration)
- **Version Control**: Git & GitHub

---

## Prerequisites

### System Requirements
- **OS**: Linux, macOS, or Windows (with WSL2)
- **Docker**: 20.10+
- **Docker Compose**: 1.29+
- **Node.js**: 18+ (for Angular development)
- **Java**: JDK 21+
- **Maven**: 3.8.1+

### Required Accounts
- MongoDB Atlas (free tier available)
- Firebase Console
- GitHub (for webhooks)

### Environment Setup
Create a `.env` file in the `backend/` directory and add `serviceAccountKey.json` from your Firebase account into your `media-service`.

```env
# MongoDB Configuration
MONGODB_URI=mongodb+srv://<username>:<password>@<cluster>.mongodb.net/<database>?retryWrites=true&w=majority

# JWT Configuration
JWT_SECRET=<your-jwt-secret-key>

# Https Certificate password
KEYSTORE_PASSWORD=<password>
```

---

## 🚀 Quick Start

### Option 1: Docker Compose (Recommended)

```bash
# Clone the repository
git clone https://github.com/husainkarim/e-commerce-platform.git
cd e-commerce-platform

# Start all services (DevOps stack)
make up

# View logs
make logs

# Stop all services
make down
```

### Option 2: Manual Local Setup

#### Backend Services & Frontend

```bash
cd backend

# Build all microservices
make build

# Start all services
make up

# Check service status
make logs

# Application will be available at http://localhost:4200
```

---

## Backend Services

### Service Details

#### API Gateway
- **Port**: 8443
- **Role**: Central entry point, request routing, authentication
- **Key Endpoints**:
  - `POST /auth/login` - User login
  - `POST /auth/register` - User registration
  - All requests are routed to respective microservices

#### User Service
- **Port**: 8100
- **Role**: User management and authentication
- **Key Responsibilities**:
  - User registration and authentication
  - Profile management
  - Role assignment (Customer/Seller)
  - User preferences

#### Product Service
- **Port**: 8200
- **Role**: Product catalog management
- **Key Responsibilities**:
  - Product CRUD operations
  - Category management
  - Inventory tracking
  - Search and filtering

#### Media Service
- **Port**: 8300
- **Role**: Media file management
- **Key Responsibilities**:
  - File upload/download
  - Image processing
  - Firebase storage integration
  - File metadata management

#### Order Service
- **Port**: 8400
- **Role**: Order and transaction management
- **Key Responsibilities**:
  - Order creation and tracking
  - Order status management
  - Order history

### Building Backend Services

```bash
cd backend

# Build Docker images
make build

# Build and start with logs
make up
make logs

# Rebuild and restart
make restart

# Clean up all containers and volumes
make remove
```

### Backend Makefile Commands

| Command | Description |
|---------|-------------|
| `make up` | Start all services in detached mode |
| `make down` | Stop all services |
| `make build` | Build all Docker images |
| `make logs` | Stream logs from all services |
| `make restart` | Restart all services |
| `make remove` | Remove all containers, volumes, and images |
| `make status` | Show running containers status |
| `make jar` | Create required JAR files |

---

## Frontend

### Project Structure

```
frontend/
├── src/
│   ├── app/
│   │   ├── components/          # Angular components
│   │   ├── services/            # API and auth services
│   │   ├── routes.ts            # Route configuration
│   │   └── config.ts            # App configuration
│   ├── assets/                  # Static assets
│   ├── index.html               # Entry HTML
│   ├── main.ts                  # Bootstrap file
│   └── styles.css               # Global styles
├── angular.json                 # Angular config
├── package.json                 # Dependencies
└── Dockerfile                   # Production image
```

### Available Routes

| Route | Component | Description |
|-------|-----------|-------------|
| `/` | HomeComponent | Landing page |
| `/products` | ProductsComponent | Product listing |
| `/products/:id` | ProductDetailComponent | Product details |
| `/login` | LoginComponent | User login |
| `/signup` | SignupComponent | User registration |
| `/profile/:id` | ProfileComponent | User profile |
| `/edit-profile/:id` | EditProfileComponent | Edit profile |
| `/seller/dashboard` | SellerDashboardComponent | Seller dashboard |
| `/seller/dashboard/create-product` | ProductFormComponent | Create product |
| `/seller/dashboard/edit-product/:id` | ProductFormComponent | Edit product |
| `/seller/dashboard/media/:id` | ManageMediaComponent | Manage media |
| `/cart` | CartComponent | Shopping cart |
| `/checkout` | CheckoutComponent | Checkout process |

### Frontend Development

```bash
cd frontend

# Install dependencies
npm install

# Start development server (with auto-reload)
npm start

# Build for production
npm run build

# Run unit tests
npm test

# Generate new component
ng generate component component-name

# Generate new service
ng generate service service-name
```

---

## 🛠️ Development

### Local Development Setup

1. **Set up environment variables** (see [Prerequisites](#prerequisites))

2. **Backend Development**:
   ```bash
   cd backend
   make build
   make up
   make logs  # Monitor services
   ```

3. **Frontend Development**:
   ```bash
   cd frontend
   npm install
   npm start  # Starts at http://localhost:4200
   ```

4. **Verify all services**:
   - API Gateway: http://localhost:8000
   - User Service: http://localhost:8001
   - Product Service: http://localhost:8002
   - Media Service: http://localhost:8003
   - Order Service: http://localhost:8004
   - Frontend: http://localhost:4200

### Code Quality Standards

We maintain high code quality through:
- **Linting**: ESLint (Frontend), Checkstyle (Backend)
- **Testing**: Unit tests for all components and services
- **Documentation**: JavaDoc (Backend) and inline comments
- **Code Reviews**: Mandatory reviews on all pull requests

### Testing

**Backend**:
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ServiceNameTest
```

**Frontend**:
```bash
# Run all tests
npm test

# Run tests with coverage
npm test -- --code-coverage
```

---

## 🛠️ CI/CD & DevOps

### DevOps Stack

The project includes a comprehensive DevOps setup with Jenkins, SonarQube, and PostgreSQL:

```bash
# Start DevOps stack (root directory)
make up

# View logs
make logs

# Stop all services
make down
```

### Services & Ports

| Service | Port | Access |
|---------|------|--------|
| **Jenkins** | `8080` | http://localhost:8080 |
| **SonarQube** | `9000` | http://localhost:9000 |
| **PostgreSQL** | `5432` | Internal (for SonarQube) |
| **ngrok** | `4040` | http://localhost:4040 |

### CI/CD Pipeline

The **Jenkinsfile** orchestrates:
1. **Build**: Compile code using Maven/npm
2. **Code Analysis**: SonarQube static analysis
3. **Testing**: Run unit and integration tests
4. **Quality Gates**: Enforce code quality standards
5. **Deployment**: Deploy to staging/production environments

### GitHub Webhook Integration

1. **Start ngrok tunnel**:
   ```bash
   ngrok http 8080
   ```

2. **Copy the generated URL** (e.g., `https://your-id.ngrok-free.app`)

3. **Add to GitHub Webhook** (Repository Settings → Webhooks):
   - Payload URL: `https://your-id.ngrok-free.app/github-webhook/`
   - Content type: `application/json`
   - Events: `Push events`

4. **Verify** the webhook is active and builds trigger automatically on push.

### Code Quality Gates

Protected branches (`main`, `production`) require:
- ✅ All status checks passed (Jenkins build, SonarQube quality gate)
- ✅ At least one code review approval
- ✅ All conversations resolved
- ✅ Up-to-date with base branch

---

## 🚀 Deployment

### Production Deployment

#### Using Docker Compose

```bash
# Build production images
docker compose -f docker-compose.yml build

# Start all services
docker compose up -d

# View logs
docker compose logs -f
```

#### Environment Variables for Production

```env

# MongoDB (Production cluster)
MONGODB_URI=mongodb+srv://<prod-user>:<prod-password>@<prod-cluster>.mongodb.net/ecommerce?retryWrites=true&w=majority

# Firebase
serviceAccountKey.json

# Security
JWT_SECRET=<strong-random-secret>

# Https Certificate password
KEYSTORE_PASSWORD=<password>

```

#### Health Checks

All services expose health endpoints:
- `GET /actuator/health` - Service health status
- `GET /actuator/health/liveness` - Liveness probe
- `GET /actuator/health/readiness` - Readiness probe

### Scaling

```bash
# Scale a specific service (e.g., product-service to 3 replicas)
docker compose up -d --scale product-service=3
```

---

## 🐛 Troubleshooting

### Common Issues

#### Services Won't Start

```bash
# Check Docker daemon is running
docker ps

# Check logs for specific service
docker logs <container-name>

# Clear and rebuild everything
make remove
make build
make up
```

#### Port Already in Use

```bash
# Find process using port (e.g., 8080)
lsof -i :8080

# Kill the process
kill -9 <PID>
```

#### MongoDB Connection Issues

- Verify MongoDB Atlas IP whitelist includes your IP
- Check credentials in `.env` file
- Ensure VPN is connected if required by your cluster

#### Frontend Build Errors

```bash
# Clear node_modules and reinstall
rm -rf frontend/node_modules
rm frontend/package-lock.json
cd frontend
npm install
```

#### SonarQube Quality Gate Fails

1. Access SonarQube dashboard at http://localhost:9000
2. Login with default credentials (admin/admin)
3. Review issues in the project dashboard
4. Fix identified bugs and vulnerabilities
5. Commit and push to trigger re-analysis

### Health Check Script

```bash
#!/bin/bash
echo "Checking API Gateway..."
curl -s http://localhost:8000/actuator/health

echo "Checking User Service..."
curl -s http://localhost:8001/actuator/health

echo "Checking Product Service..."
curl -s http://localhost:8002/actuator/health

echo "Checking Media Service..."
curl -s http://localhost:8003/actuator/health
```

---

## 📄 License

This project is licensed under the **MIT License**.

### Summary

- **Permissions**: Use, copy, modify, merge, publish, and distribute the software
- **Conditions**: Include the original copyright notice and license in copies
- **Liability**: Provided "as is" without any warranty

For the full legal text, see the [LICENSE](LICENSE) file.

---

## 🤝 Support & Community

### Getting Help

- 📖 **Documentation**: Check [HELP.md](backend/api-gateway/HELP.md) files in service directories
- 🐛 **Issues**: Create an issue on GitHub for bugs and feature requests
- 💬 **Discussions**: Use GitHub Discussions for questions and ideas

### Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Angular Documentation](https://angular.io/docs)
- [Kafka Documentation](https://kafka.apache.org/documentation/)
- [MongoDB Documentation](https://docs.mongodb.com/)
- [Docker Documentation](https://docs.docker.com/)

---

## 📝 Development Notes

### Important Files

- [important.txt](important.txt) - Project notes and quick commands
- [Jenkinsfile](Jenkinsfile) - CI/CD pipeline configuration
- [docker-compose.yml](docker-compose.yml) - DevOps stack configuration
- [Makefile](Makefile) - Development commands

### Version Information

- **Java**: 21 (LTS)
- **Spring Boot**: 3.5.6
- **Angular**: 20.3.7
- **Maven**: 3.8.1+
- **Node.js**: 18+

---

## 🎯 Future Enhancements

- [ ] Payment gateway integration (Stripe/PayPal)
- [ ] Real-time notifications via WebSocket
- [ ] Advanced search with Elasticsearch
- [ ] AI-powered product recommendations
- [ ] Mobile app (React Native)
- [ ] Kubernetes deployment manifests
- [ ] GraphQL API support
- [ ] Rate limiting and caching strategies
- [ ] Email notifications service
- [ ] Multi-language support (i18n)

---

## 📊 Project Statistics

- **Backend Services**: 5 (API Gateway, User, Product, Media, Order)
- **Frontend Pages**: 15+ components
- **Test Coverage**: Unit and integration tests
- **Docker Containers**: 8+ (including DevOps stack)
- **CI/CD Pipeline**: Jenkins with SonarQube quality gates

---

<div align="center">

Made with ❤️ by [Husain Karim](https://github.com/husainkarim)

**Star ⭐ this repository if you find it helpful!**

</div>
