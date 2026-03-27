# 경로: frontend/Dockerfile

# ── Stage 1: 빌드 단계 ──────────────────────────────────────────
FROM node:20-alpine AS builder

WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .
RUN npm run build

# ── Stage 2: 실행 단계 ──────────────────────────────────────────
FROM nginx:alpine

# 빌드 결과물 복사
COPY --from=builder /app/dist /usr/share/nginx/html

# nginx.conf 복사 (API 프록시 + SPA 라우팅 설정 포함)
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
