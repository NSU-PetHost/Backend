# Stage 0: собираем и публикуем jwt-proto в свой локальный ~/.m2 внутри контейнера
FROM gradle:8.7-jdk21 AS jwt_proto
WORKDIR /jwt-proto
#COPY jwt-proto/ .
COPY jwt-proto/gradle ./gradle
COPY jwt-proto/gradlew .
COPY jwt-proto/settings.gradle .
COPY jwt-proto/build.gradle .
COPY jwt-proto/src ./src

# Cache dependencies
RUN ./gradlew dependencies

RUN ./gradlew clean build publishToMavenLocal

# Stage 1: собираем AuthService c помощью Maven
FROM maven:3.9.9 AS builder
WORKDIR /app

COPY AuthenticationService/pom.xml .

# сразу подрубаем тот самый ~/.m2, куда Gradle выложил jwt-proto
COPY --from=jwt_proto /root/.m2 /root/.m2

RUN mvn dependency:go-offline -Dmaven.repo.local=/root/.m2

# копируем код и собираем сам сервис
COPY AuthenticationService/src ./src
RUN mvn clean package -DskipTests -Dmaven.repo.local=/root/.m2

# Stage 2: финальный образ
FROM openjdk:23-jdk
WORKDIR /app
COPY --from=builder /app/target/AuthService-0.0.1-SNAPSHOT.jar ./AuthService.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "AuthService.jar"]
