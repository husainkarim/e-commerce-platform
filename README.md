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

## Nexus Setup and Publishing Documentation

This section documents how Nexus is configured in this project, how Maven and Docker integrate with Nexus, and how CI/CD publishes and retrieves artifacts.

### 1. Nexus Setup and Configuration

Nexus is started from the root `docker-compose.yml` and exposed on:

- `8081`: Nexus UI and repository endpoints
- `5000`: Docker hosted registry (push target)
- `5001`: Docker group/proxy registry (pull target)

Start infrastructure:

```bash
cd /home/halabbood/e-commerce-platform
docker compose up -d nexus
```

Recommended Nexus repositories:

- Maven hosted (release): `maven-releases`
- Maven hosted (snapshot): `maven-snapshots`
- Docker hosted (push): `docker-hosted` mapped to `5000`
- Docker group/proxy (pull): mapped to `5001`

Create credentials in Nexus for CI usage and map them to Jenkins credentials:

- `my-nexus-settings`: Maven `settings.xml` with `<servers>` credentials
- `nexus-credentials-id`: username/password for `docker login`

### 2. Maven and Docker Integration

#### Maven integration

Each backend service defines `distributionManagement` in `pom.xml`:

- Releases: `http://nexus:8081/repository/maven-releases/`
- Snapshots: `http://nexus:8081/repository/maven-snapshots/`

Example deploy command (per service):

```bash
cd backend/user-service
mvn -s /path/to/settings.xml clean deploy -DskipTests
```

Example artifact retrieval from Nexus:

```bash
# Retrieve a published release JAR directly from Nexus
curl -u <user>:<password> -O \
  http://localhost:8081/repository/maven-releases/com/safezone/user-service/0.0.1-SNAPSHOT/user-service-0.0.1-SNAPSHOT.jar
```

Note: Adjust group/artifact/version path to match your published coordinates.

#### Docker integration

The pipeline uses two registries:

- Push: `nexus:5000`
- Pull: `nexus:5001`

Manual publish example:

```bash
docker login localhost:5000 -u <user> -p <password>
docker build -t localhost:5000/user-service:latest backend/user-service
docker push localhost:5000/user-service:latest
```

Manual retrieval example:

```bash
docker login localhost:5001 -u <user> -p <password>
docker pull localhost:5001/user-service:latest
```

### 3. CI/CD Publishing Workflow

The Jenkins pipeline publishes both Maven and Docker artifacts.

Workflow summary:

1. Checkout source and initialize GitHub commit status.
2. Run SonarQube analysis for backend services and frontend.
3. Enforce quality gate (`waitForQualityGate`).
4. Publish Maven artifacts using:
	- `mvn -s ${MAVEN_SETTINGS} deploy -DskipTests`
5. Build and push Docker images to Nexus hosted registry (`5000`) with tags:
	- `${BUILD_NUMBER}`
	- `latest`
6. Deploy stack using backend compose and published images.

Example commands used in pipeline behavior:

```bash
# Maven publish stage
mvn -s ${MAVEN_SETTINGS} deploy -DskipTests

# Docker publish stage
docker build -t nexus:5000/user-service:${BUILD_NUMBER} -t nexus:5000/user-service:latest .
docker push nexus:5000/user-service:${BUILD_NUMBER}
docker push nexus:5000/user-service:latest
```

### 4. Screenshots Checklist

Add these screenshots to your project documentation for evidence:

1. Nexus repositories list (`maven-releases`, `maven-snapshots`, docker repos).
2. Nexus browse view showing a published Maven artifact.
3. Nexus browse view showing a published Docker image/tag.
4. Jenkins stage view showing:
	- `Publish Artifacts to Nexus`
	- `Docker Build & Push to Nexus`
5. Terminal output for successful `mvn deploy` and `docker pull`.

Suggested screenshot file naming convention:

- `docs/screenshots/nexus-repositories.png`
- `docs/screenshots/nexus-maven-artifact.png`
- `docs/screenshots/nexus-docker-image.png`
- `docs/screenshots/jenkins-nexus-stages.png`
- `docs/screenshots/artifact-retrieval-terminal.png`

## Notes

- Configure environment variables in backend `.env` files before running.
- `media-service` requires Firebase credentials for image upload.

## License

This project is licensed under the MIT License. See `LICENSE` for details.
