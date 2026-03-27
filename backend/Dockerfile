# 경로: backend/Dockerfile

# ── Stage 1: 빌드 단계 ──────────────────────────────────────────
# Gradle + JDK 21이 이미 설치된 공식 이미지 사용
# → gradlew(Gradle Wrapper) 파일 없이도 빌드 가능
FROM gradle:8.11-jdk21-alpine AS builder

WORKDIR /app

# 의존성 캐시 최적화: build.gradle만 먼저 복사
# (소스코드가 바뀌어도 의존성이 그대로면 이 레이어는 재사용됨)
COPY build.gradle settings.gradle ./

# 의존성 다운로드 (캐시 레이어 분리)
RUN gradle dependencies --no-daemon 2>/dev/null || true

# 소스코드 복사 후 JAR 빌드
COPY src ./src
RUN gradle bootJar --no-daemon

# ── Stage 2: 실행 단계 ──────────────────────────────────────────
# JRE만 있는 가벼운 이미지로 최종 실행 (JDK 불필요 → 이미지 크기 절감)
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# 빌드 단계에서 만들어진 JAR만 복사
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
