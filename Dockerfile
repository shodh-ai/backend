# Use a base image with OpenJDK (Java Runtime)
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /

# Copy the jar file from the host machine to the container
COPY target/ShodhAI-0.0.1-SNAPSHOT.jar ShodhAI-0.0.1-SNAPSHOT.jar

# Expose the port that your Spring Boot app will run on
EXPOSE 8080

# Command to run the jar file
ENV SERVER_PORT=8080
ENV SPRING_SERVER_PORT=8080
ENTRYPOINT ["java", "-Dserver.port=${SERVER_PORT}", "-jar", "ShodhAI-0.0.1-SNAPSHOT.jar"]
