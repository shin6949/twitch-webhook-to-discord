FROM openjdk:11.0.11-jre-slim-buster

ADD ./target/twitchwebhook-*.jar /usr/src/myapp/twitchwebhook.jar

EXPOSE 8080

WORKDIR /usr/src/myapp
CMD ["java", "-jar", "./twitch-webhook.jar"]