# ===== Build Stage =====
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src

# Build the application (skip tests for faster builds)
RUN mvn clean package -DskipTests


# ===== Runtime Stage =====
FROM eclipse-temurin:17-jre

# Set working directory
WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/target/CoinChangeService-1.0-SNAPSHOT.jar app.jar

# Copy the config file (make sure this path is correct)
COPY config.yml config.yml

# Expose the application port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar", "server", "config.yml"]
