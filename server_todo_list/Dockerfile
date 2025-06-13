FROM gradle:8.5-jdk17 AS builder
COPY . /app
WORKDIR /app
RUN gradle shadowJar

FROM openjdk:17-jdk-alpine
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]