# Multi-stage Dockerfile for building and running the Task Management System

ARG MAVEN_IMAGE=maven:4.0.0-rc-5-ibm-semeru-25-noble
FROM ${MAVEN_IMAGE} AS build
WORKDIR /workspace

COPY pom.xml .
COPY src ./src

RUN mvn -B -DskipTests package

RUN sh -c "JAR=$(ls target/*.jar 2>/dev/null | head -n1) && if [ -n \"$JAR\" ]; then mv \"$JAR\" target/app.jar; fi"

# Runtime stage: use Eclipse Temurin JRE 11 (widely available)
FROM eclipse-temurin:11-jre
WORKDIR /app

COPY --from=build /workspace/target/*.jar /app/

# Allow overriding the main class via environment variable. Default runs the CLI.
ENV MAIN_CLASS=com.taskmanagement.cli.TaskManagementCLI

ENTRYPOINT ["sh", "-c", "exec java -cp '/app/*' ${MAIN_CLASS} \"$@\""]
