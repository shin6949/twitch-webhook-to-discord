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
import firebaseMessaging from "../components/FirebaseMessaging";

function MyApp({ Component, pageProps }: AppProps) {
  const { t } = useTranslation(["common"]);

  return (
    <>
      <Head>
        <title>{t("program-name", { ns: "common" })}</title>
      </Head>
      <ToastProvider>
        <Header />
        <Component {...pageProps} />
        <CustomToastWrapper />
      </ToastProvider>
    </>
  );
}

const CustomToastWrapper = () => {
  const { showToast, setShowToast } = useToast();
  firebaseMessaging();

  return <CustomToast showToast={showToast} setShowToast={setShowToast} />;
};

export const getStaticProps = async ({ locales }: { locales: string }) => {
  return {
    props: { ...(await serverSideTranslations(locales, ["common"])) },
  };
};

export default appWithTranslation(MyApp);
