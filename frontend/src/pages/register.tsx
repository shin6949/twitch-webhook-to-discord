import React, { useState, useEffect } from "react";
import { Button, Form, Spinner } from "react-bootstrap";
import { useTranslation } from "next-i18next";
import { TwitchNotificationRegisterResponse } from "../types/Register";
import {
  getTwitchIDSearchResult,
  postTwitchNotificationRegister,
  NotificationType,
  getNotificationTypes,
} from "../utils/Register";
import { serverSideTranslations } from "next-i18next/serverSideTranslations";
import "bootstrap/dist/css/bootstrap.min.css";

// Custom Component
import TwitchIDInput from "../components/register/TwitchIDInput";
import NotificationTypeSelect from "../components/register/NotificationTypeSelect";
import DelayTimeInput from "../components/register/DelayTimeInput";
import CustomToast from "../components/CustomToast";
import { useToast } from "../components/ToastContext";
import { getMessaging, getToken, Messaging } from "firebase/messaging";
import { useFirebaseApp } from "../context/FirebaseContext";

export const getStaticProps = async ({ locale }: { locale: string }) => {
  return {
    props: {
      ...(await serverSideTranslations(locale, ["register", "common"])),
    },
  };
};

const RegisterPage = () => {
  const { t } = useTranslation(["register", "common"]);
  const [messaging, setMessaging] = useState<Messaging | null>(null);

  // Twitch ID
  const [twitchID, setTwitchID] = useState("");
  const [twitchIDValid, setTwitchIDValid] = useState(false);
  const [twitchIDChecked, setTwitchIDChecked] = useState(false);

  // Spinner 용
  const [twitchIDChecking, setTwitchIDChecking] = useState(false);

  // Notification Type
  const [notificationTypes, setNotificationTypes] = useState<
    NotificationType[]
  >([]);
  const [selectedNotificationType, setSelectedNotificationType] = useState("");

  // Delay Time
  const [delayTime, setDelayTime] = useState<number>(10);

  // Form Submission Status
  const [isLoading, setIsLoading] = useState(false);

  // Toast
  const { showToast, setShowToast } = useToast();

  useEffect(() => {
    const firebaseApp = useFirebaseApp();

    if (typeof window !== "undefined" && messaging === null && firebaseApp) {
      setMessaging(getMessaging(firebaseApp));
    }
  }, []);

  const clickModifyTwitchIdButton = () => {
    setTwitchID("");
    setTwitchIDValid(false);
    setTwitchIDChecked(false);
  };

  const handleTwitchIDChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setTwitchID(e.target.value);
  };

  const handleTwitchIDBlur = async () => {
    if (twitchIDValid) {
      console.log("Already Checked.");
      return;
    }

    setTwitchIDChecking(true);
    setTwitchIDChecked(true);

    // 필수 조건 검증
    if (twitchID.length < 4 || twitchID.length > 25) {
      setTwitchIDValid(false);
      setTwitchIDChecking(false);
      return;
    }

    try {
      const result: boolean = Boolean(await getTwitchIDSearchResult(twitchID));
      setTwitchIDValid(result);
      setShowToast({
        show: true,
        message: t(
          result ? "feedback-valid-twitch-id" : "feedback-invalid-twitch-id"
        ),
        variant: result ? "secondary" : "danger",
      });
    } catch (e) {
      console.error(e);
      setTwitchIDValid(false);
    } finally {
      setTwitchIDChecking(false);
    }
  };

  const handleDelayTimeChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setDelayTime(Number(e.target.value));
  };

  const handleSubmit = async () => {
    if (!twitchIDValid || !selectedNotificationType) {
      return;
    }

    const handleNotificationResponse = (
      data: TwitchNotificationRegisterResponse
    ) => {
      setShowToast({
        show: true,
        message: t(
          data.result
            ? "toast-register-success-header"
            : "toast-register-error-header",
          { ns: "register" }
        ),
        variant: data.result ? "primary" : "danger",
      });
    };

    const handleError = (e: unknown) => {
      console.error(e);
      setShowToast({
        show: true,
        message: t("toast-register-error-header", { ns: "register" }),
        variant: "danger",
      });
    };

    Notification.requestPermission().then(
      async (permission: NotificationPermission) => {
        if (permission === "granted" && messaging) {
          setIsLoading(true);
          const token: string = await getToken(messaging);

          try {
            const response = await postTwitchNotificationRegister({
              twitchID: twitchID,
              notificationType: selectedNotificationType,
              delayTime: delayTime,
              registrationToken: token,
            });
            const data: TwitchNotificationRegisterResponse =
              await response.json();
            handleNotificationResponse(data);
          } catch (e) {
            handleError(e);
          } finally {
            setIsLoading(false);
          }
        }
      }
    );
  };

  const onNotificationTypeChange = (
    event: React.ChangeEvent<HTMLInputElement>
  ) => {
    setSelectedNotificationType(event.currentTarget.value);
  };

  // Set up select field
  useEffect(() => {
    (async () => {
      setNotificationTypes(await getNotificationTypes());
    })();
  }, []);

  return (
    <div className="container">
      <h1>{t("register-menu", { ns: "common" })}</h1>
      <Form>
        <TwitchIDInput
          twitchID={twitchID}
          twitchIDValid={twitchIDValid}
          twitchIDChecked={twitchIDChecked}
          onTwitchIDChange={handleTwitchIDChange}
          onTwitchIDBlur={handleTwitchIDBlur}
          onClickModifyTwitchIdButton={clickModifyTwitchIdButton}
          twitchIDChecking={twitchIDChecking}
        />

        <NotificationTypeSelect
          notificationTypes={notificationTypes}
          selectedNotificationType={selectedNotificationType}
          onNotificationTypeChange={onNotificationTypeChange}
        />

        <DelayTimeInput
          delayTime={delayTime}
          onDelayTimeChange={handleDelayTimeChange}
        />

        <Button
          variant="primary"
          onClick={handleSubmit}
          disabled={isLoading || !twitchIDValid || !selectedNotificationType}
        >
          {t("button-register", { ns: "register" })}
          {isLoading && (
            <Spinner animation="border" size="sm" className="ml-2" />
          )}
        </Button>
      </Form>

      <CustomToast showToast={showToast} setShowToast={setShowToast} />
    </div>
  );
};

export default RegisterPage;
