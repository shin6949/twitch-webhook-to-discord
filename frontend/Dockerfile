FROM node:18-alpine AS build

WORKDIR /app

COPY . .
WORKDIR /app/frontend
RUN npm install
RUN npm run build

FROM node:18-alpine
LABEL org.opencontainers.image.source='https://github.com/shin6949/twitch-webhook-to-discord'

WORKDIR /app

COPY --from=build /app/frontend/package*.json ./
RUN npm install --only=production

COPY --from=build /app/frontend/.next ./.next
COPY --from=build /app/frontend/public ./public

EXPOSE 3000

CMD ["npm", "start"]
