import React, { useState, useEffect } from "react";
import { getMessaging, getToken, Messaging } from "firebase/messaging";
import { Button, Container, Form } from "react-bootstrap";
import Head from "next/head";
import { serverSideTranslations } from "next-i18next/serverSideTranslations";
import { useTranslation } from "next-i18next";
import "bootstrap/dist/css/bootstrap.min.css";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPaperPlane } from "@fortawesome/free-solid-svg-icons";

// Custom Components
import CustomToast, { ToastState } from "../components/CustomToast";
import { app } from "../components/FirebaseMessaging";

export const getStaticProps = async ({ locale }: { locale: string }) => {
  return {
    props: {
      ...(await serverSideTranslations(locale, ["fcmpage", "common"])),
    },
  };
};

const FCMForm: React.FC = (): JSX.Element => {
  const { t } = useTranslation(["fcmpage", "common"]);

  const [showToast, setShowToast] = useState<ToastState>({
    show: false,
    message: "",
    variant: "secondary",
  });
  const [messaging, setMessaging] = useState<Messaging | null>(null);
  const [title, setTitle] = useState<string>("");
  const [content, setContent] = useState<string>("");

  useEffect(() => {
    if (typeof window !== "undefined" && messaging === null) {
      setMessaging(getMessaging(app));
    }
  }, []);

  const sendMessage = async (): Promise<void> => {
    Notification.requestPermission()
      .then(async (permission: NotificationPermission) => {
        if (permission === "granted") {
          if (!messaging) {
            setShowToast({
              show: true,
              message: t("toast_unable_to_use_notification_function", {
                ns: "fcmpage",
              }),
              variant: "danger",
            });
            return;
          }
          const token = await getToken(messaging);
          console.log(`Current Registration Token: ${token}`);

          if (!title || !content) {
            setShowToast({
              show: true,
              message: t("toast_fill_all_field", { ns: "fcmpage" }),
              variant: "danger",
            });
            return;
          }

          const response = await fetch("/api/message/send", {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
            },
            body: JSON.stringify({
              title,
              content,
              registration_token: token,
            }),
          });

          if (!response.ok) {
            setShowToast({
              show: true,
              message: t("toast_request_failed", { ns: "fcmpage" }),
              variant: "danger",
            });
          }
        } else {
          setShowToast({
            show: true,
            message: t("toast_unknown_error_occurred", { ns: "fcmpage" }),
            variant: "danger",
          });
          return;
        }
      })
      .catch((error: Error) => {
        console.error("Error:", error);
      });
  };

  return (
    <>
      <Head>
        <title>{t("test-menu", { ns: "common" })}</title>
      </Head>
      <Container>
        <h1>{t("test-menu", { ns: "common" })}</h1>
        <Form>
          <Form.Group className="mb-3">
            <Form.Label>
              {t("form_notification_title", { ns: "fcmpage" })}:
            </Form.Label>
            <Form.Control
              type="text"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              required
            />
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label>
              {t("form_notification_content", { ns: "fcmpage" })}:
            </Form.Label>
            <Form.Control
              type="text"
              value={content}
              onChange={(e) => setContent(e.target.value)}
              required
            />
          </Form.Group>

          <Button variant="primary" onClick={sendMessage}>
            <FontAwesomeIcon className={"pr-4"} icon={faPaperPlane} />
            {t("button_send", { ns: "fcmpage" })}
          </Button>
        </Form>

        <CustomToast showToast={showToast} setShowToast={setShowToast} />
      </Container>
    </>
  );
};

export default FCMForm;
