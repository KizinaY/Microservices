FROM openjdk:17-alpine

WORKDIR app

COPY target/ResourceService-0.0.1-SNAPSHOT.jar /app

EXPOSE 8030

CMD ["java", "-jar", "ResourceService-0.0.1-SNAPSHOT.jar"]