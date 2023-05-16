FROM mcr.microsoft.com/openjdk/jdk:11-ubuntu AS build
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew clean && ./gradlew build

FROM mcr.microsoft.com/openjdk/jdk:11-ubuntu
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
