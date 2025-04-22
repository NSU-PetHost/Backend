#FROM openjdk:23
## Определяем аргумент JAR_FILE, по умолчанию ожидается jar в папке target
#ARG JAR_FILE=target/AuthService-0.0.1-SNAPSHOT.jar
#
#WORKDIR /src/docker-app
#
## Копируем jar-файл в контейнер под именем app.jar
#COPY ${JAR_FILE} /src/docker-app
#
## Указываем порт, на котором приложение будет слушать
#EXPOSE 8080
#
## Определяем команду запуска
#ENTRYPOINT ["java", "-jar", "AuthService-0.0.1-SNAPSHOT.jar"]


# Stage 1: Сборка проекта с использованием Maven
FROM maven:3.9.9 AS builder
WORKDIR /app

# Копируем pom.xml и заранее скачиваем зависимости.
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Копируем исходный код после зависимости для сохранения кэша
COPY src ./src

# Собираем проект (выключая тесты, чтобы ускорить сборку)
RUN mvn clean package -DskipTests

# Stage 2: Финальный образ для запуска приложения
FROM openjdk:23
WORKDIR /src/docker-app
# Копируем собранный jar из предыдущего stage
COPY --from=builder /app/target/AuthService-0.0.1-SNAPSHOT.jar .
EXPOSE 8080
# Запускаем приложение
ENTRYPOINT ["java", "-jar", "AuthService-0.0.1-SNAPSHOT.jar"]
