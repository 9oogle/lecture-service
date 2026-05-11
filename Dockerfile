FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY build/libs/*.jar app.jar
RUN addgroup -S app && adduser -S app -G app && chown -R app:app /app
USER app
EXPOSE 9005
ENTRYPOINT ["java", "-jar", "app.jar"]