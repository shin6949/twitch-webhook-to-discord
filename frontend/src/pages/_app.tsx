import "bootstrap/dist/css/bootstrap.min.css";
import React, { useState } from "react";
import type { AppProps } from "next/app";
import { FirebaseApp, initializeApp } from "firebase/app";
import { getMessaging, onMessage, Messaging, MessagePayload } from "firebase/messaging";
import Header from "../components/Header";
import getConfig from "next/config";
import { Toast, ToastContainer } from "react-bootstrap";
import { appWithTranslation } from "next-i18next";
import { serverSideTranslations } from "next-i18next/serverSideTranslations";

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
  const [showToast, setShowToast] = useState<ToastState>({ show: false, message: "", variant: "secondary" });

  if (typeof window !== "undefined") {
    const messaging: Messaging = getMessaging(app);

    onMessage(messaging, (payload: MessagePayload) => {
      if (payload.notification) {
        setShowToast({
          show: true,
          message: `알림을 정상적으로 받았습니다.\n받은 제목: ${payload.notification.title}\n받은 내용: ${payload.notification.body}`,
          variant: "secondary",
        });
      }
    });
  }

  type ToastState = {
    show: boolean;
    message: string;
    variant: string;
    link?: string;
  };

  return (
    <>
      <Header />
      <Component {...pageProps} />
      <ToastContainer position="bottom-end">
        <Toast bg={showToast.variant} onClose={() => setShowToast({ ...showToast, show: false })} show={showToast.show} delay={3000} autohide>
          <Toast.Body style={{ color: "#ffffff", whiteSpace: "pre-wrap" }}>{showToast.message}</Toast.Body>
        </Toast>
      </ToastContainer>
    </>
  );
}

export const getStaticProps = async ({ locales }: { locales: string }) => {
  console.log(locales);
  return {
    props: { ...(await serverSideTranslations(locales, ["common"])) },
  };
};

export default appWithTranslation(MyApp);
export { app };
