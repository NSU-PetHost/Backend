# Stage 1: Сборка проекта с использованием Maven
FROM maven:3.9-eclipse-temurin-21 AS builder
# Используем образ Maven с нужной версией JDK (Temurin 21, если ваш проект на Java 21)
# 3.9.9 - это очень свежая версия Maven, можно использовать 3.8.8 или 3.9.6, если есть проблемы
# Добавил -eclipse-temurin-21 для явного указания JDK, совпадающего с вашим проектом.

WORKDIR /app

# Копируем pom.xml и заранее скачиваем зависимости для кэширования.
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Копируем исходный код
COPY src ./src

# Собираем проект (выключая тесты, чтобы ускорить сборку)
# Важно: Maven поместит JAR в /app/target/ в этом контейнере builder
RUN mvn clean package -DskipTests

# Stage 2: Финальный образ для запуска приложения
FROM openjdk:21-jdk-slim
# Используйте версию OpenJDK, соответствующую той, на которой собирался проект (Java 21)
# openjdk:23 может быть избыточным и потенциально вызвать проблемы, если собирали под 21.

WORKDIR /app

# Копируем ТОЛЬКО собранный JAR-файл из стадии 'builder'
# Это ключевой момент для многоступенчатой сборки
COPY --from=builder /app/target/NotificationService-0.0.1-SNAPSHOT.jar notification_service.jar
# ^^^^^^^^^^^^^^^^ Указываем, из какой стадии копировать
# /app/target/       Путь к JAR файлу ВНУТРИ контейнера 'builder'

# Указываем порт, который будет слушать ваше приложение внутри контейнера
EXPOSE 8080

# Команда для запуска вашего приложения
ENTRYPOINT ["java", "-jar", "notification_service.jar"]

#FROM maven:3.9.9 AS builder
#WORKDIR /app
#
## Копируем pom.xml и заранее скачиваем зависимости.
#COPY pom.xml .
#RUN mvn dependency:go-offline -B
#
## Копируем исходный код после зависимости для сохранения кэша
#COPY src ./src
#
## Собираем проект (выключая тесты, чтобы ускорить сборку)
#RUN mvn clean package -DskipTests
#
## Stage 2: Финальный образ для запуска приложения
#FROM openjdk:23
#WORKDIR /app
#
## Копируем собранный JAR-файл вашего приложения в контейнер
## Предполагается, что вы сначала собираете JAR локально (mvn clean package)
## Имя JAR файла может отличаться
#COPY target/NotificationService-0.0.1-SNAPSHOT.jar notification_service.jar
#
## Указываем порт, который будет слушать ваше приложение внутри контейнера
#EXPOSE 8080
#
## Команда для запуска вашего приложения
#ENTRYPOINT ["java", "-jar", "notification_service.jar"]