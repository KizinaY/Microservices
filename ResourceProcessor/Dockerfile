FROM openjdk:17-alpine

WORKDIR app

COPY target/ResourceProcessor-0.0.1-SNAPSHOT.jar /app

EXPOSE 8035

CMD ["java", "-jar", "ResourceProcessor-0.0.1-SNAPSHOT.jar"]