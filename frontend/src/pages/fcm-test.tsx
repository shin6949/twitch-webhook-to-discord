import React, { useState, useEffect } from "react";
import { getMessaging, getToken, Messaging } from "firebase/messaging";
import { Button, Container, Form, ToastContainer, Toast } from "react-bootstrap";
import { app } from "./_app";
import Head from "next/head";

import { serverSideTranslations } from "next-i18next/serverSideTranslations";
import { useTranslation } from "next-i18next";

export const getStaticProps = async ({ locale }: { locale: string }) => {
  return {
    props: {
      ...(await serverSideTranslations(locale, ["fcmpage", "common"])),
    },
  };
};

const FCMForm: React.FC = (): JSX.Element => {
  const { t } = useTranslation(["fcmpage", "common"]);

  const [showToast, setShowToast] = useState<ToastState>({ show: false, message: "", variant: "secondary" });
  const [messaging, setMessaging] = useState<Messaging | null>(null);
  const [title, setTitle] = useState<string>("");
  const [content, setContent] = useState<string>("");

  useEffect(() => {
    if (typeof document !== "undefined") {
      require("bootstrap/dist/css/bootstrap.min.css");
    }
  }, []);

  type ToastState = {
    show: boolean;
    message: string;
    variant: string;
  };

  useEffect(() => {
    if (typeof window !== "undefined" && messaging === null) {
      setMessaging(getMessaging(app));
      console.log("setMessaging(getMessaging(app)) Processed");
    }
  }, []);

  const sendMessage = async (): Promise<void> => {
    Notification.requestPermission()
      .then(async (permission: NotificationPermission) => {
        if (permission === "granted") {
          if (!messaging) {
            setShowToast({ show: true, message: "알림 기능을 사용할 수 없습니다.", variant: "danger" });
            return;
          }
          const token = await getToken(messaging);
          console.log(`Current Registration Token: ${token}`);

          if (!title || !content) {
            setShowToast({ show: true, message: "제목과 내용 모두 입력해주세요.", variant: "danger" });
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
            setShowToast({ show: true, message: "요청 실패", variant: "danger" });
          }
        } else {
          setShowToast({ show: true, message: "알 수 없는 오류가 발생했습니다.", variant: "danger" });
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
        <title>FCM Sample</title>
      </Head>
      <Container>
        <h1>FCM 알림 전송</h1>
        <Form>
          <Form.Group className="mb-3">
            <Form.Label>알림 제목:</Form.Label>
            <Form.Control type="text" value={title} onChange={(e) => setTitle(e.target.value)} required />
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label>알림 내용:</Form.Label>
            <Form.Control type="text" value={content} onChange={(e) => setContent(e.target.value)} required />
          </Form.Group>

          <Button variant="primary" onClick={sendMessage}>
            전송
          </Button>
        </Form>

        <ToastContainer position="bottom-end">
          <Toast bg={showToast.variant} onClose={() => setShowToast({ ...showToast, show: false })} show={showToast.show} delay={3000} autohide>
            <Toast.Body style={{ color: "#ffffff", whiteSpace: "pre-wrap" }}>{showToast.message}</Toast.Body>
          </Toast>
        </ToastContainer>
      </Container>
    </>
  );
};

export default FCMForm;
