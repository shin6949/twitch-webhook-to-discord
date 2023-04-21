import "bootstrap/dist/css/bootstrap.min.css";
import React, { useState } from "react";
import Head from "next/head";
import type { AppProps } from "next/app";
import { FirebaseApp, initializeApp } from "firebase/app";
import { getMessaging, onMessage, Messaging, MessagePayload } from "firebase/messaging";
import Header from "../components/Header";
import getConfig from "next/config";
import { appWithTranslation } from "next-i18next";
import { serverSideTranslations } from "next-i18next/serverSideTranslations";
import { useTranslation } from "next-i18next";

// Custom Components
import CustomToast, { ToastState } from '../components/CustomToast';

const { publicRuntimeConfig } = getConfig();

// Firebase configuration
const firebaseConfig: Record<string, string> = {
  apiKey: publicRuntimeConfig.FIREBASE_API_KEY as string,
  authDomain: publicRuntimeConfig.FIREBASE_AUTH_DOMAIN as string,
  projectId: publicRuntimeConfig.FIREBASE_PROJECT_ID as string,
  storageBucket: publicRuntimeConfig.FIREBASE_STORAGE_BUCKET as string,
  messagingSenderId: publicRuntimeConfig.FIREBASE_MESSAGING_SENDER_ID as string,
  appId: publicRuntimeConfig.FIREBASE_APP_ID as string,
  measurementId: publicRuntimeConfig.FIREBASE_MEASUREMENT_ID as string,
};

const app: FirebaseApp = initializeApp(firebaseConfig);
function MyApp({ Component, pageProps }: AppProps) {
  const { t } = useTranslation(["common"]);
  const [showToast, setShowToast] = useState<ToastState>({ show: false, message: "", variant: "secondary" });

  if (typeof window !== "undefined") {
    const messaging: Messaging = getMessaging(app);

    onMessage(messaging, (payload: MessagePayload) => {
      if (payload.notification) {
        setShowToast({
          show: true,
          message: t("toast_received_message",
              {title: payload.notification.title, content: payload.notification.body, ns: "common"}),
          variant: "secondary",
        });
      }
    });
  }

  return (
    <>
      <Head>
        <title>{t("program-name", { ns: "common" })}</title>
      </Head>
      <Header />
      <Component {...pageProps} />
      <CustomToast showToast={showToast} setShowToast={setShowToast} />
    </>
  );
}

export const getStaticProps = async ({ locales }: { locales: string }) => {
  return {
    props: { ...(await serverSideTranslations(locales, ["common"])) },
  };
};

export default appWithTranslation(MyApp);
export { app };
