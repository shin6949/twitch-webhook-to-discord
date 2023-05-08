import "bootstrap/dist/css/bootstrap.min.css";
import React, { useEffect } from "react";
import Head from "next/head";
import type { AppProps } from "next/app";
import Header from "../components/Header";
import { appWithTranslation } from "next-i18next";
import { serverSideTranslations } from "next-i18next/serverSideTranslations";
import { useTranslation } from "next-i18next";
import getConfig from "next/config";

const { publicRuntimeConfig } = getConfig();

// Custom Components
import CustomToast from "../components/CustomToast";
import { useToast, ToastProvider } from "../components/ToastContext";
import firebaseMessaging from "../components/FirebaseMessaging";
import { FirebaseApp, initializeApp } from "firebase/app";
import { router } from "next/client";
import FirebaseContext from "../context/FirebaseContext";

let firebaseApp: FirebaseApp | null = null;

const MyApp = ({ Component, pageProps }: AppProps) => {
  const { t } = useTranslation(["common"]);

  useEffect(() => {
    console.log(publicRuntimeConfig);

    if (!firebaseApp) {
      const firebaseConfig = publicRuntimeConfig.firebase;
      firebaseApp = initializeApp(firebaseConfig);
    }
  }, [router]);

  return (
    <>
      <Head>
        <title>{t("program-name", { ns: "common" })}</title>
      </Head>
      <FirebaseContext.Provider value={firebaseApp}>
        <ToastProvider>
          <Header />
          <Component {...pageProps} />
          <CustomToastWrapper />
        </ToastProvider>
      </FirebaseContext.Provider>
    </>
  );
};

const CustomToastWrapper = () => {
  const { showToast, setShowToast } = useToast();
  firebaseMessaging({ firebaseApp });

  return <CustomToast showToast={showToast} setShowToast={setShowToast} />;
};

MyApp.getStaticProps = async ({ locales }: { locales: string }) => {
  return {
    props: { ...(await serverSideTranslations(locales, ["common"])) },
  };
};

export default appWithTranslation(MyApp);
