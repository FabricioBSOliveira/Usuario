# 🔐 Usuario — User Management Microservice

> A production-ready **User Management** microservice built with **Java 17** and **Spring Boot 4**, featuring stateless JWT authentication, role-based access control, and full containerization with Docker. Part of a **multi-service distributed system** alongside [Agendador-tarefas](https://github.com/FabricioBSOliveira/Agendador-tarefas), [notificacao](https://github.com/FabricioBSOliveira/notificacao), and [bff-agendador-tarefas](https://github.com/FabricioBSOliveira/bff-agendador-tarefas).

---

## 🚀 What This Project Does

This service is the **authentication and identity backbone** of a task scheduling platform. It is responsible for:

- Registering and managing users
- Issuing and validating **JWT tokens** for stateless authentication
- Enforcing **role-based access control (RBAC)** via Spring Security
- Communicating with peer microservices via **OpenFeign** (declarative HTTP client)
- Exposing a self-documenting **REST API** via Springdoc/OpenAPI (Swagger UI)

---

## 🏗️ Architecture & Design Decisions

This service follows a **layered microservice architecture**:

```
┌─────────────────────────────────────────────┐
│              REST Controller Layer           │  ← Handles HTTP, input validation
├─────────────────────────────────────────────┤
│               Service Layer                  │  ← Business logic, orchestration
├─────────────────────────────────────────────┤
│             Repository Layer (JPA)           │  ← Data persistence abstraction
├─────────────────────────────────────────────┤
│              PostgreSQL Database             │  ← Relational data storage
└─────────────────────────────────────────────┘
```

**Why this matters:** Each layer has a single responsibility. The controller never touches the database; the repository never contains business rules. This separation makes the code easier to test, maintain, and extend — a fundamental principle in production Java backends.

---

## ⚙️ Tech Stack

| Layer | Technology | Purpose |
|---|---|---|
| Language | Java 17 | LTS release with modern features (records, sealed classes) |
| Framework | Spring Boot 4 | Opinionated, production-grade application framework |
| Security | Spring Security + JJWT 0.12 | Stateless JWT-based auth with role enforcement |
| Persistence | Spring Data JPA + PostgreSQL | ORM-backed relational data access |
| HTTP Client | Spring Cloud OpenFeign | Declarative inter-service communication |
| API Docs | Springdoc OpenAPI 3 | Auto-generated interactive Swagger UI |
| Build Tool | Gradle 8 | Dependency management and build pipeline |
| Containerization | Docker + Docker Compose | Reproducible dev & production environments |
| Code Quality | SonarQube | Static analysis, coverage, and code smell detection |
| Boilerplate Reduction | Lombok | Eliminates repetitive getter/setter/builder code |
| CI/CD | GitHub Actions | Automated build and test pipeline on every push |

---

## 🔒 Security Design

Authentication is **stateless** — the server holds no session state. Instead:

1. The client sends credentials → the service validates them and issues a signed **JWT**
2. Every subsequent request carries the token in the `Authorization: Bearer <token>` header
3. Spring Security intercepts the request, validates the token's signature and expiry, and enforces role-based rules
4. No session storage is needed — the service can scale horizontally without sticky sessions

This is the industry standard for microservice security.

---

## 🐳 Running Locally with Docker

The entire stack (app + database) starts with a single command — no local Java or PostgreSQL installation required.

```bash
# Clone the repository
git clone https://github.com/FabricioBSOliveira/Usuario.git
cd Usuario

# Start all services
docker compose up --build
```

The API will be available at: **http://localhost:8080**  
Swagger UI: **http://localhost:8080/swagger-ui/index.html**

### Docker Architecture

The Dockerfile uses a **multi-stage build** to keep the final image lean:

```dockerfile
# Stage 1: Build with full Gradle + JDK image
FROM gradle:8.14-jdk17 AS build
...
RUN gradle build --no-daemon

# Stage 2: Run with lightweight Alpine JDK image only
FROM eclipse-temurin:17-jdk-alpine
COPY --from=build /app/build/libs/*.jar /app/usuario.jar
```

**Why this matters:** The build stage has Gradle, source code, and all tooling. The final image has only the JAR and a minimal JDK — drastically reducing the attack surface and image size. This is a Docker best practice seen in production systems.

---

## 🛠️ Running Locally Without Docker

**Prerequisites:** Java 17, PostgreSQL

```bash
# Configure your database connection in application.properties
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/db_usuario
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=1234

# Build and run
./gradlew bootRun
```

---

## 📡 Key API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/usuario/cadastrar` | Register a new user |
| `POST` | `/usuario/login` | Authenticate and receive JWT |
| `GET` | `/usuario/{id}` | Retrieve user by ID (authenticated) |
| `PUT` | `/usuario/{id}` | Update user data (authenticated) |
| `DELETE` | `/usuario/{id}` | Delete user (authorized roles only) |

> Full interactive documentation available via Swagger UI at `/swagger-ui/index.html`

---

## 🧪 Testing & Quality Gates

- **JUnit 5** + Spring Boot Test for unit to be applied later
- **Spring Security Test** for auth-layer assertions
- **SonarQube** integrated for continuous code quality monitoring — catches bugs, vulnerabilities, and code smells before they reach production
- **GitHub Actions** CI pipeline runs tests automatically on every push to `master`

---

## 📦 Project Structure

```
src/
└── main/
    └── java/com/Fabricio/
        ├── controller/     # REST endpoints
        ├── service/        # Business logic
        ├── repository/     # JPA data access
        ├── model/          # Domain entities
        ├── dto/            # Data Transfer Objects
        ├── security/       # JWT filter, config, UserDetails
        └── config/         # OpenFeign, Security beans
```

---

## 🌐 Ecosystem Context

This service is one of four microservices in a distributed task scheduling platform:

```
[bff-agendador-tarefas]  ← API Gateway / BFF (aggregates all services)
        │
        ├── [Usuario]           ← Identity & Auth (this service)
        ├── [Agendador-tarefas] ← Task scheduling logic
        └── [notificacao]       ← Notification dispatch
```

Each service is independently deployable and communicates via HTTP using OpenFeign clients — a real-world microservices pattern.

---

## 👨‍💻 About the Author

Fabricio Butti Santos de Oliveira — a career-switching Mechanical Engineer who chose to apply the same systems constrains, critical thinking and problem solving to distributed software.

[![GitHub](https://img.shields.io/badge/GitHub-FabricioBSOliveira-181717?style=flat&logo=github)](https://github.com/FabricioBSOliveira)
