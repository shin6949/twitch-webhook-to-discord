import { useEffect } from "react";
import { FirebaseApp } from "firebase/app";
import {
  getMessaging,
  onMessage,
  Messaging,
  MessagePayload,
} from "firebase/messaging";
import { useToast } from "./ToastContext";
import { useTranslation } from "next-i18next";

interface FirebaseMessagingProps {
  firebaseApp: FirebaseApp | null;
}

const FirebaseMessaging = ({ firebaseApp }: FirebaseMessagingProps) => {
  const { t } = useTranslation(["common"]);
  const { setShowToast } = useToast();

  useEffect(() => {
    if (typeof window !== "undefined" && firebaseApp) {
      const messaging: Messaging = getMessaging(firebaseApp);

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
  }, [firebaseApp, t, setShowToast]);

  return null;
};

export default FirebaseMessaging;
