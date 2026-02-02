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

## License

Private - Swift Transport Internal Use Only
