require("dotenv").config({ path: `${__dirname}/../.env` });
const withPWA = require("next-pwa");
const withPlugins = require("next-compose-plugins");
const { i18n } = require("./next-i18next.config");

module.exports = withPlugins(
  [
    [
      withPWA,
      {
        pwa: {
          dest: "public",
        },
      },
    ],
  ],
  {
    async rewrites() {
      return [
        {
          source: "/api/:path*",
          destination: "http://localhost:8080/api/:path*", // 프록시 대상 주소
        },
        {
          source: "/:path*",
          destination: "http://localhost:8080/:path*", // 프록시 대상 주소
        },
      ];
    },

    publicRuntimeConfig: {
      FIREBASE_API_KEY: process.env.FIREBASE_API_KEY,
      FIREBASE_AUTH_DOMAIN: process.env.FIREBASE_AUTH_DOMAIN,
      FIREBASE_PROJECT_ID: process.env.FIREBASE_PROJECT_ID,
      FIREBASE_STORAGE_BUCKET: process.env.FIREBASE_STORAGE_BUCKET,
      FIREBASE_MESSAGING_SENDER_ID: process.env.FIREBASE_MESSAGING_SENDER_ID,
      FIREBASE_APP_ID: process.env.FIREBASE_APP_ID,
      FIREBASE_MEASUREMENT_ID: process.env.FIREBASE_MEASUREMENT_ID,
    },
  },
  [i18n]
);
