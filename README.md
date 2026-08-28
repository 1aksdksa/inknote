# InkNote

Markdown 笔记平台：手机也能用的记录与管理工具。

技术栈：Vue 3.5 前端 + Java Spring Boot 后端（前后端分离）。

## 目录

- `frontend/`：Vue 3.5 + Vite + TypeScript + Vue Router + Pinia（开发端口 `5174`）
- `backend/`：Spring Boot 3.4（Java 21），端口 `8080`

## 环境要求

- Node.js 20+
- JDK 21
- Maven 3.9+（或使用 IDE 内置 Maven）

## 启动后端

```bash
cd backend
mvn spring-boot:run
```

健康检查：`http://localhost:8080/api/health`

## 启动前端

```bash
cd frontend
npm install
npm run dev
```

打开：`http://localhost:5174`  
前端已将 `/api` 代理到 `http://localhost:8080`。

## 当前能力

- 前后端项目骨架
- CORS 与开发代理
- `/api/health`、`/api/hello` 示例接口
- 首页连通性检查

下一步计划：登录注册、数据库、Markdown 笔记 CRUD、手机优先布局。