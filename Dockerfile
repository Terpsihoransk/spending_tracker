FROM eclipse-temurin:25-jdk
COPY backend/target/spending-tracker-backend-0.0.1-SNAPSHOT.jar app.jar
COPY .env /app/.env
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app.jar"]