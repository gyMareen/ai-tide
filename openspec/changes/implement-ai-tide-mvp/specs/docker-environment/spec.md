# Docker Environment Specification

## ADDED Requirements

### Requirement: Docker Compose Configuration

The system SHALL provide complete Docker Compose setup for one-click startup.

#### Scenario: Start all services

- **WHEN** user runs `docker-compose up -d`
- **THEN** system starts all services in dependency order
- **AND** services are healthy within reasonable time
- **AND** services can communicate with each other

#### Scenario: Stop all services

- **WHEN** user runs `docker-compose down`
- **THEN** system stops all services gracefully
- **AND** volumes are preserved
- **AND** networks are removed

#### Scenario: View logs

- **WHEN** user runs `docker-compose logs -f`
- **THEN** system streams logs from all services
- **AND** logs include timestamps and service names

#### Scenario: Restart services

- **WHEN** user runs `docker-compose restart <service>`
- **THEN** system restarts specified service
- **AND** other services remain running

### Requirement: MySQL Service

The system SHALL provide MySQL 8.0 database service.

#### Scenario: MySQL starts successfully

- **WHEN** docker-compose starts MySQL service
- **THEN** MySQL container pulls image if needed
- **AND** MySQL initializes with specified configuration
- **AND** MySQL accepts connections on port 3306
- **AND** MySQL creates specified database

#### Scenario: MySQL data persistence

- **WHEN** MySQL service starts with volume mount
- **THEN** data persists across container restarts
- **AND** data persists across container recreation

#### Scenario: MySQL initialization

- **WHEN** MySQL starts for the first time
- **THEN** system executes initialization scripts from /docker-entrypoint-initdb.d/
- **AND** system creates database schema from init.sql
- **AND** system loads seed data if provided

#### Scenario: MySQL connection configuration

- **WHEN** backend application connects to MySQL
- **THEN** connection uses specified credentials
- **AND** connection uses database name from environment
- **AND** connection uses MySQL as hostname (not localhost)

### Requirement: Redis Service

The system SHALL provide Redis 7 cache service.

#### Scenario: Redis starts successfully

- **WHEN** docker-compose starts Redis service
- **THEN** Redis container pulls image if needed
- **AND** Redis initializes with specified configuration
- **AND** Redis accepts connections on port 6379
- **AND** Redis enables AOF persistence

#### Scenario: Redis data persistence

- **WHEN** Redis service starts with volume mount
- **THEN** data persists across container restarts
- **AND** AOF file saves to mounted volume

#### Scenario: Redis configuration

- **WHEN** Redis starts with custom configuration
- **THEN** system loads redis.conf from volume
- **AND** system applies custom memory settings
- **AND** system enables maxmemory policy if configured

#### Scenario: Backend connects to Redis

- **WHEN** backend application connects to Redis
- **THEN** connection uses Redis as hostname
- **AND** connection uses default port 6379

### Requirement: Backend API Service

The system SHALL provide Spring Boot backend service.

#### Scenario: Backend starts successfully

- **WHEN** docker-compose starts backend service
- **THEN** backend container builds from Dockerfile
- **AND** backend starts Spring Boot application
- **AND** backend accepts HTTP connections on port 8080
- **AND** backend connects to MySQL and Redis

#### Scenario: Backend depends on databases

- **WHEN** docker-compose starts backend service
- **THEN** backend waits for MySQL to be healthy
- **AND** backend waits for Redis to be healthy
- **AND** backend starts only after dependencies are ready

#### Scenario: Backend environment configuration

- **WHEN** backend service starts
- **THEN** system loads environment variables from docker-compose.yml
- **AND** system sets SPRING_PROFILES_ACTIVE
- **AND** system sets database connection details
- **AND** system sets Redis connection details

#### Scenario: Backend health check

- **WHEN** docker-compose starts backend service
- **THEN** backend exposes /actuator/health endpoint
- **AND** docker-compose uses health check to verify startup
- **AND** service is marked healthy when endpoint returns UP

#### Scenario: Backend auto-restart on failure

- **WHEN** backend application crashes
- **THEN** docker-compose automatically restarts backend container
- **AND** system logs restart event
- **AND** system attempts restart up to configured limit

### Requirement: Frontend Web Service

The system SHALL provide Vue 3 frontend web service.

#### Scenario: Frontend starts successfully

- **WHEN** docker-compose starts frontend service
- **THEN** frontend container builds from Dockerfile
- **AND** frontend serves static files with Nginx
- **THEN** frontend accepts HTTP connections on port 80
- **AND** frontend serves index.html for SPA routes

#### Scenario: Frontend production build

- **WHEN** frontend Dockerfile builds image
- **THEN** system runs `npm install` for dependencies
- **AND** system runs `npm run build` for production bundle
- **AND** system copies dist/ directory to Nginx HTML directory
- **AND** system copies custom nginx.conf to Nginx config directory

#### Scenario: Frontend Nginx configuration

- **WHEN** frontend service starts with Nginx
- **THEN** Nginx serves static files efficiently
- **AND** Nginx proxies /api/ requests to backend service
- **AND** Nginx handles SPA routing (fallback to index.html)
- **AND** Nginx enables Gzip compression

#### Scenario: Frontend API proxy

- **WHEN** frontend makes API requests
- **THEN** Nginx proxies /api/ requests to backend:8080
- **AND** Nginx forwards Host header
- **AND** Nginx forwards X-Real-IP header
- **AND** Nginx forwards X-Forwarded-For header

#### Scenario: Frontend static file caching

- **WHEN** browsers request static files (CSS, JS, images)
- **THEN** Nginx sets appropriate cache headers
- **AND** Nginx sets Cache-Control for immutable files
- **AND** Nginx enables etag for efficient revalidation

### Requirement: Nginx Reverse Proxy

The system SHALL provide Nginx reverse proxy for production deployment.

#### Scenario: Nginx handles all traffic

- **WHEN** external requests arrive on port 80 or 443
- **THEN** Nginx routes requests to appropriate services
- **AND** Nginx serves frontend static files directly
- **AND** Nginx proxies API requests to backend

#### Scenario: Nginx SSL termination (future)

- **WHEN** SSL certificates are configured (optional for MVP)
- **THEN** Nginx handles HTTPS on port 443
- **AND** Nginx terminates SSL
- **AND** Nginx proxies to services over HTTP

#### Scenario: Nginx load balancing (future)

- **WHEN** multiple backend instances run
- **THEN** Nginx distributes requests across instances
- **AND** Nginx uses round-robin or specified algorithm
- **AND** Nginx performs health checks on instances

### Requirement: Volume Management

The system SHALL persist data across container lifecycles.

#### Scenario: MySQL data volume

- **WHEN** docker-compose creates MySQL service
- **THEN** system creates named volume for MySQL data
- **AND** volume persists database files
- **AND** volume survives container deletion

#### Scenario: Redis data volume

- **WHEN** docker-compose creates Redis service
- **THEN** system creates named volume for Redis data
- **AND** volume persists AOF file
- **AND** volume survives container deletion

#### Scenario: Upload files volume

- **WHEN** docker-compose creates backend service
- **THEN** system creates bind mount for uploads directory
- **AND** mount point is /app/uploads in container
- **AND** host directory persists uploaded files

#### Scenario: Logs volume (optional)

- **WHEN** docker-compose creates backend service
- **THEN** system optionally creates volume for logs
- **AND** application logs to mounted directory
- **AND** logs survive container restarts

### Requirement: Network Configuration

The system SHALL isolate services in Docker network.

#### Scenario: Service isolation

- **WHEN** docker-compose creates services
- **THEN** system creates bridge network for project
- **AND** services communicate via service names as hostnames
- **AND** external access only through exposed ports

#### Scenario: Internal service communication

- **WHEN** backend connects to MySQL and Redis
- **THEN** backend uses 'mysql' and 'redis' as hostnames
- **AND** connections are isolated from host network
- **AND** connections are fast and secure

#### Scenario: Port exposure

- **WHEN** docker-compose configures services
- **THEN** system exposes only necessary ports:
  - MySQL: 3306 (for local development only)
  - Redis: 6379 (for local development only)
  - Backend: 8080 (for local development only)
  - Frontend: 80 (for browser access)

### Requirement: Environment Variables

The system SHALL configure services via environment variables.

#### Scenario: Database credentials

- **WHEN** docker-compose starts services
- **THEN** system sets MYSQL_ROOT_PASSWORD
- **AND** system sets MYSQL_DATABASE
- **AND** system sets MYSQL_USER
- **AND** system sets MYSQL_PASSWORD

#### Scenario: Spring profile

- **WHEN** backend service starts
- **THEN** system sets SPRING_PROFILES_ACTIVE to 'prod'
- **AND** system sets DB_HOST to 'mysql'
- **AND** system sets DB_PORT to '3306'
- **AND** system sets REDIS_HOST to 'redis'
- **AND** system sets REDIS_PORT to '6379'

#### Scenario: JWT secret configuration

- **WHEN** backend service starts
- **THEN** system sets JWT_SECRET from environment
- **AND** system sets JWT_EXPIRATION from environment
- **AND** system uses secure random secret if not provided

### Requirement: Development Convenience

The system SHALL provide convenience commands for development.

#### Scenario: Build only changed services

- **WHEN** developer runs `docker-compose up --build`
- **THEN** system rebuilds services with changed Dockerfiles
- **AND** system reuses cached layers where possible

#### Scenario: View service logs individually

- **WHEN** developer runs `docker-compose logs -f <service>`
- **THEN** system streams logs only from specified service

#### Scenario: Execute command in container

- **WHEN** developer runs `docker-compose exec <service> <command>`
- **THEN** system executes command in running container
- **AND** system attaches to container stdin/stdout

#### Scenario: Open shell in container

- **WHEN** developer runs `docker-compose exec <service> sh`
- **THEN** system opens interactive shell in container
- **AND** developer can inspect container internals

### Requirement: Health Checks

The system SHALL verify service health before marking as ready.

#### Scenario: Backend health check

- **WHEN** backend service starts
- **THEN** docker-compose checks /actuator/health endpoint
- **AND** system waits for 200 OK response
- **AND** system marks backend as healthy only after successful check

#### Scenario: MySQL health check

- **WHEN** MySQL service starts
- **THEN** system verifies MySQL accepts connections
- **AND** system verifies database exists
- **AND** system marks MySQL as healthy

#### Scenario: Redis health check

- **WHEN** Redis service starts
- **THEN** system verifies Redis accepts PING command
- **AND** system marks Redis as healthy
