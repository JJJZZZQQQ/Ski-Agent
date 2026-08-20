# 🎿 Ski Agent

> 你的滑雪专属助手 —— 通过「用户档案记忆 + 垂直知识库 + 多步可执行工作流」三件套，做通用大模型做不到的事：懂你、懂滑雪、能帮你把事办完。

> ⚠️ **AI 协作必读**：开始工作前请先阅读 [AGENTS.md](AGENTS.md)，本项目仅使用开源框架，禁止使用任何快手自研框架。

## 项目简介

滑雪 Agent 是一个区别于通用 AI 助手的垂直领域 Agent 产品，MVP 聚焦国内滑雪人群，通过三大差异化能力构建护城河：

1. **用户滑雪档案**：跨会话记忆用户的水平、装备、雪场偏好
2. **轻量化 RAG 知识库**：国内雪场 / 装备 / 动作垂直数据
3. **闭环 Agent 工作流**：多步骤自动完成任务，直接产出可执行计划

## 技术栈

### 前端
- Vue 3 + TypeScript + Vite 5
- Pinia + Vue Router 4（Hash 模式）
- Element Plus（开源 Vue 3 UI 组件库）
- @ag-ui/client + @ag-ui/core（Agent-UI 协议，SSE 流式）
- pnpm Monorepo

### 后端
- Java 17 + Spring Boot 3
- Spring AI Alibaba
- 大模型：阿里云通义千问（Qwen）
- 向量数据库：阿里云 DashVector
- MySQL 8 + Redis 7 + MyBatis-Plus

## MVP 功能清单

- ✅ 用户滑雪档案（基础档案 + 会话记忆）
- ✅ 轻量化 RAG 知识库（雪场 / 装备 / 动作）
- ✅ 三条闭环 Agent 工作流（MVP 优先做「雪场出行规划流」）
- ✅ 基础工具能力（单位换算 / 预算计算）

## 项目文档

- [项目主进度文档](docs/PROJECT_PLAN.md)

## 开发阶段

| 阶段 | 周期 | 状态 |
|---|---|---|
| 阶段1 基础架构 | 2 周 | ⬜ 待启动 |
| 阶段2 档案+RAG+爬虫 | 2 周 | ⬜ 待启动 |
| 阶段3 雪场出行规划流 | 3 周 | ⬜ 待启动 |
| 阶段4 工具+联调 | 1 周 | ⬜ 待启动 |

## License

MIT
