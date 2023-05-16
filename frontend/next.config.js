if (
  process.env.NODE_ENV === "development" ||
  process.env.BUILD_STATUS === "true"
) {
  require("dotenv").config({ path: `${__dirname}/../.env` });
  console.log("This is development environment. load .env file");
}
const { i18n } = require("./next-i18next.config");

const withPWA = require("next-pwa")({
  pwa: {
    dest: "public",
  },
});

module.exports = {
  async rewrites() {
    const apiURL = process.env.NEXT_PUBLIC_API_URL;
    if (!apiURL) {
      console.error("NEXT_PUBLIC_API_URL environment variable is not defined.");
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
    firebase: {
      apiKey: process.env.NEXT_PUBLIC_FIREBASE_API_KEY || "default-value",
      authDomain:
        process.env.NEXT_PUBLIC_FIREBASE_AUTH_DOMAIN || "default-value",
      projectId: process.env.NEXT_PUBLIC_FIREBASE_PROJECT_ID || "default-value",
      storageBucket:
        process.env.NEXT_PUBLIC_FIREBASE_STORAGE_BUCKET || "default-value",
      messagingSenderId:
        process.env.NEXT_PUBLIC_FIREBASE_MESSAGING_SENDER_ID || "default-value",
      appId: process.env.NEXT_PUBLIC_FIREBASE_APP_ID || "default-value",
      measurementId:
        process.env.NEXT_PUBLIC_FIREBASE_MEASUREMENT_ID || "default-value",
    },
  },
  serverRuntimeConfig: {},
  i18n,
  ...withPWA,
  reactStrictMode: true,
  webpack: (config) => {
    config.resolve.fallback = { fs: false };
    return config;
  },
};
