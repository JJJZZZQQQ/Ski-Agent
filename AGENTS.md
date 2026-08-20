# AGENTS.md — 滑雪 Agent 项目 AI 协作规则

> 本文件是滑雪 Agent 项目的 AI 协作规则，**仅适用于本项目**，随 git 仓库一起管理。
> 所有 AI 代理（Claude / CodeFlicker / Codex / Cursor 等）在本项目工作前必须先阅读本文件，并严格遵守以下规则。

---

## 一、项目简介

**项目名称**：滑雪 Agent（Ski Agent）

**项目定位**：区别于通用 AI 助手的垂直领域 Agent 产品，通过「用户档案记忆 + 垂直知识库 + 多步可执行工作流」三件套，做通用大模型做不到的事。

**项目性质**：**外部独立项目**，不关联任何公司内部系统。

---

## 二、硬性约束（必须遵守）

### ⛔ 规则 1：禁止使用快手自研框架，只能用开源框架

本项目为**外部独立项目**，**严禁**在任何方案、文档、代码、注释、commit message、分支命名中出现任何快手自研框架、内部工具、内部平台的字样。

**禁止词清单**（包括但不限于）：
- `快手`
- `kwai` / `@kwai` / `@kwai-design` / `@kwai-*`
- `Gundam` / `gundam`（快手内部脚手架）
- 任何快手内部平台名称（kconf / kswitch / ktrace / klog / halo / kfc / kdev / kess / mq / kgw / abtest 等）
- 任何快手内部域名（`*.kuaishou.com` / `*.corp.kuaishou.com`）

**替代方案**：

| 用途 | 禁止使用 | 开源替代 |
|---|---|---|
| Vue 3 UI 组件库 | `@kwai-design/web-vue` | **Element Plus** |
| 前端脚手架 | Gundam 脚手架 | **pnpm + Vite 原生构建** |
| 向量数据库 | 任何内部向量服务 | **阿里云 DashVector**（或 Milvus / Qdrant 等开源方案） |
| 大模型 | 任何内部模型 | **阿里云通义千问 Qwen** |
| 配置管理 | kconf | **application.yml + .env** |
| 日志 | klog | **Logback / SLF4J** |
| 特性开关 | kswitch | **功能开关自建表 + Redis 缓存** |

**适用范围**：
- ✅ 所有产品方案文档
- ✅ 所有技术设计文档
- ✅ 所有源代码（含注释、变量名、配置项）
- ✅ 所有 commit message、分支名、PR 标题
- ✅ 所有 AI 生成的回复

**违规处理**：一旦发现违规字样，必须立即替换为开源方案并重新提交。

---

## 三、技术栈（已确认，全部开源）

### 前端
- Vue 3 + TypeScript + Vite 5
- Pinia + Vue Router 4（Hash 模式）
- **Element Plus**（开源 Vue 3 UI 组件库）
- @ag-ui/client + @ag-ui/core（Agent-UI 协议，SSE 流式）
- pnpm Monorepo + Vite 原生构建
- Node ≥ 20

### 后端
- Java 17 + Spring Boot 3
- Spring AI Alibaba
- 大模型：阿里云通义千问（Qwen）
- 向量数据库：阿里云 DashVector
- MySQL 8 + Redis 7 + MyBatis-Plus

### 通信协议
- 前后端通过 AG-UI 协议（SSE 流式）通信

---

## 四、协作规范

### 4.1 文档规范
- 所有产品/技术文档放在 `docs/` 目录下
- 主进度文档：`docs/PROJECT_PLAN.md`
- 阶段文档：`docs/phases/phase-N-*.md`（后续按阶段创建）

### 4.2 提交规范
- commit message 使用 Conventional Commits 规范（`chore: / feat: / fix: / docs:` 等）
- 一个 commit 只做一件事，避免大而全的提交

### 4.3 代码规范
- 前端：Composition API + `<script setup>` + TypeScript
- 后端：遵循阿里巴巴 Java 开发手册
- 所有函数添加函数级注释

### 4.4 分支规范
- `main`：主干分支，保持可发布状态
- 功能分支：`feat/xxx`、`fix/xxx`、`docs/xxx`

---

## 五、MVP 边界（不可越界）

### ✅ MUST HAVE（MVP 必做）
1. 用户滑雪档案（基础档案 + 会话记忆）
2. 轻量化 RAG 知识库（雪场 / 装备 / 动作）
3. 三条闭环 Agent 工作流（MVP 优先做「雪场出行规划流」）
4. 基础工具能力（单位换算 / 预算计算）

### ⚠️ SHOULD HAVE（二期迭代，MVP 不做）
- 图片识别、实时数据对接、社交模块、多模态、复杂长周期记忆、多语言

### ❌ MUST NOT HAVE（绝对不做）
- 开放式闲聊、通用旅游百科、长篇教学课文本、通用生活问答

---

## 六、AI 代理工作前自检清单

每次在本项目工作前，AI 代理必须自检：

- [ ] 已阅读本 `AGENTS.md`
- [ ] 本次任务的所有产出（文档/代码/注释/commit）不含快手相关字样
- [ ] 选用的框架/工具均为开源方案
- [ ] 遵守 MVP 边界，不做二期功能
- [ ] 遵循文档规范（`docs/` 目录 + Conventional Commits）

---

*最后更新：2026-08-20*
