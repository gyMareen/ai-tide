# AI-Tide 系统架构设计文档

## 1. 文档信息

| 项目 | 内容 |
|------|------|
| 文档版本 | v1.0.0 |
| 创建日期 | 2026-03-23 |
| 文档状态 | 草稿 |
| 作者 | 架构团队 |
| 审核人 | 待定 |
| 项目名称 | AI-Tide (AI 前沿技术展示平台) |
| 文档密级 | 内部 |

---

## 2. 架构概述

### 2.1 设计目标

1. **高性能**：首页加载时间 < 2 秒，搜索响应时间 < 500 毫秒
2. **高可用**：系统可用性 > 99.5%，支持 1000+ 并发用户
3. **高安全**：完善的认证授权、数据加密、攻击防护机制
4. **易扩展**：模块化设计，便于功能扩展和水平扩展
5. **易维护**：清晰的代码结构、完善的日志和监控

### 2.2 设计原则

- **分层设计**：前后端分离，后端采用分层架构
- **单一职责**：每个模块/类只负责一个功能
- **开闭原则**：对扩展开放，对修改封闭
- **依赖倒置**：依赖抽象而非具体实现
- **接口隔离**：客户端不应依赖它不需要的接口

### 2.3 技术选型说明

| 技术层 | 选型 | 说明 |
|--------|------|------|
| 前端框架 | Vue 3 | 渐进式、组件化、生态完善 |
| 前端语言 | TypeScript | 类型安全、减少运行时错误 |
| 构建工具 | Vite | 快速冷启动、HMR |
| UI 框架 | Element Plus | 企业级、组件丰富 |
| CSS 方案 | UnoCSS | 原子化、高性能 |
| 后端框架 | Spring Boot 3 | 成熟稳定、生态丰富 |
| 后端语言 | Java 17 | LTS 版本、性能优化 |
| 安全框架 | Spring Security | 认证授权标准方案 |
| ORM | JPA/Hibernate | 对象关系映射、简化数据库操作 |
| 数据库 | MySQL 8.0 | 关系型数据库、事务支持 |
| 缓存 | Redis 7 | 高性能键值存储 |
| 容器化 | Docker | 环境一致性、便于部署 |
| 反向代理 | Nginx | 高性能、负载均衡 |

---

## 3. 系统架构

### 3.1 整体架构图

```
┌─────────────────────────────────────────────────────────────────────┐
│                         用户层                                │
├─────────────────────────────────────────────────────────────────────┤
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │    浏览器    │  │   移动端    │  │  管理后台   │      │
│  │  (Vue3 Web)  │  │  (响应式)    │  │  (Vue3)     │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────────────┐
│                      Nginx (反向代理)                         │
│              • 静态资源服务                                    │
│              • 负载均衡                                        │
│              • SSL 终止                                        │
└─────────────────────────────────────────────────────────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    应用层 (Spring Boot)                         │
├─────────────────────────────────────────────────────────────────────┤
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │                   Controller 层                         │   │
│  │  • UserController                                       │   │
│  │  • ContentController                                     │   │
│  │  • InteractionController                                  │   │
│  │  • AdminController                                      │   │
│  │  • AgentController                                      │   │
│  │  • PaymentController                                     │   │
│  └──────────────────────────────────────────────────────────────┘   │
│                              ▼                                  │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │                   Service 层                            │   │
│  │  • UserService                                          │   │
│  │  • ContentService                                        │   │
│  │  • InteractionService                                   │   │
│  │  • AdminService                                         │   │
│  │  • AgentService                                         │   │
│  │  • PaymentService                                        │   │
│  │  • ScheduleService (定时任务)                             │   │
│  └──────────────────────────────────────────────────────────────┘   │
│                              ▼                                  │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │                   Repository 层 (JPA)                     │   │
│  │  • UserRepository                                       │   │
│  │  • ContentRepository                                     │   │
│  │  • ...                                                 │   │
│  └──────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────────────┐
│                      数据层                                    │
├─────────────────────────────────────────────────────────────────────┤
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│  │   MySQL 8   │  │   Redis 7   │  │   本地存储   │        │
│  │  • 用户数据  │  │  • 缓存     │  │  • 图片     │        │
│  │  • 内容数据  │  │  • Session   │  │  • 日志     │        │
│  │  • 业务数据  │  │  • Token     │  │             │        │
│  └─────────────┘  └─────────────┘  └─────────────┘        │
└─────────────────────────────────────────────────────────────────────┘
```

### 3.2 分层架构说明

#### 用户层
- **Web 前台**：用户访问的主要入口，基于 Vue 3 + TypeScript
- **管理后台**：管理员和内容编辑的后台管理界面
- **移动端**：响应式设计，适配手机和平板

#### 网关层
- **Nginx**：作为反向代理和负载均衡器
- 功能：
  - 静态服务（CSS、JS、图片）
  - 反向代理到后端 API
  - SSL/TLS 终止
  - Gzip 压缩
  - 请求限流

#### 应用层
- **Controller 层**：接收 HTTP 请求，参数验证，返回响应
- **Service 层**：业务逻辑处理，事务管理
- **Repository 层**：数据访问，与数据库交互

#### 数据层
- **MySQL**：持久化存储，关系型数据
- **Redis**：缓存，Session，Token，热点数据

### 3.3 核心组件说明

#### 3.3.1 认证授权组件
- 技术方案：Spring Security + JWT
- JWT Token 存储位置：Redis
- Token 过期时间：7 天（记住我）/ 2 小时（普通）
- 密码加密：bcrypt

#### 3.3.2 缓存组件
- 技术方案：Redis 7
- 缓存策略：
  - 首页数据：5 分钟
  - 详情页数据：10 分钟
  - 热门内容：1 小时
  - 用户 Session：会话期
  - 验证码：5 分钟

#### 3.3.3 搜索组件
- 技术方案：MySQL 全文索引
- 搜索范围：标题、描述、内容
- 索引更新：内容发布/更新时同步

#### 3.3.4 文件存储组件
- 技术方案：本地文件系统（MVP）
- 存储路径：`/var/www/ai-tide/uploads/`
- 支持类型：JPG、PNG、GIF
- 大小限制：
  - 头像：2MB
  - 封面图：5MB
  - 内容图片：10MB

#### 3.3.5 定时任务组件
- 技术方案：Spring Scheduler
- 功能：
  - 智能体协作调度
  - 过期 Token 清理
  - 操作日志归档（每日）

#### 3.3.6 消息通知组件
- 技术方案：WebSocket（可选）
- 使用场景：
  - 实时消息推送
  - 智能体协作实时更新

---

## 4. 前端架构

### 4.1 目录结构

```
ai-tide-web/
├── src/
│   ├── views/                  # 页面视图
│   │   ├── home/              # 首页
│   │   │   ├── HomeView.vue
│   │   │   └── components/
│   │   ├── content/            # 内容相关
│   │   │   ├── DetailView.vue  # 详情页
│   │   │   ├── SearchView.vue # 搜索页
│   │   │   └── CategoryView.vue # 分类页
│   │   ├── user/              # 用户相关
│   │   │   ├── LoginView.vue
│   │   │   ├── RegisterView.vue
│   │   │   └── ProfileView.vue
│   │   └── agent/             # 智能体协作
│   │       ├── TeamListView.vue
│   │       ├── TeamDetailView.vue
│   │       └── SessionDetailView.vue
│   ├── components/              # 公共组件
│   │   ├── common/            # 通用组件
│   │   │   ├── Header.vue
│   │   │   ├── Footer.vue
│   │   │   └── Loading.vue
│   │   └── business/          # 业务组件
│   │       ├── ContentCard.vue
│   │       ├── CommentList.vue
│   │       ├── RatingStars.vue
│   │       └── AgentMessageItem.vue
│   ├── router/                  # 路由配置
│   │   ├── index.ts
│   │   └── guards.ts
│   ├── stores/                  # 状态管理 (Pinia)
│   │   ├── user.ts
│   │   ├── content.ts
│   │   └── agent.ts
│   ├── api/                    # API 接口
│   │   ├── user.ts
│   │   ├── content.ts
│   │   ├── interaction.ts
│   │   ├── admin.ts
│   │   ├── agent.ts
│   │   └── payment.ts
│   ├── utils/                  # 工具函数
│   │   ├── request.ts          # 请求封装
│   │   ├── storage.ts          # 本地存储
│   │   ├── auth.ts            # 认证工具
│   │   └── format.ts          # 格式化工具
│   ├── types/                  # TypeScript 类型
│   │   ├── user.ts
│   │   ├── content.ts
│   │   └── agent.ts
│   ├── composables/            # 组合式函数
│   │   ├── useAuth.ts
│   │   ├── usePagination.ts
│   │   └── useWebSocket.ts
│   ├── assets/                 # 静态资源
│   │   ├── images/
│   │   ├── styles/
│   │   └── icons/
│   ├── App.vue
│   └── main.ts
├── package.json
├── vite.config.ts
├── tsconfig.json
└── uno.config.ts
```

### 4.2 技术栈说明

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | 3.x | 渐进式框架 |
| TypeScript | 5.x | 类型系统 |
| Vite | 5.x | 构建工具 |
| Element Plus | 2.x | UI 组件库 |
| UnoCSS | 0.x | 原子化 CSS |
| Pinia | 2.x | 状态管理 |
| Vue Router | 4.x | 路由管理 |
| Axios | 1.x | HTTP 客户端 |
| Markdown-it | 14.x | Markdown 渲染 |
| ECharts | 5.x | 图表库 |
| Day.js | 1.x | 日期处理 |

### 4.3 状态管理方案

**技术方案**：Pinia

**Store 设计**：

```typescript
// stores/user.ts
export const useUserStore = defineStore('user', {
  state: () => ({
    token: '',
    userInfo: null as UserInfo | null,
    permissions: [] as string[]
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    hasPermission: (state) => (perm: string) => state.permissions.includes(perm)
  },

  actions: {
    async login(credentials: LoginDTO) { ... },
    async logout() { ... },
    async getUserInfo() { ... }
  }
})

// stores/content.ts
export const useContentStore = defineStore('content', {
  state: () => ({
    currentContent: null as Content | null,
    searchResults: [] as Content[],
    favoriteContents: [] as Content[]
  }),

  actions: {
    async fetchContent(id: string) { ... },
    async searchContent(params: SearchParams) { ... },
    async toggleFavorite(contentId: string) { ... }
  }
})

// stores/agent.ts
export const useAgentStore = defineStore('agent', {
  state: () => ({
    teams: [] as AgentTeam[],
    currentSession: null as CollaborationSession | null,
    messages: [] as DiscussionMessage[]
  }),

  actions: {
    async fetchTeams() { ... },
    async fetchSession(sessionId: string) { ... },
    async subscribeMessages(sessionId: string) { ... }
  }
})
```

### 4.4 路由设计

```typescript
// router/index.ts
const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/home/HomeView.vue')
  },
  {
    path: '/content/:id',
    name: 'ContentDetail',
    component: () => import('@/views/content/DetailView.vue')
  },
  {
    path: '/search',
    name: 'Search',
    component: () => import('@/views/content/SearchView.vue')
  },
  {
    path: '/category/:id',
    name: 'Category',
    component: () => import('@/views/content/CategoryView.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/user/LoginView.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/user/RegisterView.vue')
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/user/ProfileView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/agent/teams',
    name: 'AgentTeams',
    component: () => import('@/views/agent/TeamListView.vue')
  },
  {
    path: '/agent/team/:id',
    name: 'AgentTeamDetail',
    component: () => import('@/views/agent/TeamDetailView.vue')
  },
  {
    path: '/agent/session/:id',
    name: 'AgentSessionDetail',
    component: () => import('@/views/agent/SessionDetailView.vue')
  }
]
```

### 4.5 组件设计

#### 通用组件
- **Header**：顶部导航，包含 Logo、搜索框、用户菜单
- **Footer**：页脚信息，版权、链接
- **Loading**：加载状态指示器
- **Empty**：空数据状态展示
- **ErrorBoundary**：错误边界捕获

#### 业务组件
- **ContentCard**：内容卡片，用于列表展示
- **CommentList**：评论列表和评论输入
- **RatingStars**：星级评分组件
- **TagCloud**：标签云
- **Pagination**：分页组件
- **AgentMessageItem**：智能体消息项
- **AgentAvatar**：智能体头像和状态

### 4.6 性能优化策略

1. **路由懒加载**：使用动态 import 实现路由懒加载
2. **组件懒加载**：大型组件按需加载
3. **图片优化**：
   - WebP 格式支持
   - 图片懒加载
   - 响应式图片
4. **代码分割**：Vite 自动代码分割
5. **缓存策略**：
   - HTTP 缓存头设置
   - 本地缓存常用数据
6. **防抖节流**：搜索输入、滚动加载使用防抖
7. **虚拟滚动**：长列表使用虚拟滚动

---

## 5. 后端架构

### 5.1 目录结构

```
ai-tide-api/
├── src/
│   ├── main/
│   │   ├── java/com/ai/tide/
│   │   │   ├── AiTideApplication.java
│   │   │   │
│   │   │   ├── config/           # 配置类
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── JwtConfig.java
│   │   │   │   ├── RedisConfig.java
│   │   │   │   ├── WebConfig.java
│   │   │   │   ├── ScheduleConfig.java
│   │   │   │   └── WebSocketConfig.java
│   │   │   │
│   │   │   ├── controller/       # 控制器层
│   │   │   │   ├── UserController.java
│   │   │   │   ├── ContentController.java
│   │   │   │   ├── InteractionController.java
│   │   │   │   ├── AdminController.java
│   │   │   │   ├── AgentController.java
│   │   │   │   └── PaymentController.java
│   │   │   │
│   │   │   ├── service/          # 服务层
│   │   │   │   ├── UserService.java
│   │   │   │   ├── ContentService.java
│   │   │   │   ├── InteractionService.java
│   │   │   │   ├── AdminService.java
│   │   │   │   ├── AgentService.java
│   │   │   │   ├── PaymentService.java
│   │   │   │   └── ScheduleService.java
│   │   │   │
│   │   │   ├── repository/       # 数据访问层
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── ContentRepository.java
│   │   │   │   ├── ... (其他 Repository)
│   │   │   │
│   │   │   ├── entity/           # 实体类
│   │   │   │   ├── User.java
│   │   │   │   ├── Content.java
│   │   │   │   ├── ... (其他实体)
│   │   │   │
│   │   │   ├── dto/              # 数据传输对象
│   │   │   │   ├── request/     # 请求 DTO
│   │   │   │   └── response/    # 响应 DTO
│   │   │   │
│   │   │   ├── vo/               # 视图对象
│   │   │   │   ├── UserVO.java
│   │   │   │   ├── ContentVO.java
│   │   │   │   └── ...
│   │   │   │
│   │   │   ├── enums/            # 枚举类型
│   │   │     ├── exception/        # 异常处理
│   │   │   │   ├── BusinessException.java
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   └── ErrorResponse.java
│   │   │   │
│   │   │   ├── utils/            # 工具类
│   │   │   │   ├── JwtUtil.java
│   │   │   │   ├── PasswordUtil.java
│   │   │   │   ├── FileUtil.java
│   │   │   │   └── CacheUtil.java
│   │   │   │
│   │   │   ├── security/         # 安全相关
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   ├── CustomUserDetailsService.java
│   │   │   │   └── PermissionEvaluator.java
│   │   │   │
│   │   │   └── aspect/           # 切面
│   │   │       ├── LogAspect.java       # 操作日志切面
│   │   │       └── CacheAspect.java    # 缓存切面
│   │   │
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       └── db/migration/         # 数据库迁移脚本
│   │
│   └── test/                      # 测试代码
│       ├── unit/
│       └── integration/
│
├── pom.xml
└── Dockerfile
```

### 5.2 分层架构

```
┌─────────────────────────────────────────────────┐
│            Controller 层                       │
│  • 接收 HTTP 请求                            │
│  • 参数验证 (@Valid, @RequestBody)             │
│  • 调用 Service 层                           │
│  •.封装响应结果                               │
└─────────────────────────────────────────────────┘
                    ▼
┌─────────────────────────────────────────────────┐
│             Service 层                        │
│  • 业务逻辑处理                               │
│  • 事务管理 (@Transactional)                  │
│  • 调用 Repository 层                          │
│  • 调用外部服务（邮件、支付等）                  │
└─────────────────────────────────────────────────┘
                    ▼
┌─────────────────────────────────────────────────┐
│          Repository 层 (JPA)                 │
│  • 继承 JpaRepository                        │
│  • 定义自定义查询方法                          │
│  • 使用 @Query 注解编写复杂查询                 │
└─────────────────────────────────────────────────┘
                    ▼
┌─────────────────────────────────────────────────┐
│              数据库 (MySQL)                    │
└─────────────────────────────────────────────────┘
```

### 5.3 技术栈说明

| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 3.x | 应用框架 |
| Spring Security | 6.x | 安全框架 |
| Spring Data JPA | 3.x | ORM 框架 |
| Hibernate | 6.x | JPA 实现 |
| MySQL Connector | 8.x | MySQL 驱动 |
| Lettuce | 6.x | Redis 客户端 |
| Jackson | 2.x | JSON 序列化 |
| Lombok | 1.x | 简化代码 |
| Validation API | 3.x | 参数校验 |
| Swagger | 3.x | API 文档 |
| Quartz | 2.x | 定时任务 |

### 5.4 Spring Security 配置

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 禁用 CSRF（JWT 方式不需要）
            .csrf(CsrfConfigurer::disable)
            // 配置 CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // 配置 Session 管理（无状态）
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 配置权限规则
            .authorizeHttpRequests(auth -> auth
                // 公开接口
                .requestMatchers(
                    "/api/auth/**",
                    "/api/content/public/**",
                    "/api/agent/public/**",
                    "/swagger/**",
                    "/v3/api-docs/**"
                ).permitAll()
                // 需要认证的接口
                .anyRequest().authenticated())
            // 添加 JWT 过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            // 配置异常处理
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(unauthorizedHandler)
                .accessDeniedHandler(accessDeniedHandler));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

### 5.5 JWT Token 设计

**Token 结构**：

```json
{
  "header": {
    "alg": "HS256",
    "typ": "JWT"
  },
  "payload": {
    "sub": "用户ID",
    "username": "用户名",
    "role": "用户角色",
    "permissions": ["权限列表"],
    "iat": "签发时间",
    "exp": "过期时间"
  },
  "signature": "签名"
}
```

**Token 工具类**：

```java
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    // 生成 Token
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", user.getId());
        claims.put("username", user.getUsername());
        claims.put("role", user.getRole());
        claims.put("permissions", getPermissions(user));

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(SignatureAlgorithm.HS256, secret)
            .compact();
    }

    // 解析 Token
    public Claims parseToken(String token) {
        return Jwts.parser()
            .setSigningKey(secret)
            .parseClaimsJws(token)
            .getBody();
    }

    // 验证 Token
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 从 Token 获取用户 ID
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return Long.parseLong(claims.getSubject());
    }
}
```

### 5.6 异常处理

**全局异常处理器**：

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 业务异常
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        ErrorResponse response = ErrorResponse.builder()
            .code(e.getCode())
            .message(e.getMessage())
            .timestamp(System.currentTimeMillis())
            .build();
        return ResponseEntity.status(e.getHttpStatus()).body(response);
    }

    // 参数校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));
        ErrorResponse response = ErrorResponse.builder()
            .code(400)
            .message(message)
            .timestamp(System.currentTimeMillis())
            .build();
        return ResponseEntity.badRequest().body(response);
    }

    // 其他异常
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorResponse response = ErrorResponse.builder()
            .code(500)
            .message("服务器内部错误")
            .timestamp(System.currentTimeMillis())
            .build();
        return ResponseEntity.internalServerError().body(response);
    }
}
```

---

## 6. 数据架构

### 6.1 数据库设计原则

1. **范式化**：遵循第三范式（3NF），减少数据冗余
2. **索引优化**：为常用查询字段添加索引
3. **命名规范**：表名小写复数，字段名小写下划线分隔
4. **字段类型**：选择合适的字段类型，节省存储空间
5. **软删除**：使用 status 字段标记删除，便于数据恢复
6. **时间戳**：所有表包含 create_time 和 update_time

### 6.2 缓存策略

**Redis 使用场景**：

| 场景 | 缓存 Key 格式 | 过期时间 | 说明 |
|------|--------------|----------|------|
| 用户 Session | `session:{userId}` | 7 天 | 用户登录会话 |
| JWT Token | `token:{userId}` | 7 天 | JWT 黑名单 |
| 首页数据 | `home:page:{page}` | 5 分钟 | 首页内容 |
| 详情页数据 | `content:{id}` | 10 分钟 | 内容详情 |
| 热门内容 | `hot:content` | 1 小时 | 热门内容列表 |
| 验证码 | `captcha:{key}` | 5 分钟 | 图形验证码 |
| 限流计数 | `rate_limit:{userId}:{api}` | 1 分钟 | API 限流 |
| 搜索结果 | `search:{hash}` | 30 分钟 | 搜索结果缓存 |

### 6.3 全文搜索设计

**MySQL 全文索引**：

```sql
-- 为内容表添加全文索引
ALTER TABLE contents ADD FULLTEXT INDEX ft_search (title, description, content);

-- 全文搜索查询
SELECT * FROM contents
WHERE MATCH(title, description, content) AGAINST('人工智能' IN NATURAL LANGUAGE MODE)
AND status = 'PUBLISHED'
ORDER BY publish_time DESC
LIMIT 20;
```

**搜索优化策略**：
1. 关键词长度 > 2 个字符
2. 结果按相关度 + 时间综合排序
3. 分页使用 LIMIT OFFSET
4. 热门搜索结果预缓存

### 6.4 数据一致性保证

**事务管理**：

```java
@Service
@Transactional
public class ContentService {

    // 发布内容
    public void publishContent(Long contentId) {
        // 更新内容状态
        Content content = contentRepository.findById(contentId)
            .orElseThrow(() -> new BusinessException("内容不存在"));
        content.setStatus(ContentStatus.PUBLISHED);
        content.setPublishTime(LocalDateTime.now());
        contentRepository.save(content);

        // 清除缓存
        cacheUtil.evict("content:" + contentId);

        // 同步全文索引
        searchService.syncContent(content);
    }

    // 收藏内容
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void toggleFavorite(Long userId, Long contentId) {
        // 检查是否已收藏
        Optional<Favorite> existing = favoriteRepository
            .findByUserIdAndContentId(userId, contentId);

        if (existing.isPresent()) {
            // 取消收藏
            favoriteRepository.delete(existing.get());
            contentRepository.decrementFavoriteCount(contentId);
        } else {
            // 添加收藏
            Favorite favorite = new Favorite();
            favorite.setUserId(userId);
            favorite.setContentId(contentId);
            favoriteRepository.save(favorite);
            contentRepository.incrementFavoriteCount(contentId);
        }
    }
}
```

### 6.5 分表分库规划

**当前阶段**：单库单表，数据量可满足 MVP 需求

**未来扩展**（数据量增长后）：

1. **用户表**：按 ID hash 分表 `users_0`, `users_1`, ...
2. **内容表**：按分类分表 `contents_model`, `contents_product`, `contents_article`
3. **评论表**：按内容 ID 分表，每个表 100 万条记录
4. **日志表**：按日期分表 `operation_logs_20260323`

---

## 7. 接口设计

### 7.1 RESTful API 设计规范

**URL 规范**：

| 资源 | URL | 方法 | 说明 |
|------|-----|------|------|
| 用户 | `/api/users` | GET | 获取用户列表 |
|      | `/api/users/{id}` | GET | 获取用户详情 |
|      | `/api/users` | POST | 创建用户用户 |
|      | `/api/users/{id}` | PUT | 更新用户 |
|      | `/api/users/{id}` | DELETE | 删除用户 |
|      | `/api/users/{id}/avatar` | POST | 上传头像 |
| 内容 | `/api/contents` | GET | 获取内容列表 |
|      | `/api/contents/{id}` | GET | 获取内容详情 |
|      | `/api/contents` | POST | 创建内容 |
|      | `/api/contents/{id}` | PUT | 更新内容 |
|      | `/api/contents/{id}` | DELETE | 删除内容 |
|      | `/api/contents/{id}/like` | POST | 点赞 |
|      | `/api/contents/{id}/favorite` | POST | 收藏 |
| 评论 | `/api/contents/{id}/comments` | GET | 获取评论列表 |
|      | `/api/contents/{id}/comments` | POST | 发表评论 |
|      | `/api/comments/{id}` | DELETE | 删除评论 |

### 7.2 接口版本管理

**方案**：URL 路径包含版本号

```
/api/v1/users
/api/v1/contents
/api/v1/comments
```

### 7.3 统一响应格式

**成功响应**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    // 响应数据
  },
  "timestamp": 1648000000000
}
```

**失败响应**：

```json
{
  "code": 400,
  "message": "参数校验失败",
  "data": null,
  "timestamp": 1648000000000
}
```

**分页响应**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "list": [...],
    "total": 100,
    "page": 1,
    "size": 10,
    "pages": 10
  },
  "timestamp": 1648000000000
}
```

### 7.4 错误码设计

| 错误码 | 说明 | HTTP 状态码 |
|--------|------|------------|
| 200 | 操作成功 | 200 |
| 400 | 参数校验失败 | 400 |
| 401 | 未登录 | 401 |
| 403 | 无权限 | 403 |
| 404 | 资源不存在 | 404 |
| 1001 | 用户名已存在 | 400 |
| 1002 | 邮箱已存在 | 400 |
| 1003 | 密码错误 | 401 |
| 1004 | 用户已被禁用 | 403 |
| 2001 | 内容不存在 | 404 |
| 2002 | 内容已被删除 | 404 |
| 3001 | 评论内容为空 | 400 |
| 3002 | 敏感词过滤 | 400 |
| 5000 | 服务器内部错误 | 500 |

---

## 8. 安全设计

### 8.1 认证授权方案

**认证流程**：

```
┌──────────┐      ┌──────────┐      ┌──────────┐
│  用户登录 │ ───▶│  后端验证 │ ───▶│ 生成Token │
└──────────┘      └──────────┘      └──────────┘
                                             ▼
                                       ┌──────────┐
                                       │  存入Redis│
                                       └──────────┘
                                             ▼
                                       ┌──────────┐
                                       │  返回Token│
                                       └──────────┘
                                             ▼
                                       ┌──────────┐
                                       │  前端存储 │
                                       └──────────┘
```

**授权流程**：

```
┌──────────┐      ┌──────────┐      ┌──────────┐
│  API请求 │ ───▶│  JWT验证 │ ───▶│  权限检查│
└──────────┘      └──────────┘      └──────────┘
                                             ▼
                                       ┌──────────┐
                                       │  执行业务 │
                                       └──────────┘
```

### 8.2 密码安全

**加密方案**：bcrypt

```java
public class PasswordUtil {

    // 加密密码
    public static String encrypt(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    // 验证密码
    public static boolean verify(String rawPassword, String encryptedPassword) {
        return BCrypt.checkpw(rawPassword, encryptedPassword);
    }
}
```

**密码强度要求**：
- 长度：8-32 字符
- 必须包含：大写字母、小写字母、数字
- 可选包含：特殊字符

### 8.3 API 安全

**限流策略**：

```java
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, ...) {
        String userId = getUserIdFromToken(request);
        String api = request.getRequestURI();

        String key = "rate_limit:" + userId + ":" + api;
        Long count = redisTemplate.opsForValue().increment(key);

        if (count == 1) {
            redisTemplate.expire(key, 1, TimeUnit.MINUTES);
        }

        if (count > 100) { // 每分钟最多 100 次
            throw new BusinessException("请求过于频繁，请稍后再试");
        }

        return true;
    }
}
```

### 8.4 XSS/CSRF 防护

**XSS 防护**：

1. **前端**：
   - 使用 Vue 框架，自动转义输出
   - 不使用 `v-html` 除非必要
   - 使用 DOMPurify 清理 HTML

2. **后端**：
   - 输入参数验证和过滤
   - 使用 `@Valid` 注解

**CSRF 防护**：

JWT 认证方式不需要 CSRF Token，因为：
- Token 存储在客户端（LocalStorage）
- 每次请求都在 Header 中携带 Token

### 8.5 SQL 注入防护

1. **使用 JPA**：参数化查询，自动防护
2. **禁止拼接 SQL**：不使用字符串拼接构建 SQL
3. **最小权限原则**：数据库用户只授予必要权限

### 8.6 敏感数据加密

**加密工具类**：

```java
@Component
public class EncryptionUtil {

    @Value("${encryption.key}")
    private String key;

    // AES 加密
    public String encrypt(String data) {
        // AES 加密实现
    }

    // AES 解密
    public String decrypt(String encrypted) {
        // AES 解密实现
    }
}
```

**敏感字段**：使用 `@Convert` 注解自动加解密

---

## 9. 性能设计

### 9.1 缓存设计

**多级缓存策略**：

```
┌─────────────────────────────────────────┐
│         本地缓存 (Caffeine)           │
│         • LRU 淘汰策略                │
│         • 容量 1000 条                 │
└─────────────────────────────────────────┘
              ▼ 命中
              ▼ 未命中
┌─────────────────────────────────────────┐
│         Redis 缓存                   │
│         • 过期时间自动清理              │
│         • 分布式共享                   │
└─────────────────────────────────────────┘
              ▼ 未命中
              ▼
┌─────────────────────────────────────────┐
│         数据库查询                   │
└─────────────────────────────────────────┘
```

### 9.2 数据库优化

**索引设计**：

```sql
-- 用户表索引
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);

-- 内容表索引
CREATE INDEX idx_contents_type ON contents(type);
CREATE INDEX idx_contents_publish_time ON contents(publish_time);
CREATE INDEX idx_contents_like_count ON contents(like_count);

-- 评论表索引
CREATE INDEX idx_comments_content_id ON comments(content_id);
CREATE INDEX idx_comments_publish_time ON comments(publish_time);

-- 收藏表复合索引
CREATE INDEX idx_favorites_user_content ON favorites(user_id, content_id);
```

**查询优化**：

1. 避免 `SELECT *`，只查询需要的字段
2. 使用 JOIN 代替多次查询
3. 使用 LIMIT 分页，避免大结果集
4. 使用 `EXPLAIN` 分析慢查询

### 9.3 前端性能优化

1. **代码分割**：路由懒加载
2. **资源压缩**：Gzip/Brotli 压缩
3. **CDN 加速**：静态资源使用 CDN
4. **图片优化**：WebP 格式、懒加载
5. **虚拟滚动**：长列表使用虚拟滚动
6. **预加载**：关键资源预加载

### 9.4 并发处理

**Tomcat 配置**：

```yaml
server:
  tomcat:
    threads:
      max: 200          # 最大线程数
      min-spare: 10      # 最小空闲线程
    accept-count: 1000    # 最大连接数
    connection-timeout: 20000  # 连接超时（毫秒）
```

**线程池配置**：

```java
@Configuration
public class ThreadPoolConfig {

    @Bean
    public ExecutorService asyncExecutor() {
        return Executors.newFixedThreadPool(50);
    }
}
```

### 9.5 CDN 使用

**Nginx 配置**：

```nginx
# 静态资源缓存
location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2)$ {
    expires 30d;
    add_header Cache-Control "public, immutable";
}
```

---

## 10. 部署架构

### 10.1 Docker 容器化设计

**Dockerfile（后端）**：

```dockerfile
FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/ai-tide-api.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Dockerfile（前端）**：

```dockerfile
FROM node:18-alpine as builder

WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

### 10.2 Docker Compose 配置

```yaml
version: '3.8'

services:
  # MySQL
  mysql:
    image: mysql:8.0
    container_name: ai-tide-mysql
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: ${MYSQL_DATABASE}
      MYSQL_USER: ${MYSQL_USER}
      MYSQL_PASSWORD: ${MYSQL_PASSWORD}
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql
    networks:
      - ai-tide-network

  # Redis
  redis:
    image: redis:7-alpine
    container_name: ai-tide-redis
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data
    command: redis-server --appendonly yes
    networks:
      - ai-tide-network

  # 后端 API
  api:
    build: ./ai-tide-api
    container_name: ai-tide-api
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DB_HOST: mysql
      DB_PORT: 3306
      REDIS_HOST: redis
      REDIS_PORT: 6379
    depends_on:
      - mysql
      - redis
    networks:
      - ai-tide-network

  # 前端 Web
  web:
    build: ./ai-tide-web
    container_name: ai-tide-web
    ports:
      - "80:80"
    depends_on:
      - api
    networks:
      - ai-tide-network

volumes:
  mysql-data:
  redis-data:

networks:
  ai-tide-network:
    driver: bridge
```

### 10.3 Nginx 反向代理配置

```nginx
upstream api_backend {
    server api:8080;
}

server {
    listen 80;
    server_name ai-tide.com;

    # 前端静态文件
    location / {
        root /usr/share/nginx/html;
        try_files $uri $uri/ /index.html;
    }

    # API 代理
    location /api/ {
        proxy_pass http://api_backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    # Gzip 压缩
    gzip on;
    gzip_types text/plain text/css application/json application/javascript;
    gzip_min_length 1000;

    # 缓存配置
    location ~* \.(js|css|)$ {
        expires 7d;
        add_header Cache-Control "public";
    }
}
```

### 10.4 数据持久化

**Volume 挂载**：
- MySQL 数据：`mysql-data:/var/lib/mysql`
- Redis 数据：`redis-data:/data`
- 上传文件：`./uploads:/app/uploads`

**备份策略**：
- 每日自动备份 MySQL
- 备份保留 30 天
- 备份文件存储在 `/backup` 目录

### 10.5 日志收集

**日志配置**：

```yaml
# application-prod.yml
logging:
  level:
    com.ai.tide: INFO
  file:
    name: /app/logs/ai-tide.log
  pattern:
    file: "%d{yyyy-MM-dd} [%thread] %-5level %logger{36} - %msg%n"
  logback:
    rollingpolicy:
      max-file-size: 10MB
      max-history: 30
```

**日志收集方案**：
1. 容器日志通过 Docker 日志驱动收集
2. 使用 ELK Stack 分析（可选）
3. 关键错误发送邮件告警

---

## 11. 监控运维

### 11.1 日志管理

**日志级别**：
- ERROR：错误日志，需要立即处理
- WARN：警告日志，需要关注
- INFO：信息日志，记录关键操作
- DEBUG：调试日志，仅开发环境

**日志分类**：
- 应用日志：`ai-tide.log`
- 访问日志：`access.log`
- 错误日志：`error.log`

### 11.2 监控指标

**关键指标**：

| 指标类型 | 具体指标 | 告警阈值 |
|----------|----------|----------|
| 系统 | CPU 使用率 | > 80% |
|  | 内存使用率 | > 80% |
|  | 磁盘使用率 | > 85% |
| 应用 | JVM 堆内存使用率 | > 80% |
|  | 请求响应时间 | P99 > 2s |
|  | 错误率 | > 1% |
|  | QPS | < 预期值的 50% |
| 数据库 | 连接数 | > 最大连接数的 80% |
|  | 慢查询 | 执行时间 > 1s |
|  | 连接失败率 | > 5% |

### 11.3 告警机制

**告警方式**：
- 邮件告警：发送到管理员邮箱
- 钉钉/企业微信：即时通知
- 短信告警：严重问题

**告警规则**：

```yaml
alerts:
  - name: HighCPUUsage
    condition: cpu_usage > 80%
    duration: 5m
    action: send_email

  - name: HighResponseTime
    condition: p99_response_time > 2s
    duration: 3m
    action: send_dingtalk

  - name: DatabaseConnectionError
    condition: db_connection_error_rate > 5%
    duration: 1m
    action: send_sms
```

---

## 12. 后续优化方向

### 12.1 可能的优化点

1. **搜索优化**
   - 引入 Elasticsearch 提升搜索体验
   - 支持拼音搜索
   - 支持同义词扩展

2. **缓存优化**
   - 引入多级缓存（本地 + Redis）
   - 实现缓存预热
   - 使用缓存击穿防护

3. **数据库优化**
   - 读写分离
   - 分库分表
   - 引入连接池优化

4. **性能优化**
   - 引入消息队列（Kafka/RabbitMQ）
   - 异步处理非核心业务
   - CDN 加速静态资源

### 12.2 扩展性考虑

1. **水平扩展**
   - 无状态设计，支持多实例部署
   - 使用负载均衡分发请求
   - Redis 分布式锁协调

2. **微服务拆分**
   - 用户服务独立部署
   - 内容服务独立部署
   - 智能体服务独立部署
   - 支付服务独立部署

3. **多活部署**
   - 跨区域部署
   - 数据同步机制
   - 就近访问

---

**文档结束**
