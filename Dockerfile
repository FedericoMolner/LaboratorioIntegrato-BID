# Multi-stage Dockerfile
# Build stage
FROM maven:3.9.4-eclipse-temurin-17 as builder
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -U -DskipTests clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-jammy
ARG JAR_FILE=target/connector-java-0.0.1-SNAPSHOT.jar
COPY --from=builder /workspace/${JAR_FILE} /app/app.jar
WORKDIR /app

# Values may be provided by environment variables at runtime
# If using GOOGLE_CREDENTIALS_JSON (string content), it will be written to disk at start
ENV JAVA_OPTS="-Xms256m -Xmx512m"
EXPOSE 8080

ENTRYPOINT ["sh", "-lc", "if [ -n \"$GOOGLE_CREDENTIALS_JSON\" ]; then echo \"$GOOGLE_CREDENTIALS_JSON\" > /tmp/gcp-creds.json; export GOOGLE_CREDENTIALS_FILE=/tmp/gcp-creds.json; fi; exec java $JAVA_OPTS -jar /app/app.jar --google.credentials.path=${GOOGLE_CREDENTIALS_FILE:-} --connector.mode=${connector.mode:-LOCAL}"]

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
