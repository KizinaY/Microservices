FROM openjdk:17-alpine

WORKDIR app

COPY target/StorageServ-0.0.1-SNAPSHOT.jar /app

EXPOSE 8034

CMD ["java", "-jar", "/app/StorageServ-0.0.1-SNAPSHOT.jar"]