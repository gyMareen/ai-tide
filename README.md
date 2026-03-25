# AI-Tide

> 发现改变世界的 AI 技术

AI-Tide 是一个现代化的 AI 技术聚合平台，提供 AI 模型、产品、工具、框架、论文等资源的聚合与展示，并支持智能体协作功能。

## 功能特性

### 核心 MVP 功能

- 🔍 **内容浏览与搜索**
  - 分类浏览（AI 模型、AI 产品、AI 工具、AI 框架、研究论文、教程指南）
  - 关键词搜索与高级筛选
  - 内容详情与互动（点赞、收藏、评论、评分）

- 👤 **用户系统**
  - 用户注册与登录
  - JWT 认证与会话管理
  - 个人资料管理
  - 密码修改

- 🤖 **智能体协作**（后端支持，前端占位）
  - 多智能体团队管理
  - 实时会话追踪
  - 决策结论记录

- 🔒 **内容管理**
  - 内容发布与管理
  - 软删除
  - 标签系统

## 技术栈

### 后端
- **Spring Boot 3.2.0** - Java 应用框架
- **Spring Security** - 安全框架，JWT 认证
- **Spring Data JPA** - 数据持久化
- **MySQL 8.0** - 主数据库
- **Redis 7** - 缓存与会话存储
- **Swagger/OpenAPI** - API 文档
- **Flyway** - 数据库版本控制

### 前端
- **Vue 3.4.0** - 渐进式框架
- **TypeScript** - 类型安全
- **Vite 5.0** - 构建工具
- **Pinia 2.1** - 状态管理
- **Element Plus 2.7** - UI 组件库
- **UnoCSS 0.60** - 原子化 CSS
- **Vue Router 4.5** - 路由管理

### 基础设施
- **Docker & Docker Compose** - 容器化部署
- **Nginx** - 反向代理与静态文件服务

## 快速开始

### 环境要求

- Docker 20.10+
- Docker Compose 1.29+
- Node.js 20+ (仅开发需要)
- Java 17+ (仅开发需要)
- Maven 3.8+ (仅开发需要)

### 一键启动

使用 Docker Compose 启动所有服务：

```bash
# 克隆项目
git clone https://github.com/ai-tide/ai-tide.git
cd ai-tide

# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

服务启动后，访问：
- 前端: http://localhost:3000
- 后端 API: http://localhost:8080
- API 文档: http://localhost:8080/swagger-ui.html
- MySQL: localhost:3306
- Redis: localhost:6379

### 默认账号

```
用户名: admin
邮箱: admin@ai-tide.com
密码: admin123
```

⚠️ **注意**: 生产环境请立即修改默认密码！

## 开发指南

### 后端开发

```bash
cd ai-tide-api

# 安装依赖
mvn clean install

# 运行应用
mvn spring-boot:run

# 运行测试
mvn test
```

### 前端开发

```bash
cd ai-tide-web

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build

# 预览生产构建
npm run preview
```

### 数据库迁移

```bash
cd ai-tide-api

# 运行 Flyway 迁移
mvn flyway:migrate

# 查看迁移状态
mvn flyway:info
```

## 项目结构

```
ai-tide/
├── ai-tide-api/          # 后端 Spring Boot 应用
│   ├── src/main/java/com/ai/tide/
│   │   ├── config/      # 配置类
│   │   ├── controller/  # 控制器
│   │   ├── service/     # 业务逻辑
│   │   ├── repository/  # 数据访问
│   │   ├── entity/      # 实体类
│   │   ├── dto/         # 数据传输对象
│   │   ├── vo/          # 视图对象
│   │   ├── enums/       # 枚举类型
│   │   ├── exception/   # 异常处理
│   │   └── utils/       # 工具类
│   └── src/main/resources/
│       ├── application.yml
│       └── db/migration/  # Flyway 迁移脚本
│
├── ai-tide-web/          # 前端 Vue 应用
│   ├── src/
│   │   ├── components/  # 可复用组件
│   │   ├── views/       # 页面视图
│   │   ├── router/      # 路由配置
│   │   ├── stores/      # Pinia 状态
│   │   ├── api/         # API 调用
│   │   ├── utils/       # 工具函数
│   │   ├── types/       # TypeScript 类型
│   │   ├── styles/      # 样式文件
│   │   └── assets/      # 静态资源
│   ├── index.html
│   ├── vite.config.ts
│   ├── uno.config.ts
│   └── package.json
│
├── docker/               # Docker 配置
│   ├── docker-compose.yml
│   ├── mysql/
│   │   └── init.sql
│   └── redis/
│       └── redis.conf
│
├── openspec/            # OpenSpec 变更管理
│   └── changes/
│
├── .env.example          # 环境变量示例
├── docker-compose.yml     # Docker Compose 配置
├── README.md            # 项目文档
└── CONTRIBUTING.md       # 贡献指南
```

## 环境变量

复制 `.env.example` 为 `.env` 并根据需要修改：

```bash
# 数据库配置
DB_HOST=mysql
DB_PORT=3306
DB_NAME=ai_tide
DB_USER=ai_tide_user
DB_PASSWORD=your_password_here

# Redis 配置
REDIS_HOST=redis
REDIS_PORT=6379
REDIS_PASSWORD=

# JWT 配置
JWT_SECRET=your_jwt_secret_change_this_in_production
JWT_EXPIRATION=86400000

# 上传配置
UPLOAD_DIR=/app/uploads
UPLOAD_MAX_SIZE=10485760

# 应用配置
APP_NAME=AI-Tide
APP_URL=http://localhost:3000
API_URL=http://localhost:8080
```

## API 文档

启动后端服务后，访问 Swagger UI：

```
http://localhost:8080/swagger-ui.html
```

主要 API 端点：

- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/logout` - 用户登出
- `GET /api/content/list` - 获取内容列表
- `GET /api/content/{id}` - 获取内容详情
- `GET /api/search` - 搜索内容
- `POST /api/interaction/like` - 点赞/取消点赞
- `POST /api/interaction/favorite` - 收藏/取消收藏
- `POST /api/interaction/comment` - 发表评论

详细 API 文档请参考 Swagger UI。

## 部署

### Docker 部署

```bash
# 构建镜像
docker-compose build

# 启动服务
docker-compose up -d

# 停止服务
docker-compose down

# 停止并删除卷
docker-compose down -v
```

### 使用部署脚本

```bash
# 运行部署脚本
./scripts/deploy.sh

# 运行备份脚本
./scripts/backup.sh
```

## 安全建议

⚠️ 生产环境部署前请务必：

1. 修改所有默认密码
2. 使用强密码和 JWT secret
3. 启用 HTTPS
4. 配置防火墙规则
5. 定期备份数据库
6. 使用环境变量管理敏感信息
7. 启用日志监控与其他
8. 定期更新依赖包

## 常见问题

### 服务无法启动

检查端口占用：
```bash
netstat -tuln | grep -E '(3000|8080|3306|6379)'
```

### 数据库连接失败

确认 MySQL 容器已启动：
```bash
docker-compose ps mysql
docker-compose logs mysql
```

### 前端构建失败

清除缓存重新构建：
```bash
cd ai-tide-web
rm -rf node_modules dist
npm install
npm run build
```

## 贡献

欢迎贡献！请查看 [CONTRIBUTING.md](CONTRIBUTING.md) 了解详情。

## 许可证

本项目采用 MIT 许可证。详见 [LICENSE](LICENSE) 文件。

## 联系方式

- 项目地址: https://github.com/ai-tide/ai-tide
- 问题反馈: https://github.com/ai-tide/ai-tide/issues
- 邮箱: support@ai-tide.com

## 致谢

感谢所有为本项目做出贡献的开发者。

---

**AI-Tide** - 发现改变世界的 AI 技术
