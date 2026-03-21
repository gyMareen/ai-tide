# AI 前沿技术展示平台 - 项目文档

> 🚀 **AgentTerm / AI-Tide** - 发现改变世界的 AI 技术
>
> 聚合全球最新 AI 工具、框架和模型，第一时间掌握前沿动态

---

## 🏗️ 项目架构

### 技术栈

```
前端: Vue 3 + TypeScript + Vite + Element Plus + UnoCSS
后端: Java 17 + Spring Boot 3 + Spring Security + JPA
数据库: MySQL 8.0 + Redis 7 + Elasticsearch 8
部署: Docker + Docker Compose + Nginx
```

### 系统架构图

```
用户 → CDN → Nginx → Vue Frontend
                  ↓
              API Gateway
                  ↓
        Spring Boot Services
                  ↓
    MySQL / Redis / Elasticsearch
```

---

## 📁 项目结构

```
ai-tide/
├── 📁 docs/                          # 📚 项目文档
│   ├── README.md                    # 文档入口
│   ├── 01-需求文档-PRD.md           # 产品需求文档
│   ├── 02-系统架构设计.md            # 系统架构设计
│   ├── 03-数据库设计.md              # 数据库设计
│   ├── 04-API接口设计.md             # API接口设计
│   ├── 05-部署文档.md                # 部署文档
│   └── 06-原型设计.md                # 原型设计
│
├── 📁 ai-tide-api/                # 🔧 后端 API (Spring Boot)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/ai-tide/
│   │   │   │   ├── AiTideApplication.java
│   │   │   │   ├── config/          # 配置类
│   │   │   │   ├── controller/      # 控制器
│   │   │   │   ├── service/         # 服务层
│   │   │   │   ├── repository/      # 数据访问
│   │   │   │   ├── entity/          # 实体类
│   │   │   │   ├── dto/             # DTO
│   │   │   │   ├── vo/              # VO
│   │   │   │   ├── enums/           # 枚举
│   │   │   │   ├── exception/       # 异常处理
│   │   │   │   └── utils/           # 工具类
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       └── db/migration/    # 数据库迁移
│   │   └── test/
│   └── pom.xml
│
├── 📁 ai-tide-web/                # 🎨 前端前台 (Vue3)
│   ├── src/
│   │   ├── views/           # 页面视图
│   │   │   ├── home/        # 首页
│   │   │   ├── category/    # 分类页
│   │   │   ├── tech/        # 技术详情
│   │   │   ├── article/     # 文章
│   │   │   ├── search/      # 搜索
│   │   │   └── user/        # 用户中心
│   │   ├── components/      # 组件
│   │   │   ├── common/      # 通用组件
│   │   │   └── business/    # 业务组件
│   │   ├── router/          # 路由
│   │   ├── stores/          # 状态管理
│   │   ├── api/             # API 接口
│   │   ├── utils/           # 工具函数
│   │   ├── types/           # TypeScript 类型
│   │   ├── composables/     # 组合式函数
│   │   └── assets/          # 静态资源
│   ├── package.json
│   ├── vite.config.ts
│   └── tsconfig.json
│
├── 📁 ai-tide-admin/                # 🔐 管理后台 (Vue3)
│   └── src/... (同 web 结构)
│
├── 📁 docker/                          # 🐳 Docker 配置
│   ├── docker-compose.yml
│   ├── nginx/
│   │   ├── nginx.conf
│   │   └── ssl/
│   ├── mysql/
│   │   └── my.cnf
│   └── redis/
│       └── redis.conf
│
├── 📁 scripts/                         # 📜 脚本
│   ├── deploy.sh
│   ├── backup.sh
│   └── init-data.sh
│
├── 📄 README.md                        # 项目说明
├── 📄 LICENSE                          # 许可证
└── 📄 .gitignore                       # Git 忽略配置
```

---

## 🚀 快速开始

### 环境要求

- Java 17+
- Node.js 18+
- MySQL 8.0+
- Redis 7+
- Docker & Docker Compose (可选)

### 本地开发

```bash
# 1. 克隆项目
git clone https://github.com/your-username/ai-tide.git
cd ai-tide

# 2. 启动后端
cd ai-tide-api
./mvnw spring-boot:run

# 3. 启动前端
cd ../ai-tide-web
npm install
npm run dev

# 4. 访问
# 前台: http://localhost:5173
# 后台: http://localhost:5173/admin
# API: http://localhost:8080
```

### Docker 部署

```bash
# 1. 进入 docker 目录
cd docker

# 2. 配置环境变量
cp .env.example .env
# 编辑 .env 文件，设置数据库密码等

# 3. 启动服务
docker-compose up -d

# 4. 查看日志
docker-compose logs -f

# 5. 停止服务
docker-compose down
```
