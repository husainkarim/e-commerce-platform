# E-Commerce Platform

<div align="center">

A full-stack, production-ready **microservices-based e-commerce platform** built with Spring Boot, Angular, and modern DevOps practices.

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-green.svg)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-20.3.7-red.svg)](https://angular.io/)

[![MongoDB](https://img.shields.io/badge/MongoDB-Database-47A248?logo=mongodb&logoColor=white)](https://www.mongodb.com/)
[![Jenkins](https://img.shields.io/badge/Jenkins-CI%2FCD-D24939?logo=jenkins&logoColor=white)](https://www.jenkins.io/)
[![SonarQube](https://img.shields.io/badge/SonarQube-Code%20Quality-4E9BCD?logo=sonarqube&logoColor=white)](https://www.sonarsource.com/products/sonarqube/)
[![ngrok](https://img.shields.io/badge/ngrok-Tunneling-1F1E37)](https://ngrok.com/)
[![Nexus](https://img.shields.io/badge/Nexus-Artifact%20Repository-3B7FC4)](https://www.sonatype.com/products/sonatype-nexus-repository)
[![Firebase Storage](https://img.shields.io/badge/Firebase-Media%20Storage-FFCA28?logo=firebase&logoColor=black)](https://firebase.google.com/)

[Architecture](#architecture) • [Tools](#tools-used) • [Quick Start](#run-the-project)

</div>

This repository contains a full-stack e-commerce platform built with:

- Frontend: Angular
- Backend: Java microservices (Spring Boot)
- Database: MongoDB
- DevOps and delivery tools: Jenkins, SonarQube, ngrok, Nexus

## Architecture

### Frontend

- Angular application in `frontend/`
- Consumes backend APIs through the API Gateway

### Backend Microservices

All backend services are under `backend/`:

- `api-gateway`
- `user-service`
- `product-service`
- `media-service` (uploads images to Firebase)
- `order-service`

### Database

- MongoDB is used as the primary data store for services.

## Tools Used

- Jenkins: CI/CD pipeline automation
- SonarQube: static code quality analysis
- ngrok: public tunnel for local webhook and integration testing
- Nexus: artifact repository management

## Repository Structure

```text
e-commerce-platform/
├── backend/
│   ├── api-gateway/
│   ├── user-service/
│   ├── product-service/
│   ├── media-service/
│   └── order-service/
├── frontend/
├── docker-compose.yml
├── Jenkinsfile
└── README.md
```

## Run the Project

### 1. Prerequisites

- Java 21+
- Maven
- Node.js and npm
- Docker and Docker Compose
- MongoDB instance

### 2. Start Backend and Infrastructure

From the project root:

```bash
make up
```

Or run directly with Docker Compose:

```bash
docker compose up -d
```

### 3. Start Frontend

```bash
cd frontend
npm install
npm start
```

## CI/CD and Quality

- `Jenkinsfile` defines the CI/CD pipeline.
- SonarQube checks code quality and helps enforce quality gates.
- Nexus can be used to store built artifacts and dependencies.

## Notes

- Configure environment variables in backend `.env` files before running.
- `media-service` requires Firebase credentials for image upload.

## License

This project is licensed under the MIT License. See `LICENSE` for details.
