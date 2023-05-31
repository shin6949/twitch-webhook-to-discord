import React, { useState, useEffect } from "react";
import { Button, Form, Spinner } from "react-bootstrap";
import { useTranslation } from "next-i18next";
import { serverSideTranslations } from "next-i18next/serverSideTranslations";
import "bootstrap/dist/css/bootstrap.min.css";

// Custom Component
import TwitchIDInput from "../components/register/TwitchIDInput";
import ProfileCard from "../components/register/ProfileCard";
import NotificationTypeSelect from "../components/register/NotificationTypeSelect";
import IntervalMinuteInput from "../components/register/IntervalMinuteInput";
import CustomToast from "../components/CustomToast";
import { useToast } from "../components/ToastContext";
import { getMessaging, getToken, isSupported } from "firebase/messaging";
import { useFirebase } from "../context/FirebaseContext";
import {
  getTwitchIDSearchResult,
  postTwitchNotificationRegister,
  getNotificationTypes,
} from "../utils/Register";
import {
  NotificationType,
  TwitchIDSearchResponse,
  TwitchNotificationRegisterResponse,
} from "../interface/RegisterInterface";

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

  // Twitch Profile Card
  const [twitchProfileCard, setTwitchProfileCard] =
    useState<TwitchIDSearchResponse | null>(null);

  // Spinner 용
  const [twitchIDChecking, setTwitchIDChecking] = useState(false);

  // Notification Type
  const [notificationTypes, setNotificationTypes] = useState<
    NotificationType[]
  >([]);
  const [selectedNotificationType, setSelectedNotificationType] = useState("");

  // Delay Time
  const [intervalMinute, setIntervalMinute] = useState<number>(10);

  // Form Submission Status
  const [isLoading, setIsLoading] = useState(false);

  // Toast
  const { showToast, setShowToast } = useToast();

  // Firebase Messaging
  // const { messaging } = useFirebase();

  const clickModifyTwitchIdButton = () => {
    setTwitchID("");
    setTwitchIDValid(false);
    setTwitchIDChecked(false);
    setTwitchIDChecking(false);
    setTwitchProfileCard(null);
  };

  const onTwitchIDChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const newTwitchID = e.target.value;
    setTwitchID(newTwitchID);

    if (twitchIDChecked) {
      setTwitchIDChecked(false);
    }
  };

  const onTwitchIDBlur = async () => {
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
      const result: TwitchIDSearchResponse = await getTwitchIDSearchResult(
        twitchID
      );
      setTwitchIDValid(result.result);
      if (result.result && result.user !== undefined) {
        setTwitchProfileCard(result);
      }
      setShowToast({
        show: true,
        message: t(
          result.result
            ? "feedback-valid-twitch-id"
            : "feedback-invalid-twitch-id"
        ),
        variant: result.result ? "secondary" : "danger",
      });
    } catch (e) {
      console.error(e);
      setTwitchIDValid(false);
    } finally {
      setTwitchIDChecking(false);
    }
  };

  const handleIntervalMinuteChange = (
    e: React.ChangeEvent<HTMLInputElement>
  ) => {
    setIntervalMinute(Number(e.target.value));
  };

  const handleSubmit = async () => {
    console.warn(`handleSubmit called at register.`);
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
        variant: data.result ? "success" : "danger",
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

    // Firebase Messaging 초기화
    const messaging = (await isSupported()) ? getMessaging() : null;

    Notification.requestPermission().then(
      async (permission: NotificationPermission) => {
        if (permission === "granted" && messaging) {
          setIsLoading(true);
          const permissionState = await Notification.permission;
          if (permissionState === "granted") {
            const token: string = await getToken(messaging);
            console.warn(`Token at register: ${token}`);

            try {
              const response = await postTwitchNotificationRegister({
                twitchID: twitchID,
                notificationType: selectedNotificationType,
                intervalMinute: intervalMinute,
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
          onTwitchIDChange={onTwitchIDChange}
          onTwitchIDBlur={onTwitchIDBlur}
          onClickModifyTwitchIdButton={clickModifyTwitchIdButton}
          twitchIDChecking={twitchIDChecking}
        />

        {twitchIDChecked && twitchIDValid && twitchProfileCard && (
          <ProfileCard
            twitchUser={twitchProfileCard.user!}
            isLive={twitchProfileCard.is_live}
          ></ProfileCard>
        )}

        <NotificationTypeSelect
          notificationTypes={notificationTypes}
          selectedNotificationType={selectedNotificationType}
          onNotificationTypeChange={onNotificationTypeChange}
        />

        <IntervalMinuteInput
          intervalMinute={intervalMinute}
          onIntervalMinuteChange={handleIntervalMinuteChange}
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
