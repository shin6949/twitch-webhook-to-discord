FROM mcr.microsoft.com/openjdk/jdk:11-ubuntu
LABEL org.opencontainers.image.source='https://github.com/shin6949/twitch-webhook-to-discord'

ADD ./build/libs/twitchwebhook-*.jar /usr/src/myapp/twitchwebhook.jar

EXPOSE 8080

WORKDIR /usr/src/myapp
CMD ["java", "-jar", "./twitchwebhook.jar"]