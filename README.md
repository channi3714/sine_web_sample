# SINE Web Sample

---

## 기술 스택

Backend : Java 21, Spring Boot 3.x, Spring Data JPA 
Frontend : React, TypeScript, Vite, Tailwind CSS 
Database : MySQL 8.0 
DevOps : Docker, Docker Compose 
Infrastructure : AWS EC2 (Ubuntu 24.04), Nginx, Certbot (Let's Encrypt) 
Domain : channi-web-sample.shop (Gabia) 

---

## 기술 선택 이유

**Spring Boot 3.x (Java 21)** : 표준적인 엔터프라이즈 백엔드 프레임워크. JPA로 DB 연동이 간결하고, Java 21의 Record를 활용해 DTO를 간결하게 표현 가능 
**React + TypeScript + Vite** : 컴포넌트 기반 UI 구성. TypeScript로 타입 안정성 확보. Vite는 빠른 개발 빌드 속도 
**MySQL 8.0** : BaaS(Firebase 등) 없이 직접 관계형 DB를 구축해 데이터 구조와 쿼리를 직접 이해하기 위함 
**Docker & Docker Compose** : 로컬/서버 환경 차이 없이 동일하게 실행 가능. 서비스별 컨테이너 분리로 관리 편리 
**Nginx (Reverse Proxy)** : 외부에서 8080 포트 직접 노출을 차단. 80/443 트래픽을 받아 내부 컨테이너로 전달. HTTPS 처리 일원화 
**Certbot (Let's Encrypt)** : 무료 SSL 인증서 자동 발급/갱신. HTTPS 적용으로 데이터 암호화 및 신뢰성 확보 
**AWS EC2 (Ubuntu)** : 직접 서버를 관리하며 인프라 구조를 학습하기 위해 PaaS 대신 IaaS 선택 

---

## 전체 아키텍처

사용자 브라우저

[도메인] channi-web-sample.shop
     
[AWS EC2] 3.38.57.160
     
[Nginx] — Reverse Proxy (포트 80/443)
     
/api/*  →  sine_backend (Spring Boot, 포트 8080)
      
sine_db (MySQL 8.0, 포트 3306)

sine_frontend (React, 포트 3000)


외부에서 8080, 3000, 3306 포트는 직접 접근 불가. 모든 트래픽은 Nginx를 통해서만 유입

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
