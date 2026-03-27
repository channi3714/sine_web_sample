# 경로: backend/Dockerfile

# ── Stage 1: 빌드 단계 ──────────────────────────────────────────
# Gradle이 설치된 JDK 21 이미지로 JAR 파일 빌드
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# 의존성 캐시 최적화: build.gradle만 먼저 복사해서 레이어 캐싱
# (소스코드가 바뀌어도 의존성이 그대로면 이 레이어는 재사용됨)
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY gradlew ./

# Gradle Wrapper 실행 권한 부여
RUN chmod +x gradlew

# 의존성 다운로드 (캐시 레이어 분리)
RUN ./gradlew dependencies --no-daemon 2>/dev/null || true

# 소스코드 복사 후 빌드
COPY src ./src
RUN ./gradlew bootJar --no-daemon

# ── Stage 2: 실행 단계 ──────────────────────────────────────────
# JRE만 있는 가벼운 이미지로 최종 실행 (JDK 불필요 → 이미지 크기 절감)
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# 빌드 단계에서 만들어진 JAR만 복사
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

# JAR 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
