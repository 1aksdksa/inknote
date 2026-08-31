# InkNote

Markdown 笔记平台：手机也能用的记录与管理工具。

技术栈：Vue 3.5 前端 + Java Spring Boot 后端（前后端分离）。

## 目录

- `frontend/`：Vue 3.5 + Vite + TypeScript + Pinia + Tailwind + shadcn-vue（端口 `5174`）
- `backend/`：Spring Boot 3.4（Java 21），端口 `8080`
- `docker-compose.yml`：可选 PostgreSQL（需 Docker）

## 本地启动

### 后端

```bash
cd backend
mvn spring-boot:run
```

默认使用 H2 文件库：`backend/data/inknote.mv.db`

### 前端

```bash
cd frontend
npm install
npm run dev
```

打开：http://localhost:5174

## 已实现

- 注册 / 登录（JWT）
- 笔记列表、新建、编辑、删除
- Markdown 编辑 + 预览（手机优先）
- axios 二次封装（驼峰、统一 `{ code, message, data }`）
- Tailwind + shadcn-vue Button

## 主要接口

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/profile`
- `POST /api/note/page`
- `POST /api/note/detail`
- `POST /api/note/save`
- `POST /api/note/delete`