FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /build

COPY . .

RUN cd infra/config_server && mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy
VOLUME /tmp
COPY --from=builder /build/infra/config_server/target/*.jar app.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]