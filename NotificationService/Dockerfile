# Stage 1: Сборка jwt-proto с Gradle
FROM gradle:8.7-jdk21 AS jwt_proto_builder
WORKDIR /jwt-proto

COPY jwt-proto/gradle ./gradle
COPY jwt-proto/gradlew .
COPY jwt-proto/settings.gradle .
COPY jwt-proto/build.gradle .
COPY jwt-proto/src ./src

RUN ./gradlew clean build publishToMavenLocal --no-daemon # --no-daemon для Docker


# Stage 2: Сборка NotificationService с Maven
FROM maven:3.9-eclipse-temurin-21 AS builder
LABEL stage="builder"
WORKDIR /app

COPY NotificationService/pom.xml ./pom.xml

COPY --from=jwt_proto_builder /root/.m2/repository /root/.m2/repository

RUN mvn dependency:go-offline -B -Dmaven.repo.local=/root/.m2/repository # <--- Используем тот же путь

COPY NotificationService/src ./src

RUN mvn clean package -DskipTests -Dmaven.repo.local=/root/.m2/repository # <--- Используем тот же путь


# Stage 3: Финальный образ для запуска NotificationService
FROM openjdk:21-jdk-slim
LABEL stage="runner"
WORKDIR /app

COPY --from=builder /app/target/NotificationService-0.0.1-SNAPSHOT.jar notification_service.jar

EXPOSE 8084
ENTRYPOINT ["java","-jar","notification_service.jar"]
