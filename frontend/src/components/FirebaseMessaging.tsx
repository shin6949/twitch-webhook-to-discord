import { useEffect } from "react";
import { FirebaseApp, initializeApp } from "firebase/app";
import {
  getMessaging,
  onMessage,
  Messaging,
  MessagePayload,
} from "firebase/messaging";
import { useToast } from "./ToastContext";
import getConfig from "next/config";
import { useTranslation } from "next-i18next";

let { publicRuntimeConfig } = getConfig();

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

let app: FirebaseApp = initializeApp(firebaseConfig);

const firebaseMessaging = () => {
  const { t } = useTranslation(["common"]);
  const { setShowToast } = useToast();

  console.log(publicRuntimeConfig.FIREBASE_API_KEY as string);
  console.log(publicRuntimeConfig.FIREBASE_AUTH_DOMAIN as string);
  console.log(publicRuntimeConfig.FIREBASE_PROJECT_ID as string);
  console.log(publicRuntimeConfig.FIREBASE_STORAGE_BUCKET as string);
  console.log(publicRuntimeConfig.FIREBASE_MESSAGING_SENDER_ID as string);
  console.log(publicRuntimeConfig.FIREBASE_APP_ID as string);
  console.log(publicRuntimeConfig.FIREBASE_MEASUREMENT_ID as string);

  if (!app) {
    publicRuntimeConfig = getConfig();
    app = initializeApp(firebaseConfig);
  }

  useEffect(() => {
    if (typeof window !== "undefined") {
      const messaging: Messaging = getMessaging(app);

      onMessage(messaging, (payload: MessagePayload) => {
        if (payload.notification) {
          setShowToast({
            show: true,
            message: t("toast_received_message", {
              title: payload.notification.title,
              content: payload.notification.body,
              ns: "common",
            }),
            variant: "secondary",
          });
        }
      });
    }
  }, []);
};

export default firebaseMessaging;
export { app };
