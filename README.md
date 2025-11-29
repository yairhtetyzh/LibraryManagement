# 📚 Library Management System
## 🌟 Features

- 📖 **Book Management** - Register, update, and retrieve book information
- 👥 **Borrower Management** - Handle borrower registration and profiles
- 🔄 **Borrowing Operations** - Borrow and return books with history tracking
- 🔒 **Data Validation** - Comprehensive input validation and error handling
- 📊 **API Documentation** - Interactive Swagger/OpenAPI documentation
- 🐳 **Container-Ready** - Optimized Docker images with multi-stage builds
- ☸️ **Kubernetes Support** - Production-ready Kubernetes manifests
- 🚀 **Fast Startup** - Optimized for quick deployment and graceful shutdown
- 📈 **Health Checks** - Liveness and readiness probes for orchestration
- 🔍 **Observability** - Built-in metrics, logging, and monitoring endpoints

## 🏗️ Technology Stack

| Category | Technology |
|----------|-----------|
| **Language** | Java 17 |
| **Framework** | Spring Boot 3.x |
| **Build Tool** | Maven 3.8+ |
| **Database** | MySQL 8.0 |
| **Connection Pool** | HikariCP |
| **API Documentation** | Springdoc OpenAPI 3 |
| **Containerization** | Docker |
| **Orchestration** | Kubernetes |
| **Testing** | JUnit 5, Mockito |

## 🚀 Quick Start

### Local Development

```bash
# Clone the repository
git clone https://github.com/yairhtetyzh/LibraryManagement.git
cd LibraryManagement

# Run with Maven (uses dev profile by default)
./mvnw spring-boot:run

# Or build and run JAR
./mvnw clean package
java -jar target/library-*.jar
```

Access the application:
- **Application**: http://localhost:8881
- **Swagger UI**: http://localhost:8881/swagger-ui.html
- **Health Check**: http://localhost:8881/actuator/health

### Docker Quick Start

```bash
# Build image
docker build -t library-api:latest .

# Run container
docker run -p 8881:8881 \
  -e DATABASE_URL=jdbc:mysql://host.docker.internal:3306/library \
  -e DATABASE_USER=root \
  -e DATABASE_PASSWORD=root \
  library-api:latest
```

### Build Commands

```bash
# Default build 
./mvnw clean package

# Skip tests
./mvnw clean package -DskipTests

# Run tests only
./mvnw test

# Generate test coverage report
./mvnw clean test jacoco:report
```

### Build Output

The build produces: `target/library-{version}.jar`

Example: `library-1.0.0.jar`

## ▶️ Running the Application

### Run JAR Directly

```bash
# Default profile (dev)
java -jar target/library-*.jar

# With JVM options
java -XX:MaxRAMPercentage=75.0 -jar target/library-*.jar

# With custom port
java -Dserver.port=9090 -jar target/library-*.jar
```

### Run with Maven

```bash
# Default profile
./mvnw spring-boot:run


## 🐳 Docker Deployment

### Build Docker Image

```bash
# Standard build
docker build -t library-api:latest .

# With custom tag
docker build -t library-api:1.0.0 .

### Run Docker Container

```bash
# Basic run
docker run -d --name library-app -p 8080:8080 library-api:latest

# With environment variables
docker run -d --name library-app \
  -p 8080:8080 \
  -p 8081:8081 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DATABASE_URL=jdbc:mysql://mysql:3306/library \
  -e DATABASE_USER=appuser \
  -e DATABASE_PASSWORD=secret \
  library-api:latest
```

## ☸️ Kubernetes Deployment

### Prerequisites

- Kubernetes cluster (or Minikube for local testing)
- `kubectl` CLI installed and configured

### Deploy to Kubernetes

```bash
# Apply all resources
kubectl apply -f k8s/library-app.yaml

# Verify deployment
kubectl get all

# Check pods
kubectl get pods

# Check services
kubectl get svc

# View logs
kubectl logs -f deployment/library-app

# Describe deployment
kubectl describe deployment library-app
```

### Access the Application

```bash
# Get service URL (LoadBalancer)
kubectl get svc library-service

# Port forward to localhost
kubectl port-forward deployment/library-app 8080:8080

# Access at http://localhost:8080
```


### Cleanup

```bash
# Delete all resources
kubectl delete -f k8s/library-app.yaml

```

## 📖 API Documentation

### Swagger/OpenAPI

Interactive API documentation is available via Swagger UI:

**Local Development:**
```
http://localhost:8881/swagger-ui.html
```

**Kubernetes/Minikube:**
```
http://<service-url>/swagger-ui.html
```
```

### API Endpoints

#### Book Management
- `POST /v1/book/register` - Register a new book
- `GET /v1/book/getall` - Get all books
- `POST /v1/book/borrow` - Borrow a book
- `POST /v1/book/{bookId}/return` - Return a borrowed book

#### Borrower Management
- `POST /v1/borrower/register` - Register a new borrower

#### Health & Monitoring
- `GET /actuator/health` - Application health
- `GET /actuator/health/liveness` - Liveness probe
- `GET /actuator/health/readiness` - Readiness probe
- `GET /actuator/metrics` - Application metrics

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `SPRING_PROFILES_ACTIVE` | `dev` | Active Spring profile |
| `DATABASE_URL` | `jdbc:mysql://localhost:3306/library` | Database connection URL |
| `DATABASE_USER` | `root` | Database username |
| `DATABASE_PASSWORD` | `password` | Database password |
| `SERVER_PORT` | `8080` | Application HTTP port |
| `MANAGEMENT_PORT` | `8081` | Management/actuator port |
| `LOG_LEVEL` | `INFO` | Root log level |
| `SHUTDOWN_TIMEOUT` | `30s` | Graceful shutdown timeout |

### Docker Environment Variables

```bash
docker run -d \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DATABASE_URL=jdbc:mysql://mysql:3306/library \
  -e DATABASE_USER=appuser \
  -e DATABASE_PASSWORD=secretpass \
  -e SERVER_PORT=8080 \
  -e LOG_LEVEL=INFO \
  library-api:latest
```

## 🧪 Testing

### Run Tests

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=BookServiceImplTest

# Run specific test method
./mvnw test -Dtest=BookServiceImplTest#register_Success_NewBook

# Run integration tests only
./mvnw verify -P integration-tests
```

### Test Coverage

```bash
# Generate coverage report
./mvnw clean test jacoco:report

# View report at: target/site/jacoco/index.html
open target/site/jacoco/index.html
```

### Test Categories

- **Unit Tests** - Service and repository layer tests
- **Integration Tests** - Full application context tests
- **API Tests** - REST endpoint tests
- **Coverage Target** - 80%+

## 📊 Monitoring & Observability

### Health Checks

```bash
# Liveness (is app alive?)
curl http://localhost:8081/actuator/health/liveness

# Readiness (can app accept traffic?)
curl http://localhost:8081/actuator/health/readiness

# Full health
curl http://localhost:8081/actuator/health
```
---

**Made with ❤️ using Spring Boot and Java 17**