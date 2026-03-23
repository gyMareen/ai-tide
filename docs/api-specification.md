# AI-Tide API 接口设计文档

## 1. 文档信息

| 项目 | 内容 |
|------|------|
| 文档版本 | v1.0.0 |
| 创建日期 | 2026-03-24 |
| 文档状态 | 正式版 |
| 作者 | 技术团队 |
| 项目名称 | AI-Tide (AI 前沿技术展示平台) |
| 文档密级 | 内部 |

---

## 2. 概述

### 2.1 基础信息

- **API 风格**：RESTful
- **协议**：HTTPS
- **数据格式**：JSON
- **响应编码**：UTF-8
- **基础 URL**：`https://api.ai-tide.com/api/v1`

### 2.2 认证机制

系统使用 JWT（JSON Web Token）进行身份认证。

#### 认证流程

1. 用户登录成功后，服务器返回 JWT Token
2. 客户端在后续请求的 HTTP Header 中携带 Token
3. 服务器验证 Token 有效性并提取用户信息

#### 请求头格式

```
Authorization: Bearer {token}
```

#### Token 格式

```json
{
  "userId": "123",
  "username": "johndoe",
  "role": "USER",
  "exp": 1711234567,
  "iat": 1711234567
}
```

#### Token 有效期

- 默认有效期：24 小时
- 记住我功能：7 天

### 2.3 统一响应格式

#### 成功响应

```json
{
  "code": 200,
  "message": "success",
  "data": {
    // 响应数据
  },
  "timestamp": 1711234567890
}
```

#### 分页响应

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [],
    "total": 100,
    "page": 1,
    "size": 10,
    "totalPages": 10
  },
  "timestamp": 1711234567890
}
```

#### 错误响应

```json
{
  "code": 400,
  "message": "请求参数错误",
  "error": {
    "field": "username",
    "message": "用户名长度必须在 3-20 个字符之间"
  },
  "timestamp": 1711234567890
}
```

### 2.4 错误码定义

| 错误码 | 说明 | HTTP 状态码 |
|--------|------|-------------|
| 200 | 请求成功 | 200 |
| 400 | 请求参数错误 | 400 |
| 401 | 未认证 | 401 |
| 403 | 权限不足 | 403 |
| 404 | 资源不存在 | 404 |
| 409 | 资源冲突 | 409 |
| 429 | 请求过于频繁 | 429 |
| 500 | 服务器内部错误 | 500 |
| 503 | 服务暂时不可用 | 503 |

#### 业务错误码

| 错误码 | 说明 |
|--------|------|
| 1001 | 用户名已存在 |
| 1002 | 邮箱已存在 |
| 1003 | 用户名或密码错误 |
| 1004 | 账号已被禁用 |
| 1005 | 密码错误次数过多，账号已锁定 |
| 1006 | 验证码错误 |
| 1007 | 验证码已过期 |
| 1008 | Token 无效或已过期 |
| 2001 | 内容不存在 |
| 2002 | 内容已删除 |
| 2003 | 内容状态异常 |
| 2004 | 已收藏过该内容 |
| 2005 | 已点赞过该内容 |
| 2006 | 已评过分该内容 |
| 3001 | 评论不存在 |
| 3002 | 评论内容长度超限 |
| 3003 | 不允许删除他人评论 |
| 4001 | 分类不存在 |
| 4002 | 分类已存在 |
| 4003 | 分类不能删除（有子分类或内容） |
| 5001 | 标签不存在 |
| 5002 | 标签已存在 |
| 6001 | 产品不存在 |
| 6002 | 产品未上架 |
| 6003 | 已授权 |
| 6004 | 未授权 |
| 6005 | 授权已撤销 |
| 7001 | 智能体团队不存在 |
| 7002 | 智能体成员不存在 |
| 7003 | 协作会话不存在 |

### 2.5 通用参数

#### 分页参数

| 参数名 | 类型 | 必填 | 说明 | 默认值 |
|--------|------|------|------|--------|
| page | Integer | 否 | 页码，从 1 开始 | 1 |
| size | Integer | 否 | 每页数量，最大 100 | 10 |

#### 排序参数

| 参数名 | 类型 | 必填 | 说明 | 默认值 |
|--------|------|------|------|--------|
| sortBy | String | 否 | 排序字段 | id |
| sortOrder | String | 否 | 排序方向：asc/desc | desc |

#### 时间筛选参数

| 参数名 | 类型 | 必填 | 说明 | 默认值 |
|--------|------|------|------|--------|
| startTime | String | 否 | 开始时间（ISO 8601） | - |
| endTime | String | 否 | 结束时间（ISO 8601） | - |

---

## 3. 用户模块 API

### 3.1 用户注册

#### 接口描述

新用户通过注册流程创建账号。

#### 请求信息

- **请求方法**：`POST`
- **请求 URL**：`/api/v1/auth/register`
- **是否需要认证**：否

#### 请求参数

```json
{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "Password123",
  "confirmPassword": "Password123",
  "captcha": "abc123",
  "agreeTerms": true
}
```

| 参数名 | 类型 | 必填 | 说明 | 验证规则 |
|--------|------|------|------|----------|
| username | String | 是 | 用户名 | 3-20 字符，字母数字下划线 |
| email | String | 是 | 邮箱 | 有效邮箱格式 |
| password | String | 是 | 密码 | 8-32 字符，包含大小写字母和数字 |
| confirmPassword | String | 是 | 确认密码 | 必须与密码一致 |
| captcha | String | 是 | 验证码 | 图形验证码 |
| agreeTerms | Boolean | 是 | 同意用户协议 | 必须为 true |

#### 请求示例

```bash
curl -X POST https://api.ai-tide.com/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "password": "Password123",
    "confirmPassword": "Password123",
    "captcha": "abc123",
    "agreeTerms": true
  }'
```

#### 响应示例

**成功响应**

```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "userId": 123,
    "username": "johndoe",
    "email": "john@example.com",
    "nickname": null,
    "avatar": null,
    "role": "USER",
    "enabled": true,
    "registerTime": "2026-03-24T10:00:00Z"
  },
  "timestamp": 1711234567890
}
```

**错误响应**

```json
{
  "code": 1001,
  "message": "用户名已存在",
  "error": {
    "field": "username",
    "message": "用户名 johndoe 已被注册"
  },
  "timestamp": 1711234567890
}
```

#### 错误码

- `1001` - 用户名已存在
- `1002` - 邮箱已存在
- `1006` - 验证码错误
- `1007` - 验证码已过期
- `400` - 请求参数错误

---

### 3.2 用户登录

#### 接口描述

已注册用户通过登录流程访问平台。

#### 请求信息

- **请求方法**：`POST`
- **请求 URL**：`/api/v1/auth/login`
- **是否需要认证**：否

#### 请求参数

```json
{
  "account": "johndoe",
  "password": "Password123",
  "rememberMe": true
}
```

| 参数名 | 类型 | 必填 | 说明 | 默认值 |
|--------|------|------|------|--------|
| account | String | 是 | 用户名或邮箱 | - |
| password | String | 是 | 密码 | - |
| rememberMe | Boolean | 否 | 记住我（7 天） | false |

#### 请求示例

```bash
curl -X POST https://api.ai-tide.com/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "account": "johndoe",
    "password": "Password123",
    "rememberMe": true
  }'
```

#### 响应示例

**成功响应**

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "userId": 123,
      "username": "johndoe",
      "email": "john@example.com",
      "nickname": "John",
      "avatar": "https://cdn.ai-tide.com/avatars/123.jpg",
      "bio": "AI enthusiast",
      "role": "USER",
      "enabled": true,
      "registerTime": "2026-03-24T10:00:00Z",
      "lastLoginTime": "2026-03-24T12:00:00Z"
    }
  },
  "timestamp": 1711234567890
}
```

**错误响应**

```json
{
  "code": 1003,
  "message": "用户名或密码错误",
  "error": null,
  "timestamp": 1711234567890
}
```

#### 错误码

- `1003` - 用户名或密码错误
- `1004` - 账号已被禁用
- `1005` - 密码错误次数过多，账号已锁定
- `400` - 请求参数错误

---

### 3.3 找回密码 - 发送重置邮件

#### 接口描述

用户忘记密码时，通过邮箱验证重置密码。

#### 请求信息

- **请求方法**：`POST`
- **请求 URL**：`/api/v1/auth/forgot-password`
- **是否需要认证**：否

#### 请求参数

```json
{
  "email": "john@example.com"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| email | String | 是 | 注册邮箱 |

#### 请求示例

```bash
curl -X POST https://api.ai-tide.com/api/v1/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com"
  }'
```

#### 响应示例

**成功响应**

```json
{
  "code": 200,
  "message": "重置密码邮件已发送",
  "data": null,
  "timestamp": 1711234567890
}
```

---

### 3.4 找回密码 - 重置密码

#### 接口描述

使用邮件中的 Token 重置密码。

#### 请求信息

- **请求方法**：`POST`
- **请求 URL**：`/api/v1/auth/reset-password`
- **是否需要认证**：否

#### 请求参数

```json
{
  "token": "reset_token_abc123",
  "password": "NewPassword123",
  "confirmPassword": "NewPassword123"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| token | String | 是 | 重置 Token（邮件中的链接参数） |
| password | String | 是 | 新密码 |
| confirmPassword | String | 是 | 确认新密码 |

#### 请求示例

```bash
curl -X POST https://api.ai-tide.com/api/v1/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "token": "reset_token_abc123",
    "password": "NewPassword123",
    "confirmPassword": "NewPassword123"
  }'
```

#### 响应示例

**成功响应**

```json
{
  "code": 200,
  "message": "密码重置成功",
  "data": null,
  "timestamp": 1711234567890
}
```

#### 错误码

- `1008` - Token 无效或已过期
- `400` - 请求参数错误

---

### 3.5 获取当前用户信息

#### 接口描述

获取当前登录用户的详细信息。

#### 请求信息

- **请求方法**：`GET`
- **请求 URL**：`/api/v1/user/me`
- **是否需要认证**：是

#### 请求示例

```bash
curl -X GET https://api.ai-tide.com/api/v1/user/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 123,
    "username": "johndoe",
    "email": "john@example.com",
    "nickname": "John",
    "avatar": "https://cdn.ai-tide.com/avatars/123.jpg",
    "bio": "AI enthusiast",
    "role": "USER",
    "enabled": true,
    "registerTime": "2026-03-24T10:00:00Z",
    "lastLoginTime": "2026-03-24T1212:00:00Z",
    "lastLoginIp": "192.168.1.1",
    "stats": {
      "favoriteCount": 10,
      "commentCount": 5,
      "likeCount": 20
    }
  },
  "timestamp": 1711234567890
}
```

#### 错误码

- `401` - 未认证
- `1008` - Token 无效或已过期

---

### 3.6 更新用户信息

#### 接口描述

更新当前用户的个人信息。

#### 请求信息

- **请求方法**：`PUT`
- **请求 URL**：`/api/v1/user/me`
- **是否需要认证**：是

#### 请求参数

```json
{
  "nickname": "John Doe",
  "bio": "AI and ML enthusiast",
  "avatar": "https://cdn.ai-tide.com/avatars/123-updated.jpg"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| nickname | String | 否 | 昵称（最大 50 字符） |
| bio | String | 否 | 个人简介（最大 500 字符） |
| avatar | String | 否 | 头像 URL |

#### 请求示例

```bash
curl -X PUT https://api.ai-tide.com/api/v1/user/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "nickname": "John Doe",
    "bio": "AI and ML enthusiast"
  }'
```

#### 响应示例

```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "userId": 123,
    "username": "johndoe",
    "email": "john@example.com",
    "nickname": "John Doe",
    "avatar": "https://cdn.ai-tide.com/avatars/123.jpg",
    "bio": "AI and ML enthusiast",
    "role": "USER",
    "enabled": true,
    "registerTime": "2026-03-24T10:00:00Z",
    "lastLoginTime": "2026-03-24T12:00:00Z"
  },
  "timestamp": 1711234567890
}
```

---

### 3.7 修改密码

#### 接口描述

修改当前用户的密码。

#### 请求信息

- **请求方法**：`POST`
- **请求 URL**：`/api/v1/user/change-password`
- **是否需要认证**：是

#### 请求参数

```json
{
  "currentPassword": "Password123",
  "newPassword": "NewPassword456",
  "confirmPassword": "NewPassword456"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| currentPassword | String | 是 | 当前密码 |
| newPassword | String | 是 | 新密码 |
| confirmPassword | String | 是 | 确认新密码 |

#### 请求示例

```bash
curl -X POST https://api.ai-tide.com/api/v1/user/change-password \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "currentPassword": "Password123",
    "newPassword": "NewPassword456",
    "confirmPassword": "NewPassword456"
  }'
```

#### 响应示例

```json
{
  "code": 200,
  "message": "密码修改成功",
  "data": null,
  "timestamp": 1711234567890
}
```

#### 错误码

- `1003` - 当前密码错误
- `400` - 请求参数错误

---

### 3.8 上传头像

#### 接口描述

上传用户头像图片。

#### 请求信息

- **请求方法**：`POST`
- **请求 URL**：`/api/v1/user/upload-avatar`
- **是否需要认证**：是
- **Content-Type**：`multipart/form-data`

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 | 限制 |
|--------|------|------|------|------|
| file | File | 是 | 头像图片文件 | JPG/PNG，最大 2MB |

#### 请求示例

```bash
curl -X POST https://api.ai-tide.com/api/v1/user/upload-avatar \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -F "file=@/path/to/avatar.jpg"
```

#### 响应示例

```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "avatarUrl": "https://cdn.ai-tide.com/avatars/123.jpg"
  },
  "timestamp": 1711234567890
}
```

---

## 4. 内容模块 API

### 4.1 获取内容列表

#### 接口描述

获取平台内容列表，支持分页、筛选和排序。

#### 请求信息

- **请求方法**：`GET`
- **请求 URL**：`/api/v1/contents`
- **是否需要认证**：否

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 | 默认值 |
|--------|------|------|------|--------|
| page | Integer | 否 | 页码 | 1 |
| size | Integer | 否 | 每页数量 | 10 |
| type | String | 否 | 内容类型：MODEL/PRODUCT/ARTICLE | - |
| categoryId | Long | 否 | 分类 ID | - |
| tags | String | 否 | 标签（逗号分隔） | - |
| sortBy | String | 否 | 排序字段：publishTime/viewCount/likeCount | publishTime |
| sortOrder | String | 否 | 排序方向：asc/desc | desc |

#### 请求示例

```bash
curl -X GET "https://api.ai-tide.com/api/v1/contents?page=1&size=10&type=MODEL&sortBy=publishTime&sortOrder=desc"
```

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "contentId": 1,
        "title": "GPT-4",
        "description": "OpenAI 最新大语言模型",
        "coverImage": "https://cdn.ai-tide.com/covers/1.jpg",
        "type": "MODEL",
        "category": {
          "categoryId": 1,
          "name": "语言模型",
          "parentId": null
        },
        "tags": [
          {
            "tagId": 1,
            "name": "LLM"
          },
          {
            "tagId": 2,
            "name": "OpenAI"
          }
        ],
        "author": {
          "userId": 1,
          "username": "admin",
          "nickname": "管理员",
          "avatar": "https://cdn.ai-tide.com/avatars/1.jpg"
        },
        "publishTime": "2026-03-24T10:00:00Z",
        "updateTime": "2026-03-24T10:00:00Z",
        "viewCount": 1000,
        "likeCount": 50,
        "favoriteCount": 30,
        "commentCount": 10,
        "averageRating": 4.5
      }
    ],
    "total": 100,
    "page": 1,
    "size": 10,
    "totalPages": 10
  },
  "timestamp": 1711234567890
}
```

---

### 4.2 获取内容详情

#### 接口描述

获取单个内容的详细信息。

#### 请求信息

- **请求方法**：`GET`
- **请求 URL**：`/api/v1/contents/{contentId}`
- **是否需要认证**：否

#### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| contentId | Long | 是 | 内容 ID |

#### 请求示例

```bash
curl -X GET https://api.ai-tide.com/api/v1/contents/1
```

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "contentId": 1,
    "title": "GPT-4",
    "description": "OpenAI 最新大语言模型",
    "content": "# GPT-4 介绍\n\nGPT-4 是 OpenAI 发布的最新大语言模型...",
    "coverImage": "https://cdn.ai-tide.com/covers/1.jpg",
    "type": "MODEL",
    "category": {
      "categoryId": 1,
      "name": "语言模型",
      "parentId": null
    },
    "tags": [
      {
        "tagId": 1,
        "name": "LLM"
      }
    ],
    "officialUrl": "https://openai.com/gpt-4",
    "relatedLinks": [
      {
        "title": "GitHub",
        "url": "https://github.com/openai/gpt-4"
      }
    ],
    "author": {
      "userId": 1,
      "username": "admin",
      "nickname": "管理员",
      "avatar": "https://cdn.ai-tide.com/avatars/1.jpg"
    },
    "publishTime": "2026-03-24T10:00:00Z",
    "updateTime": "2026-03-24T10:00:00Z",
    "viewCount": 1000,
    "likeCount": 50,
    "favoriteCount": 30,
    "commentCount": 10,
    "averageRating": 4.5,
    "isFavorited": false,
    "isLiked": false,
    "userRating": null
  },
  "timestamp": 1711234567890
}
```

#### 错误码

- `2001` - 内容不存在
- `2002` - 内容已删除

---

### 4.3 内容搜索

#### 接口描述

根据关键词搜索内容。

#### 请求信息

- **请求方法**：`GET`
- **请求 URL**：`/api/v1/contents/search`
- **是否需要认证**：否

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 | 默认值 |
|--------|------|------|------|--------|
| keyword | String | 是 | 搜索关键词 | - |
| page | Integer | 否 | 页码 | 1 |
| size | Integer | 否 | 每页数量 | 10 |
| type | String | 否 | 内容类型 | - |
| categoryId | Long | 否 | 分类 ID | - |
| tags | String | 否 | 标签 | - |
| sortBy | String | 否 | 排序字段：relevance/publishTime/viewCount | relevance |
| sortOrder | String | 否 | 排序方向：asc/desc | desc |

#### 请求示例

```bash
curl -X GET "https://api.ai-tide.com/api/v1/contents/search?keyword=GPT-4&page=1&size=10"
```

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "contentId": 1,
        "title": "GPT-4",
        "description": "OpenAI 最新大语言模型",
        "coverImage": "https://cdn.ai-tide.com/covers/1.jpg",
        "type": "MODEL",
        "category": {
          "categoryId": 1,
          "name": "语言模型"
        },
        "publishTime": "2026-03-24T10:00:00Z",
        "viewCount": 1000,
        "likeCount": 50,
        "averageRating": 4.5
      }
    ],
    "total": 10,
    "page": 1,
    "size": 10,
    "totalPages": 1
  },
  "timestamp": 1711234567890
}
```

---

### 4.4 获取分类内容

#### 接口描述

获取指定分类下的内容列表。

#### 请求信息

- **请求方法**：`GET`
- **请求 URL**：`/api/v1/contents/category/{categoryId}`
- **是否需要认证**：否

#### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| categoryId | Long | 是 | 分类 ID |

#### 查询参数

| 参数名 | 类型 | 必填 | 说明 | 默认值 |
|--------|------|------|------|--------|
| page | Integer | 否 | 页码 | 1 |
| size | Integer | 否 | 每页数量 | 10 |
| sortBy | String | 否 | 排序字段 | publishTime |
| sortOrder | String | 否 | 排序方向 | desc |

#### 请求示例

```bash
curl -X GET "https://api.ai-tide.com/api/v1/contents/category/1?page=1&size=10"
```

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [],
    "total": 50,
    "page": 1,
    "size": 10,
    "totalPages": 5
  },
  "timestamp": 1711234567890
}
```

---

### 4.5 获取热门内容

#### 接口描述

获取热门内容列表（按综合热度排序）。

#### 请求信息

- **请求方法**：`GET`
- **请求 URL**：`/api/v1/contents/hot`
- **是否需要认证**：否

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 | 默认值 |
|--------|------|------|------|--------|
| page | Integer | 否 | 页码 | 1 |
| size | Integer | 否 | 每页数量 | 12 |

#### 请求示例

```bash
curl -X GET "https://api.ai-tide.com/api/v1/contents/hot?page=1&size=12"
```

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [],
    "total": 50,
    "page": 1,
    "size": 12,
    "totalPages": 5
  },
  "timestamp": 1711234567890
}
```

---

### 4.6 获取最新内容

#### 接口描述

获取最新发布的内容列表。

#### 请求信息

- **请求方法**：`GET`
- **请求 URL**：`/api/v1/contents/latest`
- **是否需要认证**：否

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 | 默认值 |
|--------|------|------|------|--------|
| page | Integer | 否 | 页码 | 1 |
| size | Integer | 否 | 每页数量 | 12 |

#### 请求示例

```bash
curl -X GET "https://api.ai-tide.com/api/v1/contents/latest?page=1&size=12"
```

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [],
    "total": 50,
    "page": 1,
    "size": 12,
    "totalPages": 5
  },
  "timestamp": 1711234567890
}
```

---

## 5. 互动模块 API

### 5.1 收藏内容

#### 接口描述

收藏或取消收藏内容。

#### 请求信息

- **请求方法**：`POST`
- **请求 URL**：`/api/v1/interactions/favorites/{contentId}`
- **是否需要认证**：是

#### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| contentId | Long | 是 | 内容 ID |

#### 请求示例

```bash
curl -X POST https://api.ai-tide.com/api/v1/interactions/favorites/1 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

#### 响应示例

```json
{
  "code": 200,
  "message": "收藏成功",
  "data": {
    "isFavorited": true,
    "favoriteCount": 31
  },
  "timestamp": 1711234567890
}
```

#### 错误码

- `2001` - 内容不存在
- `2002` - 内容已删除

---

### 5.2 获取收藏列表

#### 接口描述

获取当前用户的收藏列表。

#### 请求信息

- **请求方法**：`GET`
- **请求 URL**：`/api/v1/interactions/favorites`
- **是否需要认证**：是

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 | 默认值 |
|--------|------|------|------|--------|
| page | Integer | 否 | 页码 | 1 |
| size | Integer | 否 | 每页数量 | 10 |

#### 请求示例

```bash
curl -X GET "https://api.ai-tide.com/api/v1/interactions/favorites?page=1&size=10" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "favoriteId": 1,
        "content": {
          "contentId": 1,
          "title": "GPT-4",
          "description": "OpenAI 最新大语言模型",
          "coverImage": "https://cdn.ai-tide.com/covers/1.jpg"
        },
        "favoriteTime": "2026-03-24T12:00:00Z"
      }
    ],
    "total": 10,
    "page": 1,
    "size": 10,
    "totalPages": 1
  },
  "timestamp": 1711234567890
}
```

---

### 5.3 发表评论

#### 接口描述

对内容发表评论。

#### 请求信息

- **请求方法**：`POST`
- **请求 URL**：`/api/v1/interactions/comments`
- **是否需要认证**：是

#### 请求参数

```json
{
  "contentId": 1,
  "content": "这个模型真的很强大！"
}
```

| 参数名 | 类型 | 必填 | 说明 | 限制 |
|--------|------|------|------|------|
| contentId | Long | 是 | 内容 ID | - |
| content | String | 是 | 评论内容 | 1-500 字符 |

#### 请求示例

```bash
curl -X POST https://api.ai-tide.com/api/v1/interactions/comments \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "contentId": 1,
    "content": "这个模型真的很强大！"
  }'
```

#### 响应示例

```json
{
  "code": 200,
  "message": "评论成功",
  "data": {
    "commentId": 100,
    "content": "这个模型真的很强大！",
    "publishTime": "2026-03-24T12:00:00Z",
    "likeCount": 0,
    "user": {
      "userId": 123,
      "username": "johndoe",
      "nickname": "John",
      "avatar": "https://cdn.ai-tide.com/avatars/123.jpg"
    }
  },
  "timestamp": 1711234567890
}
```

#### 错误码

- `2001` - 内容不存在
- `3002` - 评论内容长度超限

---

### 5.4 获取评论列表

#### 接口描述

获取指定内容的评论列表。

#### 请求信息

- **请求方法**：`GET`
- **请求 URL**：`/api/v1/interactions/comments/{contentId}`
- **是否需要认证**：否

#### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| contentId | Long | 是 | 内容 ID |

#### 查询参数

| 参数名 | 类型 | 必填 | 说明 | 默认值 |
|--------|------|------|------|--------|
| page | Integer | 否 | 页码 | 1 |
| size | Integer | 否 | 每页数量 | 10 |

#### 请求示例

```bash
curl -X GET "https://api.ai-tide.com/api/v1/interactions/comments/1?page=1&size=10"
```

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "commentId": 100,
        "content": "这个模型真的很强大！",
        "publishTime": "2026-03-24T12:00:00Z",
        "likeCount": 0,
        "user": {
          "userId": 123,
          "username": "johndoe",
          "nickname": "John",
          "avatar": "https://cdn.ai-tide.com/avatars/123.jpg"
        }
      }
    ],
    "total": 10,
    "page": 1,
    "size": 10,
    "totalPages": 1
  },
  "timestamp": 1711234567890
}
```

---

### 5.5 删除评论

#### 接口描述

删除评论（只能删除自己的评论）。

#### 请求信息

- **请求方法**：`DELETE`
- **请求 URL**：`/api/v1/interactions/comments/{commentId}`
- **是否需要认证**：是

#### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| commentId | Long | 是 | 评论 ID |

#### 请求示例

```bash
curl -X DELETE https://api.ai-tide.com/api/v1/interactions/comments/100 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

#### 响应示例

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null,
  "timestamp": 1711234567890
}
```

#### 错误码

- `3001` - 评论不存在
- `3003` - 不允许删除他人评论

---

### 5.6 点赞内容

#### 接口描述

点赞或取消点赞内容。

#### 请求信息

- **请求方法**：`POST`
- **请求 URL**：`/api/v1/interactions/likes/{contentId}`
- **是否需要认证**：是

#### 起源参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| contentId | Long | 是 | 内容 ID |

#### 请求示例

```bash
curl -X POST https://api.ai-tide.com/api/v1/interactions/likes/1 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

#### 响应示例

```json
{
  "code": 200,
  "message": "点赞成功",
  "data": {
    "isLiked": true,
    "likeCount": 51
  },
  "timestamp": 1711234567890
}
```

#### 错误码

- `2001` - 内容不存在
- `2002` - 内容已删除

---

### 5.7 内容评分

#### 接口描述

对内容进行 1-5 星评分。

#### 请求信息

- **请求方法**：`POST`
- **请求 URL**：`/api/v1/interactions/ratings`
- **是否需要认证**：是

#### 请求参数

```json
{
  "contentId": 1,
  "score": 5
}
```

| 参数名 | 类型 | 必填 | 说明 | 限制 |
|--------|------|------|------|------|
| contentId | Long | 是 | 内容 ID | - |
| score | Integer | 是 | 评分 | 1-5 |

#### 请求示例

```bash
curl -X POST https://api.ai-tide.com/api/v1/interactions/ratings \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "contentId": 1,
    "score": 5
  }'
```

#### 响应示例

```json
{
  "code": 200,
  "message": "评分成功",
  "data": {
    "averageRating": 4.6,
    "ratingCount": 20,
    "ratingDistribution": {
      "5": 10,
      "4": 6,
      "3": 2,
      "2": 1,
      "1": 1
    }
  },
  "timestamp": 1711234567890
}
```

#### 错误码

- `2001` - 内容不存在
- `2006` - 已评过分该内容
- `400` - 请求参数错误（评分不在 1-5 范围内）

---

## 6. 内容管理模块 API

### 6.1 创建内容

#### 接口描述

创建新内容（需要 EDITOR 或 ADMIN 权限）。

#### 请求信息

- **请求方法**：`POST`
- **请求 URL**：`/api/v1/admin/contents`
- **是否需要认证**：是
- **需要权限**：EDITOR、ADMIN

#### 请求参数

```json
{
  "title": "GPT-4",
  "description": "OpenAI 最新大语言模型",
  "content": "# GPT-4 介绍\n\nGPT-4 是 OpenAI 发布的最新大语言模型...",
  "type": "MODEL",
  "categoryId": 1,
  "tags": ["LLM", "OpenAI"],
  "coverImage": "https://cdn.ai-tide.com/covers/1.jpg",
  "officialUrl": "https://openai.com/gpt-4",
  "relatedLinks": [
    {
      "title": "GitHub",
      "url": "https://github.com/openai/gpt-4"
    }
  ],
  "status": "PUBLISHED"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| title | String | 是 | 标题（1-200 字符） |
| description | String | 是 | 简短描述（1-500 字符） |
| content | String | 是 | 详细内容（Markdown 格式） |
| type | String | 是 | 内容类型：MODEL/PRODUCT/ARTICLE |
| categoryId | Long | 是 | 分类 ID |
| tags | String[] | 是 | 标签数组 |
| coverImage | String | 否 | 封面图 URL |
| officialUrl | String | 否 | 官方链接 |
| relatedLinks | Object[] | 否 | 相关链接 |
| status | String | 否 | 状态：DRAFT/PUBLISHED，默认 PUBLISHED |

#### 请求示例

```bash
curl -X POST https://api.ai-tide.com/api/v1/admin/contents \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "title": "GPT-4",
    "description": "OpenAI 最新大语言模型",
    "content": "# GPT-4 介绍",
    "type": "MODEL",
    "categoryId": 1,
    "tags": ["LLM", "OpenAI"],
    "status": "PUBLISHED"
  }'
```

#### 响应示例

```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "contentId": 1,
    "title": "GPT-4",
    "description": "OpenAI 最新大语言模型",
    "type": "MODEL",
    "status": "PUBLISHED",
    "publishTime": "2026-03-24T10:00:00Z",
    "createTime": "2026-03-24T10:00:00Z"
  },
  "timestamp": 1711234567890
}
```

#### 错误码

- `403` - 权限不足
- `4001` - 分类不存在
- `400` - 请求参数错误

---

### 6.2 更新内容

#### 接口描述

更新内容（只能更新自己创建的内容，或 ADMIN 权限）。

#### 请求信息

- **请求方法**：`PUT`
- **请求 URL**：`/api/v1/admin/contents/{contentId}`
- **是否需要认证**：是
- **需要权限**：EDITOR、ADMIN

#### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| contentId | Long | 是 | 内容 ID |

#### 请求参数

```json
{
  "title": "GPT-4 Updated",
  "description": "Updated description",
  "content": "Updated content",
  "type": "MODEL",
  "categoryId": 1,
  "tags": ["LLM", "OpenAI", "Updated"],
  "coverImage": "https://cdn.ai-tide.com/covers/1-updated.jpg",
  "officialUrl": "https://openai.com/gpt-4",
  "relatedLinks": [
    {
      "title": "GitHub",
      "url": "https://github.com/openai/gpt-4"
    }
  ]
}
```

#### 请求示例

```bash
curl -X PUT https://api.ai-tide.com/api/v1/admin/contents/1 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "title": "GPT-4 Updated",
    "description": "Updated description"
  }'
```

#### 响应示例

```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "contentId": 1,
    "title": "GPT-4 Updated",
    "updateTime": "2026-03-24T12:00:00Z"
  },
  "timestamp": 1711234567890
}
```

#### 错误码

- `2001` - 内容不存在
- `403` - 权限不足

---

### 6.3 删除内容

#### 接口描述

删除内容（软删除，状态变为 ARCHIVED）。

#### 请求信息

- **请求方法**：`DELETE`
- **请求 URL**：`/api/v1/admin/contents/{contentId}`
- **是否需要认证**：是
- **需要权限**：EDITOR、ADMIN

#### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| contentId | Long | 是 | 内容 ID |

#### 请求示例

```bash
curl -X DELETE https://api.ai-tide.com/api/v1/admin/contents/1 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

#### 响应示例

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null,
  "timestamp": 1711234567890
}
```

#### 错误码

- `2001` - 内容不存在
- `403` - 权限不足

---

### 6.4 内容审核

#### 接口描述

审核内容（ADMIN 权限）。

#### 请求信息

- **请求方法**：`POST`
- **请求 URL**：`/api/v1/admin/contents/{contentId}/review`
- **是否需要认证**：是
- **需要权限**：ADMIN

#### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| contentId | Long | 是 | 内容 ID |

#### 请求参数

```json
{
  "action": "APPROVE",
  "comment": "审核通过"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| action | String | 是 | 操作：APPROVE（通过）/REJECT（拒绝） |
| comment | String | 否 | 审核意见（拒绝时必填） |

#### 请求示例

```bash
curl -X POST https://api.ai-tide.com/api/v1/admin/contents/1/review \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "action": "APPROVE",
    "comment": "审核通过"
  }'
```

#### 响应示例

```json
{
  "code": 200,
  "message": "审核成功",
  "data": {
    "contentId": 1,
    "status": "PUBLISHED"
  },
  "timestamp": 1711234567890
}
```

#### 错误码

- `2001` - 内容不存在
- `403` - 权限不足

---

## 7. 管理模块 API

### 7.1 用户管理

#### 7.1.1 获取用户列表

**接口描述**

获取平台用户列表（ADMIN 权限）。

**请求信息**

- **请求方法**：`GET`
- **请求 URL**：`/api/v1/admin/users`
- **是否需要认证**：是
- **需要权限**：ADMIN

**请求参数**

| 参数名 | 类型 | 必填 | 说明 | 默认值 |
|--------|------|------|------|--------|
| page | Integer | 否 | 页码 | 1 |
| size | Integer | 否 | 每页数量 | 10 |
| keyword | String | 否 | 搜索关键词（用户名或邮箱） | - |
| role | String | 否 | 角色筛选 | - |
| enabled | Boolean | 否 | 状态筛选 | - |
| sortBy | String | 否 | 排序字段 | registerTime |
| sortOrder | String | 否 | 排序方向 | desc |

**请求示例**

```bash
curl -X GET "https://api.ai-tide.com/api/v1/admin/users?page=1&size=10" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**响应示例**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "userId": 1,
        "username": "admin",
        "email": "admin@example.com",
        "nickname": "管理员",
        "avatar": "https://cdn.ai-tide.com/avatars/1.jpg",
        "role": "ADMIN",
        "enabled": true,
        "registerTime": "2026-03-24T10:00:00Z",
        "lastLoginTime": "2026-03-24T12:00:00Z"
      }
    ],
    "total": 100,
    "page": 1,
    "size": 10,
    "totalPages": 10
  },
  "timestamp": 1711234567890
}
```

---

#### 7.1.2 获取用户详情

**接口描述**

获取用户详细信息（ADMIN 权限）。

**请求信息**

- **请求方法**：`GET`
- **请求 URL**：`/api/v1/admin/users/{userId}`
- **是否需要认证**：是
- **需要权限**：ADMIN

**路径参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 用户 ID |

**请求示例**

```bash
curl -X GET https://api.ai-tide.com/api/v1/admin/users/1 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**响应示例**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 1,
    "username": "admin",
    "email": "admin@example.com",
    "nickname": "管理员",
    "avatar": "https://cdn.ai-tide.com/avatars/1.jpg",
    "bio": "System administrator",
    "role": "ADMIN",
    "enabled": true,
    "registerTime": "2026-03-24T10:00:00Z",
    "lastLoginTime": "2026-03-24T12:00:00Z",
    "lastLoginIp": "192.168.1.1",
    "stats": {
      "contentCount": 10,
      "commentCount": 5,
      "favoriteCount": 20,
      "likeCount": 30
    }
  },
  "timestamp": 1711234567890
}
```

---

#### 7.1.3 编辑用户角色

**接口描述**

修改用户角色（ADMIN 权限）。

**请求信息**

- **请求方法**：`PUT`
- **请求 URL**：`/api/v1/admin/users/{userId}/role`
- **是否需要认证**：是
- **需要权限**：ADMIN

**路径参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 用户 ID |

**请求参数**

```json
{
  "role": "EDITOR"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| role | String | 是 | 角色：USER/EDITOR/ADMIN |

**请求示例**

```bash
curl -X PUT https://api.ai-tide.com/api/v1/admin/users/123/role \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "role": "EDITOR"
  }'
```

**响应示例**

```json
{
  "code": 200,
  "message": "角色修改成功",
  "data": {
    "userId": 123,
    "role": "EDITOR"
  },
  "timestamp": 1711234567890
}
```

---

#### 7.1.4 禁用/启用用户

**接口描述**

禁用或启用用户账号（ADMIN 权限）。

**请求信息**

- **请求方法**：`PUT`
- **请求 URL**：`/api/v1/admin/users/{userId}/status`
- **是否需要认证**：是
- **`需要权限**：ADMIN

**路径参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 用户 ID |

**请求参数**

```json
{
  "enabled": false,
  "reason": "违反社区规范"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| enabled | Boolean | 是 | 是否启用 |
| reason | String | 否 | 禁用原因（禁用时必填） |

**请求示例**

```bash
curl -X PUT https://api.ai-tide.com/api/v1/admin/users/123/status \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "enabled": false,
    "reason": "违反社区规范"
  }'
```

**响应示例**

```json
{
  "code": 200,
  "message": "用户已禁用",
  "data": {
    "userId": 123,
    "enabled": false
  },
  "timestamp": 1711234567890
}
```

---

#### 7.1.5 删除用户

**接口描述**

删除用户（软删除）（）ADMIN 权限）。

**请求信息**

- **请求方法**：`DELETE`
- **请求 URL**：`/api/v1/admin/users/{userId}`
- **是否需要认证**：是
- **需要权限**：ADMIN

**路径参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 用户 ID |

**请求示例**

```bash
curl -X DELETE https://api.ai-tide.com/api/v1/admin/users/123 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**响应示例**

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null,
  "timestamp": 1711234567890
}
```

---

### 7.2 内容管理

#### 7.2.1 获取所有内容列表

**接口描述**

获取平台所有内容列表（ADMIN 权限）。

**请求信息**

- **请求方法**：`GET`
- **请求 URL**：`/api/v1/admin/contents`
- **是否需要认证**：是
- **需要权限**：ADMIN

**请求参数**

| 参数名 | 类型 | 必填 | 说明 | 默认值 |
|--------|------|------|------|--------|
| page | Integer | 否 | 页码 | 1 |
| size | Integer | 否 | 每页数量 | 10 |
| keyword | String | 否 | 搜索关键词 | - |
| type | String | 否 | 内容类型 | - |
| status | String | 否 | 状态：DRAFT/PUBLISHED/ARCHIVED | - |
| categoryId | Long | 否 | 分类 ID | - |
| authorId | Long | 否 | 作者 ID | - |
| sortBy | String | 否 | 排序字段 | createTime |
| sortOrder | String | 否 | 排序方向 | desc |

**请求示例**

```bash
curl -X GET "https://api.ai-tide.com/api/v1/admin/contents?page=1&size=10" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**响应示例**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "contentId": 1,
        "title": "GPT-4",
        "description": "OpenAI 最新大语言模型",
        "type": "MODEL",
        "status": "PUBLISHED",
        "author": {
          "userId": 1,
          "username": "admin"
        },
        "publishTime": "2026-03-24T10:00:00Z",
        "viewCount": 1000,
        "likeCount": 50,
        "favoriteCount": 30,
        "commentCount": 10
      }
    ],
    "total": 100,
    "page": 1,
    "size": 10,
    "totalPages": 10
  },
  "timestamp": 1711234567890
}
```

---

#### 7.2.2 批量操作内容

**接口描述**

批量删除或归档内容（ADMIN 权限）。

**请求信息**

- **请求方法**：`POST`
- **请求 URL**：`/api/v1/admin/contents/batch`
- **是否需要认证**：是
- **需要权限**：ADMIN

**请求参数**

```json
{
  "action": "ARCHIVE",
  "contentIds": [1, 2, 3]
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| action | String | 是 | 操作：DELETE（删除）/ARCHIVE（归档） |
| contentIds | Long[] | 是 | 内容 ID 数组 |

**请求示例**

```bash
curl -X POST https://api.ai-tide.com/api/v1/admin/contents/batch \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "action": "ARCHIVE",
    "contentIds": [1, 2, 3]
  }'
```

**响应示例**

```json
{
  "code": 200,
  "message": "批量操作成功",
  "data": {
    "successCount": 3,
    "failedCount": 0
  },
  "timestamp": 1711234567890
}
```

---

### 7.3 分类管理

#### 7.3.1 获取分类列表

**接口描述**

获取分类树形列表（ADMIN 权限）。

**请求信息**

- **请求方法**：`GET`
- **请求 URL**：`/api/v1/admin/categories`
- **是否需要认证**：是
- **需要权限**：ADMIN

**请求示例**

```bash
curl -X GET https://api.ai-tide.com/api/v1/admin/categories \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**响应示例**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "categoryId": 1,
      "name": "AI 模型",
      "description": "各种 AI 模型",
      "parentId": null,
      "sortOrder": 1,
      "children": [
        {
          "categoryId": 2,
          "name": "语言模型",
          "description": "大语言模型",
          "parentId": 1,
          "sortOrder": 1,
          "children": []
        }
      ]
    }
  ],
  "timestamp": 1711234567890
}
```

---

#### 7.3.2 创建分类

**接口描述**

创建新分类（ADMIN 权限）。

**请求信息**

- **请求方法**：`POST`
- **请求 URL**：`/api/v1/admin/categories`
- **是否需要认证**：是
- **需要权限**：ADMIN

**请求参数**

```json
{
  "name": "语言模型",
  "description": "大语言模型",
  "parentId": 1,
  "sortOrder": 1
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| name | String | 是 | 分类名称（1-100 字符） |
| description | String | 否 | 分类描述 |
| parentId | Long | 否 | 爮分类 ID（根分类为空） |
| sortOrder | Integer | 否 | 排序序号 |

**请求示例**

```bash
curl -X POST https://api.ai-tide.com/api/v1/admin/categories \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "name": "语言模型",
    "description": "大语言模型",
    "parentId": 1,
    "sortOrder": 1
  }'
```

**响应示例**

```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "categoryId": 2,
    "name": "语言模型",
    "description": "大语言模型",
    "parentId": 1,
    "sortOrder": 1,
    "createTime": "2026-03-24T10:00:00Z"
  },
  "timestamp": 1711234567890
}
```

---

#### 7.3.3 更新分类

**接口描述**

更新分类信息（ADMIN 权限）。

**请求信息**

- **请求方法**：`PUT`
- **请求 URL**：`/api/v1/admin/categories/{categoryId}`
- **是否需要认证**：是
- **需要权限**：ADMIN

**路径参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| categoryId | Long | 是 | 分类 ID |

**请求参数**

```json
{
  "name": "大语言模型",
  "description": "Updated description",
  "sortOrder": 2
}
```

**请求示例**

```bash
curl -X PUT https://api.ai-tide.com/api/v1/admin/categories/2 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "name": "大语言模型"
  }'
```

**响应示例**

```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "categoryId": 2,
    "name": "大语言模型"
  },
  "timestamp": 1711234567890
}
```

---

#### 7.3.4 删除分类

**接口描述**

删除分类（ADMIN 权限）。

**请求信息**

- **请求方法**：`DELETE`
- **请求 URL**：`/api/v1/admin/categories/{categoryId}`
- **是否需要认证**：是
- **需要权限**：ADMIN

**路径参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| categoryId | Long | 是 | 分类 ID |

**请求示例**

```bash
curl -X DELETE https://api.ai-tide.com/api/v1/admin/categories/2 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**响应示例**

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null,
  "timestamp": 1711234567890
}
```

---

### 7.4 标签管理

#### 7.4.1 获取标签列表

**接口描述**

获取标签列表（ADMIN 权限）。

**请求信息**

- **请求方法**：`GET`
- **请求 URL**：`/api/v1/admin/tags`
- **是否需要认证**：是
- **需要权限**：ADMIN

**请求参数**

| 参数名 | 类型 | 必填 | 说明 | 默认值 |
|--------|------|------|------|--------|
| page | Integer | 否 | 页码 | 1 |
| size | Integer | 否 | 每页数量 | 10 |
| keyword | String | 否 | 搜索关键词 | - |

**请求示例**

```bash
curl -X GET "https://api.ai-tide.com/api/v1/admin/tags?page=1&size=10" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**响应示例**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "tagId": 1,
        "name": "LLM",
        "useCount": 10,
        "createTime": "2026-03-24T10:00:00Z"
      }
    ],
    "total": 10,
    "page": 1,
    "size": 10,
    "totalPages": 1
  },
  "timestamp": 1711234567890
}
```

---

#### 7.4.2 创建标签

**接口描述**

创建新标签（ADMIN 权限）。

**请求信息**

- **请求方法**：`POST`
- **请求 URL**：`/api/v1/admin/tags`
- **是否需要认证**：是
- **需要权限**：ADMIN

**请求参数**

```json
{
  "name": "LLM"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| name | String | 是 | 标签名称（1-50 字符） |

**请求示例**

```bash
curl -X POST https://api.ai-tide.com/api/v1/admin/tags \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "name": "LLM"
  }'
```

**响应示例**

```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "tagId": 1,
    "name": "LLM",
    "useCount": 0,
    "createTime": "2026-03-24T10:00:00Z"
  },
  "timestamp": 1711234567890
}
```

---

#### 7.4.3 删除标签

**接口描述**

删除标签（ADMIN 权限）。

**请求信息**

- **请求方法**：`DELETE`
- **请求 URL**：`/api/v1/admin/tags/{tagId}`
- **是否需要认证**：是
- **需要权限**：ADMIN

**路径参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| tagId | Long | 是 | 标签 ID |

**请求示例**

```bash
curl -X DELETE https://api.ai-tide.com/api/v1/admin/tags/1 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**响应示例**

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null,
  "timestamp": 1711234567890
}
```

---

### 7.5 系统配置

#### 7.5.1 获取系统配置

**接口描述**

获取系统配置（ADMIN 权限）。

**请求信息**

- **请求方法**：`GET`
- **请求 URL**：`/api/v1/admin/config`
- **是否需要认证**：是
- **需要权限**：ADMIN

**请求示例**

```bash
curl -X GET https://api.ai-tide.com/api/v1/admin/config \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**响应示例**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "site": {
      "name": "AI-Tide",
      "description": "AI 前沿技术展示平台",
      "logo": "https://cdn.ai-tide.com/logo.png",
      "contactEmail": "contact@ai-tide.com",
      "icpNumber": "ICP备XXXXXXXX号"
    },
    "user": {
      "allowRegistration": true,
      "passwordMinLength": 8,
      "passwordRequireLetter": true,
      "passwordRequireNumber": true,
      "maxLoginAttempts": 5,
      "lockDurationMinutes": 30
    },
    "content": {
      "pageSize": 10,
      "maxCommentLength": 500,
      "maxDailyComments": 10,
      "enableReview": false
    }
  },
  "timestamp": 1711234567890
}
```

---

#### 7.5.2 更新系统配置

**接口描述**

更新系统配置（ADMIN 权限）。

**请求信息**

- **请求方法**：`PUT`
- **请求 URL**：`/api/v1/admin/config`
- **是否需要认证**：是
- **需要权限**：ADMIN

**请求参数**

```json
{
  "site": {
    "name": "AI-Tide",
    "description": "Updated description"
  },
  "user": {
    "allowRegistration": false
  }
}
```

**请求示例**

```bash
curl -X PUT https://api.ai-tide.com/api/v1/admin/config \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "site": {
      "name": "AI-Tide",
      "description": "Updated description"
    }
  }'
```

**响应示例**

```json
{
  "code": 200,
  "message": "更新成功",
  "data": null,
  "timestamp": 1711234567890
}
```

---

### 7.6 数据统计

#### 7.6.1 获取统计数据

**接口描述**

获取平台统计数据（ADMIN 权限）。

**请求信息**

- **请求方法**：`GET`
- **请求 URL**：`/api/v1/admin/stats`
- **是否需要认证**：是
- **需要权限**：ADMIN

**请求示例**

```bash
curl -X GET https://api.ai-tide.com/api/v1/admin/stats \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**响应示例**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "user": {
      "total": 1000,
      "todayNew": 10,
      "weekNew": 50,
      "monthNew": 200,
      "active": 800
    },
    "content": {
      "total": 500,
      "todayNew": 5,
      "byType": {
        "MODEL": 200,
        "PRODUCT": 150,
        "ARTICLE": 150
      }
    },
    "interaction": {
      "totalComments": 1000,
      "totalLikes": 5000,
      "totalFavorites": 3000,
      "averageRating": 4.2
    },
    "trends": {
      "userGrowth": [
        {
          "date": "2026-03-18",
          "count": 10
        }
      ],
      "contentPublish": [],
      "pageViews": []
    }
  },
  "timestamp": 1711234567890
}
```

---

### 7.7 系统日志

#### 7.7.1 获取操作日志

**接口描述**

获取系统操作日志（ADMIN 权限）。

**请求信息**

- **请求方法**：`GET`
- **请求 URL**：`/api/v1/admin/logs`
- **是否需要认证**：是
- **需要权限**：ADMIN

**请求参数**

| 参数名 | 类型 | 必填 | 说明 | 默认值 |
|--------|------|------|------|--------|
| page | Integer | 否 | 页码 | 1 |
| size | Integer | 否 | 每页数量 | 10 |
| operationType | String | 否 | 操作类型 | - |
| userId | Long | 否 | 用户 ID | - |
| result | String | 否 | 操作结果：SUCCESS/FAILURE | - |
| startTime | String | 否 | 开始时间（ISO 8601） | - |
| endTime | String | 否 | 结束时间（ISO 8601） | - |

**请求示例**

```bash
curl -X GET "https://api.ai-tide.com/api/v1/admin/logs?page=1&size=10" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**响应示例**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "logId": 1,
        "operationType": "USER_LOGIN",
        "operationContent": "用户 johndoe 登录成功",
        "user": {
          "userId": 123,
          "username": "johndoe"
        },
        "ipAddress": "192.168.1.1",
        "result": "SUCCESS",
        "errorMessage": null,
        "operationTime": "2026-03-24T12:00:00Z"
      }
    ],
    "total": 100,
    "page": 1,
    "size": 10,
    "totalPagesPages": 10
  },
  "timestamp": 1711234567890
}
```

---

## 8. 支付授权模块 API

### 8.1 产品列表

#### 接口描述

获取可购买的产品列表。

#### 请求信息

- **请求方法**：`GET`
- **请求 URL**：`/api/v1/products`
- **是否需要认证**：否

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 | 默认值 |
|--------|------|------|------|--------|
| page | Integer | 否 | 页码 | 1 |
| size | Integer | 否 | 每页数量 | 10 |
| status | String | 否 | 状态：ACTIVE/INACTIVE | ACTIVE |

#### 请求示例

```bash
curl -X GET "https://api.ai-tide.com/api/v1/products?page=1&size=10"
```

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "productId": "prod_001",
        "contentId": 1,
        "contentTitle": "GPT-4 实战教程",
        "name": "GPT-4 实战教程（含 DEMO）",
        "price": 99.00,
        "description": "包含完整 GitHub DEMO 和源码",
        "status": "ACTIVE",
        "salesCount": 10,
        "totalRevenue": 990.00,
        "createdAt": "2026-03-24T10:00:00Z",
        "updatedAt": "2026-03-24T10:00:00Z"
      }
    ],
    "total": 10,
    "page": 1,
    "size": 10,
    "totalPages": 1
  },
  "timestamp": 1711234567890
}
```

---

### 8.2 产品详情

#### 接口描述

获取产品详细信息。

#### 请求信息

- **请求方法**：`GET`
- **请求 URL**：`/api/v1/products/{productId}`
- **是否需要认证**：否

#### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| productId | String | 是 | 产品 ID |

#### 请求示例

```bash
curl -X GET https://api.ai-tide.com/api/v1/products/prod_001
```

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "productId": "prod_001",
    "contentId": 1,
    "content": {
      "contentId": 1,
      "title": "GPT-4 实战教程",
      "description": "详细教程介绍",
      "coverImage": "https://cdn.ai-tide.com/covers/1.jpg"
    },
    "name": "GPT-4 实战教程（含 DEMO）",
    "price": 99.00,
    "description": "包含完整 GitHub DEMO 和源码",
    "status": "ACTIVE",
    "salesCount": 10,
    "totalRevenue": 990.00,
    "createdAt": "2026-03-24T10:00:00Z",
    "updatedAt": "2026-03-24T10:00:00Z",
    "isLicensed": false
  },
  "timestamp": 1711234567890
}
```

---

### 8.3 手工赋权

#### 接口描述

管理员为用户手工赋权（ADMIN 权限）。

#### 请求信息

- **请求方法**：`POST`
- **请求 URL**：`/api/v1/admin/licenses/grant`
- **是否需要认证**：是
- **需要权限**：ADMIN

#### 请求参数

```json
{
  "userId": 123,
  "productId": "prod_001",
  "grantNote": "微信支付"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 用户 ID |
| productId | String | 是 | 产品 ID |
| grantNote | String | 否 | 授权备注 |

#### 请求示例

```bash
curl -X POST https://api.ai-tide.com/api/v1/admin/licenses/grant \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 123,
    "productId": "prod_001",
    "grantNote": "微信支付"
  }'
```

#### 响应示例

```json
{
  "code": 200,
  "message": "授权成功",
  "data": {
    "licenseId": "lic_001",
    "userId": 123,
    "productId": "prod_1",
    "grantType": "MANUAL",
    "grantTime": "2026-03-24T12:00:00Z",
    "grantNote": "微信支付",
    "status": "ACTIVE"
  },
  "timestamp": 1711234567890
}
```

#### 错误码

- `6003` - 已授权

---

### 8.4 撤销授权

#### 接口描述

管理员撤销用户授权（ADMIN 权限）。

#### 请求信息

- **请求方法**：`DELETE`
- **请求 URL**：`/api/v1/admin/licenses/{licenseId}`
- **是否需要认证**：是
- **需要权限**：ADMIN

#### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| licenseId | String | 是 | 授权 ID |

#### 请求示例

```bash
curl -X DELETE https://api.ai-tide.com/api/v1/admin/licenses/lic_001 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

#### 响应示例

```json
{
  "code": 200,
  "message": "撤销成功",
  "data": null,
  "timestamp": 1711234567890
}
```

---

### 8.5 获取已购买产品

#### 接口描述

获取当前用户已购买/已授权的产品列表。

#### 请求信息

- **请求方法**：`GET`
- **请求 URL**：`/api/v1/user/licenses`
- **是否需要认证**：是

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 | 默认值 |
|--------|------|------|------|--------|
| page | Integer | 否 | 页码 | 1 |
| size | Integer | 否 | 每页数量 | 10 |

#### 请求示例

```bash
curl -X GET "https://api.ai-tide.com/api/v1/user/licenses?page=1&size=10" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "licenseId": "lic_001",
        "productId": "prod_001",
        "productName": "GPT-4 实战教程（含 DEMO）",
        "contentId": 1,
        "contentTitle": "GPT-4 实战教程",
        "grantType": "MANUAL",
        "grantTime": "2026-03-24T12:00:00Z",
        "grantNote": "微信支付",
        "status": "ACTIVE",
        "githubUrl": "https://github.com/ai-tide/gpt-4-demo"
      }
    ],
    "total": 1,
    "page": 1,
    "size": 10,
    "totalPages": 1
  },
  "timestamp": 1711234567890
}
```

---

### 8.6 访问 GitHub DEMO

#### 接口描述

获取 GitHub DEMO 访问链接。

#### 请求信息

- **请求方法**：`GET`
- **请求 URL**：`/api/v1/user/licenses/{licenseId}/github`
- **是否需要认证**：是

#### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| licenseId | String | 是 | 授权 ID |

#### 请求示例

```bash
curl -X GET https://api.ai-tide.com/api/v1/user/licenses/lic_001/github \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "githubUrl": "https://github.com/ai-tide/gpt-4-demo"
  },
  "timestamp": 1711234567890
}
```

#### 错误码

- `6004` - 未授权
- `6005` - 授权已撤销

---

## 9. 智能体协作模块 API

### 9.1 团队列表

#### 接口描述

获取智能体团队列表。

#### 请求信息

- **请求方法**：`GET`
- **请求 URL**：`/api/v1/agents/teams`
- **是否需要认证**：否

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 | 默认值 |
|--------|------|------|------|--------|
| page | Integer | 否 | 页码 | 1 |
| size | Integer | 否 | 每页数量 | 10 |
| status | String | 否 | 状态：active/inactive | - |

#### 请求示例

```bash
curl -X GET "https://api.ai-tide.com/api/v1/agents/teams?page=1&size=10"
```

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "teamId": "team_001",
        "name": "ai",
        "displayName": "🤖 AI部",
        "botAppId": "app_001",
        "managerPrompt": "作为 AI 部负责人，协调团队讨论...",
        "status": "active",
        "memberCount": 3,
        "sessionCount": 12,
        "createdAt": "2026-03-24T10:00:00Z",
        "updatedAt": "2026-03-24T10:00:00Z"
      }
    ],
    "total": 5,
    "page": 1,
    "size": 10,
    "totalPages": 1
  },
  "timestamp": 1711234567890
}
```

---

### 9.2 团队详情

#### 接口描述

获取团队详细信息。

#### 请求信息

- **请求方法**：`GET`
- **请求 URL**：`/api/v1/agents/teams/{teamId}`
- **是否需要认证**：否

#### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| teamId | String | 是 | 团队 ID |

#### 请求示例

```bash
curl -X GET https://api.ai-tide.com/api/v1/agents/teams/team_001
```

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "teamId": "team_001",
    "name": "ai",
    "displayName": "🤖 AI部",
    "botAppId": "app_001",
    "managerPrompt": "作为 AI 部负责人，协调团队讨论...",
    "status": "active",
    "createdAt": "2026-03-24T10:00:00Z",
    "updatedAt": "2026-03-24T10:00:00Z",
    "members": [
      {
        "memberId": "mem_001",
        "name": "Echo",
        "role": "manager",
        "personality": "AI 部负责人，擅长协调和总结",
        "status": "idle",
        "memorySummary": "最近参与了 3 次讨论",
        "avatarUrl": "https://cdn.ai-tide.com/agents/echo.png"
      }
    ],
    "sessions": [
     ugs {
        "sessionId": "sess_001",
        "title": "新技术评估讨论",
        "status": "completed",
        "messageCount": 50,
        "startedAt": "2026-03-24T10:00:00Z",
        "completedAt": "2026-03-24T11:00:00Z"
      }
    ]
  },
  "timestamp": 1711234567890
}
```

#### 错误码

- `7001` - 智能体团队不存在

---

### 9.3 成员列表

#### 接口描述

获取团队智能体成员列表。

#### 请求信息

- **请求方法**：`GET`
- **请求 URL**：`/api/v1/agents/teams/{teamId}/members`
- **是否需要认证**：否

#### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| teamId | String | 是 | 团队 ID |

#### 请求示例

```bash
curl -X GET https://api.ai-tide.com/api/v1/agents/teams/team_001/members
```

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "memberId": "mem_001",
      "name": "Echo",
      "role": "manager",
      "personality": "AI 部负责人，擅长协调和总结",
      "status": "idle",
      "memorySummary": "最近参与了 3 次讨论",
      "avatarUrl": "https://cdn.ai-tide.com/agents/echo.png",
      "createdAt": "2026-03-24T10:00:00Z",
      "updatedAt": "2026-03-24T10:00:00Z"
    }
  ],
  "timestamp": 1711234567890
}
```

---

### 9.4 会话列表

#### 接口描述

获取协作会话列表。

#### 请求信息

- **请求方法**：`GET`
- **请求 URL**：`/api/v1/agents/sessions`
- **是否需要认证**：否

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 | 默认值 |
|--------|------|------|------|--------|
| page | Integer | 否 | 页码 | 1 |
| size | Integer | 否 | 每页数量 | 10 |
| teamId | String | 否 | 团队 ID | - |
| status | String | 否 | 状态：pending/running/paused/completed/failed | - |

#### 请求示例

```bash
curl -X GET "https://api.ai-tide.com/api/v1/agents/sessions?page=1&size=10"
```

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "sessionId": "sess_001",
        "teamId": "team_001",
        "teamName": "🤖 AI部",
        "title": "新技术评估讨论",
        "topic": "评估 GPT-4 的实际应用场景",
        "status": "completed",
        "agentCount": 3,
        "messageCount": 50,
        "hasConclusion": true,
        "startedAt": "2026-03-24T10:00:00Z",
        "completedAt": "2026-03-24T11:00:00Z",
        "createdAt": "2026-03-24T10:00:00Z",
        "updatedAt": "2026-03-24T11:00:00Z"
      }
    ],
    "total": 12,
    "page": 1,
    "size": 10,
    "totalPages": 2
  },
  "timestamp": 1711234567890
}
```

---

### 9.5 会话详情

#### 接口描述

获取会话详细信息。

#### 请求信息

- **请求方法**：`GET`
- **请求 URL**：`/api/v1/agents/sessions/{sessionId}`
- **是否需要认证**：否

#### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| sessionId | String | 是 | 会话 ID |

#### 请求示例

```bash
curl -X GET https://api.ai-tide.com/api/v1/agents/sessions/sess_001
```

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "sessionId": "sess_001",
    "teamId": "team_001",
    "title": "新技术评估讨论",
    "topic": "评估 GPT-4 的实际应用场景",
    "status": "completed",
    "scheduleConfig": {
      "enabled": true,
      "cronExpression": "0 9 * * *",
      "maxDurationMinutes": 60,
      "maxMessages": 100
    },
    "messageCount": 50,
    "conclusion": "经过讨论，我们建议优先在以下场景应用 GPT-4：\n1. 客服自动化\n2. 内容生成\n3. 代码辅助",
    "agents": [
      {
        "memberId": "mem_001",
        "name": "Echo",
        "role": "manager",
        "avatarUrl": "https://cdn.ai-tide.com/agents/echo.png"
      }
    ],
    "startedAt": "2026-03-24T10:00:00Z",
    "completedAt": "2026-03-24T11:00:00Z",
    "createdAt": "2026-03-24T10:00:00Z",
    "updatedAt": "2026-03-24T11:00:00Z"
  },
  "timestamp": 1711234567890
}
```

#### 错误码

- `7003` - 协作会话不存在

---

### 9.6 消息列表

#### 接口描述

获取会话消息列表。

#### 请求信息

- **请求方法**：`GET`
- **请求 URL**：`/api/v1/agents/sessions/{sessionId}/messages`
- **是否需要认证**：否

#### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| sessionId | String | 是 | 会话 ID |

#### 查询参数

| 参数名 | 类型 | 必填 | 说明 | 默认值 |
|--------|------|------|------|--------|
| page | Integer | 否 | 页码 | 1 |
| size | Integer | 否 | 每页数量 | 50 |

#### 请求示例

```bash
curl -X GET "https://api.ai-tide.com/api/v1/agents/sessions/sess_001/messages?page=1&size=50"
```

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "messageId": "msg_001",
        "sessionId": "sess_001",
        "agentId": "mem_001",
        "agentName": "Echo",
        "agentRole": "manager",
        "agentAvatar": "https://cdn.ai-tide.com/agents/echo.png",
        "messageType": "text",
        "content": "各位，今天我们讨论 GPT-4 的实际应用场景",
        "metadata": {
          "role": "manager",
          "emotion": "neutral",
          "keywords": ["GPT-4", "应用场景"]
        },
        "createdAt": "2026-03-24T10:00:00Z"
      }
    ],
    "total": 50,
    "page": 1,
    "size": 50,
    "totalPages": 1
  },
  "timestamp": 1711234567890
}
```

---

### 9.7 结论获取

#### 接口描述

获取会话结论。

#### 请求信息

- **请求方法**：`GET`
- **请求 URL**：`/api/v1/agents/sessions/{sessionId}/conclusion`
- **是否需要认证**：否

#### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| sessionId | String | 是 | 会话 ID |

#### 请求示例

```bash
curl -X GET https://api.ai-tide.com/api/v1/agents/sessions/sess_001/conclusion
```

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "summary": "经过讨论，我们建议优先在以下场景应用 GPT-4",
    "keyPoints": [
      "客服自动化",
      "内容生成",
      "代码辅助"
    ],
    "decisions": [
      "采用 GPT-4 作为主要 AI 引擎",
      "分两阶段实施"
    ],
    "recommendations": [
      "先进行小规模试点",
      "建立评估机制"
    ],
    "confidenceScore": 0.85,
    "generatedAt": "2026-03-24T11:00:00Z"
  },
  "timestamp": 1711234567890
}
```

---

## 10. 附录

### 10.1 完整接口列表汇总

| 模块 | 接口名称 | 方法 | URL | 认证 | 权限 |
|------|----------|------|-----|------|------|
| **用户模块** | | | | | |
| | 用户注册 | POST | `/api/v1/auth/register` | 否 | - |
| | 用户登录 | POST | `/api/v1/auth/login` | 否 | - |
| | 找回密码-发送邮件 | POST | `/api/v1/auth/forgot-password` | 否 | - |
| | 找回密码-重置 | POST | `/api/v1/auth/reset-password` | 否 | - |
| | 获取当前用户信息 | GET | `/api/v1/user/me` | 是 | - |
| | 更新用户信息 | PUT | `/api/v1/user/me` | 是 | - |
| | 修改密码 | POST | `/api/v1/user/change-password` | 是 | - |
| | 上传头像 | POST | `/api/v1/user/upload-avatar` | 是 | - |
| **内容模块** | | | | | |
| | 获取内容列表 | GET | `/api/v1/contents` | 否 | - |
| | 获取内容详情 | GET | `/api/v1/contents/{contentId}` | 否 | - |
| | 内容搜索 | GET | `/api/v1/contents/search` | 否 | - |
| | 获取分类内容 | GET | `/api/v1/contents/category/{categoryId}` | 否 | - |
| | 获取热门内容 | GET | `/api/v1/contents/hot` | 否 | - |
| | 获取最新内容 | GET | `/api/v1/contents/latest` | 否 | - |
| **互动模块** | | | | | |
| | 收藏内容 | POST | `/api/v1/interactions/favorites/{contentId}` | 是 | - |
| | 获取收藏列表 | GET | `/api/v1/interactions/favorites` | 是 | - |
| | 发表评论 | POST | `/api/v1/interactions/comments` | 是 | - |
| | 获取评论列表 | GET | `/api/v1/interactions/comments/{contentId}` | 否 | - |
| | 删除评论 | DELETE | `/api/v1/interactions/comments/{commentId}` | 是 | - |
| | 点赞内容 | POST | `/api/v1/interactions/likes/{contentId}` | 是 | - |
| | 内容评分 | POST | `/api/v1/interactions/ratings` | 是 | - |
| **内容管理模块** | | | | | |
| | 创建内容 | POST | `/api/v1/admin/contents` | 是 | EDITOR、ADMIN |
| | 更新内容 | PUT | `/api/v1/admin/contents/{contentId}` | 是 | EDITOR、ADMIN |
| | 删除内容 | DELETE | `/api/v1/admin/contents/{contentId}` | 是 | EDITOR、ADMIN |
| | 内容审核 | POST | `/api/v1/admin/contents/{contentId}/review` | 是 | ADMIN |
| | 获取所有内容列表 | GET | `/api/v1/admin/contents` | 是 | ADMIN |
| | 批量操作内容 | POST | `/api/v1/admin/contents/batch` | 是 | ADMIN |
| **管理模块** | | | | | |
| | 获取用户列表 | GET | `/api/v1/admin/users` | 是 | ADMIN |
| | 获取用户详情 | GET | `/api/v1/admin/users/{userId}` | 是 | ADMIN |
| | 编辑用户角色 | PUT | `/api/v1/admin/users/{userId}/role` | 是 | ADMIN |
| | 禁用/启用用户 | PUT | `/api/v1/admin/users/{userId}/status` | 是 | ADMIN |
| | 删除用户 | DELETE | `/api/v1/admin/users/{userId}` | 是 | ADMIN |
| | 获取分类列表 | GET | `/api/v1/admin/categories` | 是 | ADMIN |
| | 创建分类 | POST | `/api/v1/admin/categories` | 是 | ADMIN |
| | 更新分类 | PUT | `/api/v1/admin/categories/{categoryId}` | 是 | ADMIN |
| | 删除分类 | DELETE | `/api/v1/admin/categories/{categoryId}` | 是 | ADMIN |
| | 获取标签列表 | GET | `/api/v1/admin/tags` | 是 | ADMIN |
| | 创建标签 | POST | `/api/v1/admin/tags` | 是 | ADMIN |
| | 删除标签 | DELETE | `/api/v1/admin/tags/{tagId}` | 是 | ADMIN |
| | 获取系统配置 | GET | `/api/v1/admin/config` | 是 | ADMIN |
| | 更新系统配置 | PUT | `/api/v1/admin/config` | 是 | ADMIN |
| | 获取统计数据 | GET | `/api/v1/admin/stats` | 是 | ADMIN |
| | 获取操作日志 | GET | `/api/v1/admin/logs` | 是 | ADMIN |
| **支付授权模块** | | | | | |
| | 产品列表 | GET | `/api/v1/products` | 否 | - |
| | 产品详情 | GET | `/api/v1/products/{productId}` | 否 | - |
| | 手工赋权 | POST | `/api/v1/admin/licenses/grant` | 是 | ADMIN |
| | 撤销授权 | DELETE | `/api/v1/admin/licenses/{licenseId}` | 是 | ADMIN |
| | 获取已购买产品 | GET | `/api/v1/user/licenses` | 是 | - |
| | 访问 GitHub DEMO | GET | `/api/v1/user/licenses/{licenseId}/github` | 是 | - |
| **智能体协作模块** | | | | | |
| | 团队列表 | GET | `/api/v1/agents/teams` | 否 | - |
| | 团队详情 | GET | `/api/v1/agents/teams/{teamId}` | 否 | - |
| | 成员列表 | GET | `/api/v1/agents/teams/{teamId}/members` | 否 | - |
| | 会话列表 | GET | `/api/v1/agents/sessions` | 否 | - |
| | 会话详情 | GET | `/api/v1/agents/sessions/{sessionId}` | 否 | - |
| | 消息列表 | GET | `/api/v1/agents/sessions/{sessionId}/messages` | 否 | - |
| | 结论获取 | GET | `/api/v1/agents/sessions/{sessionId}/conclusion` | 否 | - |

---

### 10.2 数据字典

#### 用户相关

| 字段名 | 类型 | 说明 | 示例值 |
|--------|------|------|--------|
| userId | Long | 用户 ID | 123 |
| username | String | 用户名 | johndoe |
| email | String | 邮箱 | john@example.com |
| nickname | String | 昵称 | John |
| avatar | String | 头像 URL | https://cdn.ai-tide.com/avatars/123.jpg |
| bio | String | 个人简介 | AI enthusiast |
| role | String | 角色：USER/EDITOR/ADMIN | USER |
| enabled | Boolean | 是否启用 | true |
| registerTime | DateTime | 注册时间 | 2026-03-24T10:00:00Z |
| lastLoginTime | DateTime | 最后登录时间 | 2026-03-24T12:00:00Z |
| lastLoginIp | String | 最后登录 IP | 192.168.1.1 |

#### 内容相关

| 字段名 | 类型 | 说明 | 示例值 |
|--------|------|------|--------|
| contentId | Long | 内容 ID | 1 |
| title | String | 标题 | GPT-4 |
| description | String | 简短描述 | OpenAI 最新大语言模型 |
| content | String | 详细内容（Markdown） | # GPT-4 介绍... |
| coverImage | String | 封面图 URL | https://cdn.ai-tide.com/covers/1.jpg |
| type | String | 内容类型：MODEL/PRODUCT/ARTICLE | MODEL |
| status | String | 状态：DRAFT/PUBLISHED/ARCHIVED | PUBLISHED |
| publishTime | DateTime | 发布时间 | 2026-03-24T10:00:00Z |
| updateTime | DateTime | 更新时间 | 2026-03-24T10:00:00Z |
| viewCount | Long | 浏览次数 | 1000 |
| likeCount | Long | 点赞数 | 50 |
| favoriteCount | Long | 收藏数 | 30 |
| commentCount | Long | 评论数 | 10 |
| averageRating | Decimal | 平均评分 | 4.5 |

#### 分类相关

| 字段名 | 类型 | 说明 | 示例值 |
|--------|------|------|--------|
| categoryId | Long | 分类 ID | 1 |
| name | String | 分类名称 | 语言模型 |
| description | String | 分类描述 | 大语言模型 |
| parentId | Long | 父分类 ID | 1 |
| sortOrder | Integer | 排序序号 | 1 |

#### 标签相关

| 字段名 | 类型 | 说明 | 示例值 |
|--------|------|------|--------|
| tagId | Long | 标签 ID | 1 |
| name | String | 标签名称 | LLM |
| useCount | Long | 使用次数 | 10 |

#### 评论相关

| 字段名 | 类型 | 说明 | 示例值 |
|--------|------|------|--------|
| commentId | Long | 评论 ID | 100 |
| content | String | 评论内容 | 这个模型真的很强大！ |
| publishTime | DateTime | 发布时间 | 2026-03-24T12:00:00Z |
| likeCount | Long | 点赞数 | 0 |

#### 评分相关

| 字段名 | 类型 | 说明 | 示例值 |
|--------|------|------|--------|
| averageRating | Decimal | 平均评分 | 4.5 |
| ratingCount | Long | 评分人数 | 20 |
| ratingDistribution | Object | 评分分布 | {"5": 10, "4": 6, ...} |

#### 产品相关

| 字段名 | 类型 | 说明 | 示例值 |
|--------|------|------|--------|
| productId | String | 产品 ID | prod_001 |
| name | String | 产品名称 | GPT-4 实战教程（含 DEMO） |
| price | Decimal | 价格 | 99.00 |
| description | String | 产品描述 | 包含完整 GitHub DEMO 和源码 |
| status | String | 状态：ACTIVE/INACTIVE | ACTIVE |
| salesCount | Long | 销售数量 | 10 |
| totalRevenue | Decimal | 总收入 | 990.00 |

#### 授权相关

| 字段名 | 类型 | 说明 | 示例值 |
|--------|------|------|--------|
| licenseId | String | 授权 ID | lic_001 |
| grantType | String | 授权类型：MANUAL/ONLINE | MANUAL |
| grantTime | DateTime | 授权时间 | 2026-03-24T12:00:00Z |
| grantNote | String | 授权备注 | 微信支付 |
| status | String | 状态：ACTIVE/REVOKED | ACTIVE |

#### 智能体团队相关

| 字段名 | 类型 | 说明 | 示例值 |
|--------|------|------|--------|
| teamId | String | 团队 ID | team_001 |
| name | String | 团队名称 | ai |
| displayName | String | 展示名称 | 🤖 AI部 |
| botAppId | String | 机器人应用 ID | app_001 |
| managerPrompt | String | 管理员提示词 | 作为 AI 部负责人... |
| status | String | 状态：active/inactive | active |

#### 智能体成员相关

| 字段名 | 类型 | 说明 | 示例值 |
|--------|------|------|--------|
| memberId | String | 成员 ID | mem_001 |
| name | String | 成员名称 | Echo |
| role | String | 角色：manager/worker/assistant | manager |
| personality | String | 人设描述 | AI 部负责人，擅长协调和总结 |
| status | String | 状态：idle/busy/error | idle |
| memorySummary | String | 记忆摘要 | 最近参与了 3 次讨论 |
| avatarUrl | String | 头像 URL | https://cdn.ai-tide.com/agents/echo.png |

#### 协作会话相关

| 字段名 | 类型 | 说明 | 示例值 |
|--------|------|------|--------|
| sessionId | String | 会话 ID | sess_001 |
| title | String | 会话标题 | 新技术评估讨论 |
| topic | String | 讨论主题 | 评估 GPT-4 的实际应用场景 |
| status | String | 状态：pending/running/paused/completed/failed | completed |
| messageCount | Long | 消息数量 | 50 |
| conclusion | String | 结论 | 经过讨论，我们建议... |
| startedAt | DateTime | 开始时间 | 2026-03-24T10:00:00Z |
| completedAt | DateTime | 完成时间 | 2026-03-24T11:00:00Z |

#### 讨论消息相关

| 字段名 | 类型 | 说明 | 示例值 |
|--------|------|------|--------|
| messageId | String | 消息 ID | msg_001 |
| messageType | String | 消息类型：text/system/error | text |
| content | String | 消息内容 | 各位，今天我们讨论 GPT-4... |
| metadata | Object | 元数据 | {"role": "manager", ...} |

---

**文档结束**
