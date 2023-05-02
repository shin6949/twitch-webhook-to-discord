if (process.env.NODE_ENV === "development") {
  require("dotenv").config({ path: `${__dirname}/../.env` });
  console.log("This is development environment. load .env file");
}
const { i18n } = require("./next-i18next.config");

const FIREBASE_API_KEY = process.env.FIREBASE_API_KEY;
const FIREBASE_AUTH_DOMAIN = process.env.FIREBASE_AUTH_DOMAIN;
const FIREBASE_PROJECT_ID = process.env.FIREBASE_PROJECT_ID;
const FIREBASE_STORAGE_BUCKET = process.env.FIREBASE_STORAGE_BUCKET;
const FIREBASE_MESSAGING_SENDER_ID = process.env.FIREBASE_MESSAGING_SENDER_ID;
const FIREBASE_APP_ID = process.env.FIREBASE_APP_ID;
const FIREBASE_MEASUREMENT_ID = process.env.FIREBASE_MEASUREMENT_ID;

console.log(FIREBASE_API_KEY);
console.log(FIREBASE_AUTH_DOMAIN);
console.log(FIREBASE_PROJECT_ID);
console.log(FIREBASE_STORAGE_BUCKET);
console.log(FIREBASE_MESSAGING_SENDER_ID);
console.log(FIREBASE_APP_ID);
console.log(FIREBASE_MEASUREMENT_ID);

const withPWA = require("next-pwa")({
  pwa: {
    dest: "public",
  },
});

module.exports = {
  async rewrites() {
    const apiURL = process.env.API_URL;
    if (!apiURL) {
      console.error("API_URL environment variable is not defined.");
    }
    const destination =
      (apiURL?.charAt(apiURL.length - 1) === "/"
        ? apiURL.substring(0, apiURL.length - 2)
        : apiURL) + "/api/:path*";
    return [
      {
        source: "/api/:path*",
        destination,
      },
    ];
  },
  publicRuntimeConfig: {
    FIREBASE_API_KEY: FIREBASE_API_KEY,
    FIREBASE_AUTH_DOMAIN: FIREBASE_AUTH_DOMAIN,
    FIREBASE_PROJECT_ID: FIREBASE_PROJECT_ID,
    FIREBASE_STORAGE_BUCKET: FIREBASE_STORAGE_BUCKET,
    FIREBASE_MESSAGING_SENDER_ID: FIREBASE_MESSAGING_SENDER_ID,
    FIREBASE_APP_ID: FIREBASE_APP_ID,
    FIREBASE_MEASUREMENT_ID: FIREBASE_MEASUREMENT_ID,
  },
  i18n,
  ...withPWA,
  reactStrictMode: true,
  webpack: (config) => {
    config.resolve.fallback = { fs: false };

    return config;
  },
};
