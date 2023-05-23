import "bootstrap/dist/css/bootstrap.min.css";
import React from "react";
import Head from "next/head";
import type { AppProps } from "next/app";
import Header from "../components/Header";
import { appWithTranslation } from "next-i18next";
import { serverSideTranslations } from "next-i18next/serverSideTranslations";
import { useTranslation } from "next-i18next";

// Custom Components
import CustomToast from "../components/CustomToast";
import { useToast, ToastProvider } from "../components/ToastContext";
import FirebaseMessaging from "../components/FirebaseMessaging";
import { FirebaseProvider } from "../context/FirebaseContext";

const MyApp = ({ Component, pageProps }: AppProps) => {
  const { t } = useTranslation(["common"]);

  return (
    <>
      <Head>
        <title>{t("program-name", { ns: "common" })}</title>
      </Head>
      <FirebaseProvider>
        <ToastProvider>
          <Header />
          <Component {...pageProps} />
          <CustomToastWrapper />
        </ToastProvider>
      </FirebaseProvider>
    </>
  );
};

const CustomToastWrapper = () => {
  const { showToast, setShowToast } = useToast();

  return (
    <>
      <FirebaseMessaging />
      <CustomToast showToast={showToast} setShowToast={setShowToast} />
    </>
  );
};

MyApp.getStaticProps = async ({ locales }: { locales: string }) => {
  return {
    props: { ...(await serverSideTranslations(locales, ["common"])) },
  };
};

export default appWithTranslation(MyApp);
