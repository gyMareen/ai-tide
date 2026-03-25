# 贡献指南

感谢您对 AI-Tide 项目的关注！我们欢迎所有形式的贡献。

## 如何贡献

### 报告问题

如果您发现了 bug 或者有功能建议，请：

1. 检查 [Issues](https://github.com/ai-tide/ai-tide/issues) 确保问题尚未被报告
2. 创建新的 Issue，使用清晰的问题描述
3. 提供复现步骤、预期行为和实际行为
4. 如果可能，提供截图或错误日志

### 提交代码

#### 1. Fork 项目

点击右上角的 Fork 按钮将项目 fork 到您的账号下。

#### 2. 克隆仓库

```bash
git clone https://github.com/YOUR_USERNAME/ai-tide.git
cd ai-tide
```

#### 3. 创建分支

为您的贡献创建一个新的分支：

```bash
git checkout -b feature/your-feature-name
# 或者
git checkout -b fix/your-bug-fix
```

#### 4. 进行修改

- 遵循项目的代码风格
- 为您的更改添加测试
- 更新相关文档

#### 5. 提交更改

```bash
git add .
git commit -m "feat: add your feature or fix: fix your issue"
```

提交消息格式：
- `feat:` 新功能
- `fix:` 修复 bug
- `docs:` 文档更新
- `style:` 代码格式调整
- `refactor:` 重构
- `test:` 测试相关
- `chore:` 构建/工具链相关

#### 6. 推送到您的 fork

```bash
git push origin feature/your-feature-name
```

#### 7. 创建 Pull Request

访问 GitHub 并创建 Pull Request 到我们的主仓库。

## 代码规范

### 后端 (Java)

- 遵循 Google Java Style Guide
- 使用 4 空格缩进
- 方法名使用驼峰命名
- 类名使用大驼峰命名
- 常量使用全大写下划线分隔

### 前端 (Vue/TypeScript)

- 使用 2 空格缩进
- 组件名使用大驼峰命名
- 文件名使用短横线命名
- 使用 TypeScript 类型注解
- 遵循 Vue 3 Composition API 最佳实践

## 开发流程

### 1. 设置开发环境

请参考 [README.md](README.md) 中的快速开始部分。

### 2. 运行测试

```bash
# 后端测试
cd ai-tide-api
mvn test

# 前端测试
cd ai-tide-web
npm test
```

### 3. 代码检查

```bash
# 后端格式检查
cd ai-tide-api
mvn checkstyle:check

# 前端代码检查
cd ai-tide-web
npm run lint
```

## 提交检查

在提交 Pull Request 之前，请确保：

- [ ] 代码通过所有测试
- [ ] 代码检查没有警告
- [ ] 提交消息符合规范
- [ ] 相关文档已更新
- [ ] 没有引入不必要的依赖
- [ ] 没有包含敏感信息

## 设计建议

如果您想对项目设计提出建议：

1. 先创建 Issue 讨论您的想法
2. 获得社区反馈后再开始实现
3. 使用 OpenSpec 工作流程管理大型变更

## 文档贡献

文档也是重要的贡献形式：

- 修正错误
- 添加使用示例
- 翻译文档
- 改善代码注释

## 行为准则

### 尊重他人

- 尊重不同的观点和经验
- 接受建设性批评
- 关注对社区最有利的事情

### 专业沟通

- 使用清晰、专业的语言
- 避免情绪化的表达
- 认真对待其他贡献者的反馈

## 许可证

通过向项目贡献，您同意您的贡献将在与项目相同的 MIT 许可证下发布。

## 获取帮助

如果您在贡献过程中遇到问题：

- 在 Issue 中提问并标注 `help wanted`
- 加入我们的 [Discord 社区](https://discord.gg/ai-tide)
- 发送邮件至 support@ai-tide.com

## 致谢

感谢所有贡献者让 AI-Tide 变得更好！

---

**Happy Coding!** 🚀
