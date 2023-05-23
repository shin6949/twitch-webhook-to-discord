import React, { useEffect, useState } from "react";
import { Container, Spinner, Row, Col } from "react-bootstrap";
import { serverSideTranslations } from "next-i18next/serverSideTranslations";
import CustomToast, { ToastState } from "../components/CustomToast";
import { getToken } from "firebase/messaging";
import { useFirebase } from "../context/FirebaseContext";

import NotificationCard from "../components/push-manage/NotificationCard";
import { useTranslation } from "next-i18next";

interface Notification {
  form_id: number;
  profile_image: string;
  nickname: string;
  login_id: string;
  notification_type: string;
  notification_count: number;
  latest_notification_time: Date;
  interval_minute: number;
}

interface DeletionResult {
  result: boolean;
  message: string;
}

export const getStaticProps = async ({ locale }: { locale: string }) => {
  return {
    props: {
      ...(await serverSideTranslations(locale, ["push-manage", "common"])),
    },
  };
};

const NotificationManagePage = () => {
  const { t } = useTranslation(["push-manage", "common"]);
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [showToast, setShowToast] = useState<ToastState>({
    show: false,
    message: "",
    variant: "secondary",
  });
  const [token, setToken] = useState<string>("");
  const [isLoading, setIsLoading] = useState(false);
  const { app, messaging } = useFirebase();

  useEffect(() => {
    const requestNotificationPermission = async () => {
      const permission = await Notification.requestPermission();
      if (!permission) {
        console.log("Notification permission denied.");
      }
    };
    requestNotificationPermission();
  }, []);

  useEffect(() => {
    const getFirebaseMessagingToken = async () => {
      if (app && messaging) {
        const token = await getToken(messaging);
        setToken(token);
      }
    };
    getFirebaseMessagingToken();
  }, [app]);

  useEffect(() => {
    const fetchNotifications = async () => {
      setIsLoading(true); // 요청 시작
      try {
        const response = await fetch(`/api/push-manage/get?token=${token}`);
        const data = await response.json();
        setNotifications(data);
        setShowToast({
          show: true,
          message: t("push-manage:receive-successfully", { ns: "push-manage" }),
          variant: "secondary",
        });
      } finally {
        setIsLoading(false); // 요청 종료
      }
    };
    if (token) {
      fetchNotifications();
    }
  }, [token, t]);

  const handleDeleteNotification = async (formId: number, token: string) => {
    const response = await fetch(
      `/api/push-manage/delete?id=${formId}&token=${token}`,
      {
        method: "DELETE",
      }
    );
    const data: DeletionResult = await response.json();
    setShowToast({
      show: true,
      message: data.message,
      variant: data.result ? "success" : "danger",
    });
    if (data.result) {
      setNotifications(
        notifications.filter((notification) => notification.form_id !== formId)
      );
    }
  };

  return (
    <Container>
      <h1>{t("push-manage:title", { ns: "push-manage" })}</h1>
      {isLoading ? (
        <div
          style={{
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            height: "100vh",
          }}
        >
          <Spinner animation="border" role="status">
            <span className="sr-only">Loading...</span>
          </Spinner>
        </div>
      ) : notifications.length === 0 ? (
        <p>{t("push-manage:no-notification-warning", { ns: "push-manage" })}</p>
      ) : (
        <Row>
          {notifications.map((notification) => (
            <Col sm={12} md={6} lg={4} xl={4}>
              <NotificationCard
                key={notification.form_id}
                formId={notification.form_id}
                profileImage={notification.profile_image}
                nickname={notification.nickname}
                loginId={notification.login_id}
                notificationType={notification.notification_type}
                intervalMinute={notification.interval_minute}
                notificationCount={notification.notification_count}
                latestNotificationTime={notification.latest_notification_time}
                token={token}
                onClick={handleDeleteNotification}
              />
            </Col>
          ))}
        </Row>
      )}
      <CustomToast showToast={showToast} setShowToast={setShowToast} />
    </Container>
  );
};

export default NotificationManagePage;
