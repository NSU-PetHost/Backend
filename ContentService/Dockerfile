# Stage 0: Сборка jwt-proto
FROM gradle:8.7-jdk21 AS jwt_proto_builder
WORKDIR /jwt-proto
COPY jwt-proto/gradle ./gradle
COPY jwt-proto/gradlew .
COPY jwt-proto/settings.gradle .
COPY jwt-proto/build.gradle .
COPY jwt-proto/src ./src

# Cache dependencies
RUN ./gradlew dependencies

RUN ./gradlew clean build publishToMavenLocal

# Stage 1: Сборка ContentService
FROM gradle:8.7-jdk21 AS builder
WORKDIR /app
COPY ContentService/gradle ./gradle
COPY ContentService/gradlew .
COPY ContentService/settings.gradle .
COPY ContentService/build.gradle .
COPY ContentService/src ./src

# сразу подрубаем тот самый ~/.m2, куда Gradle выложил jwt-proto
COPY --from=jwt_proto_builder /root/.m2 /root/.m2

RUN ./gradlew clean bootJar -x test

# Stage 2: Финальный образ с OpenJDK
FROM openjdk:23

# Рабочая директория в контейнере
WORKDIR /app

# Копируем собранный JAR-файл из builder
COPY --from=builder /app/build/libs/*.jar ContentService.jar

EXPOSE 8081

VOLUME ["/app/uploads"]
# Запуск Spring Boot приложения
ENTRYPOINT ["java", "-jar", "ContentService.jar"]
