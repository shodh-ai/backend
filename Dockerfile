FROM openjdk:17-jdk-slim

WORKDIR /

COPY target/ShodhAI-0.0.1-SNAPSHOT.jar ShodhAI-0.0.1-SNAPSHOT.jar

EXPOSE 8080

# Providing fallbacks while prioritizing SERVER_PORT
ENV SERVER_PORT=8080
ENV PORT=8080

# Simple startup command similar to the working version
ENTRYPOINT ["java", "-jar", "ShodhAI-0.0.1-SNAPSHOT.jar"]