FROM mcr.microsoft.com/openjdk/jdk:11-ubuntu

ADD ./target/twitchwebhook-*.jar /usr/src/myapp/twitchwebhook.jar

EXPOSE 8080

WORKDIR /usr/src/myapp
CMD ["java", "-jar", "./twitch-webhook.jar"]