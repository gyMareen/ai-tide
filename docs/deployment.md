# AI-Tide 部署文档

## 1. 文档信息

| 属性 | 值 |
|------|-----|
| **文档版本** | v1.0.0 |
| **文档日期** | 2026-03-24 |
| **文档状态** | Draft |
| **作者** | DevOps 团队 |
| **项目名称** | AI-Tide (AI 前沿技术展示平台) |
| **最后更新** | 2026-03-24 |

---

## 2. 部署概述

### 2.1 部署架构

```
                            ┌─────────────────┐
                            │     Internet     │
                            └────────┬────────┘
                                     │
                                     │ HTTPS: 443
                                     │
                            ┌────────▼────────┐
                            │     Nginx       │
                            │  (反向代理/SSL)  │
                            └────────┬────────┘
                                     │
              ┌──────────────────────┼──────────────────────┐
              │                      │                      │
              │ HTTP: 8080           │ HTTP: 3000           │ HTTP: 3001
              │                      │                      │
    ┌─────────▼─────────┐  ┌────────▼────────┐  ┌─────────▼─────────┐
    │  Spring Boot API  │  │   Vue Frontend  │  │  Vue Admin Panel  │
    │  (ai-tide-api)    │  │  (ai-tide-web)  │  │ (ai-tide-admin)   │
    └─────────┬─────────┘  └────────┬────────┘  └─────────┬─────────┘
              │                      │                      │
              └──────────────────────┼──────────────────────┘ser
                                     │
                                     │ Docker Network
                                     │
    ┌────────────────────────────────┼────────────────────────────────┐
    │                                │                                │
    │       ┌───────────────────────▼──────────────────────┐         │
    │       │              MySQL 8.0                      │         │
    │       │           (3306 - 数据持久化)                │         │
    │       └───────────────────────┬──────────────────────┘         │
    │                                 │                                   │
    │       ┌──────────────────────────▼──────────────────────┐         │
    │       │              Redis 7                          │         │
    │       │           (6379 - 缓存/会话)                  │         │
    │       └───────────────────────┬──────────────────────┘          │
    │                                 │                                   │
    │       ┌──────────────────────────▼──────────────────────┐         │
    │       │           Elasticsearch 8                       │         │
    │       │           (9200/9300 - 搜索引擎)                │         │
    │       └───────────────────────────────────────────────┘         │
    └─────────────────────────────────────────────────────────────────┘

数据卷挂载:
- mysql-data: 持久化数据库数据
- redis-data: 持久化 Redis 数据
- es-data: 持久化 Elasticsearch 索引
- nginx-logs: Nginx 日志
- app-logs: 应用日志
```

### 2.2 环境说明

| 环境 | 用途 | 域名/地址 | 数据隔离 | 监控 |
|------|------|-----------|---------|------|
| **开发环境** | 本地开发调试 | localhost:5173,8080 | 独立数据库 | 本地日志 |
| **测试环境** | 功能测试、集成测试 | test.ai-tide.com | 独立数据库 | Prometheus + Grafana |
| **生产环境** | 正式线上服务 | www.ai-tide.com | 生产数据库 | ELK + Prometheus + Grafana |

### 2.3 技术选型

| 组件 | 技术 | 版本 | 说明 |
|------|------|------|------|
| 前端框架 | Vue.js | 3.4+ | 渐进式 JavaScript 框架 |
| 类型系统 | TypeScript | 5.3+ | JavaScript 超集 |
| 构建工具 | Vite | 5.0+ | 下一代前端构建工具 |
| UI 组件库 | Element Plus | 2.4+ | Vue 3 组件库 |
| 原子 CSS | UnoCSS | 0.58+ | 即时原子 CSS 引擎 |
| 后端框架 | Spring Boot | 3.2+ | Java 企业级应用框架 |
| Java 版本 | JDK | 17 LTS | 长期支持版本 |
| 安全框架 | Spring Security | 6.2+ | 认证授权框架 |
| ORM | Spring Data JPA | 3.2+ | Java 持久化 API |
| 关系数据库 | MySQL | 8.0+ | 开源关系数据库 |
| 缓存 | Redis | 7.2+ | 内存数据库 |
| 搜索引擎 | Elasticsearch | 8.11+ | 分布式搜索引擎 |
| 容器引擎 | Docker | 24.0+ | 容器化平台 |
| 容器编排 | Docker Compose | 2.20+ | 多容器应用编排 |
| 反向代理 | Nginx | 1.24+ | 高性能 Web 服务器 |

---

## 3. 环境准备

### 3.1 服务器要求

#### 生产环境推荐配置

| 组件 | 最低配置 | 推荐配置 | 说明 |
|------|---------|---------|------|
| **CPU** | 4 核 | 8 核+ | 核心数影响并发处理能力 |
| **内存** | 8 GB | 16 GB+ | Redis、ES、JVM 都需要内存 |
| **磁盘** | 100 GB SSD | 500 GB SSD+ | 用于数据库、日志、备份 |
| **带宽** | 10 Mbps | 100 Mbps+ | 影响页面加载速度 |
| **操作系统** | CentOS 8+ / Ubuntu 20.04+ | Ubuntu 22.04 LTS | Linux 服务器 |

#### 测试环境推荐配置

| 组件 | 最低配置 | 推荐配置 |
|------|---------|---------|
| **CPU** | 2 核 | 4 核 |
| **内存** | 4 GB | 8 GB |
| **磁盘** | 50 GB SSD | 100 GB SSD |
| **带宽** | 5 Mbps | 20 Mbps |
| **操作系统** | Ubuntu 20.04+ | Ubuntu 22.04 LTS |

#### 开发环境配置

| 组件 | 最低配置 |
|------|---------|
| **CPU** | 4 核 |
| **内存** | 8 GB |
| **磁盘** | 50 GB |
| **操作系统** | Windows / macOS / Linux |

### 3.2 软件要求

#### Linux 服务器安装清单

```bash
# 1. 更新系统
sudo apt update && sudo apt upgrade -y

# 2. 安装基础工具
sudo apt install -y curl wget git vim unzip net-tools \
    htop iotop sysstat lsof software-properties-common

# 3. 安装 Docker (Ubuntu/Debian)
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# 4. 安装 Docker Compose
sudo apt install -y docker-compose-plugin

# 5. 添加当前用户到 docker 组
sudo usermod -aG docker $USER
newgrp docker

# 6. 配置 Docker 镜像加速（可选）
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": [
    "https://docker.mirrors.ustc.edu.cn",
    "https://hub-mirror.c.163.com"
  ],
  "log-driver": "json-file",
  "log-opts": {
    "max-size": "100m",
    "max-file": "3"
  }
}
EOF

# 7. 重启 Docker 服务
sudo systemctl daemon-reload
sudo systemctl restart docker

# 8. 设置 Docker 开机自启
sudo systemctl enable docker

# 9. 验证安装
docker --version
docker compose version
```

#### Windows/macOS 开发环境

```bash
# 1. 安装 Docker Desktop
# 下载地址: https://www.docker.com/products/docker-desktop/

# 2. 安装 Node.js
# 下载地址: https://nodejs.org/ (推荐 LTS 版本 18+)

# 3. 安装 Java 17
# 下载地址: https://adoptium.net/temurin/releases/?version=17

# 4. 安装 Git
# 下载地址: https://git-scm.com/downloads

# 5. 验证安装
node --version
java -version
docker --version
docker compose version
```

### 3.3 网络配置

#### 端口规划

| 服务 | 内部端口 | 外部端口 | 协议 | 说明 |
|------|---------|---------|------|------|
| Nginx | 80 | 80 | HTTP | Web 访问 |
| Nginx | 443 | 443 | HTTPS | 安全 Web 访问 |
| Spring Boot API | 8080 | - | HTTP | 内部 API 调用 |
| Vue Frontend | 3000 | - | HTTP | 前端服务 |
| Vue Admin | 3001 | - | HTTP | 后台管理 |
| MySQL | 3306 | - | TCP | 数据库 |
| Redis | 6379 | - | TCP | 缓存 |
| Elasticsearch | 9200 | - | HTTP | ES HTTP API |
| Elasticsearch | 9300 | - | TCP | ES 节点通信 |

#### 防火墙配置

```bash
# Ubuntu UFW 配置
sudo ufw allow 22/tcp      # SSH
sudo ufw allow 80/tcp      # HTTP
sudo ufw allow 443/tcp     # HTTPS
sudo ufw enable

# CentOS firewalld 配置
sudo firewall-cmd --permanent --add-service=ssh
sudo firewall-cmd --permanent --add-service=http
sudo firewall-cmd --permanent --add-service=https
sudo firewall-cmd --reload
```

### 3.4 安全配置

#### SSH 安全加固

```bash
# 1. 禁用 root 登录
sudo sed -i 's/#PermitRootLogin yes/PermitRootLogin no/' /etc/ssh/sshd_config

# 2. 禁用密码登录（推荐使用 SSH 密钥）
sudo sed -i 's/#PasswordAuthentication yes/PasswordAuthentication no/' /etc/ssh/sshd_config

# 3. 修改默认端口（可选）
sudo sed -i 's/#Port 22/Port 2222/' /etc/ssh/sshd_config

# 4. 重启 SSH 服务
sudo systemctl restart sshd
```

#### 系统安全优化

```bash
# 1. 限制文件描述符数量
sudo tee -a /etc/security/limits.conf <<-'EOF'
* soft nofile 65536
* hard nofile 65536
* soft nproc 65536
* hard nproc 65536
EOF

# 2. 优化内核参数
sudo tee /etc/sysctl.d/99-ai-tide.conf <<-'EOF'
# 网络优化
net.core.somaxconn = 32768
net.ipv4.tcp_max_syn_backlog = 32768
net.ipv4.tcp_tw_reuse = 1
net.ipv4.tcp_fin_timeout = 30
net.ipv4.tcp_keepalive_time = 1200
net.ipv4.ip_local_port_range = 10000 65000

# 内存优化
vm.swappiness = 10
vm.dirty_ratio = 15
vm.dirty_background_ratio = 5

# 文件系统优化
fs.file-max = 1000000
EOF

# 3. 应用配置
sudo sysctl -p /etc/sysctl.d/99-ai-tide.conf
```

---

## 4. Docker 部署

### 4.1 Dockerfile 编写

#### 4.1.1 后端 Dockerfile (ai-tide-api/Dockerfile)

```dockerfile
# 多阶段构建 - 构建阶段
FROM maven:3.9-eclipse-temurin-17-alpine AS builder

# 设置工作目录
WORKDIR /build

# 复制 pom.xml 并下载依赖（利用 Docker 缓存）
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源代码
COPY src ./src

# 构建应用
RUN mvn clean package -DskipTests -B

# 运行阶段
FROM eclipse-temurin:17-jre-alpine

# 安装时区数据和字体（用于生成验证码、报表等）
RUN apk add --no-cache \
    tzdata \
    curl \
    fontconfig \
    ttf-dejavu && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone

# 创建应用用户（安全最佳实践）
RUN addgroup -S spring && adduser -S spring -G spring

# 设置工作目录
WORKDIR /app

# 从构建阶段复制 jar 包
COPY --from=builder /build/target/*.jar app.jar

# 创建日志目录
RUN mkdir -p /app/logs && chown -R spring:spring /app

# 切换到非 root 用户
USER spring

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# JVM 参数优化
ENV JAVA_OPTS="-Xms512m -Xmx1024m \
    -XX:+UseG1GC \
    -XX:MaxGCPauseMillis=200 \
    -XX:+HeapDumpOnOutOfMemoryError \
    -XX:HeapDumpPath=/app/logs/heapdump.hprof \
    -Djava.security.egd=file:/dev/./urandom \
    -Dspring.profiles.active=prod"

# 暴露端口
EXPOSE 8080

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

#### 4.1.2 前端 Dockerfile (ai-tide-web/Dockerfile)

```dockerfile
# 多阶段构建 - 构建阶段
FROM node:18-alpine AS builder

# 设置工作目录
WORKDIR /build

# 复制 package 文件
COPY package.json package-lock.json ./

# 安装依赖（利用 Docker 缓存）
RUN npm ci --only=production

# 复制源代码
COPY . .

# 设置构建环境变量
ARG VITE_API_BASE_URL=/api
ENV VITE_API_BASE_URL=${VITE_API_BASE_URL}

# 构建应用
RUN npm run build

# 运行阶段 - 使用 Nginx 提供静态文件
FROM nginx:1.24-alpine

# 安装时区数据
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone

# 从构建阶段复制构建产物
COPY --from=builder /build/dist /usr/share/nginx/html

# 复制 Nginx 配置
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 暴露端口
EXPOSE 3000

# 启动 Nginx
CMD ["nginx", "-g", "daemon off;"]
```

#### 4.1.3 前端 Nginx 配置 (ai-tide-web/nginx.conf)

```nginx
server {
    listen 3000;
    server_name localhost;
    root /usr/share/nginx/html;
    index index.html;

    # Gzip 压缩
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_types text/plain text/css text/xml text/javascript
               application/x-javascript application/xml+rss
               application/javascript application/json;

    # 静态资源缓存
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 30d;
        add_header Cache-Control "public, immutable";
    }

    # Vue Router history 模式
    location / {
        try_files $uri $uri/ /index.html;
    }

    # 安全头
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
}
```

### 4.2 docker-compose.yml 编写

#### 4.2.1 完整 docker-compose.yml

```yaml
version: '3.8'

services:
  # ==================== 数据库服务 ====================
  mysql:
    image: mysql:8.0
    container_name: ai-tide-mysql
    restart: unless-stopped
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD:-root123456}
      MYSQL_DATABASE: ${MYSQL_DATABASE:-ai_tide}
      MYSQL_USER: ${MYSQL_USER:-ai_tide}
      MYSQL_PASSWORD: ${MYSQL_PASSWORD:-ai_tide123456}
      TZ: Asia/Shanghai
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
      - ./docker/mysql/my.cnf:/etc/mysql/conf.d/my.cnf:ro
      - ./docker/mysql/init:/docker-entrypoint-initdb.d:ro
    networks:
      - ai-tide-network
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost", "-u", "root", "-p$${MYSQL_ROOT_PASSWORD}"]
      interval: 10s
      timeout: 5s
      retries: 5
    command:
      - --character-set-server=utf8mb4
      - --collation-server=utf8mb4_unicode_ci
      - --default-authentication-plugin=mysql_native_password

  redis:
    image: redis:7.2-alpine
    container_name: ai-tide-redis
    restart: unless-stopped
    environment:
      TZ: Asia/Shanghai
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data
      - ./docker/redis/redis.conf:/usr/local/etc/redis/redis.conf:ro
    networks:
      - ai-tide-network
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5
    command: redis-server /usr/local/etc/redis/redis.conf

  elasticsearch:
    image: elasticsearch:8.11.0
    image: docker.elastic.co/elasticsearch/elasticsearch:8.11.0
    container_name: ai-tide-elasticsearch
    restart: unless-stopped
    environment:
      discovery.type: single-node
      ES_JAVA_OPTS: -Xms512m -Xmx512m
      xpack.security.enabled: "false"
      TZ: Asia/Shanghai
    ports:
      - "9200:9200"
      - "9300:9300"
    volumes:
      - es-data:/usr/share/elasticsearch/data
    networks:
      - ai-tide-network
    healthcheck:
      test: ["CMD-SHELL", "curl -f http://localhost:9200/_cluster/health || exit 1"]
      interval: 30s
      timeout: 10s
      retries: 5
      start_period: 60s

  # ==================== 应用服务 ====================
  ai-tide-api:
    build:
      context: ./ai-tide-api
      dockerfile: Dockerfile
    image: ai-tide-api:latest
    container_name: ai-tide-api
    restart: unless-stopped
    environment:
      SPRING_PROFILES_ACTIVE: ${SPRING_PROFILES_ACTIVE:-prod}
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/${MYSQL_DATABASE:-ai_tide}?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8mb4
      SPRING_DATASOURCE_USERNAME: ${MYSQL_USER:-ai_tide}
      SPRING_DATASOURCE_PASSWORD: ${MYSQL_PASSWORD:-ai_tide123456}
      SPRING_REDIS_HOST: redis
      SPRING_REDIS_PORT: 6379
      SPRING_REDIS_PASSWORD: ${REDIS_PASSWORD:-}
      ELASTICSEARCH_HOST: elasticsearch
      ELASTICSEARCH_PORT: 9200
      JWT_SECRET: ${JWT_SECRET}
      TZ: Asia/Shanghai
    ports:
      - "8080:8080"
    volumes:
      - app-logs:/app/logs
      - ./docker/api/application-prod.yml:/app/application-prod.yml:ro
    networks:
      - ai-tide-network
    depends_on:
      mysql:
        condition: service_healthy
      redis:
        condition: service_healthy
      elasticsearch:
        condition: service_healthy

  ai-tide-web:
    build:
      context: ./ai-tide-web
      dockerfile: Dockerfile
      args:
        VITE_API_BASE_URL: ${API_BASE_URL:-/api}
    image: ai-tide-web:latest
    container_name: ai-tide-web
    restart: unless-stopped
    environment:
      TZ: Asia/Shanghai
    ports:
      - "3000:3000"
    networks:
      - ai-tide-network

  ai-tide-admin:
    build:
      context: ./ai-tide-admin
      dockerfile: Dockerfile
      args:
        VITE_API_BASE_URL: ${API_BASE_URL:-/api}
    image: ai-tide-admin:latest
    container_name: ai-tide-admin
    restart: unless-stopped
    environment:
      TZ: Asia/Shanghai
    ports:
      - "3001:3000"
    networks:
      - ai-tide-network

  # ==================== 反向代理 ====================
  nginx:
    image: nginx:1.24-alpine
    container_name: ai-tide-nginx
    restart: unless-stopped
    environment:
      TZ: Asia/Shanghai
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./docker/nginx/nginx.conf:/etc/nginx/nginx.conf:ro
      - ./docker/nginx/conf.d:/etc/nginx/conf.d:ro
      - ./docker/nginx/ssl:/etc/nginx/ssl:ro
      - nginx-logs:/var/log/nginx
    networks:
      - ai-tide-network
    depends_on:
      - ai-tide-api
      - ai-tide-web
      - ai-tide-admin

# ==================== 网络定义 ====================
networks:
  ai-tide-network:
    driver: bridge
    ipam:
      config:
        - subnet: 172.20.0.0/16

# ==================== 数据卷定义 ====================
volumes:
  mysql-data:
    driver: local
  redis-data:
    driver: local
  es-data:
    driver: local
  app-logs:
    driver: local
  nginx-logs:
    driver: local
```

### 4.3 环境变量配置

#### .env 文件示例 (.env)

```bash
# ==================== 环境配置 ====================
# 环境标识: dev, test, prod
SPRING_PROFILES_ACTIVE=prod

# ==================== MySQL 配置 ====================
MYSQL_ROOT_PASSWORD=your_strong_root_password_here
MYSQL_DATABASE=ai_tide
MYSQL_USER=ai_tide
MYSQL_PASSWORD=your_strong_mysql_password_here

# ==================== Redis 配置 ====================
REDIS_PASSWORD=your_strong_redis_password_here

# ==================== JWT 配置 ====================
# 生成随机密钥: openssl rand -base64 64
JWT_SECRET=your_jwt_secret_key_minimum_32_characters_long

# ==================== API 配置 ====================
API_BASE_URL=/api

# ==================== Elasticsearch 配置 ====================
ELASTICSEARCH_HOST=elasticsearch
ELASTICSEARCH_PORT=9200
```

#### .env.dev 文件示例 (.env.dev)

```bash
SPRING_PROFILES_ACTIVE=dev

MYSQL_ROOT_PASSWORD=root
MYSQL_DATABASE=ai_tide_dev
MYSQL_USER=ai_tide_dev
MYSQL_PASSWORD=dev123456

REDIS_PASSWORD=

JWT_SECRET=dev-secret-key-for-local-development

API_BASE_URL=http://localhost:8080/api

ELASTICSEARCH_HOST=elasticsearch
ELASTICSEARCH_PORT=9200
```

#### .env.test 文件示例 (.env.test)

```bash
SPRING_PROFILES_ACTIVE=test

MYSQL_ROOT_PASSWORD=test_root_password
MYSQL_DATABASE=ai_tide_test
MYSQL_USER=ai_tide_test
MYSQL_PASSWORD=test_password_123456

REDIS_PASSWORD=test_redis_password

JWT_SECRET=test-jwt-secret-key-change-in-production

API_BASE_URL=/api

ELASTICSEARCH_HOST=elasticsearch
ELASTICSEARCH_PORT=9200
```

#### .env.prod 文件示例 (.env.prod)

```bash
SPRING_PROFILES_ACTIVE=prod

MYSQL_ROOT_PASSWORD=prod_strong_root_password
MYSQL_DATABASE=ai_tide
MYSQL_USER=ai_tide
MYSQL_PASSWORD=prod_strong_password

REDIS_PASSWORD=prod_redis_password

JWT_SECRET=prod_jwt_secret_key_minimum_64_characters_long_and_random

API_BASE_URL=/api

ELASTICSEARCH_HOST=elasticsearch
ELASTICSEARCH_PORT=9200
```

### 4.4 数据卷挂载规划

```bash
# 数据卷目录结构
/data/ai-tide/
├── mysql/              # MySQL 数据
│   └── data/
├── redis/              # Redis 数据
│   └── data/
├── elasticsearch/      # Elasticsearch 数据
│   └── data/
├── app-logs/           # 应用日志
│   ├── api/
│   ├── web/
│   └── admin/
└── nginx-logs/         # Nginx 日志
    ├── access/
    └── error/
```

### 4.5 Docker 网络配置

#### 创建自定义网络

```bash
# 创建桥接网络
docker network create \
  --driver bridge \
  --subnet 172.20.0.0/16 \
  --gateway 172.20.0.1 \
  ai-tide-network

# 查看网络详情
docker network inspect ai-tide-network
```

---

## 5. Nginx 配置

### 5.1 主配置文件 (docker/nginx/nginx.conf)

```nginx
user nginx;
worker_processes auto;
error_log /var/log/nginx/error.log warn;
pid /var/run/nginx.pid;

events {
    worker_connections 2048;
    use epoll;
    multi_accept on;
}

http {
    include /etc/nginx/mime.types;
    default_type application/octet-stream;

    # 日志格式
    log_format main '$remote_addr - $remote_user [$time_local] "$request" '
                    $status $body_bytes_sent "$http_referer" '
                    '"$http_user_agent" "$http_x_forwarded_for"';

    log_format json escape=json '{'
        '"time":"$time_iso8601",'
        '"remote_addr":"$remote_addr",'
        '"remote_user":"$remote_user",'
        '"request":"$request",'
        '"status":$status,'
        '"body_bytes_sent":$body_bytes_sent,'
        '"request_time":$request_time,'
        '"http_referrer":"$http_referer",'
        '"http_user_agent":"$http_user_agent",'
        '"http_x_forwarded_for":"$http_x_forwarded_for"'
    '}';

    access_log /var/log/nginx/access.log main;

    # 性能优化
    sendfile on;
    tcp_nopush on;
    tcp_nodelay on;
    keepalive_timeout 65;
    keepalive_requests 100;
    types_hash_max_size 2048;
    client_max_body_size 50M;

    # Gzip 压缩
    gzip on;
    gzip_vary on;
    gzip_proxied any;
    gzip_comp_level 6;
    gzip_types text/plain text/css text/xml text/javascript
               application/x-javascript application/xml+rss
               application/javascript application/json
               application/xml application/rss+xml
               application/atom+xml image/svg+xml;

    # 安全头
    server_tokens off;
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Referrer-Policy "strict-origin-when-cross-origin" always;

    # 缓冲区优化
    client_body_buffer_size 128k;
    client_header_buffer_size 1k;
    large_client_header_buffers 4 16k;
    output_buffers 1 32k;
    postpone_output 1460;

    # 超时设置
    client_body_timeout 10;
    client_header_timeout 10;
    send_timeout 10;

    # 包含站点配置
    include /etc/nginx/conf.d/*.conf;
}
```

### 5.2 站点配置 (docker/nginx/conf.d/ai-tide.conf)

```nginx
# ==================== HTTP 重定向到 HTTPS ====================
server {
    listen 80;
    server_name www.ai-tide.com ai-tide.com;

    # Let's Encrypt 验证
    location ^~ /.well-known/acme-challenge/ {
        root /var/www/letsencrypt;
    }

    # 其他请求重定向到 HTTPS
    location / {
        return 301 https://$server_name$request_uri;
    }
}

# ==================== HTTPS 主配置 ====================
server {
    listen 443 ssl http2;
    server_name www.ai-tide.com ai-tide.com;

    # ==================== SSL 配置 ====================
    ssl_certificate /etc/nginx/ssl/fullchain.pem;
    ssl_certificate_key /etc/nginx/ssl/privkey.pem;
    ssl_trusted_certificate /etc/nginx/ssl/chain.pem;

    # SSL 协议和加密套件
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers 'ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256:ECDHE-ECDSA-AES256-GCM-SHA384:ECDHE-RSA-AES256-GCM-SHA384:ECDHE-ECDSA-CHACHA20-POLY1305:ECDHE-RSA-CHACHA20-POLY1305:DHE-RSA-AES128-GCM-SHA256:DHE-RSA-AES256-GCM-SHA384';
    ssl_prefer_server_ciphers off;

    # SSL 优化
    ssl_session_cache shared:SSL:10m;
    ssl_session_timeout 1d;
    ssl_session_tickets off;

    # OCSP Stapling
    ssl_stapling on;
    ssl_stapling_verify on;
    resolver 8.8.8.8 8.8.4.4 valid=300s;
    resolver_timeout 5s;

    # HSTS
    add_header Strict-Transport-Security "max-age=63072000; includeSubDomains; preload" always;

    # ==================== 日志 ====================
    access_log /var/log/nginx/ai-tide-access.log json;
    error_log /var/log/nginx/ai-tide-error.log warn;

    # ==================== 路由配置 ====================

    # API 路由 - 后端接口
    location /api/ {
        proxy_pass http://ai-tide-api:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        # 代理超时
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;

        # 缓冲区
        proxy_buffering on;
        proxy_buffer_size 4k;
        proxy_buffers 8 4k;
        proxy_busy_buffers_size 8k;
    }

    # 管理后台 - Vue Admin
    location /admin/ {
        alias /usr/share/nginx/admin/;
        try_files $uri $uri/ /admin/index.html;

        # 缓存控制
        location ~* ^/admin/static/ {
            expires 30d;
            add_header Cache-Control "public, immutable";
        }
    }

    # 前台 - Vue Web
    location / {
        alias /usr/share/nginx/web/;
        try_files $uri $uri/ /index.html;

        # 缓存控制
        location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
            expires 30d;
            add_header Cache-Control "public, immutable";
        }
    }

    # 健康检查端点
    location /health {
        access_log off;
        return 200 "healthy\n";
        add_header Content-Type text/plain;
    }
}
```

### 5.3 SSL 配置

#### 5.3.1 使用 Let's Encrypt 免费证书

```bash
# 1. 安装 Certbot
sudo apt install -y certbot

# 2. 获取证书 (DNS 验证方式 - 推荐)
certbot certonly --manual \
  --preferred-challenges dns \
  -d ai-tide.com \
  -d www.ai-tide.com

# 或者使用 HTTP 验证 (需要先启动 Nginx)
certbot certonly --webroot \
  -w /var/www/letsencrypt \
  -d ai-tide.com \
  -d www.ai-tide.com

# 3. 复制证书到项目目录
sudo cp /etc/letsencrypt/live/ai-tide.com/fullchain.pem ./docker/nginx/ssl/
sudo cp /etc/letsencrypt/live/ai-tide.com/privkey.pem ./docker/nginx/ssl/
sudo cp /etc/letsencrypt/live/ai-tide.com/chain.pem ./docker/nginx/ssl/

# 4. 设置证书自动续期
sudo certbot renew --dry-run

# 添加 cron 任务自动续期
(crontab -l 2>/dev/null; echo "0 2 * * * certbot renew --quiet --post-hook 'docker-compose -f /path/to/docker-compose.yml restart nginx'") | crontab -
```

#### 5.3.2 使用自签名证书 (开发环境)

```bash
# 生成自签名证书
openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
  -keyout ./docker/nginx/ssl/privkey.pem \
  -out ./docker/nginx/ssl/fullchain.pem \
  -subj "/C=CN/ST=Beijing/L=Beijing/O=AI-Tide/OU=Dev/CN=localhost"

openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
  -keyout ./docker/nginx/ssl/privkey.pem \
  -out ./docker/nginx/ssl/chain.pem \
  -subj "/C=CN/ST=Beijing/L=Beijing/O=AI-Tide/OU=Dev/CN=localhost"
```

### 5.4 反向代理配置说明

#### 负载均衡配置 (多实例场景)

```nginx
upstream api_backend {
    least_conn;
    server ai-tide-api-1:8080 max_fails=3 fail_timeout=30s;
    server ai-tide-api-2:8080 max_fails=3 fail_timeout=30s;
    server ai-tide-api-3:8080 max_fails=3 fail_timeout=30s;
    keepalive 32;
}

# 使用负载均衡
location /api/ {
    proxy_pass http://api_backend/;
    # ... 其他配置
}
```

### 5.5 静态资源服务优化

```nginx
# 静态资源缓存配置
location ~* \.(jpg|jpeg|png|gif|ico|svg|webp)$ {
    expires 30d;
    add_header Cache-Control "public, immutable";
    access_log off;
}

location ~* \.(css|js)$ {
    expires 7d;
    add_header Cache-Control "public, must-revalidate";
}

location ~* \.(woff|woff2|ttf|eot|otf)$ {
    expires 365d;
    add_header Cache-Control "public, immutable";
    access_log off;
}

# 防止盗链
location ~* \.(jpg|jpeg|png|gif|webp)$ {
    valid_referers none blocked www.ai-tide.com ai-tide.com;
    if ($invalid_referer) {
        return 403;
    }
}
```

---

## 6. 数据库初始化

### 6.1 MySQL 初始化

#### 6.1.1 MySQL 配置文件 (docker/mysql/my.cnf)

```ini
[mysqld]
# 基础配置
server-id = 1
port = 3306
character-set-server = utf8mb4
collation-server = utf8mb4_unicode_ci
default_authentication_plugin = mysql_native_password

# 连接配置
max_connections = 500
max_connect_errors = 100
connect_timeout = 10
wait_timeout = 28800
interactive_timeout = 28800

# 缓冲区配置
innodb_buffer_pool_size = 1G
innodb_buffer_pool_instances = 1
innodb_log_file_size = 256M
innodb_log_buffer_size = 16M
innodb_flush_log_at_trx_commit = 2
innodb_flush_method = O_DIRECT

# 查询缓存
query_cache_size = 0
query_cache_type = 0

# 慢查询日志
slow_query_log = 1
slow_query_log_file = /var/log/mysql/slow.log
long_query_time = 2

# 二进制日志
log_bin = /var/lib/mysql/mysql-bin
binlog_format = ROW
expire_logs_days = 7

# 时区
default-time-zone = '+08:00'

[client]
default-character-set = utf8mb4

[mysql]
default-character-set = utf8mb4
```

#### 6.1.2 初始化脚本 (docker/mysql/init/01-init.sql)

```sql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS ai_tide
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE ai_tide;

-- 创建用户（如果不存在）
CREATE USER IF NOT EXISTS 'ai_tide'@'%' IDENTIFIED BY 'ai_tide123456';

-- 授权
GRANT ALL PRIVILEGES ON ai_tide.* TO 'ai_tide'@'%';
FLUSH PRIVILEGES;

-- 创建基础表结构
-- (此处应包含所有建表语句，参考数据库设计文档)

-- 插入初始数据
-- (此处应包含初始数据插入语句)
```

### 6.2 Redis 初始化

#### 6.2.1 Redis 配置文件 (docker/redis/redis.conf)

```conf
# 网络配置
bind 0.0.0.0
port 6379
protected-mode yes
tcp-backlog 511
timeout 300
tcp-keepalive 300

# 持久化配置
save 900 1
save 300 10
save 60 10000
stop-writes-on-bgsave-error yes
rdbcompression yes
rdbchecksum yes
dbfilename dump.rdb
dir /data

# AOF 配置
appendonly yes
appendfilename "appendonly.aof"
appendfsync everysec
no-appendfsync-on-rewrite no
auto-aof-rewrite-percentage 100
auto-aof-rewrite-min-size 64mb

# 内存配置
maxmemory 1gb
maxmemory-policy allkeys-lru
maxmemory-samples 5

# 日志配置
loglevel notice
logfile ""

# 安全配置
# requirepass your_redis_password

# 慢查询配置
slowlog-log-slower-than 10000
slowlog-max-len 128

# 客户端配置
maxclients 10000
```

### 6.3 数据导入脚本

#### 数据初始化脚本 (scripts/init-data.sh)

```bash
#!/bin/bash

# AI-Tide 数据初始化脚本

set -e

# 颜色输出
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${GREEN}[INFO]${NC} 开始初始化数据..."

# 等待 MySQL 启动
echo -e "${YELLOW}[WAIT]${NC} 等待 MySQL 启动..."
until docker exec ai-tide-mysql mysqladmin ping -h localhost -u root -p${MYSQL_ROOT_PASSWORD} --silent; do
    echo "MySQL 还未就绪，等待中..."
    sleep 2
done
echo -e "${GREEN}[SUCCESS]${NC} MySQL 已启动"

# 等待 Redis 启动
echo -e "${YELLOW}[WAIT]${NC} 等待 Redis 启动..."
until docker exec ai-tide-redis redis-cli ping > /dev/null 2>&1; do
    echo "Redis 还未就绪，等待中..."
    sleep 2
done
echo -e "${GREEN}[SUCCESS]${NC} Redis 已启动"

# 导入 SQL 数据
echo -e "${YELLOW}[IMPORT]${NC} 导入初始数据..."
if [ -f "./docker/mysql/init/01-init.sql" ]; then
    docker exec -i ai-tide-mysql mysql -u root -p${MYSQL_ROOT_PASSWORD} ai_tide < ./docker/mysql/init/01-init.sql
    echo -e "${GREEN}[SUCCESS]${NC} SQL 数据导入完成"
else
    echo -e "${RED}[ERROR]${NC} 初始化 SQL 文件不存在"
    exit 1
fi

# 导入测试数据（仅测试环境）
if [ "${SPRING_PROFILES_ACTIVE}" = "test" ] || [ "${SPRING_PROFILES_ACTIVE}" = "dev" ]; then
    echo -e "${YELLOW}[IMPORT]${NC} 导入测试数据..."
    if [ -f "./docker/mysql/init/02-test-data.sql" ]; then
        docker exec -i ai-tide-mysql mysql -u root -p${MYSQL_ROOT_PASSWORD} ai_tide < ./docker/mysql/init/02-test-data.sql
        echo -e "${GREEN}[SUCCESS]${NC} 测试数据导入完成"
    fi
fi

# 初始化 Redis 数据
echo -e "${YELLOW}[IMPORT]${NC} 初始化 Redis 数据..."
docker exec ai-tide-redis redis-cli SET "system:config" '{"key":"value"}' > /dev/null
echo -e "${GREEN}[SUCCESS]${NC} Redis 数据初始化完成"

# 创建 Elasticsearch 索引
echo -e "${YELLOW}[IMPORT]${NC} 创建 Elasticsearch 索引..."
until docker exec ai-tide-elasticsearch curl -s http://localhost:9200 > /dev/null 2>&1; do
    echo "Elasticsearch 还未就绪，等待中..."
    sleep 2
done
echo -e "${GREEN}[SUCCESS]${NC} Elasticsearch 已启动"

# 创建索引映射
if [ -f "./docker/elasticsearch/mappings.json" ]; then
    curl -X PUT "http://localhost:9200/ai_tide_tech" -H 'Content-Type: application/json' -d @./docker/elasticsearch/mappings.json
    echo -e "${GREEN}[SUCCESS]${NC} Elasticsearch 索引创建完成"
fi

echo -e "${GREEN}[SUCCESS]${NC} 数据初始化完成！"
```

#### Elasticsearch 索引映射 (docker/elasticsearch/mappings.json)

```json
{
  "settings": {
    "number_of_shards": 1,
    "number_of_replicas": 1,
    "analysis": {
      "analyzer": {
        "ik_max_syno": {
          "type": "custom",
          "tokenizer": "ik_max_word"
        },
        "ik_smart_syno": {
          "type": "custom",
          "tokenizer": "ik_smart"
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "id": {
        "type": "long"
      },
      "name": {
        "type": "text",
        "analyzer": "ik_max_syno"
      },
      "description": {
        "type": "text",
        "analyzer": "ik_max_syno"
      },
      "tags": {
        "type": "keyword"
      },
      "category": {
        "type": "keyword"
      },
      "status": {
        "type": "keyword"
      },
      "createdAt": {
        "type": "date"
      },
      "updatedAt": {
        "type": "date"
      }
    }
  }
}
```

---

## 7. 应用配置

### 7.1 主配置文件 (ai-tide-api/src/main/resources/application.yml)

```yaml
spring:
  application:
    name: ai-tide-api
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}

  # 数据源配置
  datasource:
    url: ${SPRING_DATASOURCE_URL:jdbc:mysql://localhost:3306/ai_tide?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8mb4}
    username: ${SPRING_DATASOURCE_USERNAME:root}
    password: ${SPRING_DATASOURCE_PASSWORD:root}
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      connection-test-query: SELECT 1

  # JPA 配置
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    open-in-view: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true
        use_sql_comments: true
        jdbc:
          batch_size: 50
          time_zone: Asia/Shanghai

  # Redis 配置
  data:
    redis:
      host: ${SPRING_REDIS_HOST:localhost}
      port: ${SPRING_REDIS_PORT:6379}
      password: ${SPRING_REDIS_PASSWORD:}
      database: 0
      lettuce:
        pool:
          max-active: 20
          max-idle: 10
          min-idle: 5
          max-wait: 3000ms
      timeout: 3000ms

  # 缓存配置
  cache:
    type: redis
    redis:
      time-to-live: 3600000
      cache-null-values: false
      key-prefix: "ai-tide:"

  # 文件上传配置
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 50MB

  # Jackson 配置
  jackson:
    time-zone: GMT+8
    date-format: yyyy-MM-dd HH:mm:ss
    serialization:
      write-dates-as-timestamps: false

# 服务器配置
server:
  port: 8080
  servlet:
    context-path: /
    encoding:
      charset: UTF-8
      enabled: true
      force: true
  shutdown: graceful
  tomcat:
    threads:
      max: 200
      min-spare: 10

# 管理端点配置
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
      base-path: /actuator
  endpoint:
    health:
      show-details: when-authorized
  health:
    redis:
      enabled: true
    db:
      enabled: true

# 日志配置
logging:
  level:
    root: INFO
    com.ai.tide: ${LOG_LEVEL:DEBUG}
    org.springframework.web: INFO
    org.hibernate.SQL: ${SHOW_SQL:false}
    org.hibernate.type.descriptor.sql.BasicBinder: ${SHOW_SQL:false}
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: /app/logs/ai-tide-api.log
    max-size: 100MB
    max-history: 30
    total-size-cap: 1GB

# JWT 配置
jwt:
  secret: ${JWT_SECRET:your-secret-key-minimum-32-characters-long}
  expiration: ${JWT_EXPIRATION:86400000}
  header: Authorization
  prefix: Bearer

# Elasticsearch 配置
elasticsearch:
  host: ${ELASTICSEARCH_HOST:localhost}
  port: ${ELASTICSEARCH_PORT:9200}
  username: ${ELASTICSEARCH_USERNAME:}
  password: ${ELASTICSEARCH_PASSWORD:}
  index:
    tech: ai_tide_tech
    article: ai_tide_article

# 应用配置
app:
  name: AI-Tide
  version: @project.version@
  environment: ${SPRING_PROFILES_ACTIVE:dev}
  upload:
    path: /app/uploads
    url-prefix: /uploads
```

### 7.2 多环境配置

#### 7.2.1 开发环境配置 (application-dev.yml)

```yaml
spring:
datasource:
  url: jdbc:mysql://localhost:3306/ai_tide_dev?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8mb4
  username: root
  password: root

jpa:
  hibernate:
    ddl-auto: update
  show-sql: true

data:
  redis:
    host: localhost
    port: 6379

logging:
  level:
    com.ai.tide: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE

elasticsearch:
  host: localhost
```

#### 7.2.2 测试环境配置 (application-test.yml)

```yaml
spring:
datasource:
  url: jdbc:mysql://mysql:3306/ai_tide_test?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8mb4

jpa:
  hibernate:
    ddl-auto: validate
  show-sql: true

data:
  redis:
    host: redis

logging:
  level:
    com.ai.tide: DEBUG
    org.hibernate.SQL: DEBUG

elasticsearch:
  host: elasticsearch
```

#### 7.2.3 生产环境配置 (application-prod.yml)

```yaml
spring:
datasource:
  url: jdbc:mysql://mysql:3306/ai_tide?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8mb4
  hikari:
    maximum-pool-size: 50
    minimum-idle: 10

jpa:
  hibernate:
    ddl-auto: validate
  show-sql: false

data:
  redis:
    host: redis

logging:
  level:
    root: INFO
    com.ai.tide: INFO
  file:
    name: /app/logs/ai-tide-api.log

elasticsearch:
  host: elasticsearch
```

### 7.3 敏感数据配置

#### 使用 Docker Secrets (推荐生产环境)

```yaml
# docker-compose.secrets.yml
version: '3.8'

secrets:
  mysql_root_password:
    external: true
  mysql_password:
    external: true
  redis_password:
    external: true
  jwt_secret:
    external: true

services:
  mysql:
    secrets:
      - mysql_root_password
      - mysql_password
    environment:
      MYSQL_ROOT_PASSWORD_FILE: /run/secrets/mysql_root_password
      MYSQL_PASSWORD_FILE: /run/secrets/mysql_password

  redis:
    secrets:
      - redis_password

  ai-tide-api:
    secrets:
      - jwt_secret
      - mysql_password
      - redis_password
    environment:
      JWT_SECRET_FILE: /run/secrets/jwt_secret
      SPRING_DATASOURCE_PASSWORD_FILE: /run/secrets/mysql_password
      SPRING_REDIS_PASSWORD_FILE: /run/secrets/redis_password
```

#### 创建 Secrets

```bash
# 创建 secrets 目录
mkdir -p /data/ai-tide/secrets

# 创建 secret 文件
echo "your_strong_root_password" | docker secret create ai_tide_mysql_root_password -
echo "your_strong_mysql_password" | docker secret create ai_tide_mysql_password -
echo "your_redis_password" | docker secret create ai_tide_redis_password -
echo "your_jwt_secret_key_minimum_64_characters" | docker secret create ai_tide_jwt_secret -

# 查看 secrets
docker secret ls
```

---

## 8. 部署流程

### 8.1 开发环境部署

#### 前置准备

```bash
# 1. 克隆项目
git clone https://github.com/your-username/ai-tide.git
cd ai-tide

# 2. 创建配置文件
cp .env.example .env
# 编辑 .env 文件
vim .env

# 3. 创建必要的目录
mkdir -p docker/mysql/init
mkdir -p docker/redis
mkdir -p docker/nginx/conf.d
mkdir -p docker/nginx/ssl
mkdir -p docker/elasticsearch
```

#### 启动开发环境

```bash
# 1. 使用开发环境配置
cp .env.dev .env

# 2. 构建并启动服务
docker compose --env-file .env up -d

# 3. 查看服务状态
docker compose ps

# 4. 查看日志
docker compose logs -f

# 5. 初始化数据
bash scripts/init-data.sh

# 6. 访问应用
# 前端: http://localhost:3000
# 后台: http://localhost:3001
# API: http://localhost:8080
```

#### 停止开发环境

```bash
# 停止服务
docker compose down

# 停止服务并删除数据卷
docker compose down -v

# 停止服务并删除镜像
docker compose down --rmi all
```

### 8.2 测试环境部署

#### 部署脚本 (scripts/deploy-test.sh)

```bash
#!/bin/bash

# AI-Tide 测试环境部署脚本

set -e

# 颜色输出
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

# 配置
ENV_FILE=".env.test"
COMPOSE_FILE="docker-compose.yml"

echo -e "${GREEN}[INFO]${NC} 开始部署测试环境..."

# 1. 检查配置文件
if [ ! -f "$ENV_FILE" ]; then
    echo -e "${RED}[ERROR]${NC} 配置文件 $ENV_FILE 不存在"
    exit 1
fi

# 2. 拉取最新代码
echo -e "${YELLOW}[GIT]${NC} 拉取最新代码..."
git pull origin main

# 3. 备份当前数据（可选）
echo -e "${YELLOW}[BACKUP]${NC} 备份数据..."
bash scripts/backup.sh test

# 4. 停止旧服务
echo -e "${YELLOW}[DOCKER]${NC} 停止旧服务..."
docker compose --env-file $ENV_FILE down

# 5. 构建镜像
echo -e "${YELLOW}[DOCKER]${NC} 构建镜像..."
docker compose --env-file $ENV_FILE build --no-cache

# 6. 启动服务
echo -e "${YELLOW}[DOCKER]${NC} 启动服务..."
docker compose --env-file $ENV_FILE up -d

# 7. 等待服务就绪
echo -e "${YELLOW}[WAIT]${NC} 等待服务就绪..."
sleep 30

# 8. 初始化数据
echo -e "${YELLOW}[DATA]${NC} 初始化数据..."
bash scripts/init-data.sh

# 9. 运行测试（可选）
echo -e "${YELLOW}[TEST]${NC} 运行测试..."
# npm run test 或 mvn test

# 10. 检查服务状态
echo -e "${GREEN}[INFO]${NC} 服务状态:"
docker compose --env-file $ENV_FILE ps

echo -e "${GREEN}[SUCCESS]${NC} 测试环境部署完成！"
echo -e "访问地址: http://test.ai-tide.com"
```

### 8.3 生产环境部署

#### 零停机部署方案 (蓝绿部署)

```bash
#!/bin/bash

# AI-Tide 生产环境蓝绿部署脚本

set -e

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

# 配置
ENV_FILE=".env.prod"
VERSION=$1
DEPLOYMENT_ID=$(date +%Y%m%d%H%M%S)

if [ -z "$VERSION" ]; then
    echo -e "${RED}[ERROR]${NC} 请指定版本号"
    exit 1
fi

echo -e "${GREEN}[INFO]${NC} 开始部署生产环境 v$VERSION..."

# 1. 预部署检查
echo -e "${YELLOW}[CHECK]${NC} 预部署检查..."
# 检查代码是否合并到主分支
# 检查测试是否通过
# 检查配置是否正确

# 2. 备份数据
echo -e "${YELLOW}[BACKUP]${NC} 备份数据..."
BACKUP_FILE="/backup/ai-tide-backup-${DEPLOYMENT_ID}.sql"
docker exec ai-tide-mysql mysqldump -u root -p${MYSQL_ROOT_PASSWORD} ai_tide > $BACKUP_FILE

# 3. 构建新版本镜像
echo -e "${YELLOW}[BUILD]${NC} 构建新版本镜像..."
docker compose --env-file $ENV_FILE build

# 4. 标记当前容器为 blue
echo -e "${YELLOW}[DEPLOY]${NC} 部署新版本..."
docker tag ai-tide-api:latest ai-tide-api:blue
docker tag ai-tide-web:latest ai-tide-web:blue

# 5. 启动新容器（green）
echo -e "${YELLOW}[DEPLOY]${NC} 启动新容器..."
docker compose --env-file $ENV_FILE up -d --scale ai-tide-api=2

# 6. 等待新容器就绪
echo -e "${YELLOW}[WAIT]${NC} 等待新容器就绪..."
sleep 60

# 7. 健康检查
echo -e "${YELLOW}[HEALTH]${NC} 健康检查..."
for i in {1..10}; do
    if curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
        echo -e "${GREEN}[SUCCESS]${NC} 健康检查通过"
        break
    fi
    if [ $i -eq 10 ]; then
        echo -e "${RED}[ERROR]${NC} 健康检查失败，回滚..."
        # 回滚逻辑
        exit 1
    fi
    sleep 5
done

# 8. 切换流量到新容器
echo -e "${YELLOW}[SWITCH]${NC} 切换流量..."
# 更新 Nginx 配置指向新容器
docker compose --env-file $ENV_FILE exec nginx nginx -s reload

# 9. 停止旧容器
echo -e "${YELLOW}[CLEANUP]${NC} 停止旧容器..."
docker compose --env-file $ENV_FILE up -d --scale ai-tide-api=1

# 10. 标记部署成功
echo -e "${GREEN}[SUCCESS]${NC} 生产环境部署完成 v$版本！"
echo "部署ID: $DEPLOYMENT_ID"
echo "备份文件: $BACKUP_FILE"
```

#### 生产环境部署清单

```markdown
## 部署前检查清单

- [ ] 代码已通过 Code Review
- [ ] 所有测试用例通过
- [ ] 版本号已更新
- [ ] CHANGELOG.md 已更新
- [ ] 数据库迁移脚本已准备
- [ ] 环境变量已配置
- [ ] SSL 证书有效
- [ ] 备份已创建
- [ ] 监控告警已配置
- [ ] 回滚方案已准备

## 部署步骤

1. 执行备份脚本
2. 停止旧服务（可选，蓝绿部署不需要）
3. 构建新镜像
4. 启动新服务
5. 健康检查
6. 切换流量
7. 停止旧服务（蓝绿部署）
8. 验证部署
9. 清理旧镜像
10. 发送部署通知

## 部署后验证

- [ ] 健康检查端点正常
- [ ] 数据库连接正常
- [ ] Redis 连接正常
- [ ] Elasticsearch 连接正常
- [ ] 静态资源加载正常
- [ ] API 接口响应正常
- [ ] 页面渲染正常
- [ ] 日志无错误
- [ ] 监控指标正常
```

### 8.4 CI/CD 集成

#### GitHub Actions 工作流 (.github/workflows/deploy.yml)

```yaml
name: Deploy AI-Tide

on:
  push:
    branches:
      - main
  workflow_dispatch:
    inputs:
      environment:
        description: '部署环境'
        required: true
        default: 'test'
        type: choice
        options:
          - test
          - prod

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout code
        uses: actions/checkout@v3

      - name: Set up Node.js
        uses: actions/setup-node@v3
        with:
          node-version: '18'
          cache: 'npm'

      - name: Install frontend dependencies
        run: |
          cd ai-tide-web
          npm ci

      - name: Run frontend tests
        run: |
          cd ai-tide-web
          npm run test

      - name: Set up Java
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: 'maven'

      - name: Run backend tests
        run: |
          cd ai-tide-api
          ./mvnw test

  build:
    needs: test
    runs-on: ubuntu-latest
    outputs:
      image_tag: ${{ steps.meta.outputs.tags }}
    steps:
      - name: Checkout code
        uses: actions/checkout@v3

      - name: Set up Docker Buildx
        uses: docker/setup-buildx-action@v2

      - name: Log in to Docker Hub
        uses: docker/login-action@v2
        with:
          username: ${{ secrets.DOCKER_USERNAME }}
          password: ${{ secrets.DOCKER_PASSWORD }}

      - name: Extract metadata
        id: meta
        uses: docker/metadata-action@v4
        with:
          images: your-username/ai-tide
          tags: |
            type=sha
            type=raw,value=latest

      - name: Build and push Docker images
        uses: docker/build-push-action@v4
        with:
          context: .
          push: true
          tags: ${{ steps.meta.outputs.tags }}
          cache-from: type=gha
          cache-to: type=gha,mode=max

  deploy-test:
    needs: build
    if: github.ref == 'refs/heads/main'
    runs-on: ubuntu-latest
    steps:
      - name: Deploy to test environment
        uses: appleboy/ssh-action@master
        with:
          host: ${{ secrets.TEST_HOST }}
          username: ${{ secrets.TEST_USER }}
          key: ${{ secrets.TEST_SSH_KEY }}
          script: |
            cd /opt/ai-tide
            docker compose pull
            docker compose up -d
            bash scripts/init-data.sh

  deploy-prod:
    needs: build
    if: github.event.inputs.environment == 'prod'
    runs-on: ubuntu-latest
    steps:
      - name: Deploy to production
        uses: appleboy/ssh-action@master
        with:
          host: ${{ secrets.PROD_HOST }}
          username: ${{ secrets.PROD_USER }}
          key: ${{ secrets.PROD_SSH_KEY }}
          script: |
            cd /opt/ai-tide
            bash scripts/deploy-prod.sh ${{ github.sha }}
```

---

## 9. 监控运维

### 9.1 日志收集

#### Docker 日志配置

```yaml
# docker-compose.yml 增加日志配置
services:
  ai-tide-api:
    logging:
      driver: "json-file"
      options:
        max-size: "100m"
        max-file: "3"
        labels: "service=ai-tide-api,environment=prod"

  ai-tide-web:
    logging:
      driver: "json-file"
      options:
        max-size: "50m"
        max-file: "3"
```

#### 日志收集脚本 (scripts/collect-logs.sh)

```bash
#!/bin/bash

# 日志收集脚本

LOG_DIR="/var/log/ai-tide"
TIMESTAMP=$(date +%Y%m%d%H%M%S)

mkdir -p $LOG_DIR

# 收集容器日志
docker logs ai-tide-api > $LOG_DIR/api-$TIMESTAMP.log 2>&1
docker logs ai-tide-web > $LOG_DIR/web-$TIMESTAMP.log 2>&1
docker logs ai-tide-nginx > $LOG_DIR/nginx-$TIMESTAMP.log 2>&1
docker logs ai-tide-mysql > $LOG_DIR/mysql-$TIMESTAMP.log 2>&1
docker logs ai-tide-redis > $LOG_DIR/redis-$TIMESTAMP.log 2>&1

# 收集系统日志
dmesg > $LOG_DIR/dmesg-$TIMESTAMP.log

# 打包压缩
tar -czf $LOG_DIR/ai-tide-logs-$TIMESTAMP.tar.gz $LOG_DIR/*-$TIMESTAMP.log
rm $LOG_DIR/*-$TIMESTAMP.log

echo "日志已打包: $LOG_DIR/ai-tide-logs-$TIMESTAMP.tar.gz"
```

### 9.2 健康检查

#### 健康检查脚本 (scripts/health-check.sh)

```bash
#!/bin/bash

# 健康检查脚本

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo "=== AI-Tide 健康检查 ==="

# 检查容器状态
echo -n "容器状态: "
if docker compose ps | grep -q "Exit"; then
    echo -e "${RED}FAIL${NC}"
    exit 1
else
    echo -e "${GREEN}OK${NC}"
fi

# 检查 API 健康
echo -n "API 健康: "
if curl -sf http://localhost:8080/actuator/health > /dev/null; then
    echo -e "${GREEN}OK${NC}"
else
    echo -e "${RED}FAIL${NC}"
fi

# 检查 MySQL 连接
echo -n "MySQL 连接: "
if docker exec ai-tide-mysql mysqladmin ping -h localhost --silent; then
    echo -e "${GREEN}OK${NC}"
else
    echo -e "${RED}FAIL${NC}"
fi

# 检查 Redis 连接
echo -n "Redis 连接: "
if docker exec ai-tide-redis redis-cli ping > /dev/null 2>&1; then
    echo -e "${GREEN}OK${NC}"
else
    echo -e "${RED}FAIL${NC}"
fi

# 检查 Elasticsearch 连接
echo -n "Elasticsearch 连接: "
if curl -sf http://localhost:9200/_cluster/health > /dev/null; then
    echo -e "${GREEN}OK${NC}"
else
    echo -e "${RED}FAIL${NC}"
fi

# 检查磁盘空间
echo -n "磁盘空间: "
DISK_USAGE=$(df /data | tail -1 | awk '{print $5}' | sed 's/%//')
if [ $DISK_USAGE -lt 80 ]; then
    echo -e "${GREEN}OK${NC} ($DISK_USAGE%)"
else
    echo -e "${YELLOW}WARNING${NC} ($DISK_USAGE%)"
fi

# 检查内存使用
echo -n "内存使用: "
MEM_USAGE=$(free | grep Mem | awk '{printf "%.0f", $3/$2 * 100.0}')
if [ $MEM_USAGE -lt 80 ]; then
    echo -e "${GREEN}OK${NC} ($MEM_USAGE%)"
else
    echo -e "${YELLOW}WARNING${NC} ($MEM_USAGE%)"
fi
```

### 9.3 性能监控

#### Prometheus 配置 (docker/prometheus/prometheus.yml)

```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

scrape_configs:
  - job_name: 'ai-tide-api'
    static_configs:
      - targets: ['ai-tide-api:8080']
    metrics_path: '/actuator/prometheus'

  - job_name: 'mysql'
    static_configs:
      - targets: ['mysql-exporter:9104']

  - job_name: 'redis'
    static_configs:
      - targets: ['redis-exporter:9121']

  - job_name: 'nginx'
    static_configs:
      - targets: ['nginx-exporter:9113']

  - job_name: 'node'
    static_configs:
      - targets: ['node-exporter:9100']
```

#### Grafana Dashboard 配置

```json
{
  "dashboard": {
    "title": "AI-Tide 监控面板",
    "panels": [
      {
        "title": "API 请求速率",
        "targets": [
          {
            "expr": "rate(http_server_requests_seconds_count[5m])"
          }
        ]
      },
      {
        "title": "API 响应时间",
        "targets": [
          {
            "expr": "rate(http_server_requests_seconds_sum[5m]) / rate(http_server_requests_seconds_count[5m])"
          }
        ]
      },
      {
        "title": "JVM 内存使用",
        "targets": [
          {
            "expr": "jvm_memory_used_bytes{area=\"heap\"}"
          }
        ]
      },
      {
        "title": "数据库连接池",
        "targets": [
          {
            "expr": "hikaricp_connections_active"
          }
        ]
      }
    ]
  }
}
```

### 9.4 备份策略

#### 自动备份脚本 (scripts/backup.sh)

```bash
#!/bin/bash

# 自动备份脚本

set -e

ENVIRONMENT=${1:-prod}
BACKUP_DIR="/backup/ai-tide"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
RETENTION_DAYS=30

mkdir -p $BACKUP_DIR

echo "开始备份数据..."

# 备份 MySQL
echo "备份 MySQL..."
docker exec ai-tide-mysql mysqldump \
  -u root \
  -p${MYSQL_ROOT_PASSWORD} \
  --single-transaction \
  --quick \
  --lock-tables=false \
  ai_tide > $BACKUP_DIR/mysql-$TIMESTAMP.sql

# 压缩备份文件
echo "压缩备份..."
gzip $BACKUP_DIR/mysql-$TIMESTAMP.sql

# 备份 Redis
echo "备份 Redis..."
docker exec ai-tide-redis redis-cli BGSAVE

# 备份 Elasticsearch 快照
echo "备份 Elasticsearch..."
curl -X PUT "http://localhost:9200/_snapshot/backup/snapshot-$TIMESTAMP?wait_for_completion=true"

# 删除旧备份
echo "清理旧备份..."
find $BACKUP_DIR -name "mysql-*.sql.gz" -mtime +$RETENTION_DAYS -delete

echo "备份完成: $BACKUP_DIR/mysql-$TIMESTAMP.sql.gz"
```

#### 定时备份 (Cron)

```bash
# 每天凌晨 2 点执行备份
0 2 * * * cd /opt/ai-tide && bash scripts/backup.sh prod >> /var/log/backup.log 2>&1

# 每周日凌晨 3 点执行完整备份
0 3 * * 0 cd /opt/ai-tide && bash scripts/backup.sh prod full >> /var/log/backup.log 2>&1
```

---

## 10. 常见问题

### 10.1 部署相关问题

#### 问题 1: Docker 容器无法启动

**症状**: `docker compose up` 后容器无法启动，日志显示连接失败

**原因**: 数据库或依赖服务未就绪

**解决方案**:

```bash
# 1. 检查依赖服务状态
docker compose ps

# 2. 查看详细日志
docker compose logs mysql
docker compose logs redis

# 3. 检查网络连接
docker network inspect ai-tide-network

# 4. 增加健康检查等待时间
# 在 docker-compose.yml 中增加 healthcheck.start_period

# 5. 手动启动依赖服务
docker compose up -d mysql redis
sleep 20
docker compose up -d ai-tide-api
```

#### 问题 2: 端口被占用

**症状**: `Error starting userland proxy: listen tcp 0.0.0.0:80: bind: address already in use`

**原因**: 端口已被其他进程占用

**解决方案**:

```bash
# 1. 查找占用端口的进程
sudo netstat -tlnp | grep :80
sudo lsof -i :80

# 2. 停止占用进程
sudo kill -9 <PID>

# 或者修改 docker-compose.yml 中的端口映射
# "8080:80" -> "8081:80"
```

#### 问题 3: 权限问题

**症状**: `Permission denied` 错误

**原因**: 文件权限或容器用户权限问题

**解决方案**:

```bash
# 1. 修复文件权限
sudo chown -R $USER:$USER ./data
sudo chmod -R 755 ./data

# 2. 以 root 用户运行容器（不推荐）
# 在 docker-compose.yml 中添加
user: root

# 3. 使用卷而非绑定挂载
volumes:
  - app-data:/app/data
```

### 10.2 运行时问题

#### 问题 1: 数据库连接池耗尽

**症状**: `HikariPool-1 - Connection is not available`

**原因**: 连接未正确释放或连接池配置过小

**解决方案**:

```yaml
# 调整连接池配置
spring:
  datasource:
    hikari:
      maximum-pool-size: 50      # 增加最大连接数
      minimum-idle: 10
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      leak-detection-threshold: 5000  # 检测连接泄漏
```

```bash
# 查看当前连接数
docker exec ai-tide-mysql mysql -u root -p${MYSQL_ROOT_PASSWORD} -e "SHOW PROCESSLIST;"

# 杀死长连接
docker exec ai-tide-mysql mysql -u root -p${MYSQL_ROOT_PASSWORD} -e "KILL <PROCESS_ID>;"
```

#### 问题 2: 内存溢出 (OOM)

**症状**: `java.lang.OutOfMemoryError: Java heap space`

**原因**: JVM 堆内存不足

**解决方案**:

```bash
# 1. 调整 JVM 参数
export JAVA_OPTS="-Xms1g -Xmx2g -XX:+UseG1GC"

# 2. 启用堆转储
export JAVA_OPTS="$JAVA_OPTS -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/app/logs/heapdump.hprof"

# 3. 分析堆转储文件
# 使用 jvisualvm 或 MAT 分析 heapdump.hprof

# 4. 检查内存泄漏
docker stats ai-tide-api
```

#### 问题 3: Redis 连接超时

**症状**: `RedisConnectionFailureException: Unable to connect to Redis`

**原因**: Redis 服务未启动或网络不通

**解决方案**:

```bash
# 1. 检查 Redis 容器状态
docker compose ps redis

# 2. 测试 Redis 连接
docker exec ai-tide-redis redis-cli ping

# 3. 检查网络
docker exec ai-tide-api ping redis

# 4. 重启 Redis
docker compose restart redis
```

### 10.3 网络问题

#### 问题 1: 容器间无法通信

**症状**: `Connection refused` 或 `Name or service not known`

**原因**: 容器不在同一网络或 DNS 解析失败

**解决方案**:

```bash
# 1. 检查容器网络
docker network inspect ai-tide-network

# 2. 确保容器在同一网络
docker compose up -d --force-recreate

# 3. 使用容器名而非 IP 进行连接
# application.yml 中使用 redis 而非 172.20.0.x

# 4. 手动连接容器到网络
docker network connect ai-tide-network container_name
```

#### 问题 2: Nginx 502 Bad Gateway

**症状**: 访问网站返回 502 错误

**原因**: 后端服务未启动或无法访问

**解决方案**:

```bash
# 1. 检查后端服务状态
docker compose ps ai-tide-api

# 2. 检查 Nginx 日志
docker logs ai-tide-nginx

# 3. 测试后端服务
curl http://localhost:8080/actuator/health

# 4. 检查 Nginx 配置
docker exec ai-tide-nginx nginx -t

# 5. 重启 Nginx
docker compose restart nginx
```

#### 问题 3: SSL 证书问题

**症状**: `SSL certificate problem`

**原因**: 证书过期或配置错误

**解决方案**:

```bash
# 1. 检查证书有效期
openssl x509 -in ./docker/nginx/ssl/fullchain.pem -noout -dates

# 2. 更新证书
sudo certbot renew

# 3. 验证证书配置
docker exec ai-tide-nginx nginx -t

# 4. 重启 Nginx
docker compose restart nginx
```

---

## 11. 回滚策略

### 11.1 版本回滚

#### 回滚脚本 (scripts/rollback.sh)

```bash
#!/bin/bash

# 版本回滚脚本

set -e

VERSION=$1
ENVIRONMENT=${2:-prod}

if [ -z "$VERSION" ]; then
    echo "用法: $0 <version> [environment]"
    exit 1
fi

echo "开始回滚到版本 $VERSION..."

# 1. 查找备份
BACKUP_FILE=$(find /backup/ai-tide -name "*mysql-$VERSION*.sql.gz" | head -1)

if [ -z "$BACKUP_FILE" ]; then
    echo "错误: 找不到版本 $VERSION 的备份"
    exit 1
fi

echo "找到备份: $BACKUP_FILE"

# 2. 停止应用服务
docker compose --env-file .env.$ENVIRONMENT stop ai-tide-api

# 3. 恢复数据库
echo "恢复数据库..."
gunzip -c $BACKUP_FILE | docker exec -i ai-tide-mysql mysql -u root -p${MYSQL_ROOT_PASSWORD} ai_tide

# 4. 回滚代码（如果使用 Git）
# git checkout $VERSION
# docker compose build

# 5. 重启服务
docker compose --env-file .env.$ENVIRONMENT up -d

# 6. 健康检查
sleep 30
if curl -f http://localhost:8080/actuator/health > /dev/null; then
    echo "回滚成功！"
else
    echo "警告: 健康检查失败，请手动检查"
fi
```

### 11.2 数据恢复

#### 数据恢复脚本 (scripts/restore-data.sh)

```bash
#!/bin/bash

# 数据恢复脚本

BACKUP_FILE=$1

if [ -z "$BACKUP_FILE" ]; then
    echo "用法: $0 <backup_file>"
    exit 1
fi

if [ ! -f "$BACKUP_FILE" ]; then
    echo "错误: 备份文件不存在"
    exit 1
fi

echo "开始恢复数据: $BACKUP_FILE"

# 1. 验证备份文件
echo "验证备份文件..."
if file "$BACKUP_FILE" | grep -q "gzip"; then
    echo "备份文件格式: GZIP"
    gunzip -t "$BACKUP_FILE"
else
    echo "备份文件格式: SQL"
    grep -q "CREATE DATABASE" "$BACKUP_FILE" || { echo "无效的 SQL 备份文件"; exit 1; }
fi

# 2. 创建恢复前快照
echo "创建恢复前快照..."
docker exec ai-tide-mysql mysqldump \
  -u root -p${MYSQL_ROOT_PASSWORD} \
  ai_tide > /tmp/pre-restore.sql

# 3. 停止应用
echo "停止应用服务..."
docker compose stop ai-tide-api

# 4. 恢复数据
echo "恢复数据库..."
if file "$BACKUP_FILE" | grep -q "gzip"; then
    gunzip -c "$BACKUP_FILE" | docker exec -i ai-tide-mysql \
      mysql -u root -p${MYSQL_ROOT_PASSWORD} ai_tide
else
    docker exec -i ai-tide-mysql \
      mysql -u root -p${MYSQL_ROOT_PASSWORD} ai_tide < "$BACKUP_FILE"
fi

# 5. 重启应用
echo "重启应用服务..."
docker compose start ai-tide-api

# 6. 验证
echo "验证数据..."
docker compose exec ai-tide-api mysql -u root -p${MYSQL_ROOT_PASSWORD} \
  -e "SELECT COUNT(*) FROM users;"

echo "数据恢复完成！"
```

### 11.3 紧急回滚方案

#### 紧急回滚检查清单

```markdown
## 紧急回滚决策流程

### 判断是否需要回滚
- [ ] 严重影响用户体验
- [ ] 核心功能无法使用
- [ ] 数据丢失风险
- [ ] 安全漏洞
- [ ] 性能严重下降

### 回滚前准备
- [ ] 通知相关团队
- [ ] 记录问题症状
- [ ] 收集现场日志
- [ ] 保存当前配置
- [ ] 确认回滚点

### 立即回滚步骤

#### 1. 代码回滚（5分钟）
```bash
# 查看最近的提交
git log --oneline -10

# 回滚到上一个稳定版本
git revert HEAD
git push origin main

# 或直接切换到指定版本
git checkout <stable_commit>
docker compose build
```

#### 2. 数据库回滚（10分钟）
```bash
# 立即停止应用
docker compose stop ai-tide-api

# 恢复最近的有效备份
LATEST_BACKUP=$(ls -t /backup/ai-tide/mysql-*.sql.gz | head -1)
bash scripts/restore-data.sh $LATEST_BACKUP

# 重启应用
docker compose start ai-tide-api
```

#### 3. 配置回滚（5分钟）
```bash
# 恢复之前的配置
cp /backup/ai-tide/config/application.yml ./docker/api/
docker compose restart ai-tide-api
```

#### 4. 验证（5分钟）
```bash
# 健康检查
bash scripts/health-check.sh

# 功能测试
curl http://localhost:8080/api/health
```

### 回滚后操作
- [ ] 验证系统功能
- [ ] 检查数据一致性
- [ ] 通知用户
- [ ] 编写事故报告
- [ ] 根本原因分析
- [ ] 制定预防措施
```

#### 快速回滚命令

```bash
# 一键回滚到上一个版本
rollback_to_previous() {
    echo "=== 紧急回滚开始 ==="

    # 1. 停止应用
    docker compose stop ai-tide-api

    # 2. 找到最近的备份
    BACKUP=$(ls -t /backup/ai-tide/mysql-*.sql.gz 2>/dev/null | head -1)

    if [ -z "$BACKUP" ]; then
        echo "错误: 找不到备份文件"
        return 1
    fi

    echo "恢复备份: $BACKUP"

    # 3. 恢复数据
    gunzip -c $BACKUP | docker exec -i ai-tide-mysql \
      mysql -u root -p${MYSQL_ROOT_PASSWORD} ai_tide

    # 4. 重启应用
    docker compose start ai-tide-api

    echo "=== 紧急回滚完成 ==="
    echo "请验证系统功能是否正常"
}

# 使用
rollback_to_previous
```

---

## 附录

### A. 常用命令速查

```bash
# ==================== Docker Compose ====================
docker compose up -d              # 启动服务
docker compose down               # 停止服务
docker compose ps                 # 查看状态
docker compose logs -f            # 查看日志
docker compose restart <service>  # 重启服务
docker compose exec <service> sh # 进入容器
docker compose build              # 构建镜像
docker compose pull               # 拉取镜像

# ==================== 容器操作 ====================
docker ps                        # 查看运行中的容器
docker ps -a                     # 查看所有容器
docker logs <container>          # 查看容器日志
docker exec -it <container> sh   # 进入容器
docker stats <container>         # 查看资源使用
docker inspect <container>       # 查看容器详情

# ==================== 数据库操作 ====================
docker exec ai-tide-mysql mysql -u root -p
docker exec ai-tide-mysql mysqldump -u root -p ai_tide > backup.sql
docker exec ai-tide-redis redis-cli
docker exec ai-tide-redis redis-cli BGSAVE

# ==================== 日志查看 ====================
docker logs --tail=100 ai-tide-api
docker logs --since 1h ai-tide-api
docker logs --until 2024-03-24T10:00:00 ai-tide-api

# ==================== 网络操作 ====================
docker network ls
docker network inspect ai-tide-network
docker network connect ai-tide-network <container>

# ==================== 数据卷操作 ====================
docker volume ls
docker volume inspect ai-tide_mysql-data
docker volume rm ai-tide_mysql-data

# ==================== 系统操作 ====================
docker system df                 # 查看磁盘使用
docker system prune              # 清理未使用的资源
docker image prune -a            # 清理未使用的镜像
```

### B. 环境变量清单

| 变量名 | 默认值 | 说明 | 必填 |
|--------|--------|------|------|
| `SPRING_PROFILES_ACTIVE` | `dev` | 运行环境 | 是 |
| `MYSQL_ROOT_PASSWORD` | - | MySQL root 密码 | 是 |
| `MYSQL_DATABASE` | `ai_tide` | 数据库名称 | 否 |
| `MYSQL_USER` | `ai_tide` | MySQL 用户名 | 是 |
| `MYSQL_PASSWORD` | - | MySQL 密码 | 是 |
| `REDIS_PASSWORD` | - | Redis 密码 | 否 |
| `JWT_SECRET` | - | JWT 密钥 | 是 |
| `JWT_EXPIRATION` | `86400000` | JWT 过期时间(毫秒) | 否 |
| `ELASTICSEARCH_HOST` | `elasticsearch` | ES 主机 | 否 |
| `ELASTICSEARCH_PORT` | `9200` | ES 端口 | 否 |

### C. 端口清单

| 服务 | 内部端口 | 外部端口 | 说明 |
|------|---------|---------|------|
| Nginx | 80 | 80 | HTTP |
| Nginx | 443 | 443 | HTTPS |
| API | 8080 | - | 应用 API |
| Web | 3000 | - | 前端服务 |
| Admin | 3000 | - | 后台服务 |
| MySQL | 3306 | - | 数据库 |
| Redis | 6379 | - | 缓存 |
| ES | 9200 | - | ES HTTP |
| ES | 9300 | - | ES TCP |

### D. 目录结构清单

```
/opt/ai-tide/                # 项目根目录
├── ai-tide-api/            # 后端代码
├── ai-tide-web/            # 前台代码
├── ai-tide-admin/          # 后台代码
├── docker/                 # Docker 配置
│   ├── mysql/
│   │   ├── my.cnf
│   │   └── init/
│   ├── redis/
│   │   └── redis.conf
│   ├── nginx/
│   │   ├── nginx.conf
│   │   ├── conf.d/
│   │   └── ssl/
│   └── elasticsearch/
│       └── mappings.json
├── scripts/                # 部署脚本
│   ├── deploy.sh
│   ├── backup.sh
│   ├── init-data.sh
│   ├── restore-data.sh
│   ├── rollback.sh
│   ├── health-check.sh
│   └── collect-logs.sh
├── .env                    # 环境变量
├── .env.dev               # 开发环境变量
├── .env.test              # 测试环境变量
├── .env.prod              # 生产环境变量
└── docker-compose.yml     # Docker Compose 配置

/data/ai-tide/              # 数据目录
├── mysql-data/            # MySQL 数据
├── redis-data/            # Redis 数据
├── es-data/               # Elasticsearch 数据
├── app-logs/              # 应用日志
└── nginx-logs/            # Nginx 日志

/backup/ai-tide/            # 备份目录
└── mysql-*.sql.gz         # 数据库备份
```

---

## 文档修订历史

| 版本 | 日期 | 作者 | 修订说明 |
|------|------|------|----------|
| v1.0.0 | 2026-03-24 | DevOps 团队 | 初始版本 |

---

## 联系方式

如有部署相关问题，请联系：

- **技术支持**: support@ai-tide.com
- **运维团队**: ops@ai-tide.com
- **GitHub Issues**: https://github.com/your-username/ai-tide/issues

---

**文档结束**
