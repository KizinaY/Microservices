FROM openjdk:17-alpine

WORKDIR app

COPY target/gateway-0.0.1-SNAPSHOT.jar /app

EXPOSE 8035

CMD ["java", "-jar", "/app/gateway-0.0.1-SNAPSHOT.jar"]