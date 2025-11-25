FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app
COPY ./target/api-gateway-0.0.1-SNAPSHOT.jar /app
EXPOSE 8443
CMD ["java", "-jar", "api-gateway-0.0.1-SNAPSHOT.jar"]