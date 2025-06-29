FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and pom.xml first for better caching
COPY mvnw pom.xml ./
COPY .mvn .mvn

RUN chmod +x ./mvnw
# Download dependencies this layer will be cached if pom.xml doesn't change
RUN ./mvnw dependency:go-offline -B

COPY src src

RUN ./mvnw clean package -DskipTests

# ---- Stage 2: Run the application ----
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copy built JAR from the previous stage
COPY --from=builder /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
