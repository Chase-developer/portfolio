FROM amazoncorretto:17-alpine AS runtime
WORKDIR /app
COPY build/libs/*.jar app.jar
EXPOSE 80
ENTRYPOINT ["java", "-jar", "app.jar"]



