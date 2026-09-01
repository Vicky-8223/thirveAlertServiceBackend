# ─── Stage 1: Build ────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper + pom first for layer caching
COPY pom.xml .
COPY src ./src

# Build the fat JAR, skip tests (tests can be run separately in CI)
RUN apk add --no-cache maven && \
    mvn package -DskipTests -q

# ─── Stage 2: Runtime ─────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime

LABEL maintainer="CyberPulse Team"
LABEL service="alert-gateway"

WORKDIR /app

# Copy only the fat JAR from the builder stage
COPY --from=builder /app/target/cyberpulse-gateway-1.0.0.jar app.jar

# Alert Gateway runs on port 9090
EXPOSE 9090

# Health check — wait for Spring Boot actuator (or just the port)
HEALTHCHECK --interval=15s --timeout=5s --start-period=30s --retries=3 \
  CMD wget -qO- http://localhost:9090/actuator/health 2>/dev/null || wget -qO- http://localhost:9090/ 2>/dev/null || exit 1

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
