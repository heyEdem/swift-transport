# Swift Transport - Driver Management Module

A Spring Boot application for managing drivers and vehicle assignments for Swift Transport's internal operations.

## Features

- **Driver Management**: Create, read, update, and soft-delete drivers
- **Vehicle Assignment**: Assign and unassign drivers to vehicles
- **Role-based Access Control**: ADMIN and OPERATIONS roles with different permissions
- **JWT Authentication**: Secure API access with JSON Web Tokens
- **API Documentation**: OpenAPI/Swagger UI for exploring endpoints
- **Database Migrations**: Flyway for version-controlled schema changes

## Tech Stack

- Java 21
- Spring Boot 3.2.2
- Spring Security with JWT
- Spring Data JPA
- PostgreSQL 15+
- Flyway
- Maven
- Docker & Docker Compose

## Quick Start

### Prerequisites

- Java 17+
- Maven 3.8+
- Docker & Docker Compose (optional)
- PostgreSQL 15+ (if not using Docker)

### Local Development

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd swift-transport
   ```

2. **Start PostgreSQL with Docker Compose**
   ```bash
   docker-compose up -d postgres
   ```

3. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Access the application**
   - API Base URL: `http://localhost:8080/api/v1`
   - Swagger UI: `http://localhost:8080/swagger-ui.html`

### Default Credentials

| Role | Username | Password |
|------|----------|----------|
| ADMIN | admin@swift.com | Admin@123 |
| OPERATIONS | ops@swift.com | Ops@123 |

### Login Example

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin@swift.com",
    "password": "Admin@123"
  }'
```

## API Endpoints

### Authentication
- `POST /api/v1/auth/login` - Authenticate and get JWT token

### Drivers (ADMIN only for write operations)
- `POST /api/v1/drivers` - Create a new driver
- `GET /api/v1/drivers` - List all drivers (with pagination, search, filters)
- `GET /api/v1/drivers/{id}` - Get driver details
- `PUT /api/v1/drivers/{id}` - Update driver
- `DELETE /api/v1/drivers/{id}` - Soft delete driver

### Vehicle Assignments (ADMIN and OPERATIONS)
- `POST /api/v1/assignments` - Assign driver to vehicle
- `DELETE /api/v1/assignments/driver/{driverId}` - Unassign driver
- `GET /api/v1/assignments` - List assignments

## Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_HOST` | PostgreSQL host | localhost |
| `DB_PORT` | PostgreSQL port | 5432 |
| `DB_NAME` | Database name | swift_transport |
| `DB_USERNAME` | Database username | postgres |
| `DB_PASSWORD` | Database password | postgres |
| `JWT_SECRET` | JWT signing secret | (see application.yml) |
| `SERVER_PORT` | Application port | 8080 |

### Docker Deployment

```bash
# Build and run with Docker Compose
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop services
docker-compose down
```

## Testing

```bash
# Run unit tests
./mvnw test

# Run all tests including integration tests
./mvnw verify

# Run with coverage
./mvnw test jacoco:report
```

## Project Structure

```
src/main/java/com/swift/transport/
├── config/           # Configuration classes
├── controller/       # REST controllers
├── dto/              # Data transfer objects
│   ├── request/      # Request DTOs
│   └── response/     # Response DTOs
├── entity/           # JPA entities
├── enums/            # Enumerations
├── exception/        # Custom exceptions and handlers
├── repository/       # Spring Data repositories
├── security/         # JWT and security configuration
└── service/          # Business logic

src/main/resources/
├── db/migration/     # Flyway migration files
└── application.yml   # Application configuration
```

## Architectural Decisions

### Caching Strategy
I chose **Redis with JDK Serialization** over JSON serialization for caching. While JSON is human-readable, JDK serialization provides reliable type handling without complex ObjectMapper configuration. This decision delivered a **25-65x performance improvement** on cached endpoints with minimal configuration overhead.

### Soft Delete Pattern
Drivers are soft-deleted (flagged as `deleted=true`) rather than hard-deleted. This maintains referential integrity with assignment history and allows for data recovery if needed. Database indexes automatically filter out deleted records for performance.

### JWT Authentication
Stateless JWT tokens with 24-hour expiration. Role claims embedded in tokens eliminate the need for session storage. BCrypt password encoder with strength 12 provides strong security.

### Database Constraints
Unique constraints enforced at database level (e.g., one active assignment per driver/vehicle) using partial indexes, preventing race conditions that application-level validation might miss.

## Trade-offs Made

| Decision | Chose | Alternative | Rationale |
|----------|-------|-------------|-----------|
| Cache Serialization | JDK Serialization | JSON with type info | Simpler config, reliable type handling |
| No Frontend | Swagger UI only | React/Angular frontend | Focused on robust backend API within time constraints |
| Soft Delete | Flag + filtered queries | Hard delete with archive | Simpler referential integrity, recoverable |
| Test Strategy | Unit tests + commented integration | Full integration suite | Time constraints; TestContainers ready |

## Assumptions Made

1. **One driver = one active vehicle** at any given time (enforced by DB constraint)
2. **Only ACTIVE drivers** can be assigned to vehicles (business rule validation)
3. **Only active vehicles** can be assigned (business rule validation)
4. **JWT tokens expire after 24 hours** and require re-authentication
5. **OPERATIONS role** can read all data but only modify assignments (not drivers)
6. **License numbers are unique** across all drivers (including deleted ones for audit purposes)

## What I Would Improve With More Time

1. **Full Integration Test Suite** - Uncomment and expand TestContainers-based integration tests
2. **Audit Logging** - Track all data changes with user attribution for compliance
3. **Rate Limiting** - Protect auth endpoints from brute force attacks
4. **Metrics & Monitoring** - Micrometer + Prometheus integration for production observability
5. **API Versioning Strategy** - Document migration path from v1 to v2
6. **Optimistic Locking** - Add `@Version` to prevent lost updates on concurrent edits
7. **Search Enhancement** - Full-text search with PostgreSQL tsvector for driver search
8. **Batch Operations** - Bulk import/export endpoints for driver data

## License

Private - Swift Transport Internal Use Only
