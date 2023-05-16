FROM node:18-alpine
LABEL org.opencontainers.image.source='https://github.com/shin6949/twitch-webhook-to-discord'

WORKDIR /app
COPY . .
RUN rm -rf node_modules && npm install

EXPOSE 3000

CMD npm run build && npm start