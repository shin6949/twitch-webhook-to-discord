FROM mcr.microsoft.com/openjdk/jdk:11-ubuntu AS build
WORKDIR /app
COPY . .
WORKDIR /app/backend
RUN chmod +x ./gradlew
RUN ./gradlew clean && ./gradlew build

FROM mcr.microsoft.com/openjdk/jdk:11-ubuntu
WORKDIR /app
COPY --from=build /app/backend/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
