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
import CustomToast, { ToastState } from "../components/CustomToast";

export const getStaticProps = async ({ locale }: { locale: string }) => {
  return {
    props: {
      ...(await serverSideTranslations(locale, ["register", "common"])),
    },
  };
};

const RegisterPage = () => {
  const { t } = useTranslation(["register", "common"]);

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
  const [showToast, setShowToast] = useState<ToastState>({
    show: false,
    message: "",
    variant: "secondary",
  });

  const clickModifyTwitchIdButton = () => {
    setTwitchID("");
    setTwitchIDValid(false);
    setTwitchIDChecked(false);
  };

  const handleTwitchIDChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setTwitchID(e.target.value);
    // setTwitchIDValid(false);
  };

  const handleTwitchIDBlur = async () => {
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

    setIsLoading(true);
    try {
      const response = await postTwitchNotificationRegister({
        twitchID,
        notificationType: selectedNotificationType,
        delayTime,
      });
      const data: TwitchNotificationRegisterResponse = await response.json();
      if (data.result) {
        setShowToast({
          show: true,
          message: t("toast-register-success-header", { ns: "register" }),
          variant: "primary",
        });
      } else {
        setShowToast({
          show: true,
          message: t("toast-register-error-header", { ns: "register" }),
          variant: "danger",
        });
      }
    } catch (e) {
      console.error(e);
      setShowToast({
        show: true,
        message: t("toast-register-error-header", { ns: "register" }),
        variant: "danger",
      });
    } finally {
      setIsLoading(false);
    }
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
