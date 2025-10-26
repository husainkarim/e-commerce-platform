# Use a maintained Temurin base image with an up-to-date Ubuntu (Jammy) rootfs
FROM eclipse-temurin:21-jdk-alpine AS builder

# Set working directory
WORKDIR /app

# Copy the project files
COPY pom.xml .
COPY mvnw .
COPY .mvn ./.mvn

RUN chmod +x mvnw

COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Adjust 'target/*.jar' if your JAR name is more specific or you are using Gradle
ARG JAR_FILE=target/*.jar


FROM eclipse-temurin:21-jre-alpine AS runner

# Set working directory
WORKDIR /app

# Expose the service port
EXPOSE 8080

# Copy the built jar from the builder stage
COPY --from=builder /app/${JAR_FILE} /app/app.jar

# Run the application
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]