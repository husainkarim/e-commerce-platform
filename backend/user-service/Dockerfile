FROM nexus:5001/eclipse-temurin:21-jdk-alpine

WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8100
CMD ["java", "-jar", "app.jar"]