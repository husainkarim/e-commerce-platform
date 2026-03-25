FROM nexus:5000/eclipse-temurin:21-jdk-alpine

WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8200
CMD ["java", "-jar", "app.jar"]