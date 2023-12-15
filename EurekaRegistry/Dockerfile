FROM openjdk:17-alpine

WORKDIR app

COPY target/EurekaRegistry-0.0.1-SNAPSHOT.jar /app

EXPOSE 8032

CMD ["java", "-jar", "/app/EurekaRegistry-0.0.1-SNAPSHOT.jar"]