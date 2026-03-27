# 경로: frontend/Dockerfile

# ── Stage 1: 빌드 단계 ──────────────────────────────────────────
# Node.js로 React 앱을 정적 파일(HTML/CSS/JS)로 빌드
FROM node:20-alpine AS builder

WORKDIR /app

# 의존성 캐시 최적화
COPY package*.json ./
RUN npm ci

# 소스코드 복사 후 빌드
COPY . .
RUN npm run build

# ── Stage 2: 실행 단계 ──────────────────────────────────────────
# Nginx로 빌드된 정적 파일 서빙 (Node.js 필요 없음 → 이미지 크기 절감)
FROM nginx:alpine

# 빌드 결과물을 Nginx가 서빙하는 폴더로 복사
COPY --from=builder /app/dist /usr/share/nginx/html

# SPA(Single Page App) 라우팅을 위한 Nginx 설정
# /profile, /login 등 React Router 경로를 index.html로 보내줘야 함
RUN echo 'server { \
    listen 80; \
    location / { \
        root /usr/share/nginx/html; \
        index index.html; \
        try_files $uri $uri/ /index.html; \
    } \
}' > /etc/nginx/conf.d/default.conf

EXPOSE 80
