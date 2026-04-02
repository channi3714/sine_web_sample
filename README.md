# SINE Web Sample

사이버보안 동아리 **SINE**의 웹 기초 실습 프로젝트입니다.
회원가입, 로그인, 프로필 조회 기능을 직접 구현하고 AWS EC2에 배포합니다.

---

## 기술 스택

| 분류 | 기술 |
|------|------|
| Backend | Java 21, Spring Boot 3.x, Spring Data JPA |
| Frontend | React, TypeScript, Vite, Tailwind CSS |
| Database | MySQL 8.0 |
| DevOps | Docker, Docker Compose |
| Infrastructure | AWS EC2 (Ubuntu 24.04), Nginx, Certbot (Let's Encrypt) |
| Domain | channi-web-sample.shop (Gabia) |

---

## 기술 선택 이유

| 기술 | 선택 이유 |
|------|-----------|
| **Spring Boot 3.x (Java 21)** | 표준적인 엔터프라이즈 백엔드 프레임워크. JPA로 DB 연동이 간결하고, Java 21의 Record를 활용해 DTO를 간결하게 표현 가능 |
| **React + TypeScript + Vite** | 컴포넌트 기반 UI 구성. TypeScript로 타입 안정성 확보. Vite는 빠른 개발 빌드 속도 |
| **MySQL 8.0** | BaaS(Firebase 등) 없이 직접 관계형 DB를 구축해 데이터 구조와 쿼리를 직접 이해하기 위함 |
| **Docker & Docker Compose** | 로컬/서버 환경 차이 없이 동일하게 실행 가능. 서비스별 컨테이너 분리로 관리 편리 |
| **Nginx (Reverse Proxy)** | 외부에서 8080 포트 직접 노출을 차단. 80/443 트래픽을 받아 내부 컨테이너로 전달. HTTPS 처리 일원화 |
| **Certbot (Let's Encrypt)** | 무료 SSL 인증서 자동 발급/갱신. HTTPS 적용으로 데이터 암호화 및 신뢰성 확보 |
| **AWS EC2 (Ubuntu)** | 직접 서버를 관리하며 인프라 구조를 학습하기 위해 PaaS 대신 IaaS 선택 |

---

## 전체 아키텍처

```
사용자 브라우저
      │
      ▼
[도메인] channi-web-sample.shop
      │
      ▼
[AWS EC2] 3.38.57.160
      │
      ▼
[Nginx] — Reverse Proxy (포트 80/443)
      │
      ├──▶ /api/*  →  sine_backend (Spring Boot, 포트 8080)
      │                     │
      │                     ▼
      │              sine_db (MySQL 8.0, 포트 3306)
      │
      └──▶ /*      →  sine_frontend (React, 포트 3000)
```

> **핵심 원칙**: 외부에서 8080, 3000, 3306 포트는 직접 접근 불가. 모든 트래픽은 Nginx를 통해서만 유입.

---

## 디렉토리 구조

```
.
├── backend/          # Spring Boot 애플리케이션
├── frontend/         # React 애플리케이션
├── nginx/
│   └── nginx.conf    # Nginx Reverse Proxy 설정
└── docker-compose.yml
```

---

## 배포 과정

### 1단계 — 로컬 개발 환경

`docker-compose.yml` 하나로 전체 스택 실행:

```bash
docker compose up -d --build
```

### 2단계 — AWS 인프라 준비

1. EC2 인스턴스 생성 (Ubuntu 24.04 LTS, t2.micro)
2. 보안 그룹 인바운드 규칙 설정:

| 포트 | 프로토콜 | 소스 | 용도 |
|------|----------|------|------|
| 22 | TCP | 0.0.0.0/0 | SSH 접속 |
| 80 | TCP | 0.0.0.0/0 | HTTP |
| 443 | TCP | 0.0.0.0/0 | HTTPS |

3. 탄력적 IP(Elastic IP) 할당 후 인스턴스에 연결 (재시작 시 IP 고정)

### 3단계 — 도메인 연결

가비아 DNS 관리에서 A레코드 추가:

| 타입 | 호스트 | 값 |
|------|--------|-----|
| A | @ | 탄력적 IP |
| A | www | 탄력적 IP |

### 4단계 — EC2 서버 세팅

```bash
# Docker 설치
curl -fsSL https://get.docker.com | sudo sh
sudo usermod -aG docker ubuntu

# Docker Compose 플러그인 설치
sudo apt install docker-compose-plugin -y

# 레포 클론 & 실행
git clone https://github.com/channi3714/sine_web_sample.git ~/app
cd ~/app
docker compose up -d --build
```

### 5단계 — Nginx + HTTPS 설정

```bash
# Nginx 설치
sudo apt install nginx -y

# nginx.conf 적용
sudo cp ~/app/nginx/nginx.conf /etc/nginx/nginx.conf

# Certbot으로 SSL 인증서 발급
sudo certbot --nginx -d channi-web-sample.shop -d www.channi-web-sample.shop

# Nginx 재시작
sudo systemctl reload nginx
```

---

## 트러블슈팅

### SSH 접속 불가 (Connection Timeout)
- **원인**: Docker 빌드 중 메모리 사용률 86% 도달 → SSH 데몬 응답 불가
- **해결**: AWS 콘솔의 EC2 Instance Connect (브라우저 터미널)로 접속 후 Docker 재시작
```bash
sudo systemctl restart docker
```

### `docker compose ps` 명령어 무한 대기
- **원인**: Docker 데몬이 메모리 부족으로 응답 불가 상태
- **해결**: Docker 재시작 후 컨테이너 재실행
```bash
sudo systemctl restart docker
cd ~/app && docker compose up -d
```

### EC2 재시작마다 IP 변경
- **원인**: EC2 기본 퍼블릭 IP는 재시작마다 변경됨
- **해결**: 탄력적 IP(Elastic IP) 할당 후 인스턴스에 연결하여 고정 IP 확보

---

## Reverse Proxy와 HTTPS의 보안 이점

### Reverse Proxy (Nginx)를 사용하는 이유

직접 `http://IP:8080` 으로 백엔드를 노출하면:
- 어떤 포트에 어떤 서비스가 있는지 외부에 노출됨
- 포트 스캔 공격에 취약
- 여러 서비스를 하나의 도메인으로 묶기 어려움

Nginx를 앞에 두면:
- 외부는 80/443 포트만 접근 가능
- 내부 구조(8080, 3000 등)가 완전히 숨겨짐
- 하나의 도메인으로 프론트/백엔드 라우팅 가능

### HTTPS (SSL/TLS)의 이점

- **암호화**: 사용자와 서버 간 데이터를 암호화 → 로그인 정보, 토큰 등 탈취 방지
- **무결성**: 중간에 데이터가 변조되지 않음을 보장
- **신뢰성**: 브라우저 주소창의 자물쇠 아이콘 → 사용자 신뢰 확보
- **SEO**: 검색엔진이 HTTPS 사이트를 우선순위로 처리
