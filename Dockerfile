# ── Stage: runtime ──────────────────────────────────────────────────────────
# Eclipse Temurin 17 JRE on Alpine Linux for a minimal production image.
FROM eclipse-temurin:17-jre-alpine

# Non-root user for improved container security
RUN addgroup -S busko && adduser -S busko -G busko
USER busko

WORKDIR /app

# Copy the fat-jar produced by `mvn package` (explicit name avoids ambiguity)
COPY target/backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
