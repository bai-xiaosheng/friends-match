# Docker 镜像构建
#FROM maven:3.9.6 as builder
#FROM maven:3.9.6-eclipse-temurin-8-focal AS builder
## Copy local code to the container image.
WORKDIR /app
#COPY pom.xml .
#COPY src ./src

# Build a release artifact.
# RUN mvn clean install -U
# RUN mvn package -DskipTests -e -X

# Final stage
# FROM openjdk:8-jre
FROM eclipse-temurin:8-jre

# Copy the jar from the builder stage to the final stage
#COPY --from=builder /app/target/friends-backend-0.0.1-SNAPSHOT.jar /app/
COPY target/friends-backend-0.0.1-SNAPSHOT.jar /app/friends-backend-0.0.1-SNAPSHOT.jar
# Run the web service on container startup.
CMD ["java", "-jar", "/app/friends-backend-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=prod"]