import React, { useState, useEffect } from "react";
import { getMessaging, onMessage, getToken, Messaging, MessagePayload } from "firebase/messaging";
import { Button, Container, Form, Toast, ToastContainer } from "react-bootstrap";
import { app } from "./_app";
import Header from "../components/Header";
import Head from "next/head";

const FCMForm: React.FC = (): JSX.Element => {
  const [showToast, setShowToast] = useState<ToastState>({ show: false, message: "", variant: "secondary" });
  const [messaging, setMessaging] = useState<Messaging | null>(null);
  const [title, setTitle] = useState<string>("");
  const [content, setContent] = useState<string>("");

  useEffect(() => {
    if (typeof document !== "undefined") {
      require("bootstrap/dist/css/bootstrap.min.css");
      // require("bootstrap/dist/js/bootstrap.bundle.min.js");
    }
  }, []);

  type ToastState = {
    show: boolean;
    message: string;
    variant: string;
  };

  useEffect(() => {
    if (typeof window !== "undefined") {
      setMessaging(getMessaging(app));
    }
  }, []);

  useEffect(() => {
    if (messaging) {
      onMessage(messaging, (payload: MessagePayload) => {
        if (payload.notification) {
          console.log(payload.notification.title);
          console.log(payload.notification.body);
          setShowToast({
            show: true,
            message: `알림을 정상적으로 받았습니다.\n받은 제목: ${payload.notification.title}\n받은 내용: ${payload.notification.body}`,
            variant: "secondary",
          });
          console.log("TOAST CALLED!");
        }
      });
    }
  }, [messaging]);

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
      <Header />
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
