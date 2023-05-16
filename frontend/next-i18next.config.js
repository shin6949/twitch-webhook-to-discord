const path = require("path");

module.exports = {
  i18n: {
    defaultLocale: "ko",
    locales: ["en", "ko"],
  },
  localePath: path.resolve("./public/locales"),
  debug: true,
};
