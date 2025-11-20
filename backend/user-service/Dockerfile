FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app
COPY ./target/user-service-0.0.1-SNAPSHOT.jar /app
EXPOSE 8100
CMD ["java", "-jar", "user-service-0.0.1-SNAPSHOT.jar"]