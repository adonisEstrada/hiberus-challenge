# Multi-stage Dockerfile for Payment Initiation Microservice

# Stage 1: Build stage
FROM gradle:8.11-jdk21-alpine AS builder

WORKDIR /app

# Copy gradle configuration files
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# Copy OpenAPI specification
COPY openapi_specification.yaml ./

# Copy source code
COPY src ./src
COPY config ./config

# Build the application (skip tests for faster builds, tests run in CI/CD)
RUN ./gradlew clean build -x test --no-daemon

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jre-alpine

# Add metadata
LABEL maintainer="hiberus-challenge"
LABEL description="Payment Initiation Microservice - SOAP to REST Migration"
LABEL version="1.0.0"

# Create non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Change ownership to non-root user
RUN chown spring:spring app.jar

# Switch to non-root user
USER spring:spring

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# JVM optimization parameters
ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75.0 -XX:+UseContainerSupport"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
