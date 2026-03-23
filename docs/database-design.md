# 数据库设计文档

## 1. 文档信息

| 项目 | 内容 |
|------|------|
| 版本 | v1.0.0 |
| 日期 | 2026-03-23 |
| 状态 | Draft |
| 作者 | AI-Tide Database Team |
| 数据库类型 | MySQL 8.0 |
| 字符集 | utf8mb4 |
| 排序规则 | utf8mb4_unicode_ci |
| 存储引擎 | InnoDB |

---

## 2. 数据库概述

### 2.1 数据库配置

```ini
[mysqld]
# 基础配置
version = 8.0
character-set-server = utf8mb4
collation-server = utf8mb4_unicode_ci

# 存储引擎
default-storage-engine = INNODB

# 连接配置
max_connections = 500
wait_timeout = 28800
interactive_timeout = 28800

# InnoDB 配置
innodb_buffer_pool_size = 2G
innodb_log_file_size = = 512M
innodb_flush_log_at_trx_commit = 2
innodb_flush_method = O_DIRECT

# 查询缓存（MySQL 8.0 已移除，使用其他优化）
```

### 2.2 设计原则

1. **命名规范**
   - 表名：小写字母，使用下划线分隔（如：`user`, `operation_log`, `content`）
   - 字段名：小写字母，使用下划线分隔（如：`user_id`, `created_at`）
   - 索引名：`idx_表名_字段名`（如：`idx_content_user_id`）
   - 外键名：`fk_表名_引用表名`（如：`fk_content_user`）

2. **主键设计**
   - 优先使用自增 `BIGINT` 类型
   - 需要全局唯一性的表使用 `VARCHAR(36)` UUID 类型

3. **时间戳**
   - 所有表包含 `created_at` 和 `updated_at` 字段
   - 类型：`DATETIME` 或 `TIMESTAMP`
   - 自动管理：`created_at` 在创建时设置，`updated_at` 在更新时自动刷新

4. **索引优化**
   - 为常用查询字段（`user_id`, `content_id`, `status`）添加索引
   - 复合索引遵循最左前缀原则
   - 避免过度索引，影响写入性能

5. **数据一致性**
   - 使用外键约束确保数据完整性
   - 适当使用级联操作（`ON DELETE CASCADE`）
   - 重要操作使用事务

6. **软删除**
   - 重要数据使用 `status` 字段标记删除
   - 状态枚举：`ACTIVE`, `INACTIVE`, `DELETED`
   - 保留数据用于审计和历史记录

---

## 3. ER 图（文字描述）

### 3.1 实体关系概览

```
用户相关：
┌─────────────┐
│    User     │
└──────┬──────┘
       │ 1
       │
       │ N
┌──────▼──────┐
│OperationLog │
└─────────────┘

内容相关：
┌────────────┐       ┌──────────────┐
│  Category  │1─────N│   Content    │
└────────────┘       └──────┬───────┘
                            │ 1
                            │
                            │ N
┌────────────┐       ┌──────▼───────┐
│    Tag     │N─────M│   Content    │
└────────────┘       └──────────────┘

┌──────────────┐1─────N│   Content    │
│ RelatedLink  │      └──────────────┘
└──────────────┘

互动相关：
┌──────────────┐
│   Content    │
└──────┬───────┘
       │ 1
       │
       │ N
┌──────▼───────┐
│   Comment    │
└──────────────┘

┌──────────────┐
│    User      │
└──────┬───────┘
       │ 1
       │
       │ N
┌──────▼───────┐
│   Favorite   │
│     Like     │
│    Rating    │
└──────────────┘

管理相关：
┌──────────────┐
│SystemConfig  │
└──────────────┘

支付授权相关：
┌──────────────┐       ┌──────────────────┐
│ PaidProduct  │1─────N│ ProductLicense   │
└──────────────┘       └────────┬─────────┘
                                │ 1
                                │
                                │ N
                        ┌───────▼──────────┐
                        │ PaymentOrder     │
                        └──────────────────┘

智能体协作相关：
┌──────────────┐1─────N│ AgentMember      │
│  AgentTeam   │      └──────────────────┘
└──────┬───────┘
       │ 1
       │
       │ N
┌──────▼───────┐
│Collaboration │
│   Session    │
└──────┬───────┘
       │ 1
       │
       │ N
┌──────▼───────┐
│Discussion    │
│  Message     │
└──────────────┘

┌──────────────┐
│Collaboration │
│  Conclusion  │
└──────────────┘
```

### 3.2 关系类型说明

| 关系类型 | 说明 | 示例 |
|----------|------|------|
| 1:1 (一对一) | 一个实体对应另一个实体 | User ↔ UserProfile |
| 1:N (一对多) | 一个实体对应多个实体 | User 1:N OperationLog |
| N:1 (多对一) | 多个实体对应一个实体 | Content N:1 Category |
| N:M (多对多) | 多个实体对应多个实体 | Content M:N Tag |

---

## 4. 数据表设计

### 4.1 用户相关

#### 4.1.1 user 表

**用途**：存储用户基本信息和认证信息

**字段列表**：

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | 是 | AUTO_INCREMENT | 用户主键 |
| uuid | VARCHAR(36) | 是 | - | 全局唯一标识符（UUID） |
| username | VARCHAR(50) | 是 | - | 用户名（唯一） |
| email | VARCHAR(100) | 是 | - | 邮箱（唯一） |
| password_hash | VARCHAR(255) | 是 | - | 密码哈希值 |
| phone | VARCHAR(20) | 否 | NULL | 手机号 |
| avatar_url | VARCHAR(500) | 否 | NULL | 头像 URL |
| nickname | VARCHAR(100) | 否 | NULL | 昵称 |
| bio | TEXT | 否 | NULL | 个人简介 |
| status | VARCHAR(20) | 是 | 'ACTIVE' | 状态：ACTIVE, INACTIVE, DELETED, BANNED |
| is_verified | BOOLEAN | 是 | FALSE | 是否已验证 |
| role | VARCHAR(20) | 是 | 'USER' | 角色：USER, ADMIN, SUPER_ADMIN |
| last_login_at | DATETIME | 否 | NULL | 最后登录时间 |
| last_login_ip | VARCHAR(50) | 否 | NULL | 最后登录 IP |
| created_at | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 是 | CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

**索引设计**：

```sql
CREATE UNIQUE INDEX idx_user_uuid ON user(uuid);
CREATE UNIQUE INDEX idx_user_username ON user(username);
CREATE UNIQUE INDEX idx_user_email ON user(email);
CREATE INDEX idx_user_status ON user(status);
CREATE INDEX idx_user_created_at ON user(created_at);
```

**外键约束**：无

---

#### 4.1.2 operation_log 表

**用途**：记录用户操作日志，用于审计和追踪

**字段列表**：

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | 是 | AUTO_INCREMENT | 日志主键 |
| uuid | VARCHAR(36) | 是 | - | 全局唯一标识符 |
| user_id | BIGINT | 是 | - | 用户 ID |
| operation_type | VARCHAR(50) | 是 | - | 操作类型：LOGIN, LOGOUT, CREATE, UPDATE, DELETE |
| resource_type | VARCHAR(50) | 是 | - | 资源类型：CONTENT, COMMENT, USER, CONFIG |
| resource_id | VARCHAR(100) | 否 | NULL | 资源 ID |
| description | TEXT | 否 | NULL | 操作描述 |
| request_ip | VARCHAR(50) | 否 | NULL | 请求 IP |
| user_agent | VARCHAR(500) | 否 | NULL | 用户代理 |
| request_params | TEXT | 否 | NULL | 请求参数（JSON） |
| response_status | INT | 否 | NULL | 响应状态码 |
| execution_time_ms | INT | 否 | NULL | 执行时间（毫秒） |
| status | VARCHAR(20) | 是 | 'ACTIVE' | 状态：ACTIVE, INACTIVE |
| created_at | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 是 | CURRENT_TIMESTAMP | 更新时间 |

**索引设计**：

```sql
CREATE UNIQUE INDEX idx_operation_log_uuid ON operation_log(uuid);
CREATE INDEX idx_operation_log_user_id ON operation_log(user_id);
CREATE INDEX idx_operation_log_operation_type ON operation_log(operation_type);
CREATE INDEX idx_operation_logressource_type ON operation_log(resource_type);
CREATE INDEX idx_operation_log_created_at ON operation_log(created_at);
CREATE INDEX idx_operation_log_user_id_created_at ON operation_log(user_id, created_at);
```

**外键约束**：

```sql
ALTER TABLE operation_log
ADD CONSTRAINT fk_operation_log_user
FOREIGN KEY (user_id) REFERENCES user(id)
ON DELETE CASCADE
ON UPDATE CASCADE;
```

---

### 4.2 内容相关

#### 4.2.1 content 表

**用途**：存储内容主体信息

**字段列表**：

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | 是 | AUTO_INCREMENT | 内容主键 |
| uuid | VARCHAR(36) | 是 | - | 全局唯一标识符 |
| user_id | BIGINT | 是 | - | 创建用户 ID |
| category_id | BIGINT | 否 | NULL | 分类 ID |
| title | VARCHAR(200) | 是 | - | 标题 |
| summary | VARCHAR(500) | 否 | NULL | 摘要 |
| content | LONGTEXT | 是 | - | 内容正文 |
| cover_image_url | VARCHAR(500) | 否 | NULL | 封面图片 URL |
| view_count | BIGINT | 是 | 0 | 浏览次数 |
| like_count | BIGINT | 是 | 0 | 点赞数 |
| comment_count | BIGINT | 是 | 0 | 评论数 |
| favorite_count | BIGINT | 是 | 0 | 收藏数 |
| rating_average | DECIMAL(3,2) | 是 | 0.00 | 平均评分 |
| rating_count | INT | 是 | 0 | 评分人数 |
| is_published | BOOLEAN | 是 | FALSE | 是否已发布 |
| is_paid | BOOLEAN | 是 | FALSE | 是否付费内容 |
| price | DECIMAL(10,2) | 否 | NULL | 价格 |
| status | VARCHAR(20) | 是 | 'ACTIVE' | 状态：ACTIVE, INACTIVE, DELETED, DRAFT |
| published_at | DATETIME | 否 | NULL | 发布时间 |
| created_at | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 是 | CURRENT_TIMESTAMP | 更新时间 |

**索引设计**：

```sql
CREATE UNIQUE INDEX idx_content_uuid ON content(uuid);
CREATE INDEX idx_content_user_id ON content(user_id);
CREATE INDEX idx_content_category_id ON content(category_id);
CREATE INDEX idx_content_status ON content(status);
CREATE INDEX idx_content_is_published ON content(is_published);
CREATE INDEX idx_content_is_paid ON content(is_paid);
CREATE INDEX idx_content_created_at ON content(created_at);
CREATE INDEX idx_content_published_at ON content(published_at);
CREATE INDEX idx_content_user_id_status ON content(user_id, status);
CREATE FULLTEXT INDEX idx_content_title_content ON content(title, content);
```

**外键约束**：

```sql
ALTER TABLE content
ADD CONSTRAINT fk_content_user
FOREIGN KEY (user_id) REFERENCES user(id)
ON DELETE CASCADE
ON UPDATE CASCADE;

ALTER TABLE content
ADD CONSTRAINT fk_content_category
FOREIGN KEY (category_id) REFERENCES category(id)
ON DELETE SET NULL
ON UPDATE CASCADE;
```

---

#### 4.2.2 category 表

**用途**：内容分类管理

**字段列表**：

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | 是 | AUTO_INCREMENT | 分类主键 |
| uuid | VARCHAR(36) | 是 | - | 全局唯一标识符 |
| parent_id | BIGINT | 否 | NULL | 父分类 ID |
| name | VARCHAR(100) | 是 | - | 分类名称 |
| slug | VARCHAR(100) | 是 | - | URL 友好名称 |
| description | TEXT | 否 | NULL | 分类描述 |
| icon | VARCHAR(100) | 否 | NULL | 图标 |
| sort_order | INT | 是 | 0 | 排序序号 |
| level | INT | 是 | 1 | 分类层级 |
| path | VARCHAR(500) | 否 | NULL | 分类路径（如：/tech/ai） |
| status | VARCHAR(20) | 是 | 'ACTIVE' | 状态：ACTIVE, INACTIVE, DELETED |
| created_at | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 是 | CURRENT_TIMESTAMP | 更新时间 |

**索引设计**：

```sql
CREATE UNIQUE INDEX idx_category_uuid ON category(uuid);
CREATE UNIQUE INDEX idx_category_slug ON category(slug);
CREATE INDEX idx_category_parent_id ON category(parent_id);
CREATE INDEX idx_category_status ON category(status);
CREATE INDEX idx_category_sort_order ON category(sort_order);
CREATE INDEX idx_category_level ON category(level);
```

**外键约束**：

```sql
ALTER TABLE category
ADD CONSTRAINT fk_category_parent
FOREIGN KEY (parent_id) REFERENCES category(id)
ON DELETE CASCADE
ON UPDATE CASCADE;
```

---

#### 4.2.3 tag 表

**用途**：内容标签管理

**字段列表**：

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | 是 | AUTO_INCREMENT | 标签主键 |
| uuid | VARCHAR(36) | 是 | - | 全局唯一标识符 |
| name | VARCHAR(50) | 是 | - | 标签名称 |
| slug | VARCHAR(50) | 是 | - | URL 友好名称 |
| description | TEXT | 否 | NULL | 标签描述 |
| color | VARCHAR(20) | 否 | NULL | 标签颜色 |
| usage_count | BIGINT | 是 | 0 | 使用次数 |
| status | VARCHAR(20) | 是 | 'ACTIVE' | 状态：ACTIVE, INACTIVE, DELETED |
| created_at | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 是 | CURRENT_TIMESTAMP | 更新时间 |

**索引设计**：

```sql
CREATE UNIQUE INDEX idx_tag_uuid ON tag(uuid);
CREATE UNIQUE INDEX idx_tag_name ON tag(name);
CREATE UNIQUE INDEX idx_tag_slug ON tag(slug);
CREATE INDEX idx_tag_status ON tag(status);
CREATE INDEX idx_tag_usage_count ON tag(usage_count);
```

**外键约束**：无

---

#### 4.2.4 content_tag 表（关联表）

**用途**：内容与标签的多对多关系

**字段列表**：

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | 是 | AUTO_INCREMENT | 主键 |
| content_id | BIGINT | 是 | - | 内容 ID |
| tag_id | BIGINT | 是 | - | 标签 ID |
| created_at | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |

**索引设计**：

```sql
CREATE UNIQUE INDEX idx_content_tag_content_id_tag_id ON content_tag(content_id, tag_id);
CREATE INDEX idx_content_tag_content_id ON content_tag(content_id);
CREATE INDEX idx_content_tag_tag_id ON content_tag(tag_id);
```

**外键约束**：

```sql
ALTER TABLE content_tag
ADD CONSTRAINT fk_content_tag_content
FOREIGN KEY (content_id) REFERENCES content(id)
ON DELETE CASCADE
ON UPDATE CASCADE;

ALTER TABLE content_tag
ADD CONSTRAINT fk_content_tag_tag
FOREIGN KEY (tag_id) REFERENCES tag(id)
ON DELETE CASCADE
ON UPDATE CASCADE;
```

---

#### 4.2.5 related_link 表

**用途**：内容相关链接

**字段列表**：

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | 是 | AUTO_INCREMENT | 主键 |
| uuid | VARCHAR(36) | 是 | - | 全局唯一标识符 |
| content_id | BIGINT | 是 | - | 内容 ID |
| title | VARCHAR(200) | 是 | - | 链接标题 |
| url | VARCHAR(500) | 是 | - | 链接 URL |
| description | TEXT | 否 | NULL | 链接描述 |
| icon | VARCHAR(100) | 否 | NULL | 链接图标 |
| sort_order | INT | 是 | 0 | 排序序号 |
| is_external | BOOLEAN | 是 | TRUE | 是否外部链接 |
| status | VARCHAR(20) | 是 | 'ACTIVE' | 状态：ACTIVE, INACTIVE, DELETED |
| created_at | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 是 | CURRENT_TIMESTAMP | 更新时间 |

**索引设计**：

```sql
CREATE UNIQUE INDEX idx_related_link_uuid ON related_link(uuid);
CREATE INDEX idx_related_link_content_id ON related_link(content_id);
CREATE INDEX idx_related_link_status ON related_link(status);
CREATE INDEX idx_related_link_sort_order ON related_link(sort_order);
```

**外键约束**：

```sql
ALTER TABLE related_link
ADD CONSTRAINT fk_related_link_content
FOREIGN KEY (content_id) REFERENCES content(id)
ON DELETE CASCADE
ON UPDATE CASCADE;
```

---

### 4.3 互动相关

#### 4.3.1 comment 表

**用途**：评论数据

**字段列表**：

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | 是 | AUTO_INCREMENT | 评论主键 |
| uuid | VARCHAR(36) | 是 | - | 全局唯一标识符 |
| content_id | BIGINT | 是 | - | 内容 ID |
| user_id | BIGINT | 是 | - | 用户 ID |
| parent_id | BIGINT | 否 | NULL | 父评论 ID（用于回复） |
| content | TEXT | 是 | - | 评论内容 |
| like_count | INT | 是 | 0 | 点赞数 |
| reply_count | INT | 是 | 0 | 回复数 |
| is_pinned | BOOLEAN | 是 | FALSE | 是否置顶 |
| status | VARCHAR(20) | 是 | 'ACTIVE' | 状态：ACTIVE, INACTIVE, DELETED, HIDDEN |
| created_at | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 是 | CURRENT_TIMESTAMP | 更新时间 |

**索引设计**：

```sql
CREATE UNIQUE INDEX idx_comment_uuid ON comment(uuid);
CREATE INDEX idx_comment_content_id ON comment(content_id);
CREATE INDEX idx_comment_user_id ON comment(user_id);
CREATE INDEX idx_comment_parent_id ON comment(parent_id);
CREATE INDEX idx_comment_status ON comment(status);
CREATE INDEX idx_comment_created_at ON comment(created_at);
CREATE INDEX idx_comment_content_id_created_at ON comment(content_id, created_at);
```

**外键约束**：

```sql
ALTER TABLE comment
ADD CONSTRAINT fk_comment_content
FOREIGN KEY (content_id) REFERENCES content(id)
ON DELETE CASCADE
ON UPDATE CASCADE;

ALTER TABLE comment
ADD CONSTRAINT fk_comment_user
FOREIGN KEY (user_id) REFERENCES user(id)
ON DELETE CASCADE
ON UPDATE CASCADE;

ALTER TABLE comment
ADD CONSTRAINT fk_comment_parent
FOREIGN KEY (parent_id) REFERENCES comment(id)
ON DELETE CASCADE
ON UPDATE CASCADE;
```

---

#### 4.3.2 favorite 表

**用途**：用户收藏内容

**字段列表**：

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | 是 | AUTO_INCREMENT | 主键 |
| uuid | VARCHAR(36) | 是 | - | 全局唯一标识符 |
| user_id | BIGINT | 是 | - | 用户 ID |
| content_id | BIGINT | 是 | - | 内容 ID |
| note | VARCHAR(500) | 否 | NULL | 收藏备注 |
| status | VARCHAR(20) | 是 | 'ACTIVE' | 状态：ACTIVE, INACTIVE |
| created_at | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 是 | CURRENT_TIMESTAMP | 更新时间 |

**索引设计**：

```sql
CREATE UNIQUE INDEX idx_favorite_uuid ON favorite(uuid);
CREATE UNIQUE INDEX idx_favorite_user_id_content_id ON favorite(user_id, content_id);
CREATE INDEX idx_favorite_user_id ON favorite(user_id);
CREATE INDEX idx_favorite_content_id ON favorite(content_id);
CREATE INDEX idx_favorite_created_at ON favorite(created_at);
```

**外键约束**：

```sql
ALTER TABLE favorite
ADD CONSTRAINT fk_favorite_user
FOREIGN KEY (user_id) REFERENCES user(id)
ON DELETE CASCADE
ON UPDATE CASCADE;

ALTER TABLE favorite
ADD CONSTRAINT fk_favorite_content
FOREIGN KEY (content_id) REFERENCES content(id)
ON DELETE CASCADE
ON UPDATE CASCADE;
```

---

#### 4.3.3 like 表

**用途**：用户点赞记录

**字段列表**：

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | 是 | AUTO_INCREMENT | 主键 |
| uuid | VARCHAR(36) | 是 | - | 全局唯一标识符 |
| user_id | BIGINT | 是 | - | 用户 ID |
| content_id | BIGINT | 是 | - | 内容 ID |
| like_type | VARCHAR(20) | 是 | 'CONTENT' | 点赞类型：CONTENT, COMMENT |
| target_id | BIGINT | 是 | - | 目标 ID（内容 ID 或评论 ID） |
| status | VARCHAR(20) | 是 | 'ACTIVE' | 状态：ACTIVE, INACTIVE |
| created_at | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 是 | CURRENT_TIMESTAMP | 更新时间 |

**索引设计**：

```sql
CREATE UNIQUE INDEX idx_like_uuid ON like(uuid);
CREATE UNIQUE INDEX idx_like_user_id_target_id_like_type ON like(user_id, target_id, like_type);
CREATE INDEX idx_like_user_id ON like(user_id);
CREATE INDEX idx_like_content_id ON like(content_id);
CREATE INDEX idx_like_target_id ON like(target_id);
CREATE INDEX idx_like_created_at ON like(created_at);
```

**外键约束**：

```sql
ALTER TABLE like
ADD CONSTRAINT fk_like_user
FOREIGN KEY (user_id) REFERENCES user(id)
ON DELETE CASCADE
ON UPDATE CASCADE;

ALTER TABLE like
ADD CONSTRAINT fk_like_content
FOREIGN KEY (content_id) REFERENCES content(id)
ON DELETE CASCADE
ON UPDATE CASCADE;
```

---

#### 4.3.4 rating 表

**用途**：用户评分记录

**字段列表**：

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | 是 | AUTO_INCREMENT | 主键 |
| uuid | VARCHAR(36) | 是 | - | 全局唯一标识符 |
| user_id | BIGINT | 是 | - | 用户 ID |
| content_id | BIGINT | 是 | - | 内容 ID |
| score | INT | 是 | - | 评分（1-5） |
| comment | TEXT | 否 | NULL | 评分评论 |
| status | VARCHAR(20) | 是 | 'ACTIVE' | 状态：ACTIVE, INACTIVE |
| created_at | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 是 | CURRENT_TIMESTAMP | 更新时间 |

**索引设计**：

```sql
CREATE UNIQUE INDEX idx_rating_uuid ON rating(uuid);
CREATE UNIQUE INDEX idx_rating_user_id_content_id ON rating(user_id, content_id);
CREATE INDEX idx_rating_user_id ON rating(user_id);
CREATE INDEX idx_rating_content_id ON rating(content_id);
CREATE INDEX idx_rating_score ON rating(score);
CREATE INDEX idx_rating_created_at ON rating(created_at);
```

**外键约束**：

```sql
ALTER TABLE rating
ADD CONSTRAINT fk_rating_user
FOREIGN KEY (user_id) REFERENCES user(id)
ON DELETE CASCADE
ON UPDATE CASCADE;

ALTER TABLE rating
ADD CONSTRAINT fk_rating_content

FOREIGN KEY (content_id) REFERENCES content(id)
ON DELETE CASCADE
ON UPDATE CASCADE;
```

---

### 4.4 管理相关

#### 4.4.1 system_config 表

**用途**：系统配置管理

**字段列表**：

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | 是 | AUTO_INCREMENT | 主键 |
| config_key | VARCHAR(100) | 是 | - | 配置键（唯一） |
| config_value | TEXT | 是 | - | 配置值 |
| config_type | VARCHAR(20) | 是 | 'STRING' | 配置类型：STRING, INTEGER, BOOLEAN, JSON |
| description | VARCHAR(500) | 否 | NULL | 配置描述 |
| category | VARCHAR(50) | 是 | 'GENERAL' | 配置分类：GENERAL, SECURITY, EMAIL, STORAGE |
| is_public | BOOLEAN | 是 | FALSE | 是否公开（前端可访问） |
| is_editable | BOOLEAN | 是 | TRUE | 是否可编辑 |
| status | VARCHAR(20) | 是 | 'ACTIVE' | 状态：ACTIVE, INACTIVE |
| created_at | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 是 | CURRENT_TIMESTAMP | 更新时间 |

**索引设计**：

```sql
CREATE UNIQUE INDEX idx_system_config_config_key ON system_config(config_key);
CREATE INDEX idx_system_config_category ON system_config(category);
CREATE INDEX idx_system_config_status ON system_config(status);
CREATE INDEX idx_system_config_is_public ON system_config(is_public);
```

**外键约束**：无

---

### 4.5 支付授权相关

#### 4.5.1 paid_product 表

**用途**：付费产品管理

**字段列表**：

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | 是 | AUTO_INCREMENT | 产品主键 |
| uuid | VARCHAR(36) | 是 | - | 全局唯一标识符 |
| name | VARCHAR(200) | 是 | - | 产品名称 |
| slug | VARCHAR(100) | 是 | - | URL 友好名称 |
| description | TEXT | 否 | NULL | 产品描述 |
| price | DECIMAL(10,2) | 是 | - | 价格 |
| currency | VARCHAR(10) | 是 | 'CNY' | 货币类型 |
| discount_price | DECIMAL(10,2) | 否 | NULL | 折扣价 |
| duration_days | INT | 否 | NULL | 授权时长（天），NULL 为永久 |
| max_devices | INT | 否 | NULL | 最大设备数，NULL 为无限制 |
| features | TEXT | 否 | NULL | 功能列表（JSON） |
| is_featured | BOOLEAN | 是 | FALSE | 是否推荐 |
| sort_order | INT | 是 | 0 | 排序序号 |
| status | VARCHAR(20) | 是 | 'ACTIVE' | 状态：ACTIVE, INACTIVE, DELETED |
| created_at | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 是 | CURRENT_TIMESTAMP | 更新时间 |

**索引设计**：

```sql
CREATE UNIQUE INDEX idx_paid_product_uuid ON paid_product(uuid);
CREATE UNIQUE INDEX idx_paid_product_slug ON paid_product(slug);
CREATE INDEX idx_paid_product_status ON paid_product(status);
CREATE INDEX idx_paid_product_is_featured ON paid_product(is_featured);
CREATE INDEX idx_paid_product_sort_order ON paid_product(sort_order);
```

**外键约束**：无

---

#### 4.5.2 product_license 表

**用途**：产品授权记录

**字段列表**：

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | 是 | AUTO_INCREMENT | 主键 |
| uuid | VARCHAR(36) | 是 | - | 全局唯一标识符 |
| user_id | BIGINT | 是 | - | 用户 ID |
| product_id | BIGINT | 是 | - | 产品 ID |
| license_key | VARCHAR(100) | 是 | - | 授权密钥 |
| device_id | VARCHAR(100) | 否 | NULL | 设备 ID |
| expires_at | DATETIME | 否 | NULL | 过期时间，NULL 为永久 |
| is_active | BOOLEAN | 是 | TRUE | 是否激活 |
| status | VARCHAR(20) | 是 | 'ACTIVE' | 状态：ACTIVE, INACTIVE, EXPIRED, REVOKED |
| created_at | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 是 | CURRENT_TIMESTAMP | 更新时间 |

**索引设计**：

```sql
CREATE UNIQUE INDEX idx_product_license_uuid ON product_license(uuid);
CREATE UNIQUE INDEX idx_product_license_license_key ON product_license(license_key);
CREATE INDEX idx_product_license_user_id ON product_license(user_id);
CREATE INDEX idx_product_license_product_id ON product_license(product_id);
CREATE INDEX idx_product_license_device_id ON product_license(device_id);
CREATE INDEX idx_product_license_expires_at ON product_license(expires_at);
CREATE INDEX idx_product_license_status ON product_license(status);
```

**外键约束**：

```sql
ALTER TABLE product_license
ADD CONSTRAINT fk_product_license_user
FOREIGN KEY (user_id) REFERENCES user(id)
ON DELETE CASCADE
ON UPDATE CASCADE;

ALTER TABLE product_license
ADD CONSTRAINT fk_product_license_product
FOREIGN KEY (product_id) REFERENCES paid_product(id)
ON DELETE CASCADE
ON UPDATE CASCADE;
```

---

#### 4.5.3 payment_order 表（预留）

**用途**：支付订单记录（预留功能）

**字段列表**：

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | 是 | AUTO_INCREMENT | 订单主键 |
| uuid | VARCHAR(36) | 是 | - | 全局唯一标识符 |
| order_no | VARCHAR(50) | 是 | - | 订单号（唯一） |
| user_id | BIGINT | 是 | - | 用户 ID |
| product_id | BIGINT | 是 | - | 产品 ID |
| amount | DECIMAL(10,2) | 是 | - | 订单金额 |
| currency | VARCHAR(10) | 是 | 'CNY' | 货币类型 |
| payment_method | VARCHAR(20) | 是 | - | 支付方式：ALIPAY, WECHAT, CREDIT_CARD |
| payment_status | VARCHAR(20) | 是 | 'PENDING' | 支付状态：PENDING, PAID, FAILED, REFUNDED |
| transaction_id | VARCHAR(100) | 否 | NULL | 交易 ID |
| paid_at | DATETIME | 否 | NULL | 支付时间 |
| expired_at | DATETIME | 是 | - | 订单过期时间 |
| client_ip | VARCHAR(50) | 否 | NULL | 客户端 IP |
| status | VARCHAR(20) | 是 | 'ACTIVE' | 状态：ACTIVE, INACTIVE, DELETED |
| created_at | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 是 | CURRENT_TIMESTAMP | 更新时间 |

**索引设计**：

```sql
CREATE UNIQUE INDEX idx_payment_order_uuid ON payment_order(uuid);
CREATE UNIQUE INDEX idx_payment_order_order_no ON payment_order(order_no);
CREATE INDEX idx_payment_order_user_id ON payment_order(user_id);
CREATE INDEX idx_payment_order_product_id ON payment_order(product_id);
CREATE INDEX idx_payment_order_payment_status ON payment_order(payment_status);
CREATE INDEX idx_payment_order_transaction_id ON payment_order(transaction_id);
CREATE INDEX idx_payment_order_created_at ON payment_order(created_at);
```

**外键约束**：

```sql
ALTER TABLE payment_order
ADD CONSTRAINT fk_payment_order_user
FOREIGN KEY (user_id) REFERENCES user(id)
ON DELETE CASCADE
ON UPDATE CASCADE;

ALTER TABLE payment_order
ADD CONSTRAINT fk_payment_order_product
FOREIGN KEY (product_id) REFERENCES paid_product(id)
ON DELETE CASCADE
ON UPDATE CASCADE;
```

---

### 4.6 智能体协作相关

#### 4.6.1 agent_team 表

**用途**：智能体团队

**字段列表**：

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | 是 | AUTO_INCREMENT | 团队主键 |
| uuid | VARCHAR(36) | 是 | - | 全局唯一标识符 |
| name | VARCHAR(100) | 是 | - | 团队名称 |
| description | TEXT | 否 | NULL | 团队描述 |
| avatar_url | VARCHAR(500) | 否 | NULL | 团队头像 |
| owner_id | BIGINT | 是 | - | 所有者用户 ID |
| max_members | INT | 是 | 10 | 最大成员数 |
| status | VARCHAR(20) | 是 | 'ACTIVE' | 状态：ACTIVE, INACTIVE, DELETED |
| created_at | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 是 | CURRENT_TIMESTAMP | 更新时间 |

**索引设计**：

```sql
CREATE UNIQUE INDEX idx_agent_team_uuid ON agent_team(uuid);
CREATE INDEX idx_agent_team_owner_id ON agent_team(owner_id);
CREATE INDEX idx_agent_team_status ON agent_team(status);
CREATE INDEX idx_agent_team_created_at ON agent_team(created_at);
```

**外键约束**：

```sql
ALTER TABLE agent_team
ADD CONSTRAINT fk_agent_team_owner
FOREIGN KEY (owner_id) REFERENCES user(id)
ON DELETE CASCADE
ON UPDATE CASCADE;
```

---

#### 4.6.2 agent_member 表

**用途**：智能体成员

**字段列表**：

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | 是 | AUTO_INCREMENT | 成员主键 |
| uuid | VARCHAR(36) | 是 | - | 全局唯一标识符 |
| team_id | BIGINT | 是 | - | 团队 ID |
| user_id | BIGINT | 是 | - | 用户 ID |
| agent_name | VARCHAR(100) | 是 | - | 智能体名称 |
| agent_type | VARCHAR(50) | 是 | 'AI' | 智能体类型：AI, HUMAN |
| agent_role | VARCHAR(50) | 是 | 'MEMBER' | 角色：LEADER, MEMBER, OBSERVER |
| capabilities | TEXT | 否 | NULL | 能力列表（JSON） |
| status | VARCHAR(20) | 是 | 'ACTIVE' | 状态：ACTIVE, INACTIVE, DELETED |
| joined_at | DATETIME | 是 | CURRENT_TIMESTAMP | 加入时间 |
| created_at | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 是 | CURRENT_TIMESTAMP | 更新时间 |

**索引设计**：

```sql
CREATE UNIQUE INDEX idx_agent_member_uuid ON agent_member(uuid);
CREATE UNIQUE INDEX idx_agent_member_team_id_user_id ON agent_member(team_id, user_id);
CREATE INDEX idx_agent_member_team_id ON agent_member(team_id);
CREATE INDEX idx_agent_member_user_id ON agent_member(user_id);
CREATE INDEX idx_agent_member_agent_type ON agent_member(agent_type);
CREATE INDEX idx_agent_member_status ON agent_member(status);
```

**外键约束**：

```sql
ALTER TABLE agent_member
ADD CONSTRAINT fk_agent_member_team
FOREIGN KEY (team_id) REFERENCES agent_team(id)
ON DELETE CASCADE
ON UPDATE CASCADE;

ALTER TABLE agent_member
ADD CONSTRAINT fk_agent_member_user
FOREIGN KEY (user_id) REFERENCES user(id)
ON DELETE CASCADE
ON UPDATE CASCADE;
```

---

#### 4.6.3 collaboration_session 表

**用途**：协作会话

**字段列表**：

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | 是 | AUTO_INCREMENT | 会话主键 |
| uuid | VARCHAR(36) | 是 | - | 全局唯一标识符 |
| team_id | BIGINT | 是 | - | 团队 ID |
| title | VARCHAR(200) | 是 | - | 会话标题 |
| description | TEXT | 否 | NULL | 会话描述 |
| initiator_id | BIGINT | 是 | - | 发起人 ID |
| context_content | LONGTEXT | 否 | NULL | 上下文内容 |
| goal | VARCHAR(500) | 否 | NULL | 协作目标 |
| status | VARCHAR(20) | 是 | 'ACTIVE' | 状态：ACTIVE, INACTIVE, COMPLETED, CANCELLED |
| started_at | DATETIME | 是 | CURRENT_TIMESTAMP | 开始时间 |
| ended_at | DATETIME | 否 | NULL | 结束时间 |
| created_at | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 是 | CURRENT_TIMESTAMP | 更新时间 |

**索引设计**：

```sql
CREATE UNIQUE INDEX idx_collaboration_session_uuid ON collaboration_session(uuid);
CREATE INDEX idx_collaboration_session_team_id ON collaboration_session(team_id);
CREATE INDEX idx_collaboration_session_initiator_id ON collaboration_session(initiator_id);
CREATE INDEX idx_collaboration_session_status ON collaboration_session(status);
CREATE INDEX idx_collaboration_session_started_at ON collaboration_session(started_at);
CREATE INDEX idx_collaboration_session_ended_at ON collaboration_session(ended_at);
```

**外键约束**：

```sql
ALTER TABLE collaboration_session
ADD CONSTRAINT fk_collaboration_session_team
FOREIGN KEY (team_id) REFERENCES agent_team(id)
ON DELETE CASCADE
ON UPDATE CASCADE;

ALTER TABLE collaboration_session
ADD CONSTRAINT fk_collaboration_session_initiator
FOREIGN KEY (initiator_id) REFERENCES agent_member(id)
ON DELETE CASCADE
ON UPDATE CASCADE;
```

---

#### 4.6.4 discussion_message 表

**用途**：讨论消息

**字段列表**：

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | 是 | AUTO_INCREMENT | 消息主键 |
| uuid | VARCHAR(36) | 是 | - | 全局唯一标识符 |
| session_id | BIGINT | 是 | - | 会话 ID |
| sender_id | BIGINT | 是 | - | 发送者成员 ID |
| content | TEXT | 是 | - | 消息内容 |
| message_type | VARCHAR(20) | 是 | 'TEXT' | 消息类型：TEXT, IMAGE, FILE, CODE |
| metadata | TEXT | 否 | NULL | 元数据（JSON） |
| is_system | BOOLEAN | 是 | FALSE | 是否系统消息 |
| status | VARCHAR(20) | 是 | 'ACTIVE' | 状态：ACTIVE, INACTIVE, DELETED |
| created_at | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 是 | CURRENT_TIMESTAMP | 更新时间 |

**索引设计**：

```sql
CREATE UNIQUE INDEX idx_discussion_message_uuid ON discussion_message(uuid);
CREATE INDEX idx_discussion_message_session_id ON discussion_message(session_id);
CREATE INDEX idx_discussion_message_sender_id ON discussion_message(sender_id);
CREATE INDEX idx_discussion_message_message_type ON discussion_message(message_type);
CREATE INDEX idx_discussion_message_created_at ON discussion_message(created_at);
CREATE INDEX idx_discussion_message_session_id_created_at ON discussion_message(session_id, created_at);
```

**外键约束**：

```sql
ALTER TABLE discussion_message
ADD CONSTRAINT fk_discussion_message_session
FOREIGN KEY (session_id) REFERENCES collaboration_session(id)
ON DELETE CASCADE
ON UPDATE CASCADE;

ALTER TABLE discussion_message
ADD CONSTRAINT fk_discussion_message_sender
FOREIGN KEY (sender_id) REFERENCES agent_member(id)
ON DELETE CASCADE
ON UPDATE CASCADE;
```

---

#### 4.6.5 collaboration_conclusion 表

**用途**：协作结论

**字段列表**：

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | 是 | AUTO_INCREMENT | 结论主键 |
| uuid | VARCHAR(36) | 是 | - | 全局唯一标识符 |
| session_id | BIGINT | 是 | - | 会话 ID |
| title | VARCHAR(200) | 是 | - | 结论标题 |
| summary | TEXT | 否 | NULL | 结论摘要 |
| content | LONGTEXT | 是 | - | 结论内容 |
| attachments | TEXT | 否 | NULL | 附件列表（JSON） |
| action_items | TEXT[] | 否 | NULL | 行动项（JSON 数组） |
| is_approved | BOOLEAN | 是 | FALSE | 是否已批准 |
| approved_by | BIGINT | 否 | NULL | 批准人 ID |
| approved_at | DATETIME | 否 | NULL | 批准时间 |
| status | VARCHAR(20) | 是 | 'DRAFT' | 状态：DRAFT, PENDING, APPROVED, REJECTED |
| created_at | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 是 | CURRENT_TIMESTAMP | 更新时间 |

**索引设计**：

```sql
CREATE UNIQUE INDEX idx_collaboration_conclusion_uuid ON collaboration_conclusion(uuid);
CREATE INDEX idx_collaboration_conclusion_session_id ON collaboration_conclusion(session_id);
CREATE INDEX idx_collaboration_conclusion_status ON collaboration_conclusion(status);
CREATE INDEX idx_collaboration_conclusion_is_approved ON collaboration_conclusion(is_approved);
CREATE INDEX idx_collaboration_conclusion_created_at ON collaboration_conclusion(created_at);
```

**外键约束**：

```sql
ALTER TABLE collaboration_conclusion
ADD CONSTRAINT fk_collaboration_conclusion_session
FOREIGN KEY (session_id) REFERENCES collaboration_session(id)
ON DELETE CASCADE
ON UPDATE CASCADE;

ALTER TABLE collaboration_conclusion
ADD CONSTRAINT fk_collaboration_conclusion_approved_by
FOREIGN KEY (approved_by) REFERENCES agent_member(id)
ON DELETE SET NULL
ON UPDATE CASCADE;
```

---

## 5. 关系说明

### 5.1 用户模块关系

| 源表 | 目标表 | 关系类型 | 说明 |
|------|--------|----------|------|
| user | operation_log | 1:N | 一个用户可以有多条操作日志 |

### 5.2 内容模块关系

| 源表 | 目标表 | 关系类型 | 说明 |
|------|--------|----------|------|
| category | category | 1:N | 分类自关联（父子分类） |
| category | content | 1:N | 一个分类可以包含多个内容 |
| content | content_tag | 1:N | 一个内容可以有多个标签关联 |
| tag | content_tag | 1:N | 一个标签可以关联多个内容 |
| content | related_link | 1:N | 一个内容可以有多个相关链接 |

### 5.3 互动模块关系

| 源表 | 目标表 | 关系类型 | 说明 |
|------|--------|----------|------|
| content | comment | 1:N | 一个内容可以有多个评论 |
| comment | comment | 1:N | 评论自关联（回复评论） |
| user | comment | 1:N | 一个用户可以发表多个评论 |
| user | favorite | 1:N | 一个用户可以收藏多个内容 |
| content | favorite | 1:N | 一个内容可以被多个用户收藏 |
| user | like | 1:N | 一个用户可以点赞多次 |
| content | like | 1:N | 一个内容可以被多个用户点赞 |
| user | rating | 1:N | 一个用户可以多次评分 |
| content | rating | 1:N | 一个内容可以被多个用户评分 |

### 5.4 支付授权模块关系

| 源表 | 目标表 | 关系类型 | 说明 |
|------|--------|----------|------|
| paid_product | product_license | 1:N | 一个产品可以授权给多个用户 |
| user | product_license | 1:N | 一个用户可以拥有多个授权 |
| paid_product | payment_order | 1:N | 一个产品可以有多笔订单 |
| user | payment_order | 1:N | 一个用户可以有多笔订单 |

### 5.5 智能体协作模块关系

| 源表 | 目标表 | 关系类型 | 说明 |
|------|--------|----------|------|
| user | agent_team | 1:N | 一个用户可以创建多个团队 |
| agent_team | agent_member | 1:N | 一个团队可以有多个成员 |
| user | agent_member | 1:N | 一个用户可以作为多个成员 |
| agent_team | collaboration_session | 1:N | 一个团队可以有多个协作会话 |
| agent_member | collaboration_session | 1:N | 一个成员可以发起多个会话 |
| collaboration_session | discussion_message | 1:N | 一个会话可以有多条讨论消息 |
| agent_member | discussion_message | 1:N | 一个成员可以发送多条消息 |
| collaboration_session | collaboration_conclusion | 1:N | 一个会话可以有多个结论 |

---

## 6. 索引设计

### 6.1 索引总览

| 表名 | 索引名 | 索引类型 | 字段 | 说明 |
|------|--------|----------|------|------|
| user | idx_user_uuid | UNIQUE | uuid | UUID 唯一索引 |
| user | idx_user_username | UNIQUE | username | 用户名唯一索引 |
| user | idx_user_email | UNIQUE | email | 邮箱唯一索引 |
| user | idx_user_status | INDEX | status | 状态查询索引 |
| user | idx_user_created_at | INDEX | created_at | 创建时间索引 |
| operation_log | idx_operation_log_uuid | UNIQUE | uuid | UUID 唯一索引 |
| operation_log | idx_operation_log_user_id | INDEX | user_id | 用户 ID 索引 |
| operation_log | idx_operation_log_operation_type | INDEX | operation_type | 操作类型索引 |
| operation_log | idx_operation_log_created_at | INDEX | created_at | 创建时间索引 |
| operation_log | idx_operation_log_user_id_created_at | INDEX | user_id, created_at | 复合索引 |
| content | idx_content_uuid | UNIQUE | uuid | UUID 唯一索引 |
| content | idx_content_user_id | INDEX | user_id | 用户 ID 索引 |
| content | idx_content_category_id | INDEX | category_id | 分类 ID 索引 |
| content | idx_content_status | INDEX | status | 状态索引 |
| content | idx_content_is_published | INDEX | is_published | 发布状态索引 |
| content | idx_content_created_at | INDEX | created_at | 创建时间索引 |
| content | idx_content_title_content | FULLTEXT | title, content | 全文搜索索引 |
| category | idx_category_uuid | UNIQUE | uuid | UUID 唯一索引 |
| category | idx_category_slug | UNIQUE | slug | slug 唯一索引 |
| category | idx_category_parent_id | INDEX | parent_id | 父分类索引 |
| tag | idx_tag_uuid | UNIQUE | uuid | UUID 唯一索引 |
| tag | idx_tag_name | UNIQUE | name | 标签名唯一索引 |
| comment | idx_comment_uuid | UNIQUE | uuid | UUID 唯一索引 |
| comment | idx_comment_content_id | INDEX | content_id | 内容 ID 索引 |
| comment | idx_comment_user_id | INDEX | user_id | 用户 ID 索引 |
| favorite | idx_favorite_user_id_content_id | UNIQUE | user_id, content_id | 防止重复收藏 |
| like | idx_like_user_id_target_id_like_type | UNIQUE | user_id, target_id, like_type | 防止重复点赞 |
| rating | idx_rating_user_id_content_id | UNIQUE | user_id, content_id | 防止重复评分 |

### 6.2 索引使用场景

#### 6.2.1 单列索引

**使用场景**：
- 状态过滤：`WHERE status = 'ACTIVE'`
- 用户查询：`WHERE user_id = ?`
- 时间范围查询：`WHERE created_at >= ? AND created_at <= ?`
- 类型筛选：`WHERE is_published = TRUE`

#### 6.2.2 复合索引

**使用场景**：
- 用户操作日志查询：`WHERE user_id = ? AND created_at >= ?`
- 内容列表查询：`WHERE user_id = ? AND status = 'ACTIVE'`
- 评论排序查询：`WHERE content_id = ? ORDER BY created_at`

**最左前缀原则**：
- 索引 `(user_id, created_at)` 可以优化：
  - `WHERE user_id = ?`
  - `WHERE user_id = ? AND created_at >= ?`
  - `ORDER BY user_id, created_at`
- 不能优化：
  - `WHERE created_at >= ?`

#### 6.2.3 全文索引

**使用场景**：
- 内容搜索：`WHERE MATCH(title, content) AGAINST('关键词' IN BOOLEAN MODE)`

#### 6.2.4 唯一索引

**使用场景**：
- 防止重复数据：`user.username`, `user.email`, `user.uuid`
- 防止重复操作：`favorite(user_id, content_id)`, `like(user_id, target_id)`

### 6.3 索引优化建议

1. **避免过度索引**
   - 每个表索引数量不宜超过 5 个
   - 复合索引字段数量不宜超过 3 个

2. **监控索引使用率**
   - 使用 `SHOW INDEX FROM table_name` 查看索引统计
   - 定期清理未使用的索引

3. **索引列顺序**
   - 高选择性列放在前面（如 `status` 优于 `is_published`）
   - 考虑查询频率和 WHERE 条件顺序

4. **覆盖索引**
   - 尽量让索引包含查询所需的所有字段
   - 避免回表查询

---

## 7. 初始数据

### 7.1 初始分类数据

```sql
-- 插入初始分类数据
INSERT INTO category (uuid, parent_id, name, slug, description, icon, sort_order, level, path, status, created_at, updated_at) VALUES
-- 一级分类
('cat-0001', NULL, '技术', 'tech', '技术相关内容', 'code', 1, 1, '/tech', 'ACTIVE', NOW(), NOW()),
('cat-0002', NULL, '生活', 'life', '生活相关内容', 'heart', 2, 1, '/life', 'ACTIVE', NOW(), NOW()),
('cat-0003', NULL, '娱乐', 'entertainment', '娱乐相关内容', 'play', 3, 1, '/entertainment', 'ACTIVE', NOW(), NOW()),
('cat-0004', NULL, '教育', 'education', '教育相关内容', 'book', 4, 1, '/education', 'ACTIVE', NOW(), NOW());

-- 二级分类（技术）
INSERT INTO category (uuid, parent_id, name, slug, description, icon, sort_order, level, path, status, created_at, updated_at) VALUES
('cat-0005', (SELECT id FROM category WHERE slug = 'tech'), '人工智能', 'ai', '人工智能相关内容', 'robot', 1, 2, '/tech/ai', 'ACTIVE', NOW(), NOW()),
('cat-0006', (SELECT id FROM category WHERE slug = 'tech'), '编程', 'programming', '编程语言和开发', 'terminal', 2, 2, '/tech/programming', 'ACTIVE', NOW(), NOW()),
('cat-0007', (SELECT id FROM category WHERE slug = 'tech'), '数据科学', 'data-science', '数据科学和分析', 'chart', 3, 2, '/tech/data-science', 'ACTIVE', NOW(), NOW());

-- 二级分类（生活）
INSERT INTO category (uuid, parent_id, name, slug, description, icon, sort_order, level, path, status, created_at, updated_at) VALUES
('cat-0008', (SELECT id FROM category WHERE slug = 'life'), '健康', 'health', '健康生活', 'medical', 1, 2, '/life/health', 'ACTIVE', NOW(), NOW()),
('cat-0009', (SELECT id FROM category WHERE slug = 'life'), '旅行', 'travel', '旅行和探索', 'plane', 2, 2, '/life/travel', 'ACTIVE', NOW(), NOW()),
('cat-0010', (SELECT id FROM category WHERE slug = 'life'), '美食', 'food', '美食和烹饪', 'utensils', 3, 2, '/life/food', 'ACTIVE', NOW(), NOW());
```

### 7.2 初始标签数据

```sql
-- 插入初始标签数据
INSERT INTO tag (uuid, name, slug, description, color, usage_count, status, created_at, updated_at) VALUES
('tag-0001', '机器学习', 'machine-learning', '机器学习和算法', '#3498db', 0, 'ACTIVE', NOW(), NOW()),
('tag-0002', '深度学习', 'deep-learning', '深度学习和神经网络', '#9b59b6', 0, 'ACTIVE', NOW(), NOW()),
('tag-0003', '自然语言处理', 'nlp', '自然语言处理技术', '#e74c3c', 0, 'ACTIVE', NOW(), NOW()),
('tag-0004', '计算机视觉', 'computer-vision', '计算机视觉技术', '#2ecc71', 0, 'ACTIVE', NOW(), NOW()),
('tag-0005', 'Python', 'python', 'Python 编程语言', '#3776ab', 0, 'ACTIVE', NOW(), NOW()),
('tag-0006', 'JavaScript', 'javascript', 'JavaScript 编程语言', '#f7df1e', 0, 'ACTIVE', NOW(), NOW()),
('tag-0007', 'Java', 'java', 'Java 编程语言', '#007396', 0, 'ACTIVE', NOW(), NOW()),
('tag-0008', 'Go', 'golang', 'Go 编程语言', '#00add8', 0, 'ACTIVE', NOW(), NOW()),
('tag-0009', '教程', 'tutorial', '教程和指南', '#e67e22', 0, 'ACTIVE', NOW(), NOW()),
('tag-0010', '入门', 'beginner', '入门级内容', '#34495e', 0, 'ACTIVE', NOW(), NOW()),
('tag-0011', '进阶', 'advanced', '进阶级内容', '#c0392b', 0, 'ACTIVE', NOW(), NOW()),
('tag-0012', '实战', 'practice', '实战项目', '#16a085', 0, 'ACTIVE', NOW(), NOW());
```

### 7.3 初始系统配置

```sql
-- 插入初始系统配置
INSERT INTO system_config (config_key, config_value, config_type, description, category, is_public, is_editable, status, created_at, updated_at) VALUES
-- 常规配置
('site.name', 'AI-Tide', 'STRING', '网站名称', 'GENERAL', TRUE, TRUE, 'ACTIVE', NOW(), NOW()),
('site.description', 'AI 内容创作与协作平台', 'STRING', '网站描述', 'GENERAL', TRUE, TRUE, 'ACTIVE', NOW(), NOW()),
('site.keywords', 'AI, 内容创作, 协作', 'STRING', '网站关键词', 'GENERAL', TRUE, TRUE, 'ACTIVE', NOW(), NOW()),
('site.logo_url', '/assets/logo.png', 'STRING', '网站 Logo URL', 'GENERAL', TRUE, TRUE, 'ACTIVE', NOW(), NOW()),
('site.favicon_url', '/assets/favicon.ico', 'STRING', '网站 Favicon URL', 'GENERAL', TRUE, TRUE, 'ACTIVE', NOW(), NOW()),

-- 安全配置
('security.session_timeout', '7200', 'INTEGER', '会话超时时间（秒）', 'SECURITY', FALSE, TRUE, 'ACTIVE', NOW(), NOW()),
('security.max_login_attempts', '5', 'INTEGER', '最大登录尝试次数', 'SECURITY', FALSE, TRUE, 'ACTIVE', NOW(), NOW()),
('security.password_min_length', '8', 'INTEGER', '密码最小长度', 'SECURITY', FALSE, TRUE, 'ACTIVE', NOW(), NOW()),
('security.enable_two_factor', 'false', 'BOOLEAN', '是否启用双因素认证', 'SECURITY', FALSE, TRUE, 'ACTIVE', NOW(), NOW()),

-- 邮件配置
('email.smtp_host', 'smtp.example.com', 'STRING', 'SMTP 服务器地址', 'EMAIL', FALSE, TRUE, 'ACTIVE', NOW(), NOW()),
('email.smtp_port', '587', 'INTEGER', 'SMTP 端口', 'EMAIL', FALSE, TRUE, 'ACTIVE', NOW(), NOW()),
('email.smtp_username', '', 'STRING', 'SMTP 用户名', 'EMAIL', FALSE, TRUE, 'ACTIVE', NOW(), NOW()),
('email.smtp_password', '', 'STRING', 'SMTP 密码', 'EMAIL', FALSE, TRUE, 'ACTIVE', NOW(), NOW()),
('email.from_address', 'noreply@ai-tide.com', 'STRING', '发件人地址', 'EMAIL', FALSE, TRUE, 'ACTIVE', NOW(), NOW()),
('email.from_name', 'AI-Tide', 'STRING', '发件人名称', 'EMAIL', FALSE, TRUE, 'ACTIVE', NOW(), NOW()),

-- 存储配置
('storage.max_file_size', '104857600', 'INTEGER', '最大文件大小（字节，默认 100MB）', 'STORAGE', FALSE, TRUE, 'ACTIVE', NOW(), NOW()),
('storage.allowed_file_types', 'jpg,jpeg,png,gif,pdf,doc,docx', 'STRING', '允许的文件类型', 'STORAGE', FALSE, TRUE, 'ACTIVE', NOW(), NOW()),
('storage.upload_path', '/uploads', 'STRING', '文件上传路径', 'STORAGE', FALSE, TRUE, 'ACTIVE', NOW(), NOW()),

-- 内容配置
('content.max_content_length', '100000', 'INTEGER', '最大内容长度', 'GENERAL', FALSE, TRUE, 'ACTIVE', NOW(), NOW()),
('content.enable_moderation', 'true', 'BOOLEAN', '是否启用内容审核', 'GENERAL', FALSE, TRUE, 'ACTIVE', NOW(), NOW()),
('content.auto_publish', 'false', 'BOOLEAN', '是否自动发布', 'GENERAL', FALSE, TRUE, 'ACTIVE', NOW(), NOW()),

-- 支付配置
('payment.currency', 'CNY', 'STRING', '默认货币', 'GENERAL', FALSE, TRUE, 'ACTIVE', NOW(), NOW()),
('payment.order_timeout', '1800', 'INTEGER', '订单超时时间（秒）', 'GENERAL', FALSE, TRUE, 'ACTIVE', NOW(), NOW()),

-- 智能体配置
('agent.max_team_size', '10', 'INTEGER', '最大团队人数', 'GENERAL', FALSE, TRUE, 'ACTIVE', NOW(), NOW()),
('agent.max_session_duration', '3600', 'INTEGER', '最大会话时长（秒）', 'GENERAL', FALSE, TRUE, 'ACTIVE', NOW(), NOW()),
('agent.enable_auto_conclusion', 'true', 'BOOLEAN', '是否自动生成结论', 'GENERAL', FALSE, TRUE, 'ACTIVE', NOW(), NOW());
```

### 7.4 初始化管理员用户

```sql
-- 注意：密码需要使用 BCrypt 或其他安全算法进行哈希
-- 以下示例使用占位符，实际部署时需要替换
INSERT INTO user (uuid, username, email, password_hash, nickname, role, status, is_verified, created_at, updated_at) VALUES
('user-0001', 'admin', 'admin@ai-tide.com', '$2a$10$placeholder_hash', '系统管理员', 'SUPER_ADMIN', 'ACTIVE', TRUE, NOW(), NOW());
```

---

## 8. 数据迁移

### 8.1 Flyway 迁移脚本

#### V1__init_schema.sql

```sql
-- ============================================
-- AI-Tide 数据库初始化脚本
-- 版本: V1
-- 日期: 2026-03-23
-- ============================================

-- 设置字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================
-- 用户相关表
-- ============================================

-- 用户表
CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户主键',
  `uuid` VARCHAR(36) NOT NULL COMMENT '全局唯一标识符',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `email` VARCHAR(100) NOT NULL COMMENT '邮箱',
  `password_hash` VARCHAR(255) NOT NULL COMMENT '密码哈希值',
  `phone` VARCHAR(20) NULL COMMENT '手机号',
  `avatar_url` VARCHAR(500) NULL COMMENT '头像 URL',
  `nickname` VARCHAR(100) NULL COMMENT '昵称',
  `bio` TEXT NULL COMMENT '个人简介',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
  `is_verified` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否已验证',
  `role` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色',
  `last_login_at` DATETIME NULL COMMENT '最后登录时间',
  `last_login_ip` VARCHAR(50) NULL COMMENT '最后登录 IP',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_uuid` (`uuid`),
  UNIQUE KEY `idx_user_username` (`username`),
  UNIQUE KEY `idx_user_email` (`email`),
  KEY `idx_user_status` (`status`),
  KEY `idx_user_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 操作日志表
CREATE TABLE `operation_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `uuid` VARCHAR(36) NOT NULL COMMENT '全局唯一标识符',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `operation_type` VARCHAR(50) NOT NULL COMMENT '操作类型',
  `resource_type` VARCHAR(50) NOT NULL COMMENT '资源类型',
  `resource_id` VARCHAR(100) NULL COMMENT '资源 ID',
  `description` TEXT NULL COMMENT '操作描述',
  `request_ip` VARCHAR(50) NULL COMMENT '请求 IP',
  `user_agent` VARCHAR(500) NULL COMMENT '用户代理',
  `request_params` TEXT NULL COMMENT '请求参数',
  `response_status` INT NULL COMMENT '响应状态码',
  `execution_time_ms` INT NULL COMMENT '执行时间（毫秒）',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_operation_log_uuid` (`uuid`),
  KEY `idx_operation_log_user_id` (`user_id`),
  KEY `idx_operation_log_operation_type` (`operation_type`),
  KEY `idx_operation_log_created_at` (`created_at`),
  KEY `idx_operation_log_user_id_created_at` (`user_id`, `created_at`),
  CONSTRAINT `fk_operation_log_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- ============================================
-- 内容相关表
-- ============================================

-- 分类表
CREATE TABLE `category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类主键',
  `uuid` VARCHAR(36) NOT NULL COMMENT '全局唯一标识符',
  `parent_id` BIGINT NULL COMMENT '父分类 ID',
  `name` VARCHAR(100) NOT NULL COMMENT '分类名称',
  `slug` VARCHAR(100) NOT NULL COMMENT 'URL 友好名称',
  `description` TEXT NULL COMMENT '分类描述',
  `icon` VARCHAR(100) NULL COMMENT '图标',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序序号',
  `level` INT NOT NULL DEFAULT 1 COMMENT '分类层级',
  `path` VARCHAR(500) NULL COMMENT '分类路径',
  `status`" VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_category_uuid` (`uuid`),
  UNIQUE KEY `idx_category_slug` (`slug`),
  KEY `idx_category_parent_id` (`parent_id`),
  KEY `idx_category_status` (`status`),
  KEY `idx_category_sort_order` (`sort_order`),
  KEY `idx_category_level` (`level`),
  CONSTRAINT `fk_category_parent` FOREIGN KEY (`parent_id`) REFERENCES `category` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';

-- 标签表
CREATE TABLE `tag` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '标签主键',
  `uuid` VARCHAR(36) NOT NULL COMMENT '全局唯一标识符',
  `name` VARCHAR(50) NOT NULL COMMENT '标签名称',
  `slug` VARCHAR(50) NOT NULL COMMENT 'URL 友好名称',
  `description` TEXT NULL COMMENT '标签描述',
  `color` VARCHAR(20) NULL COMMENT '标签颜色',
  `usage_count` BIGINT NOT NULL DEFAULT 0 COMMENT '使用次数',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_tag_uuid` (`uuid`),
  UNIQUE KEY `idx_tag_name` (`name`),
  UNIQUE KEY `idx_tag_slug` (`slug`),
  KEY `idx_tag_status` (`status`),
  KEY `idx_tag_usage_count` (`usage_count`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

-- 内容表
CREATE TABLE `content` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '内容主键',
  `uuid` VARCHAR(36) NOT NULL COMMENT '全局唯一标识符',
  `user_id` BIGINT NOT NULL COMMENT '创建用户 ID',
  `category_id` BIGINT NULL COMMENT '分类 ID',
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `summary` VARCHAR(500) NULL COMMENT '摘要',
  `content` LONGTEXT NOT NULL COMMENT '内容正文',
  `cover_image_url` VARCHAR(500) NULL COMMENT '封面图片 URL',
  `view_count` BIGINT NOT NULL DEFAULT 0 COMMENT '浏览次数',
  `like_count` BIGINT NOT NULL DEFAULT 0 COMMENT '点赞数',
  `comment_count` BIGINT NOT NULL DEFAULT 0 COMMENT '评论数',
  `favorite_count` BIGINT NOT NULL DEFAULT 0 COMMENT '收藏数',
  `rating_average` DECIMAL(3,2) NOT NULL DEFAULT 0.00 COMMENT '平均评分',
  `rating_count` INT NOT NULL DEFAULT 0 COMMENT '评分人数',
  `is_published` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否已发布',
  `is_paid` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否付费内容',
  `price` DECIMAL(10,2) NULL COMMENT '价格',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
  `published_at` DATETIME NULL COMMENT '发布时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_content_uuid` (`uuid`),
  KEY `idx_content_user_id` (`user_id`),
  KEY `idx_content_category_id` (`category_id`),
  KEY `idx_content_status` (`status`),
  KEY `idx_content_is_published` (`is_published`),
  KEY `idx_content_is_paid` (`is_paid`),
  KEY `idx_content_created_at` (`created_at`),
  KEY `idx_content_published_at` (`published_at`),
  KEY `idx_content_user_id_status` (`user_id`, `status`),
  FULLTEXT KEY `idx_content_title_content` (`title`, `content`),
  CONSTRAINT `fk_content_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_content_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容表';

-- 内容标签关联表
CREATE TABLE `content_tag` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `content_id` BIGINT NOT NULL COMMENT '内容 ID',
  `tag_id` BIGINT NOT NULL COMMENT '标签 ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_content_tag_content_id_tag_id` (`content_id`, `tag_id`),
  KEY `idx_content_tag_content_id` (`content_id`),
  KEY `idx_content_tag_tag_id` (`tag_id`),
  CONSTRAINT `fk_content_tag_content` FOREIGN KEY (`content_id`) REFERENCES `content` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_content_tag_tag` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容标签关联表';

-- 相关链接表
CREATE TABLE `related_link` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `uuid` VARCHAR(36) NOT NULL COMMENT '全局唯一标识符',
  `content_id` BIGINT NOT NULL COMMENT '内容 ID',
  `title` VARCHAR(200) NOT NULL COMMENT '链接标题',
  `url` VARCHAR(500) NOT NULL COMMENT '链接 URL',
  `description` TEXT NULL COMMENT '链接描述',
  `icon` VARCHAR(100) NULL COMMENT '链接图标',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序序号',
  `is_external` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否外部链接',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_related_link_uuid` (`uuid`),
  KEY `idx_related_link_content_id` (`content_id`),
  KEY `idx_related_link_status` (`status`),
  KEY `idx_related_link_sort_order` (`sort_order`),
  CONSTRAINT `fk_related_link_content` FOREIGN KEY (`content_id`) REFERENCES `content` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='相关链接表';

-- ============================================
-- 互动相关表
-- ============================================

-- 评论表
CREATE TABLE `comment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评论主键',
  `uuid` VARCHAR(36) NOT NULL COMMENT '全局唯一标识符',
  `content_id` BIGINT NOT NULL COMMENT '内容 ID',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `parent_id` BIGINT NULL COMMENT '父评论 ID',
  `content` TEXT NOT NULL COMMENT '评论内容',
  `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
  `reply_count` INT NOT NULL DEFAULT 0 COMMENT '回复数',
  `is_pinned` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否置顶',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_comment_uuid` (`uuid`),
  KEY `idx_comment_content_id` (`content_id`),
  KEY `idx_comment_user_id` (`user_id`),
  KEY `idx_comment_parent_id` (`parent_id`),
  KEY `idx_comment_status` (`status`),
  KEY `idx_comment_created_at` (`created_at`),
  KEY `idx_comment_content_id_created_at` (`content_id`, `created_at`),
  CONSTRAINT `fk_comment_content` FOREIGN KEY (`content_id`) REFERENCES `content` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_comment_parent` FOREIGN KEY (`parent_id`) REFERENCES `comment` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- 收藏表
CREATE TABLE `favorite` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `uuid` VARCHAR(36) NOT NULL COMMENT '全局唯一标识符',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `content_id` BIGINT NOT NULL COMMENT '内容 ID',
  `note` VARCHAR(500) NULL COMMENT '收藏备注',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_favorite_uuid` (`uuid`),
  UNIQUE KEY `idx_favorite_user_id_content_id` (`user_id`, `content_id`),
  KEY `idx_favorite_user_id` (`user_id`),
  KEY `idx_favorite_content_id` (`content_id`),
  KEY `idx_favorite_created_at` (`created_at`),
  CONSTRAINT `fk_favorite_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_favorite_content` FOREIGN KEY (`content_id`) REFERENCES `content` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';

-- 点赞表
CREATE TABLE `like` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `uuid` VARCHAR(36) NOT NULL COMMENT '全局唯一标识符',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `content_id` BIGINT NOT NULL COMMENT '内容 ID',
  `like_type` VARCHAR(20) NOT NULL DEFAULT 'CONTENT' COMMENT '点赞类型',
  `target_id` BIGINT NOT NULL COMMENT '目标 ID',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_like_uuid` (`uuid`),
  UNIQUE KEY `idx_like_user_id_target_id_like_type` (`user_id`, `target_id`, `like_type`),
  KEY `idx_like_user_id` (`user_id`),
  KEY `idx_like_content_id` (`content_id`),
  KEY `idx_like_target_id` (`target_id`),
  KEY `idx_like_created_at` (`created_at`),
  CONSTRAINT `fk_like_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_like_content` FOREIGN KEY (`content_id`) REFERENCES `content` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='点赞表';

-- 评分表
CREATE TABLE `rating` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `uuid` VARCHAR(36) NOT NULL COMMENT '全局唯一标识符',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `content_id` BIGINT NOT NULL COMMENT '内容 ID',
  `score` INT NOT NULL COMMENT '评分（1-5）',
  `comment` TEXT NULL COMMENT '评分评论',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_rating_uuid` (`uuid`),
  UNIQUE KEY `idx_rating_user_id_content_id` (`user_id`, `content_id`),
  KEY `idx_rating_user_id` (`user_id`),
  KEY `idx_rating_content_id` (`content_id`),
  KEY `idx_rating_score` (`score`),
  KEY `idx_rating_created_at` (`created_at`),
  CONSTRAINT `fk_rating_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_rating`_content FOREIGN KEY (`content_id`) REFERENCES `content` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评分表';

-- ============================================
-- 管理相关表
-- ============================================

-- 系统配置表
CREATE TABLE `system_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
  `config_value` TEXT NOT NULL COMMENT '配置值',
  `config_type` VARCHAR(20) NOT NULL DEFAULT 'STRING' COMMENT '配置类型',
  `description` VARCHAR(500) NULL COMMENT '配置描述',
  `category` VARCHAR(50) NOT NULL DEFAULT 'GENERAL' COMMENT '配置分类',
  `is_public` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否公开',
  `is_editable` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否可编辑',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_system_config_config_key` (`config_key`),
  KEY `idx_system_config_category` (`category`),
  KEY `idx_system_config_status` (`status`),
  KEY `idx_system_config_is_public` (`is_public`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- ============================================
-- 支付授权相关表
-- ============================================

-- 付费产品表
CREATE TABLE `paid_product` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '产品主键',
  `uuid` VARCHAR(36) NOT NULL COMMENT '全局唯一标识符',
  `name` VARCHAR(200) NOT NULL COMMENT '产品名称',
  `slug` VARCHAR(100) NOT NULL COMMENT 'URL 友好名称',
  `description` TEXT NULL COMMENT '产品描述',
  `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
  `currency` VARCHAR(10) NOT NULL DEFAULT 'CNY' COMMENT '货币类型',
  `discount_price` DECIMAL(10,2) NULL COMMENT '折扣价',
  `duration_days` INT NULL COMMENT '授权时长（天）',
  `max_devices` INT NULL COMMENT '最大设备数',
  `features` TEXT NULL COMMENT '功能列表',
  `is_featured` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否推荐',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序序号',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_paid_product_uuid` (`uuid`),
  UNIQUE KEY `idx_paid_product_slug` (`slug`),
  KEY `idx_paid_product_status` (`status`),
  KEY `idx_paid_product_is_featured` (`is_featured`),
  KEY `idx_paid_product_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='付费产品表';

-- 产品授权表
CREATE TABLE `product_license` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `uuid` VARCHAR(36) NOT NULL COMMENT '全局唯一标识符',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `product_id` BIGINT NOT NULL COMMENT '产品 ID',
  `license_key` VARCHAR(100) NOT NULL COMMENT '授权密钥',
  `device_id` VARCHAR(100) NULL COMMENT '设备 ID',
  `expires_at` DATETIME NULL COMMENT '过期时间',
  `is_active` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否激活',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_product_license_uuid` (`uuid`),
  UNIQUE KEY `idx_product_license_license_key` (`license_key`),
  KEY `idx_product_license_user_id` (`user_id`),
  KEY `idx_product_license_product_id` (`product_id`),
  KEY `idx_product_license_device_id` (`device_id`),
  KEY `idx_product_license_expires_at` (`expires_at`),
  KEY `idx_product_license_status` (`status`),
  CONSTRAINT `fk_product_license_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_product_license_product` FOREIGN KEY (`product_id`) REFERENCES `paid_product` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产品授权表';

-- 支付订单表
CREATE TABLE `payment_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单主键',
  `uuid` VARCHAR(36) NOT NULL COMMENT '全局唯一标识符',
  `order_no` VARCHAR(50) NOT NULL COMMENT '订单号',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `product_id` BIGINT NOT NULL COMMENT '产品 ID',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '订单金额',
  `currency` VARCHAR(10) NOT NULL DEFAULT 'CNY' COMMENT '货币类型',
  `payment_method` VARCHAR(20) NOT NULL COMMENT '支付方式',
  `payment_status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '支付状态',
  `transaction_id` VARCHAR(100) NULL COMMENT '交易 ID',
  `paid_at` DATETIME NULL COMMENT '支付时间',
  `expired_at` DATETIME NOT NULL COMMENT '订单过期时间',
  `client_ip` VARCHAR(50) NULL COMMENT '客户端 IP',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_payment_order_uuid` (`uuid`),
  UNIQUE KEY `idx_payment_order_order_no` (`order_no`),
  KEY `idx_payment_order_user_id` (`user_id`),
  KEY `idx_payment_order_product_id` (`product_id`),
  KEY `idx_payment_order_payment_status` (`payment_status`),
  KEY `idx_payment_order_transaction_id` (`transaction_id`),
  KEY `idx_payment_order_created_at` (`created_at`),
  CONSTRAINT `fk_payment_order_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_payment_order_product` FOREIGN KEY (`product_id`) REFERENCES `paid_product` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付订单表';

-- ============================================
-- 智能体协作相关表
-- ============================================

-- 智能体团队表
CREATE TABLE `agent_team` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '团队主键',
  `uuid` VARCHAR(36) NOT NULL COMMENT '全局唯一标识符',
  `name` VARCHAR(100) NOT NULL COMMENT '团队名称',
  `description` TEXT NULL COMMENT '团队描述',
  `avatar_url` VARCHAR(500) NULL COMMENT '团队头像',
  `owner_id` BIGINT NOT NULL COMMENT '所有者用户 ID',
  `max_members` INT NOT NULL DEFAULT 10 COMMENT '最大成员数',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_agent_team_uuid` (`uuid`),
  KEY `idx_agent_team_owner_id` (`owner_id`),
  KEY `idx_agent_team_status` (`status`),
  KEY `idx_agent_team_created_at` (`created_at`),
  CONSTRAINT `fk_agent_team_owner` FOREIGN KEY (`owner_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='智能体团队表';

-- 智能体成员表
CREATE TABLE `agent_member` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '成员主键',
  `uuid` VARCHAR(36) NOT NULL COMMENT '全局唯一标识符',
  `team_id` BIGINT NOT NULL COMMENT '团队 ID',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `agent_name` VARCHAR(100) NOT NULL COMMENT '智能体名称',
  `agent_type` VARCHAR(50) NOT NULL DEFAULT 'AI' COMMENT '智能体类型',
  `agent_role` VARCHAR(50) NOT NULL DEFAULT 'MEMBER' COMMENT '角色',
  `capabilities` TEXT NULL COMMENT '能力列表',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
  `joined_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_agent_member_uuid` (`uuid`),
  UNIQUE KEY `idx_agent_member_team_id_user_id` (`team_id`, `user_id`),
  KEY `idx_agent_member_team_id` (`team_id`),
  KEY `idx_agent_member_user_id` (`user_id`),
  KEY `idx_agent_member_agent_type` (`agent_type`),
  KEY `idx_agent_member_status` (`status`),
  CONSTRAINT `fk_agent_member_team` FOREIGN KEY (`team_id`) REFERENCES `agent_team` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_agent_member_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='智能体成员表';

-- 协作会话表
CREATE TABLE `collaboration_session` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '会话主键',
  `uuid` VARCHAR(36) NOT NULL COMMENT '全局唯一标识符',
  `team_id` BIGINT NOT NULL COMMENT '团队 ID',
  `title` VARCHAR(200) NOT NULL COMMENT '会话标题',
  `description` TEXT NULL COMMENT '会话描述',
  `initiator_id` BIGINT NOT NULL COMMENT '发起人 ID',
  `context_content` LONGTEXT NULL COMMENT '上下文内容',
  `goal` VARCHAR(500) NULL COMMENT '协作目标',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
  `started_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
  `ended_at` DATETIME NULL COMMENT '结束时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_collaboration_session_uuid` (`uuid`),
  KEY `idx_collaboration_session_team_id` (`team_id`),
  KEY `idx_collaboration_session_initiator_id` (`initiator_id`),
  KEY `idx_collaboration_session_status` (`status`),
  KEY `idx_collaboration_session_started_at` (`started_at`),
  KEY `idx_collaboration_session_ended_at` (`ended_at`),
  CONSTRAINT `fk_collaboration_session_team` FOREIGN KEY (`team_id`) REFERENCES `agent_team` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_collaboration_session_initiator` FOREIGN KEY (`initiator_id`) REFERENCES `agent_member` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='协作会话表';

-- 讨论消息表
CREATE TABLE `discussion_message` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '消息主键',
  `uuid` VARCHAR(36) NOT NULL COMMENT '全局唯一标识符',
  ``session_id` BIGINT NOT NULL COMMENT '会话 ID',
  `sender_id` BIGINT NOT NULL COMMENT '发送者成员 ID',
  `content` TEXT NOT NULL COMMENT '消息内容',
  `message_type` VARCHAR(20) NOT NULL DEFAULT 'TEXT' COMMENT '消息类型',
  `metadata` TEXT NULL COMMENT '元数据',
  `is_system` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否系统消息',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_discussion_message_uuid` (`uuid`),
  KEY `idx_discussion_message_session_id` (`session_id`),
  KEY `idx_discussion_message_sender_id` (`sender_id`),
  KEY `idx_discussion_message_message_type` (`message_type`),
  KEY `idx_discussion_message_created_at` (`created_at`),
  KEY `idx_discussion_message_session_id_created_at` (`session_id`, `created_at`),
  CONSTRAINT `fk_discussion_message_session` FOREIGN KEY (`session_id`) REFERENCES `collaboration_session` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_discussion_message_sender` FOREIGN KEY (`sender_id`) REFERENCES `agent_member` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='讨论消息表';

-- 协作结论表
CREATE TABLE `collaboration_conclusion` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '结论主键',
  `uuid` VARCHAR(36) NOT NULL COMMENT '全局唯一标识符',
  `session_id` BIGINT NOT NULL COMMENT '会话 ID',
  `title` VARCHAR(200) NOT NULL COMMENT '结论标题',
  `summary` TEXT NULL COMMENT '结论摘要',
  `content` LONGTEXT NOT NULL COMMENT '结论内容',
  `attachments` TEXT NULL COMMENT '附件列表',
  `action_items` TEXT NULL COMMENT '行动项',
  `is_approved` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否已批准',
  `approved_by` BIGINT NULL COMMENT '批准人 ID',
  `approved_at` DATETIME NULL COMMENT '批准时间',
  `status` VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT '状态',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_collaboration_conclusion_uuid` (`uuid`),
  KEY `idx_collaboration_conclusion_session_id` (`session_id`),
  KEY `idx_collaboration_conclusion_status` (`status`),
  KEY `idx_collaboration_conclusion_is_approved` (`is_approved`),
  KEY `idx_collaboration_conclusion_created_at` (`created_at`),
  CONSTRAINT `fk_collaboration_conclusion_session` FOREIGN KEY (`session_id`) REFERENCES `collaboration_session` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_collaboration_conclusion_approved_by` FOREIGN KEY (`approved_by`) REFERENCES `agent_member` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='协作结论表';

SET FOREIGN_KEY_CHECKS = 1;
```

#### V2__insert_initial_data.sql

```sql
-- ============================================
-- 插入初始数据
-- 版本: V2
-- 日期: 2026-03-23
-- ============================================

-- (这里插入第 7 节中的初始数据)
-- 初始分类数据、初始标签数据、初始系统配置、初始化管理员用户
```

### 8.2 Liquibase 迁移脚本（备选）

#### db.changelog-master.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog
    xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
    http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-4.0.xsd">

    <include file="db/changelog/changeset/V1__init_schema.xml"/>
    <include file="db/changelog/changeset/V2__insert_initial_data.xml"/>
</databaseChangeLog>
```

---

## 9. 性能优化

### 9.1 查询优化建议

#### 9.1.1 避免全表扫描

**优化前**：
```sql
SELECT * FROM content WHERE title LIKE '%AI%';
```

**优化后**：
```sql
-- 使用全文索引
SELECT * FROM content
WHERE MATCH(title, content) AGAINST('AI' IN BOOLEAN MODE);
```

#### 9.1.2 使用覆盖索引

**优化前**：
```sql
SELECT id, title, created_at FROM content WHERE user_id = ?;
```

**优化后**：
```sql
-- 创建覆盖索引
CREATE INDEX idx_content_user_id_cover ON content(user_id, title, created_at);
SELECT id, title, created_at FROM content WHERE user_id = ?;
```

#### 9.1.3 限制返回字段

**优化前**：
```sql
SELECT * FROM user WHERE id = ?;
```

**优化后**：
```sql
SELECT id, username, email, avatar_url FROM user WHERE id = ?;
```

#### 9.1.4 使用分页查询

**优化前**：
```sql
SELECT * FROM content ORDER BY created_at DESC LIMIT 1000;
```

**优化后**：
```sql
-- 使用游标分页
SELECT * FROM content
WHERE created_at < ?
ORDER BY created_at DESC
LIMIT 20;
```

#### 9.1.5 优化 JOIN 查询

**优化前**：
```sql
SELECT c.*, u.username
FROM content c
LEFT JOIN user u ON c.user_id = u.id
WHERE c.status = 'ACTIVE'
ORDER BY c.created_at DESC;
```

**优化后**：
```sql
-- 添加复合索引
CREATE INDEX idx_content_status_created_at ON content(status, created_at);
SELECT c.*, u.username
FROM content c
LEFT JOIN user u ON c.user_id = u.id
WHERE c.status = 'ACTIVE'
ORDER BY c.created_at DESC
LIMIT 20;
```

### 9.2 分表分库策略

#### 9.2.1 何时需要分表分库

**触发条件**：
- 单表数据量超过 1000 万行
- 单表数据文件超过 10GB
- 单库 QPS 超过 5000
- 慢查询频繁出现

#### 9.2.2 分表策略

**按时间分表**（适用于日志类数据）：
```sql
-- operation_log 表按月分表
operation_log_202601
operation_log_202602
operation_log_202603
...
```

**按用户 ID 分表**（适用于用户相关数据）：
```sql
-- 按 user_id % 10 分表
favorite_0
favorite_1
favorite_2
...
```

**分表路由规则**：
```java
// Java 伪代码
String getTableName(Long userId) {
    int tableIndex = (int) (userId % 10);
    return "favorite_" + tableIndex;
}
```

#### 9.2.3 分库策略

**按业务垂直拆分**：
```
ai-tide-user-db:     用户、权限相关表
ai-tide-content-db:  内容、互动相关表
ai-tide-agent-db:    智能体协作相关表
ai-tide-payment-db:  支付授权相关表
```

**按用户水平拆分**：
```
ai-tide-db-0: user_id % 4 = 0
ai-tide-db-1: user_id % 4 = 1
ai-tide-db-2: user_id % 4 = 2
ai-tide-db-3: user_id % 4 = 3
```

### 9.3 缓存策略

#### 9.3.1 Redis 缓存设计

**热点数据缓存**：
```
Key: user:{user_id}
Value: {用户信息}
TTL: 3600 秒

Key: content:{content_id}
Value: {内容信息}
TTL: 1800 秒

Key: config:{config_key}
Value: {配置值}
TTL: 7200 秒
```

**列表缓存**：
```
Key: content:list:{category_id}:{page}
Value: [内容列表]
TTL: 600 秒
```

#### 9.3.2 缓存更新策略

**Cache-Aside 模式**：
1. 读取时先查缓存，未命中再查数据库
2. 写入时先更新数据库，再删除缓存

**Write-Through 模式**（适用于高一致性要求）：
1. 写入时同时更新缓存和数据库
2. 使用事务保证原子性

### 9.4 慢查询优化

#### 9.4.1 开启慢查询日志

```sql
-- 开启慢查询
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 1;
SET GLOBAL log_queries_not_using_indexes = 'ON';
```

#### 9.4.2 分析慢查询

```sql
-- 查看慢查询日志
SHOW VARIABLES LIKE 'slow_query_log_file';

-- 使用 mysqldumpslow 分析
mysqldumpslow -s t -t 10 /var/log/mysql/slow-query.log
```

#### 9.4.3 使用 EXPLAIN 分析

```sql
EXPLAIN SELECT * FROM content
WHERE user_id = 123 AND status = 'ACTIVE'
ORDER BY created_at DESC;
```

**关注字段**：
- `type`: 访问类型（ALL 为全表扫描，需优化）
- `key`: 实际使用的索引
- `rows`: 预估扫描行数
- `Extra`: 额外信息（Using filesort, Using temporary 需优化）

---

## 10. 备份与恢复

### 10.1 备份策略

#### 10.1.1 全量备份

**使用 mysqldump**：
```bash
# 全量备份
mysqldump -u root -p \
  --single-transaction \
  --routines \
  --triggers \
  --events \
  --databases ai_tide > /backup/ai_tide_full_$(date +%Y%m%d).sql

# 压缩备份
mysqldump -u root -p \
  --single-transaction \
  --routines \
  --triggers \
  --events \
  --databases ai_tide | gzip > /backup/ai_tide_full_$(date +%Y%m%d).sql.gz
```

**使用 Percona XtraBackup**（适用于大型数据库）：
```bash
# 全量备份
xtrabackup --backup \
  --target-dir=/backup/xtrabackup_$(date +%Y%m%d) \
  --user=root --password=xxx

# 准备备份
xtrabackup --prepare --target-dir=/backup/xtrabackup_$(date +%Y%m%d)
```

#### 10.1.2 增量备份

**基于 LSN 的增量备份**：
```bash
# 全量备份
xtrabackup --backup --target-dir=/backup/base --user=root --password=xxx

# 增量备份
xtrabackup --backup \
  --target-dir=/backup/inc1 \
  --incremental-basedir=/backup/base \
  --user=root --password=xxx
```

#### 10.1.3 自动备份脚本

```bash
#!/bin/bash
# auto_backup.sh

BACKUP_DIR="/backup/mysql"
DATE=$(date +%Y%m%d)
RETENTION_DAYS=7

# 创建备份目录
mkdir -p ${BACKUP_DIR}

# 执行备份
mysqldump -u root -p${MYSQL_PASSWORD} \
  --single-transaction \
  --routines \
  --triggers \
  --events \
  --databases ai_tide | gzip > ${BACKUP_DIR}/ai_tide_${DATE}.sql.gz

# 删除过期备份
find ${BACKUP_DIR} -name "ai_tide_*.sql.gz" -mtime +${RETENTION_DAYS} -delete

# 记录日志
echo "Backup completed at $(date)" >> /var/log/backup.log
```

**设置定时任务**：
```bash
# 每天凌晨 2 点执行备份
0 2 * * * /scripts/auto_backup.sh
```

### 10.2 恢复方案

#### 10.2.1 从 SQL 文件恢复

```bash
# 解压并恢复
gunzip < /backup/ai_tide_20260323.sql.gz | mysql -u root -p ai_tide

# 直接恢复
mysql -u root -p ai_tide < /backup/ai_tide_20260323.sql
```

#### 10.2.2 从 XtraBackup 恢复

```bash
# 停止 MySQL
systemctl/mysql stop mysqld

# 恢复数据
xtrabackup --copy-back --target-dir=/backup/xtrabackup_20260323

# 修改权限
chown -R mysql:mysql /var/lib/mysql

# 启动 MySQL
systemctl/mysql start mysqld
```

#### 10.2.3 时间点恢复（PITR）

```bash
# 1. 恢复全量备份
mysql -u root -p ai_tide < /backup/ai_tide_full_20260323.sql

# 2. 应用二进制日志
mysqlbinlog --start-datetime="2026-03-23 10:00:00" \
            --stop-datetime="2026-03-23 12:00:00" \
            /var/log/mysql/mysql-bin.000123 | mysql -u root -p ai_tide
```

### 10.3 备份验证

#### 10.3.1 定期验证备份

```bash
#!/bin/bash
# verify_backup.sh

BACKUP_FILE=$1
TEST_DB="ai_tide_test"

# 创建测试数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS ${TEST_DB}"

# 恢复到测试数据库
gunzip < ${BACKUP_FILE} | mysql -u root -p ${TEST_DB}

# 验证数据完整性
mysql -u root -p ${TEST_DB} -e "
  SELECT COUNT(*) as user_count FROM user;
  SELECT COUNT(*) as content_count FROM content;
"

# 删除测试数据库
mysql -u root -p -e "DROP DATABASE ${TEST_DB}"
```

### 10.4 云备份方案

#### 10.4.1 云存储集成

**AWS S3**：
```bash
# 上传备份到 S3
aws s3 cp /backup/ai_tide_20260323.sql.gz \
  s3://ai-tide-backups/mysql/ai_tide_20260323.sql.gz
```

**阿里云 OSS**：
```bash
# 上传备份到 OSS
ossutil cp /backup/ai_tide_20260323.sql.gz \
  oss://ai-tide-backups/mysql/ai_tide_20260323.sql.gz
```

#### 10.4.2 备份策略建议

| 备份类型 | 频率 | 保留时间 | 存储位置 |
|----------|------|----------|----------|
| 全量备份 | 每天 | 7 天 | 本地 + 云 |
| 增量备份 | 每小时 | 1 天 | 本地 |
| 归档备份 | 每周 | 永久 | 云 |

---

## 11. 安全考虑

### 11.1 敏感数据加密

#### 11.1.1 密码存储

**使用 BCrypt 哈希**：
```java
// Java 示例
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
String hashedPassword = encoder.encode("plainPassword");

// 验证密码
boolean matches = encoder.matches("plainPassword", hashedPassword);
```

**数据库字段**：
- `user.password_hash`: 存储哈希后的密码
- 永远不要存储明文密码

#### 11.1.2 个人信息加密

**使用 AES 加密**：
```java
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtil {
    private static final String ALGORITHM = "AES";
    private static final String KEY = "your-secret-key-32";

    public static String encrypt(String data) {
        // 加密逻辑
    }

    public static String decrypt(String encryptedData) {
        // 解密逻辑
    }
}
```

**加密字段**：
- `user.phone`: 手机号加密存储
- `user.email`: 邮箱加密存储（可选）
- `payment_order.client_ip`: IP 地址加密存储

### 11.2 访问控制

#### 11.2.1 数据库用户权限

**创建专用用户**：
```sql
-- 应用用户（读写）
CREATE USER 'ai_tide_app'@'localhost' IDENTIFIED BY 'strong_password';
GRANT SELECT, INSERT, UPDATE, DELETE ON ai_tide.* TO 'ai_tide_app'@'localhost';

-- 只读用户（报表）
CREATE USER 'ai_tide_readonly'@'localhost' IDENTIFIED BY 'strong_password';
GRANT SELECT ON ai_tide.* TO 'ai_tide_readonly'@'localhost';

-- 管理用户（备份）
CREATE USER 'ai_tide_admin'@'localhost' IDENTIFIED BY 'strong_password';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, INDEX, ALTER ON ai_tide.* TO 'ai_tide_admin'@'localhost';
```

#### 11.2.2 应用层权限控制

**基于角色的访问控制（RBAC）**：
```java
// 检查用户权限
public boolean hasPermission(User user, String resource, String action) {
    Role role = user.getRole();
    return role.hasPermission(resource, action);
}

// 使用示例
if (!hasPermission(currentUser, "content", "delete")) {
    throw new AccessDeniedException("没有删除权限");
}
```

**行级安全（RLS）**：
```java
// 查询时自动过滤用户数据
public List<Content> getUserContents(Long userId) {
    return contentRepository.findByUserId(userId);
}
```

### 11.3 SQL 注入防护

#### 11.3.1 使用参数化查询

**错误示例**：
```java
String sql = "SELECT * FROM user WHERE username = '" + username + "'";
Statement stmt = connection.createStatement();
ResultSet rs = stmt.executeQuery(sql); // 不安全
```

**正确示例**：
```java
String sql = "SELECT * FROM user WHERE username = ?";
PreparedStatement pstmt = connection.prepareStatement(sql);
pstmt.setString(1, username);
ResultSet rs = pstmt.executeQuery(); // 安全
```

#### 11.3.2 使用 ORM 框架

**Spring Data JPA**：
```java
@Query("SELECT u FROM User u WHERE u.username = :username")
User findByUsername(@Param("username") String username);
```

**MyBatis**：
```xml
<select id="findByUsername" resultType="User">
  SELECT * FROM user WHERE username = #{username}
</select>
```

### 11.4 数据脱敏

#### 11.4.1 日志脱敏

```java
public class LogSanitizer {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\\b");
    private static final Pattern PHONE_PATTERN = Pattern.compile("\\b1[3-9]\\d{9}\\b");

    public static String sanitize(String log) {
        // 脱敏邮箱
        log = EMAIL_PATTERN.matcher(log).replaceAll("xxx@xxx.xxx");
        // 脱敏手机号
        log = PHONE_PATTERN.matcher(log).replaceAll("1xxxxxxxxx");
        return log;
    }
}
```

#### 11.4.2 查询结果脱敏

```java
public class UserDTO {
    private String username;
    private String email; // 脱敏：xxx@xxx.xxx
    private String phone; // 脱敏：1xxxxxxxxx
    // 其他字段
}
```

### 11.5 审计日志

#### 11.5.1 记录敏感操作

```java
@Aspect
@Component
public class AuditAspect {

    @Around("@annotation(auditable)")
    public Object audit(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        // 记录操作前状态
        String operation = auditable.operation();
        Object[] args = joinPoint.getArgs();

        // 执行操作
        Object result = joinPoint.proceed();

        // 记录操作后状态
        OperationLog log = new OperationLog();
        log.setOperationType(operation);
        log.setUserId(getCurrentUserId());
        log.setDescription(auditable.description());
        operationLogRepository.save(log);

        return result;
    }
}
```

#### 11.5.2 定期审计

```sql
-- 查询敏感操作
SELECT * FROM operation_log
WHERE operation_type IN ('DELETE', 'UPDATE', 'GRANT', 'REVOKE')
  AND created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)
ORDER BY created_at DESC;

-- 查询异常操作
SELECT * FROM operation_log
WHERE response_status >= 400
  AND created_at >= DATE_SUB(NOW(), INTERVAL 1 DAY)
ORDER BY created_at DESC;
```

---

## 附录

### A. 数据库字典

| 表名 | 说明 | 记录数预估 | 增长速度 |
|------|------|------------|----------|
| user | 用户表 | 10,000 | 慢 |
| operation_log | 操作日志 | 1,000,000 | 快 |
| content | 内容表 | 100,000 | 中 |
| category | 分类表 | 100 | 慢 |
| tag | 标签表 | 1,000 | 慢 |
| content_tag | 内容标签关联 | 300,000 | 中 |
| related_link | 相关链接 | 50,000 | 中 |
| comment | 评论表 | 500,000 | 快 |
| favorite | 收藏表 | 200,000 | 中 |
| like | 点赞表 | 1,000,000 | 快 |
| rating | 评分表 | 100,000 | 中 |
| system_config | 系统配置 | 50 | 慢 |
| paid_product | 付费产品 | 10 | 慢 |
| product_license | 产品授权 | 10,000 | 中 |
| payment_order | 支付订单 | 50,000 | 中 |
| agent_team | 智能体团队 | 1,000 | 慢 |
| agent_member | 智能体成员 | 5,000 | 中 |
| collaboration_session | 协作会话 | 10,000 | 中 |
| discussion_message | 讨论消息 | 1,000,000 | 快 |
| collaboration_conclusion | 协作结论 | 20,000 | 中 |

### B. 常用 SQL 示例

#### B.1 用户查询

```sql
-- 查询用户列表（分页）
SELECT id, username, email, avatar_url, status, created_at
FROM user
WHERE status = 'ACTIVE'
ORDER BY created_at DESC
LIMIT 20 OFFSET 0;

-- 查询用户详情
SELECT * FROM user WHERE uuid = ?;

-- 更新用户信息
UPDATE user
SET nickname = ?, bio = ?, avatar_url = ?, updated_at = NOW()
WHERE id = ?;
```

#### B.2 内容查询

```sql
-- 查询内容列表
SELECT c.*, u.username as author_name, cat.name as category_name
FROM content c
LEFT JOIN user u ON c.user_id = u.id
LEFT JOIN category cat ON c.category_id = cat.id
WHERE c.status = 'ACTIVE' AND c.is_published = TRUE
ORDER BY c.created_at DESC
LIMIT 20 OFFSET 0;

-- 全文搜索
SELECT c.*, MATCH(c.title, c.content) AGAINST('人工智能' IN BOOLEAN MODE) as score
FROM content c
WHERE c.status = 'ACTIVE' AND c.is_published = TRUE
  AND MATCH(c.title, c.content) AGAINST('人工智能' IN BOOLEAN MODE)
ORDER BY score DESC;

-- 查询用户的内容
SELECT * FROM content
WHERE user_id = ? AND status = 'ACTIVE'
ORDER BY created_at DESC;
```

#### B.3 互动统计

```sql
-- 统计内容互动数据
SELECT
  c.id,
  c.title,
  c.view_count,
  c.like_count,
  c.comment_count,
  c.favorite_count,
  c.rating_average
FROM content c
WHERE c.id = ?;

-- 更新点赞数
UPDATE content
SET like_count = (
  SELECT COUNT(*) FROM like WHERE content_id = ? AND status = 'ACTIVE'
)
WHERE id = ?;

-- 更新平均评分
UPDATE content
SET rating_average = (
  SELECT AVG(score) FROM rating WHERE content_id = ? AND status = 'ACTIVE'
),
rating_count = (
  SELECT COUNT(*) FROM rating WHERE content_id = ? AND status = 'ACTIVE'
)
WHERE id = ?;
```

### C. 性能监控指标

#### C.1 数据库性能指标

| 指标 | 说明 | 正常值 | 告警阈值 |
|------|------|--------|----------|
| QPS | 每秒查询数 | < 1000 | > 5000 |
| TPS | 每秒事务数 | < 100 | > 500 |
| 慢查询数 | 慢查询数量 | < 10 | > 50 |
| 连接数 | 活动连接数 | < 100 | > 300 |
| 缓存命中率 | 查询缓存命中率 | > 90% | < 80% |
| 磁盘 I/O | 磁盘读写速度 | < 50% | > 80% |

#### C.2 监控 SQL

```sql
-- 查看 InnoDB 状态
SHOW ENGINE INNODB STATUS;

-- 查看连接数
SHOW STATUS LIKE 'Threads_connected';

-- 查看查询统计
SHOW STATUS LIKE 'Com_select';
SHOW STATUS LIKE 'Com_insert';
SHOW STATUS LIKE 'Com_update';
SHOW STATUS LIKE 'Com_delete';

-- 查看表大小
SELECT
  table_name,
  ROUND(((data_length + index_length) / 1024 / 1024), 2) AS size_mb
FROM information_schema.tables
WHERE table_schema = 'ai_tide'
ORDER BY (data_length + index_length) DESC;
```

---

**文档结束**

*本文档由 AI-Tide Database Team 编写，版本 v1.0.0，最后更新于 2026-03-23*
