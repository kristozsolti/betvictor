# Use the Maven base image (includes Maven)
FROM maven:3.9.8-amazoncorretto-21 AS builder
WORKDIR /app
COPY . .
RUN cd shared && mvn clean install
RUN cd /app/processing && mvn package -D skipTests
RUN cd /app/repository && mvn package -D skipTests

# PROCESSING APP IMAGE
FROM openjdk:21-jdk as processing-app-image
WORKDIR /app
COPY --from=builder /app/processing/target/processing-1.0.0.jar processing-app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "processing-app.jar"]

# REPOSITORY APP IMAGE
FROM openjdk:21-jdk as repository-app-image
WORKDIR /app
COPY --from=builder /app/repository/target/repository-1.0.0.jar repository-app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "repository-app.jar"]
