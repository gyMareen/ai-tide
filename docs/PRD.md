# AI-Tide 产品需求文档 (PRD)

## 1. 文档信息

| 项目 | 内容 |
|------|------|
| 文档版本 | v1.0.0 |
| 创建日期 | 2026-03-23 |
| 文档状态 | 草稿 |
| 作者 | 产品团队 |
| 审核人 | 待定 |
| 项目名称 | AI-Tide (AI 前沿技术展示平台) |
| 文档密级 | 内部 |

---

## 2. 项目概述

### 2.1 项目背景

随着人工智能技术的爆炸式发展，AI 工具、框架和模型层出不穷，每天都有新的突破和创新。开发者、研究者和企业面临着信息过载的挑战：

- AI 工具数量呈指数级增长，难以追踪最新动态
- 技术更新迭代极快，获取准确、及时的信息成本高
- 缺乏一个集中的、高质量的平台来聚合和展示 AI 前沿技术
- 用户需要了解不同 AI 技术的特点、适用场景和实际应用效果

AI-Tide 应运而生，旨在解决这些痛点，为用户提供一个专业、及时、全面的 AI 技术信息聚合平台。

### 2.2 项目目标

#### 短期目标（MVP 阶段）
- 实现基础的内容展示和浏览功能
- 支持用户注册、登录和基础互动（收藏、评论）
- 建立内容分类和搜索机制
- 实现内容管理和审核流程

#### 中期目标
- 建立数据爬取和自动聚合能力
- 实现个性化推荐算法
- 增加社区互动功能（讨论区、问答）
- 开发移动端适配

#### 长期目标
- 成为 AI 领域最权威的信息聚合平台
- 建立开发者生态（API 开放、插件系统）
- 实现商业化变现（付费内容、企业服务）

### 2.3 项目范围

#### 包含的功能

**核心功能**
- 内容浏览与展示（AI 模型、工具、技术文章）
- 搜索与筛选（全文搜索、标签筛选、分类导航）
- 用户系统（注册、登录、个人中心）
- 互动功能（收藏、评论、点赞、评分）
- 内容管理（发布、编辑、审核、删除）
- 系统管理（用户管理、内容管理、配置管理）

**辅助功能**
- 数据统计与分析
- 内容推荐（基础版本）
- 通知系统
- 帮助文档与 FAQ

#### 不包含的功能

**暂不实现**（可作为后续迭代方向）
- 实时数据爬取（MVP 阶段手动维护）
- 社区论坛（聚焦内容展示）
- 视频内容（仅支持图文）
- 商业化功能
- 移动端独立应用

### 2.4 非功能需求

#### 性能需求
| 指标 | 目标值 |
|------|--------|
| 首页加载时间 | < 2 秒 |
| 详情页加载时间 | < 1.5 秒 |
| 搜索响应时间 | < 500 毫秒 |
| 并发用户数 | 支持 1000+ |
| 数据库查询响应 | < 100 毫秒（带索引的查询） |

#### 可用性需求
- 页面兼容主流浏览器（Chrome、Firefox、Safari、Edge 最新两个版本）
- 支持响应式设计，适配桌面端（1920x1080 及以上）
- 关键操作成功率 > 99.9%
- 系统可用性 > 99.5%

#### 安全需求
- 所有用户密码使用 bcrypt 加密存储
- 敏感操作需要二次验证
- 实施 CSRF 防护
- API 接口实现速率限制
- 用户输入严格过滤和验证，防止 XSS、SQL 注入
- 实施 HTTPS 传输加密

#### 可维护性需求
- 代码遵循统一的编码规范
- 关键模块单元测试覆盖率 > 80%
- 提供完整的 API 文档
- 实现日志记录和监控
- 支持 Docker 容器化部署

#### 可扩展性需求
- 采用微服务架构思路，便于模块拆分
- 实现缓存机制，减轻数据库压力
- 使用搜索引擎支持海量数据检索
- 数据库设计支持水平扩展

---

## 3. 用户角色与权限

### 3.1 角色定义

| 角色编码 | 角色名称 | 角色描述 | 默认权限 |
|----------|----------|----------|----------|
| GUEST | 普通访客 | 未登录的用户，仅可浏览 | 浏览、搜索 |
| USER | 注册用户 | 已注册的普通用户 | 浏览、搜索、收藏、评论、点赞、评分 |
| EDITOR | 内容编辑 | 负责内容发布和编辑的工作人员 | 用户权限 + 发布、编辑、删除内容 |
| ADMIN | 管理员 | 系统管理员 | 所有权限 |

### 3.2 权限矩阵

| 功能模块 | 具体操作 | 普通访客 | 注册用户 | 内容编辑 | 管理员 |
|----------|----------|----------|----------|----------|--------|
| **内容浏览** | 查看首页 | ✅ | ✅ | ✅ | ✅ |
|  | 查看详情页 | ✅ | ✅ | ✅ | ✅ |
|  | 查看分类内容 | ✅ | ✅ | ✅ | ✅ |
| **搜索** | 基础搜索 | ✅ | ✅ | ✅ | ✅ |
|  | 高级筛选 | ✅ | ✅ | ✅ | ✅ |
|  | 保存搜索历史 | ❌ | ✅ | ✅ | ✅ |
| **用户系统** | 注册账号 | ✅ | - | - | | - |
|  | 登录 | ✅ | - | - | | - |
|  | 修改个人信息 | ❌ | ✅（仅自己） | ✅（仅自己） | ✅ |
|  | 修改密码 | ❌ | ✅（仅自己） | ✅（仅自己） | ✅ |
|  | 查看收藏列表 | ❌ | ✅（仅自己） | ✅（仅自己） | ✅ |
|  | 查看浏览历史 | ❌ | ✅（仅自己） | | ✅（仅自己） | ✅ |
| **互动功能** | 收藏内容 | ❌ | ✅ | ✅ | ✅ |
|  | 取消收藏 | ❌ | ✅（仅自己） | ✅（仅自己） | ✅ |
|  | 发表评论 | ❌ | ✅ | ✅ | ✅ |
|  | 删除评论 | ❌ | ✅（仅自己） | ✅（仅自己） | ✅ |
|  | 点赞内容 | ❌ | ✅ | ✅ | ✅ |
|  | 取消点赞 | ❌ | ✅（仅自己） | | ✅（仅自己） | ✅ |
|  | 内容评分 | ❌ | ✅（仅一次） | ✅（仅一次） | ✅ |
| **内容管理** | 发布内容 | ❌ | ❌ | ✅ | ✅ |
|  | 编辑内容 | ❌ | ❌ | ✅（仅自己） | ✅ |
|  | 删除内容 | ❌ | ❌ | ✅（仅自己） | ✅ |
|  | 审核内容 | ❌ | ❌ | ❌ | ✅ |
|  | 管理分类 | ❌ | ❌ | ❌ | ✅ |
|  | 管理标签 | ❌ | ❌ | ❌ | ✅ |
| **系统管理** | 用户管理 | ❌ | ❌ | ❌ | ✅ |
|  | 禁用/封号 | ❌ | ❌ | ❌ | ✅ |
|  | 修改用户角色 | ❌ | ❌ | ❌ | ✅ |
|  | 系统配置 | ❌ | ❌ | ❌ | ✅ |
|  | 查看统计数据 | ❌ | ❌ | ✅（部分） | ✅ |
|  | 系统日志 | ❌ | ❌ | ❌ | ✅ |

---

### 3.3 权限设计说明

#### 角色继承关系
- 用户权限体系采用角色继承模式
- **ADMIN** 继承 **EDITOR** 继承 **USER** 的权限
- **GUEST** 为独立的最低权限角色

#### 权限实现方式
- 基于 Spring Security 的 RBAC（基于角色的访问控制）
- 使用 `@PreAuthorize` 注解进行方法级权限控制
- 前端路由守卫实现页面级权限控制
- 按钮级权限控制通过指令实现

#### 特殊权限说明
- **内容删除**：只有内容创建者或管理员可以删除
- **评论管理**：用户只能删除自己的评论，管理员可删除任意评论
- **评分限制**：每个用户对同一内容只能评分一次
- **角色变更**：只有管理员可以修改用户角色

---

## 4. 功能需求

### 4.1 用户模块

#### 4.1.1 用户注册

**功能描述**
新用户通过注册流程创建账号，成为平台的注册用户。

**功能流程**
1. 用户访问注册页面
2. 填写注册信息：
   - 用户名（唯一标识，3-20字符，字母数字下划线）
   - 邮箱（唯一标识，需验证格式）
   - 密码（8-32字符，必须包含大小写字母和数字）
   - 确认密码
   - 验证码（图形验证码）
   - 同意用户协议（必选）
3. 前端验证数据格式
4. 提交注册请求
5. 后端验证：
   - 用户名和邮箱唯一性
   - 密码强度
   - 验证码正确性
6. 保存用户信息（密码使用 bcrypt 加密）
7. 发送邮箱验证邮件
8. 跳转至登录页面，提示验证邮箱

**数据字段**
```typescript
interface RegisterDTO {
  username: string;      // 用户名，唯一
  email: string;         // 邮箱，唯一
  password: string;      // 密码（传输时加密）
  confirmPwd: string;    // 确认密码
  captcha: string;       // 图形验证码
  agreeTerms: boolean;   // 同意协议
}
```

**验收标准**
- ✅ 用户名必须唯一，重复时提示友好错误信息
- ✅ 邮箱格式正确且唯一
- ✅ 密码必须符合强度要求
- ✅ 验证码校验正确
- ✅ 密码在数据库中使用 bcrypt 加密存储
- ✅ 注册成功后发送验证邮件
- ✅ 新注册用户默认角色为 USER
- ✅ 注册表单有完善的前端验证
- ✅ 防止重复提交（Token 机制）

---

#### 4.1.2 用户登录

**功能描述**
已注册用户通过登录流程访问平台，获得个人身份标识。

**功能流程**
1. 用户访问登录页面
2. 输入登录凭证：
   - 用户名/邮箱（支持两种方式登录）
   - 密码
   - 记住我（可选，保存 7 天）
3. 前端验证非空
4. 提交登录请求
5. 后端验证：
   - 用户存在性
   - 账号状态（未禁用）
   - 密码正确性
6. 生成 JWT Token
7. 存储 Token（LocalStorage 或 Cookie）
8. 更新最后登录时间
9. 跳转至首页，携带用户信息

**数据字段**
```typescript
interface LoginDTO {
  account: string;       // 用户名或邮箱
  password: string;      // 密码
  rememberMe: boolean;   // 记住我
}
```

**验收标准**
- ✅ 支持用户名或邮箱登录
- ✅ 密码错误超过 5 次锁定账号 30 分钟
- ✅ 登录成功生成有效 JWT Token
- ✅ Token 包含用户 ID、角色、过期时间
- ✅ 记住我功能正确实现
- ✅ 更新用户最后登录时间和 IP
- ✅ 被禁用用户无法登录
- ✅ 登录失败有清晰的错误提示
- ✅ 实现「忘记密码」功能

---

#### 4.1.3 找回密码

**功能描述**
用户忘记密码时，通过邮箱验证重置密码。

**功能流程**
1. 用户点击「忘记密码」
2. 输入注册邮箱
3. 系统验证邮箱是否存在
4. 发送包含重置 Token 的邮件
5. 用户点击邮件中的链接
6. 验证 Token 有效性和时效性
7. 输入新密码和确认密码
8. 验证密码强度
9. 更新密码
10. 失效重置 Token
11. 提示重置成功，跳转登录页

**验收标准**
- ✅ 邮箱不存在时也提示「邮件已发送」（防止邮箱枚举攻击）
- ✅ 重置 Token 有效期为 30 分钟
- ✅ Token 使用后立即失效
- ✅ 新密码必须符合强度要求
- ✅ 重置成功后需重新登录
- ✅ 邮件发送失败有友好提示

---

#### 4.1.4 个人中心

**功能描述**
用户查看和编辑个人信息，管理个人数据。

**功能模块**

**基本信息**
- 用户名（不可修改）
- 头像（上传自定义头像或使用默认）
- 昵称（可修改）
- 邮箱（不可修改）
- 个人简介（可修改）
- 注册时间
- 最后登录时间

**修改密码**
- 输入当前密码
- 输入新密码
- 确认新密码
- 验证当前密码正确性
- 验证新密码强度
- 更新密码

**数据统计**
- 收藏数量
- 发表评论数量
- 点赞数量
- 浏览历史记录

**账户设置**
- 邮件通知开关
- 推荐通知开关
- 主题设置（浅色/深色）
- 语言设置（中文/英文）

**验收标准**
- ✅ 用户名和邮箱不可修改
- ✅ 头像上传支持 JPG/PNG 格式，最大 2MB
- ✅ 修改密码需验证当前密码
- ✅ 修改成功后重新登录
- ✅ 个人信息保存后立即生效
- ✅ 主题设置实时切换
- ✅ 浏览历史按时间倒序展示
- ✅ 分页展示历史记录

---

### 4.2 内容模块

#### 4.2.1 首页/探索页

**功能描述**
展示平台最新、最热门的 AI 技术内容，引导用户探索。

**页面布局**

**顶部导航**
- Logo
- 导航菜单：首页、分类、关于
- 搜索框（全局搜索）
- 用户信息/登录注册按钮

**轮播推荐区**
- 展示 5 个精选内容
- 自动轮播（5 秒间隔）
- 支持手动切换
- 显示标题、简介、标签

**热门内容区**
- 标题：热门推荐
- 展示 12 个热度最高的内容
- 网格布局（4 列 x 3 行）
- 卡片信息：缩略图、标题、简介、评分、点赞数

**最新内容区**
- 标题：最新发布
- 展示 12 个最新发布的内容
- 瀑布流或网格布局
- 卡片信息：缩略图、标题、分类、发布时间

**分类快捷入口**
- 展示主要分类（AI 模型、AI 产品、技术文章）
- 每个分类显示图标、名称、内容数量

**数据加载**
- 首次加载完整页面
- 实现懒加载优化
- 使用 Redis 缓存首页数据（缓存 5 分钟）

**验收标准**
- ✅ 首页加载时间 < 2 秒
- ✅ 轮播图支持自动和手动切换
- ✅ 热门内容按综合热度排序
- ✅ 最新内容按发布时间倒序
- ✅ 点击卡片跳转至详情页
- ✅ 移动端适配良好
- ✅ 空数据状态显示友好提示

---

#### 4.2.2 详情页

**功能描述**
展示单个 AI 技术/工具/文章的完整信息。

**页面布局**

**内容信息区**
- 封面图（大图展示）
- 标题
- 元信息：分类、标签、发布时间、更新时间、作者
- 评分显示（星级）
- 简短描述
- 详细内容（富文本展示，支持 Markdown）
- 官方链接（跳转至官方网站）
- 相关链接（GitHub、文档、教程等）

**统计数据**
- 浏览次数
- 点赞数
- 收藏数
- 评论数

**互动按钮区**
- 点赞按钮（带数量）
- 收藏按钮（带状态）
- 分享按钮（生成分享链接、复制）

**评论区**
- 评论列表（最新评论优先）
- 发表评论输入框
- 评论分页加载
- 每条评论显示：用户头像、用户名、内容、时间、点赞、删除（自己的）

**相关推荐**
- 标题：相关推荐
- 展示 6 个相似内容
- 基于标签相似度推荐

**数据模型**
```typescript
interface Content {
  id: string;
  title: string;
  description: string;
  content: string;              // Markdown 格式
  coverImage: string;
  category: Category;
  tags: Tag[];
  type: 'MODEL' | 'PRODUCT' | 'ARTICLE';
  officialUrl: string;
  relatedLinks: RelatedLink[];
  author: User;
  publishTime: Date;
  updateTime: Date;
  viewCount: number;
  likeCount: number;
  favoriteCount: number;
  commentCount: number;
  averageRating: number;
  status: 'DRAFT' | 'PUBLISHED' | 'ARCHIVED';
}
```

**验收标准**
- ✅ 详情页加载时间 < 1.5 秒
- ✅ 浏览后自动增加浏览次数
- ✅ 富文本正确渲染 Markdown
- ✅ 官方链接新窗口打开
- ✅ 点赞、收藏状态实时更新
- ✅ 评论发表后立即显示在列表
- ✅ 相关推荐内容不重复显示
- ✅ 删除内容后跳转首页

---

#### 4.2.3 搜索页

**功能描述**
提供全文搜索和高级筛选功能，帮助用户快速找到目标内容。

**搜索输入**
- 搜索框（支持关键词搜索）
- 搜索历史（本地存储，最多 10 条）
- 搜索建议（输入时显示热门关键词）
- 回车或点击搜索按钮执行搜索

**高级筛选**
- 分类筛选：AI 模型、AI 产品、技术文章（多选）
- 标签筛选：从已有标签中选择（多选）
- 时间筛选：全部、近一周、近一月、近三月、近一年
- 排序方式：相关度、最新发布、最多浏览、最多点赞、评分最高

**搜索结果**
- 结果数量统计
- 列表展示（卡片或列表视图切换）
- 每个结果显示：标题、简介、分类、标签、评分、时间
- 分页加载（每页 12 条）
- 无结果时显示友好提示

**搜索高亮**
- 关键词在标题和描述中高亮显示

**性能优化**
- 使用 MySQL 全文索引实现搜索
- 搜索关键词缓存
- 防抖处理（输入 300ms 后触发建议）

**验收标准**
- ✅ 搜索响应时间 < 500 毫秒
- ✅ 支持模糊搜索和精确搜索
- ✅ 多个筛选条件可以组合使用
- ✅ 搜索历史正确保存和清除
- ✅ 搜索建议按热门度排序
- ✅ 分页功能正确
- ✅ 空搜索提示友好
- ✅ 高亮显示搜索关键词

---

#### 4.2.4 分类页

**功能描述**
按分类浏览内容，提供分类导航和筛选。

**分类列表**
- 左侧：分类导航树
  - AI 模型
    - 语言模型
    - 图像生成模型
    - 音频模型
    - 视频模型
  - AI 产品
    - 文本助手
    - 图像工具
    - 编程助手
    - 设计工具
  - 技术文章
    - 研究论文
    - 技术教程
    - 行业新闻
    - 技术分析

- 右侧：分类内容列表
  - 顶部：当前分类名称、描述、内容总数
  - 筛选：标签筛选、时间筛选、排序方式
  - 内容列表（同搜索结果展示）
  - 分页加载

**面包屑导航**
- 首页 > 分类 > 子分类

**验收标准**
- ✅ 点击分类正确筛选内容
- ✅ 支持多级分类展开/折叠
- ✅ 当前分类高亮显示
- ✅ 筛选条件与分类组合正确
- ✅ 面包屑导航正确
- ✅ 空分类显示引导提示

---

### 4.3 互动模块

#### 4.3.1 收藏功能

**功能描述**
用户可以将感兴趣的内容收藏到个人收藏夹，方便后续查看。

**功能流程**
1. 用户在详情页点击「收藏」按钮
2. 检查登录状态（未登录跳转登录）
3. 检查是否已收藏
4. 添加收藏记录
5. 更新内容收藏数
6. 按钮状态变为「已收藏」
7. 显示成功提示

**取消收藏**
1. 点击「已收藏」按钮
2. 删除收藏记录
3. 更新内容收藏数
4. 按钮状态变为「收藏」
5. 显示取消提示

**收藏列表**
- 在个人中心查看收藏列表
- 展示收藏时间、内容信息
- 支持取消收藏
- 支持搜索收藏内容
- 分页展示

**数据模型**
```typescript
interface Favorite {
  id: string;
  userId: string;
  contentId: string;
  favoriteTime: Date;
}
```

**验收标准**
- ✅ 收藏按钮状态实时切换
- ✅ 同一内容不能重复收藏
- ✅ 收藏数量准确统计
- ✅ 收藏列表按时间倒序
- ✅ 取消收藏后立即从列表移除
- ✅ 删除内容后自动清除收藏记录

---

#### 4.3.2 评论功能

**功能描述**
用户可以对内容发表评论，参与讨论。

**发表评论**
1. 用户在详情页评论区输入评论内容
2. 检查登录状态
3. 验证评论内容（1-500 字符）
4. 过滤敏感词
5. 保存评论
6. 更新内容评论数
7. 评论立即显示在列表顶部
8. 显示成功提示

**评论列表**
- 按时间倒序展示
- 每条评论显示：
  - 用户头像、用户名
  - 评论内容（支持换行）
  - 发表时间（相对时间）
  - 点赞数、点赞按钮
  - 删除按钮（仅自己的评论）
- 分页加载（每页 10 条）

**删除评论**
1. 用户点击自己评论的删除按钮
2. 弹出确认对话框
3. 确认后删除评论
4. 更新内容评论数
5. 评论从列表移除

**数据模型**
```typescript
interface Comment {
  id: string;
  userId: string;
  contentId: string;
  content: string;
  publishTime: Date;
  likeCount: number;
}
```

**验收标准**
- ✅ 评论内容长度限制正确
- ✅ 敏感词过滤生效
- ✅ 评论发表后立即显示
- ✅ 只能删除自己的评论
- ✅ 删除前二次确认
- ✅ 删除内容后评论级联删除
- ✅ 分页功能正确

---

#### 4.3.3 点赞功能

**功能描述**
用户可以对内容表达喜欢，提升内容热度。

**功能流程**
1. 用户在详情页或列表页点击「点赞」按钮
2. 检查登录状态
3. 检查是否已点赞
4. 添加点赞记录
5. 更新内容点赞数
6. 按钮状态变为「已点赞」
7. 显示点赞动画效果

**取消点赞**
1. 点击「已点赞」按钮
2. 删除点赞记录
3. 更新内容点赞数
4. 按钮状态变为「点赞」

**数据模型**
```typescript
interface Like {
  id: string;
  userId: string;
  contentId: string;
  likeTime: Date;
}
```

**验收标准**
- ✅ 点赞按钮状态实时切换
- ✅ 同一内容不能重复点赞
- ✅ 点赞数量准确统计
- ✅ 取消点赞后计数减少
- ✅ 删除内容后自动清除点赞记录
- ✅ 点赞动画流畅

---

#### 4.3.4 评分功能

**功能描述**
用户可以对内容进行 1-5 星评分，帮助其他用户判断内容质量。

**功能流程**
1. 用户在详情页看到评分组件
2. 检查登录状态
3. 检查是否已评分
4. 选择 1-5 星
5. 保存评分
6. 更新内容平均评分
7. 显示评分成功提示
8. 禁用评分组件（每人只能评一次）

**评分展示**
- 显示平均评分（保留 1 位小数）
- 显示评分人数
- 显示评分分布（5 星到 1 星的条形图）

**数据模型**
```typescript
interface Rating {
  id: string;
  userId: string;
  contentId: string;
  score: number;        // 1-5
  ratingTime: Date;
}
```

**验收标准**
- ✅ 每个用户对同一内容只能评分一次
- ✅ 评分后立即更新平均分
- ✅ 评分必须为 1-5 星
- ✅ 评分分布统计准确
- ✅ 已评分状态正确显示
- ✅ 删除内容后评分级联删除

---

### 4.4 内容管理模块

#### 4.4.1 内容发布

**功能描述**
内容编辑可以创建和发布新的 AI 技术内容。

**功能流程**
1. 内容编辑访问内容管理页面
2. 点击「新建内容」
3. 填写内容信息：
   - 标题（必填，1-100 字符）
   - 内容类型（必填：AI 模型/产品/文章）
   - 分类（必填，从分类树选择）
   - 标签（必填，从已有标签选择或新建）
   - 封面图（选填，上传或使用默认）
   - 简短描述（必填，1-200 字符）
   - 详细内容（必填，Markdown 编辑器）
   - 官方链接（选填）
   - 相关链接（选填，可添加多个）
4. 保存为草稿或直接发布
5. 发布后状态为「已发布」

**Markdown 编辑器**
- 支持 Markdown 语法高亮
- 支持实时预览
- 支持上传图片
- 支持代码块语法高亮
- 支持常用工具栏按钮

**数据验证**
- 标题和内容必填
- 分类必须有效
- 标签至少一个
- URL 格式验证

**验收标准**
- ✅ 所有必填字段有验证
- ✅ 草稿可以多次编辑
- ✅ 发布后不可再改为草稿
- ✅ 图片上传支持 JPG/PNG，最大 5MB
- ✅ Markdown 正确渲染
- ✅ 标签数量限制（最多 10 个）
- ✅ 新建标签时验证唯一性

---

#### 4.4.2 内容编辑

**功能描述**
内容编辑可以修改已发布的内容。

**功能流程**
1. 在内容管理页面查看内容列表
2. 找到要编辑的内容
3. 点击「编辑」按钮
4. 加载内容信息
5. 修改内容信息
6. 保存修改
7. 更新内容更新时间

**权限控制**
- 只能编辑自己发布的内容
- 管理员可以编辑所有内容

**版本记录**
- 每次编辑保存历史版本
- 可查看版本对比
- 可恢复到历史版本

**验收标准**
- ✅ 编辑功能权限控制正确
- ✅ 修改后立即生效
- ✅ 更新时间自动更新
- ✅ 历史版本正确保存
- ✅ 版本对比功能正确

---

#### 4.4.3 内容删除

**功能描述**
内容编辑和管理员可以删除内容。

**功能流程**
1. 在内容管理页面找到要删除的内容
2. 点击「删除」按钮
3. 弹出确认对话框（提示级联删除）
4. 确认后删除内容
5. 级联删除相关数据：
   - 收藏记录
   - 评论
   - 点赞
   - 评分
6. 从列表移除

**软删除**
- 使用 `status` 字段标记为 `ARCHIVED`
- 数据不完全物理删除
- 管理员可以恢复已删除内容

**验收标准**
- ✅ 删除前二次确认
- ✅ 级联删除正确执行
- ✅ 删除后不可再访问
- ✅ 管理员可以恢复删除的内容

---

#### 4.4.4 内容审核（可选，MVP 可跳过）

**功能描述**
管理员可以审核内容发布请求。

**审核流程**
1. 内容编辑提交内容后状态为「待审核」
2. 管理员在审核列表查看待审核内容
3. 管理员审核内容：
   - 通过：状态改为「已发布」，通知内容编辑
   - 拒绝：状态改为「已拒绝」，填写拒绝原因，通知内容编辑
4. 内容编辑可以根据拒绝原因修改后重新提交

**验收标准**
- ✅ 待审核内容不在公开列表显示
- ✅ 拒绝原因必填
- ✅ 审核通知正确发送

---

### 4.5 管理模块

#### 4.5.1 用户管理

**功能描述**
管理员可以查看和管理平台用户。

**用户列表**
- 展示所有用户
- 每个用户显示：
  - 头像、用户名、邮箱
  - 角色
  - 注册时间
  - 最后登录时间
  - 账号状态（正常/禁用）
  - 操作：编辑角色、禁用/启用、删除

**搜索筛选**
- 按用户名搜索
- 按角色筛选
- 按账号状态筛选
- 按注册时间筛选

**编辑用户角色**
- 修改用户角色（USER/EDITOR/ADMIN）
- 不能修改为比自己更高的权限
- 修改后记录操作日志

**禁用/启用用户**
- 禁用用户后无法登录
- 禁用原因必填
- 禁用后用户所有数据保留
- 可以重新启用

**删除用户**
- 删除前二次确认
- 级联删除用户数据：
  - 收藏记录
  - 评论
  - 点赞
  - 评分
  - 发布的内容
- 使用软删除，便于恢复

**验收标准**
- ✅ 用户列表分页正确
- ✅ 搜索筛选功能正确
- ✅ 禁用用户后无法登录
- ✅ 角色修改权限控制正确
- ✅ 删除用户级联删除正确
- ✅ 操作日志正确记录

---

#### 4.5.2 内容管理

**功能描述**
管理员可以查看和管理所有内容。

**内容列表**
- 展示所有内容
- 每个内容显示：
  - 封面图、标题
  - 类型、分类、标签
  - 作者
  - 发布时间
  - 浏览数、点赞数、收藏数
  - 状态（草稿/已发布/已归档）
  - 操作：编辑、删除、归档

**搜索筛选**
- 按标题搜索
- 按类型筛选
- 按分类筛选
- 按状态筛选
- 按作者筛选

**批量操作**
- 批量删除
- 批量归档
- 批量发布

**验收标准**
- ✅ 管理员可以查看所有内容
- ✅ 筛选功能正确
- ✅ 批量操作正确
- ✅ 操作权限正确

---

#### 4.5.3 分类管理

**功能描述**
管理员可以管理内容分类。

**分类树**
- 树形展示分类结构
- 支持拖拽排序
- 支持展开/折叠

**新建分类**
- 输入分类名称（必填，1-50 字符）
- 选择父分类（可选）
- 输入分类描述（选填）
- 保存后添加到分类树

**编辑分类**
- 修改分类名称
- 修改分类描述
- 不能修改父分类（防止死循环）

**删除分类**
- 检查是否有子分类
- 检查是否有内容
- 有子分类或内容时提示无法删除
- 删除成功后从树移除

**验收标准**
- ✅ 分类名称唯一（同级）
- ✅ 分类层级不超过 3 级
- ✅ 有子分类时不能删除
- ✅ 有内容时不能删除
- ✅ 拖拽排序正确

---

#### 4.5.4 标签管理

**功能描述**
管理员可以管理内容标签。

**标签列表**
- 展示所有标签
- 每个标签显示：
  - 标签名称
  - 使用次数（关联的内容数）
  - 创建时间
  - 操作：编辑、删除

**新建标签**
- 输入标签名称（必填，1-20 字符）
- 保存后添加到列表

**编辑标签**
- 修改标签名称
- 名称不能与现有标签重复

**删除标签**
- 检查是否被使用
- 被使用时提示是否同时从内容中移除
- 确认后删除标签并从内容中移除关联

**验收标准**
- ✅ 标签名称唯一
- ✅ 使用次数统计准确
- ✅ 删除关联正确处理
- ✅ 标签不能为空

---

#### 4.5.5 系统配置

**功能描述**
管理员可以配置系统参数。

**配置项**

**基础配置**
- 网站名称
- 网站描述
- 网站 Logo
- 联系邮箱
- 备案号

**用户配置**
- 是否开放注册
- 密码最小长度
- 密码必须包含字母
- 密码必须包含数字
- 密码错误最大次数
- 账号锁定时长（分钟）

**内容配置**
- 每页显示数量
- 评论内容最大长度
- 每人每天评论最大数量
- 审核是否开启

**通知配置**
- 邮件服务器配置
- SMTP 端口
- SMTP 用户名
- SMTP 密码
- 是否启用邮件通知

**缓存配置**
- Redis 缓存时长（秒）
- 是否启用缓存

**验收标准**
- ✅ 配置修改后立即生效
- ✅ 敏感配置（如密码）不明文显示
- ✅ 配置有默认值
- ✅ 配置修改记录日志

---

#### 4.5.6 数据统计

**功能描述**
管理员可以查看平台数据统计。

**统计维度**

**用户统计**
- 总用户数
- 今日新增用户
- 本周新增用户
- 本月新增用户
- 活跃用户数（最近 7 天有登录）

**内容统计**
- 总内容数
- 今日新增内容
- 各类型内容数量
- 各分类内容数量

**互动统计**
- 总评论数
- 总点赞数
- 总收藏数
- 平均评分

**数据趋势**
- 用户增长趋势（近 30 天）
- 内容发布趋势（近 30 天）
- 浏览量趋势（近 30 天）

**可视化图表**
- 使用 ECharts 或类似库展示
- 支持导出图片

**验收标准**
- ✅ 统计数据准确
- ✅ 趋势图正确渲染
- ✅ 数据实时更新（缓存 5 分钟）
- ✅ 支持日期范围选择

---

#### 4.5.7 系统日志

**功能描述**
管理员可以查看系统操作日志。

**日志类型**
- 用户操作：登录、注册、修改密码
- 内容操作：发布、编辑、删除
- 管理操作：修改角色、禁用用户、系统配置
- 系统操作：错误日志、异常日志

**日志列表**
- 展示日志记录
- 每条日志显示：
  - 操作时间
  - 操作类型
  - 操作人
  - 操作内容
  - IP 地址
  - 结果（成功/失败）

**搜索筛选**
- 按操作类型筛选
- 按操作人筛选
- 按时间范围筛选
- 按结果筛选

**日志保留**
- 保留最近 90 天日志
- 超期自动清理

**验收标准**
- ✅ 日志完整记录关键操作
- ✅ 筛选功能正确
- ✅ 分页展示正确
- ✅ 日志自动清理

---

#### 4.5.8 支付与授权模块（预留）

**功能描述**
预留支付接口，支持用户付费后获取对应文章实现的 GitHub DEMO 访问权限。现阶段通过手工赋权实现。

**业务场景**
- 用户可以购买特定内容的 GitHub DEMO 访问权限
- 付款成功后，用户获得该内容的访问权限
- 用户可以在个人中心查看已购买的内容
- 内容作者可以查看购买记录和收入统计
- 现阶段支付功能由管理员手工处理，然后为用户赋权

**实现说明**
- **当前阶段**：不实现完整支付流程，通过管理员手工赋权
- **未来扩展**：预留支付接口，支持对接支付宝、微信支付等第三方支付

---

#### 4.5.8.1 产品与定价管理

**功能描述**
管理员可以设置可购买的产品和定价。

**产品列表**
- 展示所有可购买的产品（内容的 GitHub DEMO）
- 每个产品显示：
  - 关联的内容信息
  - 产品名称
  - 定价（元）
  - 描述
  - 销售数量
  - 总收入
  - 状态（上架/下架）
  - 创建时间、更新时间

**创建产品**
- 选择关联内容
- 设置产品名称
- 设置定价（必须 > 0）
- 设置产品描述
- 设置状态（上架/下架）
- 保存后产品创建成功

**编辑产品**
- 修改产品名称
- 修改定价
- 修改产品描述
- 切换状态
- 修改后立即生效

**删除产品**
- 删除前检查是否有购买记录
- 删除成功后从列表移除

**数据模型**
```typescript
interface PaidProduct {
  id: string;                  // 产品 ID (UUID)
  contentId: string;           // 关联内容 ID
  name: string;                // 产品名称
  price: number;               // 定价（元）
  description: string;         // 产品描述
  status: 'ACTIVE' | 'INACTIVE'; // 状态
  salesCount: number;         // 销售数量
  totalRevenue: number;       // 总收入
  createdAt: Date;
  updatedAt: Date;
}
```

**验收标准**
- ✅ 产品定价必须大于 0
- ✅ 每个内容只能关联一个产品
- ✅ 状态切换生效
- ✅ 销售数据统计准确

---

#### 4.5.8.2 手工赋权管理

**功能描述**
管理员可以为用户手工赋权，使其获得特定产品的访问权限（暂时代替支付功能）。

**赋权操作**
1. 管理员访问手工赋权页面
2. 输入用户名或邮箱
3. 选择要授权的产品
4. 输入授权备注（如："微信支付"）
5. 确认授权
6. 系统创建授权记录
7. 用户获得该产品的访问权限
8. 记录操作日志

**授权列表**
- 展示所有授权记录
- 每条记录显示：
  - 用户信息
  - 产品信息
  - 授权时间
  - 授权方式（手工赋权/在线支付）
  - 授权备注
  - 操作人
  - 状态

**撤销授权**
- 管理员可以撤销已授权的权限
- 撤销后用户失去该产品的访问权限
- 记录撤销操作日志

**数据模型**
```typescript
interface ProductLicense {
  id: string;                  // 授权 ID (UUID)
  userId: string;              // 用户 ID
  productId: string;           // 产品 ID
  grantType: 'MANUAL' | 'ONLINE'; // 授权类型
  grantTime: Date;             // 授权时间
  grantNote: string;           // 授权备注
  grantedBy: string;           // 授权人（用户 ID）
  status: 'ACTIVE' | 'REVOKED'; // 状态
  createdAt: Date;
  updatedAt: Date;
}
```

**验收标准**
- ✅ 同一用户对同一产品只能授权一次
- ✅ 授权记录完整保存
- ✅ 撤销授权后权限立即失效
- ✅ 操作日志正确记录

---

#### 4.5.8.3 用户购买记录

**功能描述**
用户可以在个人中心查看已购买的产品和授权记录。

**已购买产品列表**
- 展示用户所有已购买/已授权的产品
- 每个产品显示：
  - 产品名称
  - 关联内容信息
  - 授权时间
  - 授权方式
  - GitHub DEMO 链接（有权限时显示）

**访问 GitHub DEMO**
- 用户点击 GitHub DEMO 链接
- 系统验证用户权限
- 有权限：跳转至 GitHub
- 无权限：提示需要购买或联系管理员

**数据模型**
```typescript
// 复用 ProductLicense 实体
interface UserProductInfo {
  productId: string;
  productName: string;
  contentId: string;
  contentTitle: string;
  grantTime: Date;
  grantType: 'MANUAL' | 'ONLINE';
  githubUrl: string;           // GitHub DEMO 链接
}
```

**验收标准**
- ✅ 已购买产品正确显示
- ✅ 权限验证准确
- ✅ GitHub 链接正确跳转
- ✅ 无权限时提示清晰

---

#### 4.5.8.4 支付接口预留（未来实现）

**功能描述**
预留支付接口，未来支持对接第三方支付平台。

**支付流程（未来）**
1. 用户在产品详情页点击"购买"按钮
2. 系统生成支付订单
3. 调用第三方支付平台 API（支付宝/微信）
4. 用户完成支付
5. 支付平台回调通知
6. 系统验证支付结果
7. 支付成功：为用户赋权
8. 支付失败：提示用户

**订单管理（未来）**
- 创建支付订单
- 查询订单状态
- 处理支付回调
- 订单超时处理
- 订单退款处理

**数据模型（预留）**
```typescript
interface PaymentOrder {
  id: string;                  // 订单号
  userId: string;              // 用户 ID
  productId: string;           // 产品 ID
  amount: number;              // 支付金额
  status: 'PENDING' | 'PAID' | 'FAILED' | 'REFUNDED';
  paymentMethod: string;       // 支付方式（支付宝/微信）
  transactionId: string;      // 第三方交易号
  createdAt: Date;
  paidAt: Date;
  updatedAt: Date;
}
```

**验收标准（未来）**
- ✅ 订单生成正确
- ✅ 支付回调处理准确
- ✅ 订单状态正确更新
- ✅ 重复支付防护

---

#### 4.5.8.5 权限矩阵（扩展）

| 功能模块 | 具体操作 | 普通访客 | 注册用户 | 内容编辑 | 管理员 |
|----------|----------|----------|----------|----------|--------|
| **支付授权** | 查看产品列表 | ✅ | ✅ | ✅ | ✅ |
|  | 查看产品详情 | ✅ | ✅ | ✅ | ✅ |
|  | 查看已购买产品 | ❌ | ✅（仅自己） | ✅（仅自己） | ✅ |
|  | 访问 GitHub DEMO | ❌ | ✅（有权限） | ✅（有权限） | ✅ |
|  | 创建产品 | ❌ | ❌ | ❌ | ✅ |
|  | 编辑产品 | ❌ | ❌ | ❌ | ✅ |
|  | 删除产品 | ❌ | ❌ | ❌ | ✅ |
|  | 手工赋权 | ❌ | ❌ | ❌ | ✅ |
|  | 撤销授权 | ❌ | ❌ | ❌ | ✅ |
|  | 查看授权记录 | ❌ | ❌ | ❌ | ✅ |
|  | 查看销售统计 | ❌ | ❌ | ✅（自己产品） | ✅ |

---

#### 4.5.8.6 验收标准总结

- ✅ 产品管理功能完整
- ✅ 手工赋权功能正常
- ✅ 权限验证准确
- ✅ 用户购买记录正确
- ✅ GitHub DEMO 访问控制正确
- ✅ 权限矩阵正确实现
- ✅ 销售数据统计准确
- ✅ 操作日志完整记录

---

### 4.6 小龙虾智能体协作展示模块

#### 4.6.1 功能概述

**功能描述**
在前台展示调优后的"小龙虾"多智能体系统，展示智能体团队、成员以及他们通过定时器调度进行的协作讨论记忆和最终结论。

**业务场景**
- 展示多个智能体团队（如 AI 部、产品部、技术部等）
- 每个团队包含多个智能体成员，每个成员有特定的人设和角色
- 智能体通过定时触发器进行协作讨论
- 记录完整的讨论过程（智能体之间的交互消息）
- 展示讨论得出的结论和总结
- 支持用户查看历史讨论会话

**用户权限**
- **普通访客**：可查看公开的讨论记录和结论
- **注册用户**：可查看公开讨论，可收藏讨论会话
- **内容编辑**：可管理智能体团队和成员信息
- **管理员**：完整权限，包括配置定时器、管理所有讨论

---

#### 4.6.2 智能体团队管理

**功能描述**
内容编辑和管理员可以管理智能体团队。

**团队列表**
- 展示所有智能体团队
- 每个团队显示：
  - 团队标识（display_name，如 "🤖 AI部"）
  - 团队名称
  - 团队状态
  - 智能体数量
  - 讨论会话数量
  - 创建时间、更新时间

**新建团队**
- 输入团队信息：
  - 团队标识（必填，如 "team-ai"）
  - 团队名称（必填，如 "ai"）
  - 展示名称（必填，如 "🤖 AI部"）
  - 管理员提示词（选填）
  - 状态：启用/禁用
- 保存后团队创建成功

**编辑团队**
- 修改团队展示名称
- 修改管理员提示词
- 切换团队状态
- 修改后立即生效

**删除团队**
- 删除前检查是否有关联的智能体或会话
- 有关联时提示先清理关联数据
- 删除成功后从列表移除

**数据模型**
```typescript
interface AgentTeam {
  id: string;                  // 团队唯一标识
  name: string;                // 团队内部名称
  display_name: string;        // 展示名称（带 emoji）
  bot_app_id: string | null;    // 关联的机器人应用 ID
  manager_prompt: string | null;// 管理员提示词
  status: 'active' | 'inactive';// 状态
  created_at: Date;
  updated_at: Date;
}
```

**验收标准**
- ✅ 团队标识唯一
- ✅ 团队状态切换生效
- ✅ 有关联数据时不能删除
- ✅ 团队列表正确排序

---

#### 4.6.3 智能体成员管理

**功能描述**
管理智能体团队成员，每个智能体有独特的人设和职责。

**智能体列表**
- 支持按团队筛选
- 每个智能体显示：
  - 头像（默认或自定义）
  - 名称
  - 所属团队
  - 角色（manager/worker/assistant）
  - 人设简介
  - 当前状态
  - 记忆摘要
  - 创建时间、更新时间

**新建智能体**
- 输入智能体信息：
  - 智能体 ID（自动生成 UUID）
  - 名称（必填，如 "Echo"）
  - 所属团队（必选）
  - 角色（必选：manager/worker/assistant）
  - 人设描述（必填，详细的角色设定）
  - 头像 URL（选填）
  - 初始状态（idle/busy）
- 保存后智能体创建成功

**编辑智能体**
- 修改名称
- 修改角色
- 修改人设描述
- 修改头像
- 修改后立即生效

**删除智能体**
- 删除前检查是否有参与的会话
- 删除后从列表移除

**数据模型**
```typescript
interface AgentMember {
  id: string;                  // 智能体唯一标识 (UUID)
  name: string;                // 智能体名称
  team_id: string;             // 所属团队 ID
  role: 'manager' | 'worker' | 'assistant'; // 角色
  personality: string;          // 人设描述
  status: 'idle' | 'busy' | 'error'; // 当前状态
  memory_summary: string;      // 记忆摘要
  avatar_url: string | null;   // 头像 URL
  created_at: Date;
  updated_at: Date;
}
```

**验收标准**
- ✅ 智能体名称在团队内唯一

- ✅ 人设描述不能为空
- ✅ 头像上传支持常见图片格式
- ✅ 状态更新实时反映

---

#### 4.6.4 协作会话管理

**功能描述**
记录和管理智能体团队的协作讨论会话。

**会话列表（前台）**
- 支持按团队筛选
- 支持按时间筛选
- 每个会话显示：
  - 会话标题/主题
  - 涉及的团队
  - 参与的智能体
  - 讨论状态（进行中/已完成/已暂停）
  - 消息数量
  - 开始时间、结束时间/最后更新时间
  - 是否有结论

**会话详情（前台）**
- 左侧：参与智能体列表及状态
- 右侧：聊天式讨论界面
  - 消息按时间顺序展示
  - 显示发送者智能体信息
  - 显示消息内容
  - 支持 Markdown 渲染
- 底部：结论区
  - 展示最终结论（如有）
  - 支持复制结论
  - 支持收藏会话

**会话管理（后台）**
- 创建新会话：
  - 选择团队
  - 设置会话主题
  - 选择参与智能体
  - 设置定时器配置
- 手动启动/暂停会话
- 删除会话
- 查看详细日志

**数据模型**
```typescript
interface CollaborationSession {
  id: string;                  // 会话 ID (UUID)
  team_id: string;             // 团队 ID
  title: string;               // 会话标题
  topic: string;               // 讨论主题
  agent_ids: string[];         // 参与的智能体 ID 列表
  status: 'pending' | 'running' | 'paused' | 'completed' | 'failed';
  schedule_config: ScheduleConfig; // 定时器配置
  message_count: number;       // 消息数量
  conclusion: string | null;   // 最终结论
  started_at: Date | null;
  completed_at: Date | null;
  created_at: Date;
  updated_at: Date;
}

interface ScheduleConfig {
  enabled: boolean;            // 是否启用定时器
  cron_expression: string;      // Cron 表达式
  max_duration_minutes: number; // 最大持续时长
  max_messages: number;         // 最大消息数
}
```

**验收标准**
- ✅ 会话列表正确筛选和排序
- ✅ 消息按时间正确排序
- ✅ Markdown 正确渲染
- ✅ 结论区正确显示
- ✅ 状态转换正确
- ✅ 定时器配置验证

---

#### 4.6.5 讨论消息记录

**功能描述**
记录智能体之间协作讨论的详细消息。

**消息展示**
- 聊天式界面展示
- 每条消息显示：
  - 发送者智能体信息（头像、名称、角色）
  - 发送时间
  - 消息类型（text/system/error）
  - 消息内容
  - 关键词高亮（如果与主题相关）

**消息类型**
- **text**：智能体的正常发言
- **system**：系统提示、状态变更
- **error**：错误消息

**实时更新（可选）**
- WebSocket 连接实时接收新消息
- 新消息自动滚动到底部
- 声音提示（可选）

**数据模型**
```typescript
interface DiscussionMessage {
  id: string;                  // 消息 ID (UUID)
  session_id: string;           // 会话 ID
  agent_id: string | null;      // 发送者智能体 ID（系统消息为 null）
  message_type: 'text' | 'system' | 'error';
  content: string;              // 消息内容
  metadata: {                 // 附加元数据
    role?: string;              // 智能体角色
    emotion?: string;           // 情绪标签
    keywords?: string[];        // 关键词
  };
  created_at: Date;
}
```

**验收标准**
- ✅ 消息正确按时间排序
- ✅ 发送者信息正确关联
- ✅ Markdown 正确渲染
- ✅消息类型正确区分和样式
- ✅ 实时更新功能正常

---

#### 4.6.6 定时器调度管理

**功能描述**
配置和管理智能体协作的定时触发机制。

**定时器配置**
- 启用/禁用定时器
- 设置 Cron 表达式（如 "0 9 * * *" 表示每天 9 点）
- 设置最大持续时长（防止无限循环）
- 设置最大消息数量限制
- 设置触发条件（可选）

**定时器触发流程**
1. 定时器触发
2. 创建/继续协作会话
3. 按预定顺序或逻辑调度智能体发言
4. 记录每条消息
5. 检查终止条件
6. 生成最终结论
7. 更新会话状态为完成

**终止条件**
- 达到最大消息数量
- 达到最大持续时长
- 达成共识/结论
- 用户手动停止
- 发生错误

**验收标准**
- ✅ Cron 表达式正确解析
- ✅ 定时触发功能正常
- ✅ 终止条件正确判断
- ✅ 错误处理完善

---

#### 4.6.7 结论生成与展示

**功能描述**
生成并展示智能体协作讨论的最终结论。

**结论生成**
- 系统自动总结讨论要点
- 提取关键信息和决策
- 生成结构化结论

**结论展示**
- 在会话详情页底部展示
- 支持完整视图和摘要视图
- 支持导出为文本或 Markdown
- 支持分享功能

**数据模型**
```typescript
interface Conclusion {
  id: string;                  // 结论 ID
  session_id: string;           // 关联会话 ID
  summary: string;              // 摘要
  key_points: string[];         // 关键点
  decisions: string[];          // 决策项
  recommendations: string[];   // 建议
  confidence_score: number;     // 置信度 0-1
  generated_at: Date;
}
```

**验收标准**- ✅ 结论正确生成
- ✅ 摘要准确反映讨论内容
- ✅ 关键点提取正确
- ✅ 导出功能正常
- ✅ 分享功能正常

---

#### 4.6.8 前台页面设计

**功能描述**
在前台创建独立的智能体协作展示页面。

**页面结构**

**导航入口**
- 在顶部导航添加"AI 协作"入口
- 或在首页添加特色模块展示

**团队展示页**
```
┌─────────────────────────────────────────────────┐
│  🤖 小龙虾智能体协作                             │
├─────────────────────────────────────────────────┤
│                                                 │
│  团队列表（卡片式或列表式）                      │
│                                                 │
│  ┌────────┐  ┌────────┐  ┌────────┐           │
│  │ 🤖 AI部│  │ 🎨设计部│  │ 💼 产品部│           │
│  │ 3成员  │  │ 2成员  │  │ 4成员  │           │
│  │ 12会话 │  │ 8会话  │ 20会话 │           │
│  └────────┘  └────────┘  └────────┘           │
│                                                 │
└─────────────────────────────────────────────────┘
```

**团队详情页**
```
┌─────────────────────────────────────────────────┐
│  🤖 AI部 - 智能体团队                             │
├─────────────────────────────────────────────────┤
│                                                 │
│  团队成员区                                    │
│  ┌────┐ ┌────┐ ┌────┐                         │
│  │Echo│ │Bob │ │Alice│                         │
│  │经理│ │专家│ │分析│                         │
│  └────┘ └────┘ └────┘                         │
│                                                 │
│  讨论会话列表（时间倒序）                       │
│  - [2026-03-23] 新技术评估讨论 ✓ 已完成        │
│  - [2026-03-22] 产品方案评审 🔄 进行中         │
│  - [2026-03-20] 系统架构设计 ✓ 已完成        │
│                                                 │
└─────────────────────────────────────────────────┘
```

**会话详情页**
```
┌────────────────────┬────────────────────────────┐
│   参与智能体        │      讨论内容               │
│                    │                            │
│  ┌─────────────┐   │  Echo [经理] 09:00        :│
│  │ 💬 Echo     │   │  各位，今天讨论新技术      │
│  │ 经理       │   │  评估方案...               │
│  │ 状态: idle │   │                            │
│  └─────────────┘   │  Bob [专家] 09:01          │
│                    │  我先分析一下技术可行性... │
│  ┌─────────────┐   │                            │
│  │ 💬 Bob      │   │  Alice [分析] 09:03       │
│  │ 专家       │   │  从数据角度来看...         │
│  │ 状态: busy │   │                            │
│  └─────────────┘   │  ...                       │
│                    │                            │
│  ┌─────────────┐   ├────────────────────────────┤
│  │ 💬 Alice    │   │      📋 讨论结论           │
│  │ 分析师     │   │                            │
│  │ 状态: idle │   │  经过讨论，我们建议...      │
│  └─────────────┘   │                            │
│                    │  关键决策：                 │
│                    │  1. 采用方案 A              │
│                    │  2. 分两阶段实施            │
│                    │                            │
│                    └────────────────────────────┘
```

**验收标准**
- ✅ 页面布局美观、清晰
- ✅ 响应式设计良好
- ✅ 交互流畅
- ✅ 加载速度快

---

#### 4.6.9 权限矩阵（扩展）

| 功能模块 | 具体操作 | 普通访客 | 注册用户 | 内容编辑 | 管理员 |
|----------|----------|----------|----------|----------|--------|
| **智能体协作** | 查看团队列表 | ✅ | ✅ | ✅ | ✅ |
|  | 查看团队详情 | ✅ | ✅ | ✅ | ✅ |
|  | 查看智能体成员 | ✅ | ✅ | ✅ | ✅ |
|  | 查看会话列表 | ✅ | ✅ | ✅ | ✅ |
|  | 查看会话详情 | ✅ | ✅ | ✅ | ✅ |
|  | 查看讨论消息 | ✅ | ✅ | ✅ | ✅ |
|  | 查看结论 | ✅ | ✅ | ✅ | ✅ |
|  | 收藏会话 | ❌ | ✅ | ✅ | ✅ |
|  | 创建团队 | ❌ | ❌ | ✅ | ✅ |
|  | 编辑团队 | ❌ | ❌ | ✅ | ✅ |
|  | 删除团队 | ❌ | ❌ | ✅ | ✅ |
|  | 创建智能体 | ❌ | ❌ | ✅ | ✅ |
|  | 编辑智能体 | ❌ | ❌ | ✅ | ✅ |
|  | 删除智能体 | ❌ | ❌ | ✅ | ✅ |
|  | 创建会话 | ❌ | ❌ | ✅ | ✅ |
|  | 启动/停止会话 | ❌ | ❌ | ✅ | ✅ |
|  | 配置定时器 | ❌ | ❌ | ✅ | ✅ |
|  | 删除会话 | ❌ | ❌ | ✅ | ✅ |

---

#### 4.6.10 性能要求

| 指标 | 目标值 |
|------|--------|
| 团队列表加载 | < 500 毫秒 |
| 会话详情加载 | < 1 秒 |
| 消息列表加载 | < 500 毫秒（首批 50 条）|
| 实时消息延迟 | < 100 毫秒 |
| 结论生成 | < 3 秒 |

---

#### 4.6.11 验收标准总结

- ✅ 团队管理功能完整
- ✅ 智能体成员管理功能完整
- ✅ 协作会话记录完整
- ✅ 消息展示清晰、有序
- ✅ 定时器调度正确
- ✅ 结论生成准确
- ✅ 前台页面展示美观
- ✅ 权限控制正确
- ✅ 性能达标
- ✅ 错误处理完善

---

## 5. 数据模型

### 5.1 核心实体

#### 5.1.1 用户 (User)

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 50)
    private String nickname;

    @Column(length = 500)
    private String avatar;

    @Column(length = 500)
    private String bio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(nullable = false)
    private LocalDateTime registerTime;

    private LocalDateTime lastLoginTime;

    private String lastLoginIp;

    @Column(nullable = false)
    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
```

#### 5.1.2 内容 (Content)

```java
@Entity
@Table(name = "contents")
public class Content {
    @Id
       @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(length = 500)
    private String coverImage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentType type;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToMany
    @JoinTable(
        name = "content_tags",
        joinColumns = @JoinColumn(name = "content_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags = new ArrayList<>();

    @Column(length = 500)
    private String officialUrl;

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL)
    private List<RelatedLink> relatedLinks = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @Column(nullable = false)
    private LocalDateTime publishTime;

    private LocalDateTime updateTime;

    @Column(nullable = false)
    private Long viewCount = 0L;

    @Column(nullable = false)
    private Long likeCount = 0L;

    @Column(nullable = false)
    private Long favoriteCount = 0L;

    @Column(nullable = false)
    private Long commentCount = 0L;

    @Column(nullable = false, precision = 2, scale = 1)
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentStatus status = ContentStatus.DRAFT;

    @Column(nullable = false)
    private LocalDateTime createTime;

    private LocalDateTime deletedTime;
}
```

#### 5.1.3 分类 (Category)

```java
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> children = new ArrayList<>();

    @Column(nullable = false)
    private Integer sortOrder = 0;

    @Column(nullable = false)
    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
```

#### 5.1.4 标签 (Tag)

```java
@Entity
@Table(name = "tags")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(nullable = false)
    private Long useCount = 0L;

    @Column(nullable = false)
    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
```

#### 5.1.5 评论 (Comment)

```java
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime publishTime;

    @Column(nullable = false)
    private Long likeCount = 0L;

    @Column(nullable = false)
    private LocalDateTime createTime;
}
```

#### 5.1.6 收藏 (Favorite)

```java
@Entity
@Table(name = "favorites")
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @Column(nullable = false)
    private LocalDateTime favoriteTime;

    @Column(nullable = false)
    private LocalDateTime createTime;
}
```

#### 5.1.7 点赞 (Like)

```java
@Entity
@Table(name = "likes")
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @Column(nullable = false)
    private LocalDateTime likeTime;

    @Column(nullable = false)
    private LocalDateTime createTime;
}
```

#### 5.1.8 评分 (Rating)

```java
@Entity
@Table(name = "ratings")
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false)
    private LocalDateTime ratingTime;

    @Column(nullable = false)
    private LocalDateTime createTime;
}
```

#### 5.1.9 相关链接 (RelatedLink)

```java
@Entity
@Table(name = "related_links")
public class RelatedLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private LocalDateTime createTime;
}
```

#### 5.1.10 系统配置 (SystemConfig)

```java
@Entity
@Table(name = "system_configs")
public class SystemConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String configKey;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String configValue;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
```

#### 5.1.11 操作日志 (OperationLog)

```java
@Entity
@Table(name = "operation_logs")
public class OperationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OperationType operationType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String operationContent;

    private String ipAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OperationResult result;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @Column(nullable = false)
    private LocalDateTime operationTime;
}
```

#### 5.1.12 智能体团队 (AgentTeam)

```java
@Entity
@Table(name = "agent_teams")
public class AgentTeam {
    @Id
    @Column(nullable = false, unique = true, length = 100)
    private String id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 100)
    private String displayName;

    @Column(length = 100)
    private String botAppId;

    @Column(columnDefinition = "TEXT")
    private String managerPrompt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeamStatus status = TeamStatus.ACTIVE;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<AgentMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<CollaborationSession> sessions = new ArrayList<>();
}
```

#### 5.1.13 智能体成员 (AgentMember)

```java
@Entity
@Table(name = "agent_members")
public class AgentMember {
    @Id
    @Column(nullable = false, unique = true, length = 100)
    private String id;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private AgentTeam team;

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgentRole role;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String personality;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgentStatus status = AgentStatus.IDLE;

    @Column(columnDefinition = "TEXT")
    private String memorySummary;

    @Column(length = 500)
    private String avatarUrl;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
```

#### 5.1.14 协作会话 (CollaborationSession)

```java
@Entity
@Table(name = "collaboration_sessions")
public class CollaborationSession {
    @Id
    @Column(nullable = false, unique = true, length = 100)
    private String id;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private AgentTeam team;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String topic;

    @ManyToMany
    @JoinTable(
        name = "session_agents",
        joinColumns = @JoinColumn(name = "session_id"),
        inverseJoinColumns = @JoinColumn(name = "agent_id")
    )
    private List<AgentMember> agents = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status = SessionStatus.PENDING;

    @Embedded
    private ScheduleConfig scheduleConfig;

    @Column(nullable = false)
    private Long messageCount = 0L;

    @Column(columnDefinition = "TEXT")
    private String conclusion;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
    private List<DiscussionMessage> messages = new ArrayList<>();
}
```

#### 5.1.15 讨论消息 (DiscussionMessage)

```java
@Entity
@Table(name = "discussion_messages")
public class DiscussionMessage {
    @Id
    @Column(nullable = false, unique = true, length = 100)
    private String id;

    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private CollaborationSession session;

    @ManyToOne
    @JoinColumn(name = "agent_id")
    private AgentMember agent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType messageType = MessageType.TEXT;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Embedded
    private MessageMetadata metadata;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
```

#### 5.1.16 协作结论 (CollaborationConclusion)

```java
@Entity
@Table(name = "collaboration_conclusions")
public class CollaborationConclusion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String sessionId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String keyPoints;  // JSON array stored as text

    @Column(columnDefinition = "TEXT")
    private String decisions;   // JSON array stored as text

    @Column(columnDefinition = "TEXT")
    private String recommendations; // JSON array stored as text

    @Column(nullable = false, precision = 3, scale = 2)
    private BigDecimal confidenceScore;

    @Column(nullable = false)
    private LocalDateTime generatedAt;
}
```

#### 5.1.17 付费产品 (PaidProduct)

```java
@Entity
@Table(name = "paid_products")
public class PaidProduct {
    @Id
    @Column(nullable = false, unique = true, length = 100)
    private String id;

    @ManyToOne
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status = ProductStatus.ACTIVE;

    @Column(nullable = false)
    private Long salesCount = 0L;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalRevenue = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductLicense> licenses = new ArrayList<>();
}
```

#### 5.1.18 产品授权 (ProductLicense)

```java
@Entity
@Table(name = "product_licenses")
public class ProductLicense {
    @Id
    @Column(nullable = false, unique = true, length = 100)
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private PaidProduct product;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GrantType grantType = GrantType.MANUAL;

    @Column(nullable = false)
    private LocalDateTime grantTime;

    @Column(length = 500)
    private String grantNote;

    @ManyToOne
    @JoinColumn(name = "granted_by")
    private User grantedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LicenseStatus status = LicenseStatus.ACTIVE;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
```

#### 5.1.19 支付订单（预留）

```java
@Entity
@Table(name = "payment_orders")
public class PaymentOrder {
    @Id
    @Column(nullable = false, unique = true, length = 100)
    private String orderNo;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private PaidProduct product;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(length = 50)
    private String paymentMethod;

    @Column(length = 100)
    private String transactionId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime paidAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
```

### 5.2 枚举类型

#### 5.2.1 角色枚举 (Role)

```java
public enum Role {
    GUEST,
    USER,
    EDITOR,
    ADMIN
}
```

#### 5.2.2 内容类型枚举 (ContentType)

```java
public enum ContentType {
    MODEL,      // AI 模型
    PRODUCT,    // AI 产品
    ARTICLE     // 技术文章
}
```

#### 5.2.3 内容状态枚举 (ContentStatus)

```java
public enum ContentStatus {
    DRAFT,      // 草稿
    PUBLISHED,  // 已发布
    ARCHIVED    // 已归档
}
```

#### 5.2.4 操作类型枚举 (OperationType)

```java
public enum OperationType {
    // 用户操作
    USER_REGISTER,
    USER_LOGIN,
    USER_UPDATE,
    USER_DELETE,

    // 内容操作
    CONTENT_CREATE,
    CONTENT_UPDATE,
    CONTENT_DELETE,
    CONTENT_PUBLISH,

    // 管理操作
    ROLE_CHANGE,
    USER_DISABLE,
    USER_ENABLE,
    SYSTEM_CONFIG_UPDATE,

    // 系统操作
    SYSTEM_ERROR,
    SYSTEM_EXCEPTION
}
```

#### 5.2.5 操作结果枚举 (OperationResult)

```java
public enum OperationResult {
    SUCCESS,
    FAILURE
}
```

#### 5.2.6 团队状态枚举 (TeamStatus)

```java
public enum TeamStatus {
    ACTIVE,      // 活跃
    INACTIVE     // 非活跃
}
```

#### 5.2.7 智能体角色枚举 (AgentRole)

```java
public enum AgentRole {
    MANAGER,     // 管理者
    WORKER,      // 工作者
    ASSISTANT    // 助手
}
```

#### 5.2.8 智能体状态枚举 (AgentStatus)

```java
public enum AgentStatus {
    IDLE,        // 空闲
    BUSY,        // 忙碌
    ERROR        // 错误
}
```

#### 5.2.9 会话状态枚举 (SessionStatus)

```java
public enum SessionStatus {
    PENDING,     // 待开始
    RUNNING,     // 运行中
    PAUSED,      // 已暂停
    COMPLETED,   // 已完成
    FAILED       // 失败
}
```

#### 5.2.10 消息类型枚举 (MessageType)

```java
public enum MessageType {
    TEXT,        // 文本消息
    SYSTEM,      // 系统消息
    ERROR        // 错误消息
}
```

#### 5.2.11 产品状态枚举 (ProductStatus)

```java
public enum ProductStatus {
    ACTIVE,      // 上架
    INACTIVE     // 下架
}
```

#### 5.2.12 授权类型枚举 (GrantType)

```java
public enum GrantType {
    MANUAL,      // 手工赋权
    ONLINE       // 在线支付
}
```

#### 5.2.13 授权状态枚举 (LicenseStatus)

```java
public enum LicenseStatus {
    ACTIVE,      // 有效
    REVOKED      // 已撤销
}
```

#### 5.2.14 订单状态枚举 (OrderStatus)

```java
public enum OrderStatus {
    PENDING,     // 待支付
    PAID,        // 已支付
    FAILED,      // 支付失败
    REFUNDED     // 已退款
}
```

### 5.3 数据库索引设计

```sql
-- 用户表索引
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_register_time ON users(register_time);

-- 内容表索引
CREATE INDEX idx_contents_type ON contents(type);
CREATE INDEX idx_contents_category_id ON contents(category_id);
CREATE INDEX idx_contents_author_id ON contents(author_id);
CREATE INDEX idx_contents_publish_time ON contents(publish_time);
CREATE INDEX idx_contents_view_count ON contents(view_count);
CREATE INDEX idx_contents_like_count ON contents(like_count);
CREATE INDEX idx_contents_status ON contents(status);

-- 分类表索引
CREATE INDEX idx_categories_parent_id ON categories(parent_id);
CREATE INDEX idx_categories_sort_order ON categories(sort_order);

-- 标签表索引
CREATE INDEX idx_tags_name ON tags(name);
CREATE INDEX idx_tags_use_count ON tags(use_count);

-- 评论表索引
CREATE INDEX idx_comments_user_id ON comments(user_id);
CREATE INDEX idx_comments_content_id ON comments(content_id);
CREATE INDEX idx_comments_publish_time ON comments(publish_time);

-- 收藏表索引
CREATE INDEX idx_favorites_user_id ON favorites(user_id);
CREATE INDEX idx_favorites_content_id ON favorites(content_id);
CREATE INDEX idx_favorites_favorite_time ON favorites(favorite_time);

-- 点赞表索引
CREATE INDEX idx_likes_user_id ON likes(user_id);
CREATE INDEX idx_likes_content_id ON likes(content_id);

-- 评分表索引
CREATE INDEX idx_ratings_user_id ON ratings(user_id);
CREATE INDEX idx_ratings_content_id ON ratings(content_id);
CREATE INDEX idx_ratings_score ON ratings(score);

-- 操作日志索引
CREATE INDEX idx_operation_logs_user_id ON operation_logs(user_id);
CREATE INDEX idx_operation_logs_operation_type ON operation_logs(operation_type);
CREATE INDEX idx_operation_logs_operation_time ON operation_logs(operation_time);
CREATE INDEX idx_operation_logs_result ON operation_logs(result);

-- 智能体团队索引
CREATE INDEX idx_agent_teams_status ON agent_teams(status);
CREATE INDEX idx_agent_teams_created_at ON agent_teams(created_at);

-- 智能体成员索引
CREATE INDEX idx_agent_members_team_id ON agent_members(team_id);
CREATE INDEX idx_agent_members_role ON agent_members(role);
CREATE INDEX idx_agent_members_status ON agent_members(status);

-- 协作会话索引
CREATE INDEX idx_collaboration_sessions_team_id ON collaboration_sessions(team_id);
CREATE INDEX idx_collaboration_sessions_status ON collaboration_sessions(status);
CREATE INDEX idx_collaboration_sessions_created_at ON collaboration_sessions(created_at);
CREATE INDEX idx_collaboration_sessions_started_at ON collaboration_sessions(started_at);

-- 讨论消息索引
CREATE INDEX idx_discussion_messages_session_id ON discussion_messages(session_id);
CREATE INDEX idx_discussion_messages_agent_id ON discussion_messages(agent_id);
CREATE INDEX idx_discussion_messages_message_type ON discussion_messages(message_type);
CREATE INDEX idx_discussion_messages_created_at ON discussion_messages(created_at);

-- 协作结论索引
CREATE INDEX idx_collaboration_conclusions_session_id ON collaboration_conclusions(session_id);
CREATE INDEX idx_collaboration_conclusions_generated_at ON collaboration_conclusions(generated_at);

-- 付费产品索引
CREATE INDEX idx_paid_products_content_id ON paid_products(content_id);
CREATE INDEX idx_paid_products_status ON paid_products(status);
CREATE INDEX idx_paid_products_created_at ON paid_products(created_at);

-- 产品授权索引
CREATE INDEX idx_product_licenses_user_id ON product_licenses(user_id);
CREATE INDEX idx_product_licenses_product_id ON product_licenses(product_id);
CREATE INDEX idx_product_licenses_status ON product_licenses(status);
CREATE INDEX idx_product_licenses_grant_time ON product_licenses(grant_time);

-- 支付订单索引
CREATE INDEX idx_payment_orders_user_id ON payment_orders(user_id);
CREATE INDEX idx_payment_orders_product_id ON payment_orders(product_id);
CREATE INDEX idx_payment_orders_status ON payment_orders(status);
CREATE INDEX idx_payment_orders_created_at ON payment_orders(created_at);
```

---

## 6. 流程图

### 6.1 用户注册流程

1. 用户访问注册页面
2. 填写注册信息（用户名、邮箱、密码、确认密码、验证码）
3. 前端验证数据格式
4. 提交注册请求到后端
5. 后端验证用户名唯一性
6. 后端验证邮箱唯一性
7. 后端验证密码强度
8. 后端验证验证码正确性
9. 保存用户信息到数据库（密码 bcrypt 加密）
10. 生成邮箱验证 Token
11. 发送验证邮件
12. 返回注册成功
13. 前端跳转至登录页，提示验证邮箱

### 6.2 用户登录流程

1. 用户访问登录页面
2. 输入用户名/邮箱和密码
3. 提交登录请求到后端
4. 后端查询用户
5. 验证用户存在性
6. 验证账号状态（是否禁用）
7. 验证密码正确性
8. 验证失败计数（超过 5 次锁定）
9. 生成 JWT Token（包含用户 ID、角色、过期时间）
10. 更新用户最后登录时间和 IP
11. 返回 Token 和用户信息
12. 前端存储 Token
13. 前端保存用户信息到 Vuex/Pinia
14. 跳转至首页

### 6.3 内容发布流程

1. 内容编辑访问内容管理页面
2. 点击「新建内容」
3. 填写内容信息（标题、类型、分类、标签、封面图、描述、详细内容、官方链接）
4. 选择「保存草稿」或「发布」
5. 前端验证必填字段
6. 提交内容到后端
7. 后端验证分类有效性
8. 后端验证标签有效性
9. 保存内容到数据库
10. 如果选择「发布」：
    - 状态设为「已发布」
    - 设置发布时间为当前时间
11. 如果选择「保存草稿」：
    - 状态设为「草稿」
12. 返回成功
13. 前端跳转至内容列表

### 6.4 内容搜索流程

1. 用户在搜索框输入关键词
2. 选择筛选条件（分类、标签、时间、排序）
3. 前端防抖处理（300ms）
4. 发送搜索请求到后端
5. 后端查询数据库（使用 MySQL LIKE 或全文索引）
6. 构建查询条件：
    - 关键词匹配（标题、描述、内容）
    - 分类过滤
    - 标签过滤
    - 时间范围过滤
    - 排序（相关度、时间、浏览数、点赞数、评分）
7. 执行查询
8. 获取查询结果
9. 转换为前端所需格式
10. 返回搜索结果
11. 前端渲染搜索结果列表
12. 保存搜索历史到本地存储

### 6.5 收藏流程

1. 用户在详情页点击「收藏」按钮
2. 前端检查登录状态
3. 未登录：跳转至登录页
4. 已登录：发送收藏请求到后端
5. 后端验证用户存在
6. 后端验证内容存在
7. 检查是否已收藏
8. 如果已收藏：
    - 删除收藏记录
    - 更新内容收藏数（减 1）
    - 返回「取消收藏成功」
9. 如果未收藏：
    - 创建收藏记录
    - 更新内容收藏数（加 1）
    - 返回「收藏成功」
10. 前端更新按钮状态
11. 前端更新收藏数显示

### 6.6 评论发表流程

1. 用户在详情页评论区输入评论内容
2. 点击「发表评论」按钮
3. 前端检查登录状态
4. 未登录：跳转至登录页
5. 已登录：验证评论内容长度（1-500 字符）
6. 发送评论请求到后端
7. 后端验证用户存在
8. 后端验证内容存在
9. 过滤敏感词
10. 保存评论到数据库
11. 更新内容评论数（加 1）
12. 返回评论信息
13. 前端将新评论插入到列表顶部
14. 前端更新评论数显示

### 6.7 管理员禁用用户流程

1. 管理员访问用户管理页面
2. 找到要禁用的用户
3. 点击「禁用」按钮
4. 弹出禁用对话框
5. 输入禁用原因
6. 确认禁用
7. 发送禁用请求到后端
8. 后端验证管理员权限
9. 后端验证用户存在
10. 验证不能禁用自己
11. 验证不能禁用更高权限用户
12. 更新用户状态为「禁用」
13. 记录操作日志
14. 返回禁用成功
15. 前端更新用户状态显示
16. 被禁用用户下次登录失败

### 6.8 手工赋权流程

1. 管理员访问手工赋权页面
2. 输入用户名或邮箱
3. 系统查询用户信息
4. 验证用户存在
5. 选择要授权的产品
6. 输入授权备注（如："微信支付"）
7. 确认授权
8. 发送授权请求到后端
9. 后端验证管理员权限
10. 后端验证用户存在
11. 后端验证产品存在
12. 检查是否已授权（防止重复授权）
13. 创建授权记录
14. 更新产品销售数量和总收入
15. 记录操作日志
16. 返回授权成功
17. 前端显示成功提示
18. 用户获得该产品的访问权限

### 6.9 访问 GitHub DEMO 流程

1. 用户在已购买产品列表点击 GitHub DEMO 链接
2. 发送权限验证请求到后端
3. 后端验证用户登录状态
4. 后端查询用户对该产品的授权记录
5. 检查授权状态（ACTIVE）
6. 有权限：
   - 返回 GitHub DEMO 链接
   - 前端新窗口打开 GitHub
7. 无权限：
   - 返回无权限提示
   - 前端提示需要购买或联系管理员

---

## 7. 验收标准

### 7.1 用户模块验收标准

#### 用户注册
- ✅ 注册表单所有字段必填验证正确
- ✅ 用户名格式验证（3-20字符，字母数字下划线）
- ✅ 邮箱格式验证正确
- ✅ 密码强度验证（8-32字符，大小写字母+数字）
- ✅ 确认密码与密码一致性验证
- ✅ 验证码校验正确
- ✅ 用户名重复提示友好
- ✅ 邮箱重复提示友好
- ✅ 注册成功后跳转登录页
- ✅ 密码在数据库中使用 bcrypt 加密
- ✅ 新注册用户默认角色为 USER
- ✅ 发送验证邮件

#### 用户登录
- ✅ 支持用户名或邮箱登录
- ✅ 密码错误超过 5 次锁定账号
- ✅ 锁定时长为 30 分钟
- ✅ 登录成功生成 JWT Token
- ✅ Token 包含用户 ID、角色、过期时间
- ✅ 记住我功能保存 7 天
- ✅ 更新最后登录时间和 IP
- ✅ 被禁用用户无法登录
- ✅ 登录失败提示清晰

#### 个人中心
- ✅ 用户名和邮箱不可修改
- ✅ 头像上传支持 JPG/PNG，最大 2MB
- ✅ 修改密码需验证当前密码
- ✅ 修改成功后需重新登录
- ✅ 个人信息保存后立即生效
- ✅ 主题设置实时切换
- ✅ 浏览历史按时间倒序
- ✅ 收藏列表正确显示

### 7.2 内容模块验收标准

#### 首页/探索页
- ✅ 首页加载时间 < 2 秒
- ✅ 轮播图自动切换（5 秒间隔）
- ✅ 轮播图支持手动切换
- ✅ 热门内容按综合热度排序
- ✅ 最新内容按发布时间倒序
- ✅ 点击卡片跳转详情页
- ✅ 移动端适配良好
- ✅ 空数据状态友好提示

#### 详情页
- ✅ 详情页加载时间 < 1.5 秒
- ✅ 浏览后自动增加浏览次数
- ✅ 富文本正确渲染 Markdown
- ✅ 官方链接新窗口打开
- ✅ 点赞、收藏状态实时更新
- ✅ 评论发表后立即显示
- ✅ 相关推荐内容不重复

#### 搜索页
- ✅ 搜索响应时间 < 500 毫秒
- ✅ 支持模糊搜索
- ✅ 多个筛选条件可以组合
- ✅ 搜索历史正确保存
- ✅ 搜索建议按热门度排序
- ✅ 分页功能正确
- ✅ 空搜索提示友好
- ✅ 高亮显示关键词

#### 分类页
- ✅ 点击分类正确筛选内容
- ✅ 多级分类展开/折叠正确
- ✅ 当前分类高亮显示
- ✅ 筛选条件与分类组合正确
- ✅ 面包屑导航正确

### 7.3 互动模块验收标准

#### 收藏功能
- ✅ 收藏按钮状态实时切换
- ✅ 同一内容不能重复收藏
- ✅ 收藏数量准确统计
- ✅ 收藏列表按时间倒序
- ✅ 取消收藏后立即移除
- ✅ 删除内容后自动清除收藏

#### 评论功能
- ✅ 评论内容长度限制正确
- ✅ 敏感词过滤生效
- ✅ 评论发表后立即显示
- ✅ 只能删除自己的评论
- ✅ 删除前二次确认
- ✅ 删除内容后评论级联删除

#### 点赞功能
- ✅ 点赞按钮状态实时切换
- ✅ 同一内容不能重复点赞
- ✅ 点赞数量准确统计
- ✅ 取消点赞后计数减少
- ✅ 点赞动画流畅

#### 评分功能
- ✅ 每个用户对同一内容只能评一次
- ✅ 评分后立即更新平均分
- ✅ 评分必须为 1-5 星
- ✅ 评分分布统计准确
- ✅ 已评分状态正确显示

### 7.4 内容管理模块验收标准

#### 内容发布
- ✅ 所有必填字段有验证
- ✅ 草稿可以多次编辑
- ✅ 发布后不可再改为草稿
- ✅ 图片上传支持 JPG/PNG，最大 5MB
- ✅ Markdown 正确渲染
- ✅ 标签数量限制正确（最多 10 个）
- ✅ 新建标签时验证唯一性

#### 内容编辑
- ✅ 编辑功能权限控制正确
- ✅ 修改后立即生效
- ✅ 更新时间自动更新
- ✅ 历史版本正确保存
- ✅ 版本对比功能正确

#### 内容删除
- ✅ 删除前二次确认
- ✅ 级联删除正确执行
- ✅ 删除后不可再访问
- ✅ 管理员可以恢复删除内容

### 7.5 管理模块验收标准

#### 用户管理
- ✅ 用户列表分页正确
- ✅ 搜索筛选功能正确
- ✅ 禁用用户后无法登录
- ✅ 角色修改权限控制正确
- ✅ 删除用户级联删除正确
- ✅ 操作日志正确记录

#### 内容管理
- ✅ 管理员可以查看所有内容
- ✅ 筛选功能正确
- ✅ 批量操作正确
- ✅ 操作权限正确

#### 分类管理
- ✅ 分类名称唯一（同级）
- ✅ 分类层级不超过 3 级
- ✅ 有子分类时不能删除
- ✅ 有内容时不能删除
- ✅ 拖拽排序正确

#### 标签管理
- ✅ 标签名称唯一
- ✅ 使用次数统计准确
- ✅ 删除关联正确处理
- ✅ 标签不能为空

#### 系统配置
- ✅ 配置修改后立即生效
- ✅ 敏感配置不明文显示
- ✅ 配置有默认值
- ✅ 配置修改记录日志

#### 数据统计
- ✅ 统计数据准确
- ✅ 趋势图正确渲染
- ✅ 数据实时更新（缓存 5 分钟）
- ✅ 支持日期范围选择

#### 系统日志
- ✅ 日志完整记录关键操作
- ✅ 筛选功能正确
- ✅ 分页展示正确
- ✅ 日志自动清理

### 7.6 性能验收标准

| 指标 | 目标值 | 验收标准 |
|------|--------|----------|
| 首页加载时间 | < 2 秒 | 测量 10 次，平均值 < 2 秒 |
| 详情页加载时间 | < 1.5 秒 | 测量 10 次，平均值 < 1.5 秒 |
| 搜索响应时间 | < 500 毫秒 | 测量 10 次，平均值 < 500 毫秒 |
| 并发用户数 | 1000+ | 压力测试支持 1000 并发 |
| 数据库查询响应 | < 100 毫秒 | 带索引查询 < 100 毫秒 |

### 7.7 安全验收标准

- ✅ 所有用户密码使用 bcrypt 加密存储
- ✅ 敏感操作需要二次验证
- ✅ CSRF 防护生效
- ✅ API 接口实现速率限制
- ✅ 用户输入严格过滤和验证
- ✅ 防止 XSS 攻击
- ✅ 防止 SQL 注入
- ✅ 实施 HTTPS 传输加密

### 7.8 兼容性验收标准

| 浏览器 | 版本 | 兼容性 |
|--------|------|--------|
| Chrome | 最新两个版本 | ✅ 完全兼容 |
| Firefox | 最新两个版本 | ✅ 完全兼容 |
| Safari | 最新两个版本 | ✅ 完全兼容 |
| Edge | 最新两个版本 | ✅ 完全兼容 |

---

## 8. 实现方案确认

### 8.1 产品相关方案

1. **内容审核机制**
   - 方案：MVP 阶段不实现内容审核功能，由内容编辑直接发布内容

2. **评论审核**
   - 方案：使用敏感词过滤，无需人工审核

3. **评分机制**
   - 方案：仅允许登录用户评分，匿名用户无法评分

4. **内容来源**
   - 方案：内容编辑手动录入内容，后续考虑数据爬取

5. **推荐算法**
   - 方案：使用基于标签的简单推荐，不需要个性化推荐

### 8.2 技术相关方案

1. **邮件服务**
   - 方案：使用 SMTP 服务发送邮件

2. **图片存储**
   - 方案：MVP 阶段图片存储在本地，后续使用 OSS

3. **搜索实现**
   - 方案：使用 MySQL 全文索引实现搜索，不支持拼音搜索

4. **缓存策略**
   - 方案：首页缓存 5 分钟，详情页缓存 10 分钟

5. **日志保留**
   - 方案：操作日志保留 90 天

### 8.3 设计相关方案

1. **Logo 设计**
   - 方案：简洁科技感风格，主色调蓝色

2. **默认封面图**
   - 方案：按内容类型设置不同的默认图

3. **移动端适配**
   - 方案：使用响应式设计，无需独立移动端页面

### 8.4 运营相关方案

1. **内容更新频率**
   - 方案：初期每周新增 10-20 条内容

2. **用户增长目标**
   - 方案：上线 3 个月内达到 1000 注册用户

3. **数据统计维度**
   - 方案：集成百度统计

### 8.5 支付相关方案

1. **支付功能**
   - 方案：MVP 阶段通过管理员手工赋权实现，预留支付接口

2. **GitHub DEMO 访问**
   - 方案：用户购买后获得 GitHub DEMO 访问权限，权限验证通过后显示链接

### 8.6 后续迭代方向

1. **数据爬取**
   - 实现自动爬取 AI 技术信息
   - 需要考虑反爬虫策略

2. **社区功能**
   - 增加讨论区
   - 增加问答功能

3. **API 开放**
   - 提供 REST API
   - 开放内容数据接口

4. **在线支付**
   - 接入支付宝支付
   - 接入微信支付

5. **移动应用**
   - 开发 iOS 应用
   - 开发 Android 应用
   - 开发小程序

---

## 9. 附录

### 9.1 术语表

| 术语 | 英文 | 说明 |
|------|------|------|
| 产品需求文档 | PRD | Product Requirements Document |
| 最小可行产品 | MVP | Minimum Viable Product |
| 基于角色的访问控制 | RBAC | Role-Based Access Control |
| JSON Web 令牌 | JWT | JSON Web Token |
| 人工智能 | AI | Artificial Intelligence |
| 大语言模型 | LLM | Large Language Model |
| 多因素认证 | MFA | Multi-Factor Authentication |

### 9.2 参考文档

- Vue 3 官方文档: https://vuejs.org/
- Spring Boot 官方文档: https://spring.io/projects/spring-boot
- Element Plus 文档: https://element-plus.org/
/
- Redis 文档: https://redis.io/documentation

### 9.3 联系方式

- 产品经理：待定
- 技术负责人：待定
- 设计负责人：待定
- 项目邮箱：待定

---

**文档结束**
