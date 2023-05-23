import { useEffect } from "react";
import { onMessage, MessagePayload } from "firebase/messaging";
import { useToast } from "./ToastContext";
import { useTranslation } from "next-i18next";
import { useFirebase } from "../context/FirebaseContext";

const FirebaseMessaging = () => {
  const { t } = useTranslation(["common"]);
  const { setShowToast } = useToast();

  const { app, messaging } = useFirebase();

  useEffect(() => {
    if (typeof window !== "undefined" && app && messaging) {
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
  }, [app, messaging, t, setShowToast]);

  return null;
};

export default FirebaseMessaging;
