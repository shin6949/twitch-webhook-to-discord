import { useState, useEffect } from "react";
import { Button, Form, Spinner, Toast } from "react-bootstrap";
import { useTranslation } from "next-i18next";
import { TwitchIDSearchResponse, TwitchNotificationRegisterResponse } from "../types/Register";
import { getTwitchIDSearchResult, postTwitchNotificationRegister, NotificationType, getNotificationTypes } from "../utils/Register";
import { serverSideTranslations } from "next-i18next/serverSideTranslations";
import "bootstrap/dist/css/bootstrap.min.css";

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
  const [twitchIDChecking, setTwitchIDChecking] = useState(false);

  // Notification Type
  const [notificationTypes, setNotificationTypes] = useState<NotificationType[]>([]);
  const [selectedNotificationType, setSelectedNotificationType] = useState("");

  // Delay Time
  const [delayTime, setDelayTime] = useState<number>(10);

  // Form Submission Status
  const [isLoading, setIsLoading] = useState(false);
  const [successToast, setSuccessToast] = useState(false);
  const [errorToast, setErrorToast] = useState(false);

  const handleTwitchIDChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setTwitchID(e.target.value);
    setTwitchIDValid(false);
  };

  const handleTwitchIDBlur = async () => {
    setTwitchIDChecking(true);
    try {
      const response = await getTwitchIDSearchResult(twitchID);
      const data: TwitchIDSearchResponse = await response.json();
      if (data.result) {
        setTwitchIDValid(true);
      } else {
        setTwitchIDValid(false);
        setTwitchID("");
      }
    } catch (e) {
      console.error(e);
    } finally {
      setTwitchIDChecking(false);
    }
  };

  const handledelayTimeChange = (e: React.ChangeEvent<HTMLInputElement>) => {
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
        setSuccessToast(true);
      } else {
        setErrorToast(true);
      }
    } catch (e) {
      console.error(e);
      setErrorToast(true);
    } finally {
      setIsLoading(false);
    }
  };

  // Set up select field
  useEffect(() => {
    const fetchNotificationTypes = async () => {
      const types = await getNotificationTypes();
      setNotificationTypes(types);
    };
    fetchNotificationTypes();
  }, []);

  return (
    <div className="container">
      <h1>{t("register-menu", { ns: "common" })}</h1>
      <Form>
        <Form.Group controlId="formTwitchID">
          <Form.Label>{t("label-input-twitch-id", { ns: "register" })}</Form.Label>
          <div className="input-group">
            <Form.Control
              type="text"
              placeholder={"" ?? t("placehodler-twitch-id", { ns: "register" })}
              value={twitchID}
              onChange={handleTwitchIDChange}
              onBlur={handleTwitchIDBlur}
              isInvalid={!twitchIDValid}
              readOnly={twitchIDChecking || twitchIDValid}
            />

            {twitchIDChecking && (
              <div className="input-group-append">
                <div className="input-group-text">
                  <Spinner animation="border" size="sm" />
                </div>
              </div>
            )}
          </div>
          <Form.Control.Feedback type="invalid">{t("feedback-invalid-twitch-id", { ns: "register" })}</Form.Control.Feedback>
        </Form.Group>

        <Form.Group controlId="formNotificationType">
          <Form.Label>{t("label-notification-type", { ns: "register" })}</Form.Label>
          <Form.Control as="select" value={selectedNotificationType} onChange={(e) => setSelectedNotificationType(e.currentTarget.value)}>
            <option value="">{t("select-notification-type", { ns: "register" })}</option>
            {notificationTypes.map((notificationType) => (
              <option key={notificationType.value} value={notificationType.name}>
                {notificationType.name}
              </option>
            ))}
          </Form.Control>
        </Form.Group>

        <Form.Group controlId="formdelayTime">
          <Form.Label>{t("label-delay-time", { ns: "register" })}</Form.Label>
          <Form.Control type="number" placeholder={"" ?? t("placehodler-delay-time", { ns: "register" })} value={delayTime} onChange={handledelayTimeChange} />
        </Form.Group>

        <Button variant="primary" onClick={handleSubmit} disabled={isLoading || !twitchIDValid || !selectedNotificationType}>
          {t("button-register", { ns: "register" })}
          {isLoading && <Spinner animation="border" size="sm" className="ml-2" />}
        </Button>
      </Form>

      <Toast show={successToast} onClose={() => setSuccessToast(false)} className="mt-3">
        <Toast.Header>
          <strong className="mr-auto">{t("toast-register-success-header", { ns: "register" })}</strong>
        </Toast.Header>
        <Toast.Body>{t("toast-register-success-content", { ns: "register" })}</Toast.Body>
      </Toast>

      <Toast show={errorToast} onClose={() => setErrorToast(false)} className="mt-3">
        <Toast.Header>
          <strong className="mr-auto">{t("toast-register-error-header", { ns: "register" })}</strong>
        </Toast.Header>
        <Toast.Body>{t("toast-register-error-content", { ns: "register" })}</Toast.Body>
      </Toast>
    </div>
  );
};

export default RegisterPage;
