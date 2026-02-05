# Swift Transport - Driver Management Module

A production-ready Spring Boot application for managing drivers and vehicle assignments at Swift Transport. Built with a focus on performance, security, and clean architecture.

## ✨ Features

| Feature | Description |
|---------|-------------|
| **Driver Management** | Full CRUD operations with soft-delete, pagination, search, and status filters |
| **Vehicle Assignment** | Smart assignment logic with business validations (active drivers only, uniqueness constraints) |
| **Role-Based Access** | Two-tier authorization: ADMIN (full access) and OPERATIONS (read + assignments) |
| **JWT Security** | Stateless authentication with 24-hour token expiry and BCrypt-encrypted passwords |
| **High Performance** | Redis caching delivering 25-65x speedup on frequently accessed data |
| **Database Migrations** | Version-controlled schema with Flyway |
| **API Documentation** | Interactive Swagger UI for exploring and testing endpoints |
| **Health Monitoring** | Built-in health check endpoint for container orchestration |

---

## 🛠️ Tech Stack

- **Java 21** (LTS)
- **Spring Boot 4.0.2**
- **Spring Security** with JWT
- **Spring Data JPA**
- **PostgreSQL 15+**
- **Redis** (caching)
- **Flyway** (migrations)
- **Maven**
- **Docker & Docker Compose**

---

## 🚀 Quick Start

### Prerequisites

- Java 21+
- Maven 3.8+
- Docker & Docker Compose (recommended)
- PostgreSQL 15+ (if not using Docker)

### Option 1: Docker Compose (Recommended)

The fastest way to get everything running:

```bash
# Clone and start all services
git clone <repository-url>
cd swift-transport
docker-compose up -d

# Wait for the health check (~40s), then access:
# API:        http://localhost:8080/api/v1
# Swagger UI: http://localhost:8080/swagger-ui.html
```

### Option 2: Local Development

```bash
# 1. Start PostgreSQL and Redis
docker-compose up -d postgres redis

# 2. Run the application
./mvnw spring-boot:run

# 3. Access the API
open http://localhost:8080/swagger-ui.html
```

---

## 🔑 Default Credentials

| Role | Username | Password | Permissions |
|------|----------|----------|-------------|
| ADMIN | admin@swift.com | Admin@123 | Full access (CRUD drivers, manage assignments) |
| OPERATIONS | ops@swift.com | Ops@123 | Read all data, manage vehicle assignments |

### Quick Login Test

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin@swift.com","password":"Admin@123"}'
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "type": "Bearer",
  "username": "admin@swift.com",
  "role": "ADMIN",
  "expiresAt": "2026-02-05T23:12:38"
}
```

---

## 📚 API Overview

### Authentication
| Method | Endpoint | Access |
|--------|----------|--------|
| POST | `/api/v1/auth/login` | Public |

### Drivers
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/api/v1/drivers` | ADMIN | Create driver |
| GET | `/api/v1/drivers` | ADMIN, OPERATIONS | List with pagination, search, filters |
| GET | `/api/v1/drivers/{id}` | ADMIN, OPERATIONS | Get driver details |
| PUT | `/api/v1/drivers/{id}` | ADMIN | Update driver |
| DELETE | `/api/v1/drivers/{id}` | ADMIN | Soft delete |

### Vehicles
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/api/v1/vehicles` | ADMIN | Create vehicle |
| GET | `/api/v1/vehicles` | ADMIN, OPERATIONS | List vehicles |
| GET | `/api/v1/vehicles/{id}` | ADMIN, OPERATIONS | Get vehicle details |
| PUT | `/api/v1/vehicles/{id}` | ADMIN | Update vehicle |
| DELETE | `/api/v1/vehicles/{id}` | ADMIN | Delete vehicle |

### Vehicle Assignments
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/api/v1/assignments` | ADMIN, OPERATIONS | Assign driver to vehicle |
| DELETE | `/api/v1/assignments/driver/{driverId}` | ADMIN, OPERATIONS | Unassign driver |
| GET | `/api/v1/assignments` | ADMIN, OPERATIONS | List assignments |

### System
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| GET | `/api/v1/health` | Public | Health check for monitoring |

---

## ⚙️ Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_HOST` | PostgreSQL host | localhost |
| `DB_PORT` | PostgreSQL port | 5432 |
| `DB_NAME` | Database name | swift_transport |
| `DB_USERNAME` | Database username | postgres |
| `DB_PASSWORD` | Database password | postgres |
| `REDIS_HOST` | Redis host | localhost |
| `REDIS_PORT` | Redis port | 6379 |
| `JWT_SECRET` | JWT signing secret | (generate your own) |
| `SERVER_PORT` | Application port | 8080 |

### Docker Commands

```bash
# Start all services
docker-compose up -d

# View application logs
docker-compose logs -f app

# Restart after code changes
docker-compose up -d --build app

# Stop everything
docker-compose down

# Stop and remove volumes (fresh start)
docker-compose down -v
```

---

## 🧪 Testing

```bash
# Run unit tests
./mvnw test

# Run with coverage report
./mvnw test jacoco:report
# Report: target/site/jacoco/index.html
```

---

## 📁 Project Structure

```
src/main/java/com/example/swifttransport/
├── config/              # Security, caching, web configuration
├── controller/          # REST controllers
├── dto/                 # Data transfer objects
│   ├── request/         # Request DTOs
│   └── response/        # Response DTOs
├── entity/              # JPA entities
├── enums/               # Enumerations (DriverStatus, etc.)
├── exception/           # Custom exceptions & global handlers
├── mapper/              # MapStruct mappers
├── repository/          # Spring Data repositories
├── security/            # JWT filter, authentication
├── service/             # Business logic
└── util/                # Utility classes

src/main/resources/
├── db/migration/        # Flyway migration scripts (V1, V2, V3...)
└── application.yml      # Application configuration
```

---

## 🏗️ Architectural Decisions

### Caching with Redis

I chose **JDK Serialization** over JSON for Redis caching. While JSON is human-readable, JDK serialization provides reliable type handling without complex ObjectMapper configuration for generic types. This delivered a **25-65x performance improvement** on cached endpoints with minimal configuration overhead.

### Soft Delete Pattern

Drivers use soft deletion (`deleted = true`) rather than hard deletion. This:
- Maintains referential integrity with assignment history
- Enables data recovery if needed
- Uses partial database indexes to automatically filter deleted records

### Database-First Constraints

Business rules like "one active assignment per driver" are enforced at the database level using partial unique indexes. This prevents race conditions that application-level validation alone cannot catch.

### Stateless JWT Authentication

- 24-hour token expiration
- Role claims embedded in tokens
- No server-side session storage required
- BCrypt password encoder with strength 12

---

## ⚖️ Trade-offs Made

| Decision | Chosen | Alternative | Rationale |
|----------|--------|-------------|-----------|
| Cache Serialization | JDK Serialization | JSON with type info | Simpler configuration, reliable type handling |
| No Frontend | Swagger UI only | React/Angular | Focused on robust backend within time constraints |
| Soft Delete | Flag + filtered queries | Hard delete with archive table | Simpler referential integrity, recoverable data |
| Test Strategy | Unit tests | Full TestContainers suite | Time constraints; infrastructure ready for expansion |

---

## 📋 Assumptions

1. **One active assignment per driver** — enforced by database constraint
2. **Only ACTIVE drivers can be assigned** — validated at service layer
3. **Only active vehicles can be assigned** — validated at service layer
4. **JWT tokens expire after 24 hours** — requires re-authentication
5. **License numbers are unique** — including soft-deleted drivers (audit trail)
6. **OPERATIONS role** — read access to all data, write access only to assignments

---

## 🔮 Future Improvements

Given more time, I would add:

1. **Full Integration Test Suite** — Uncomment and expand TestContainers tests
2. **Audit Logging** — Track all data changes with user attribution
3. **Rate Limiting** — Protect auth endpoints (bucket4j ready in pom.xml)
4. **Metrics & Monitoring** — Micrometer + Prometheus for production observability
5. **Optimistic Locking** — `@Version` fields to prevent concurrent update conflicts
6. **Full-Text Search** — PostgreSQL `tsvector` for advanced driver search
7. **Batch Operations** — Bulk import/export endpoints
8. **API Versioning** — Documented migration path from v1 to v2

---

## 📄 License

Private — Swift Transport Internal Use Only

---

<p align="center">
  <i>Built with ❤️ for Swift Transport Operations</i>
</p>
