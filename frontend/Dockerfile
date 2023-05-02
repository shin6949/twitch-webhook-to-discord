FROM node:18-alpine AS build

WORKDIR /app

COPY . .
RUN export API_URL="http://localhost:8080" && \
    npm install && \
    npm run build && \
    unset API_URL

FROM node:18-alpine
LABEL org.opencontainers.image.source='https://github.com/shin6949/twitch-webhook-to-discord'

WORKDIR /app

COPY --from=build /app/package*.json ./
RUN npm install --only=production

COPY --from=build /app/.next ./.next
COPY --from=build /app/public ./public

EXPOSE 3000

CMD ["npm", "start"]
