# Use a base image with OpenJDK (Java Runtime)
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copy the jar file from the host machine to the container
COPY target/ShodhAI-0.0.1-SNAPSHOT.jar ShodhAI-0.0.1-SNAPSHOT.jar

# Expose the port that your Spring Boot app will run on
EXPOSE 8080

# Command to run the jar file - handle both PORT and SERVER_PORT for compatibility
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${SERVER_PORT:-${PORT:-8080}} -jar ShodhAI-0.0.1-SNAPSHOT.jar"]