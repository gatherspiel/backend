FROM eclipse-temurin:17-alpine
COPY target/app.jar /app.jar
EXPOSE 7070
CMD ["java", "-jar", "/app.jar"]
