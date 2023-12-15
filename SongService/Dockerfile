FROM openjdk:17-alpine

WORKDIR app

COPY target/SongService-0.0.1-SNAPSHOT.jar /app

EXPOSE 8031

CMD ["java", "-jar", "/app/SongService-0.0.1-SNAPSHOT.jar"]