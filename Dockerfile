# Stage 1

FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2

FROM eclipse-temurin:17-jre-alpine

ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS=""

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]