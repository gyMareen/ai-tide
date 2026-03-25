# AI-Tide Docker Environment Setup

## Quick Start

### 1. Copy environment file
```bash
cp docker/.env.example docker/.env
# Edit docker/.env and change passwords for production
```

### 2. Start all services
```bash
docker-compose up -d
```

### 3. View logs
```bash
docker-compose logs -f
```

### 4. Stop services
```bash
docker-compose down
```

## Services

- **MySQL 8.0**: Database service (port 3306)
- **Redis 7**: Cache service (port 6379)
- **API**: Spring Boot backend (port 8080)
- **Web**: Vue 3 frontend (port 80)
- **Nginx**: Reverse proxy (ports 80/443)

## Access URLs

- Frontend: http://localhost
- API: http://localhost:8080/api
- Swagger UI: http://localhost:8080/swagger-ui.html
- Health Check: http://localhost:8080/actuator/health

## Database Credentials

Default credentials (change in production!):
- Root: `ai_tide_root_password`
- User: `ai_tide_user`
- Password: `ai_tide_password`
- Database: `ai_tide`

## Volumes

- `mysql-data`: MySQL data persistence
- `redis-data`: Redis data persistence
- `./uploads`: File uploads directory
- `./logs`: Application logs

## Development

### View logs from specific service
```bash
docker-compose logs api
docker-compose logs mysql
```

### Execute command in container
```bash
docker-compose exec api bash
docker-compose exec mysql mysql -u root -p ai_tide
```

### Rebuild services
```bash
docker-compose up -d --build
```

### Stop and remove volumes
```bash
docker-compose down -v
```
