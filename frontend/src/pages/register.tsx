import React, { useState, useEffect } from "react";
import { Button, Form, Spinner, Toast, ToastContainer } from "react-bootstrap";
import { useTranslation } from "next-i18next";
import { TwitchNotificationRegisterResponse } from "../types/Register";
import { getTwitchIDSearchResult, postTwitchNotificationRegister, NotificationType, getNotificationTypes } from "../utils/Register";
import { serverSideTranslations } from "next-i18next/serverSideTranslations";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPen, faCheck } from "@fortawesome/free-solid-svg-icons";
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
  const [twitchIDChecked, setTwitchIDChecked] = useState(false);

  // Spinner 용
  const [twitchIDChecking, setTwitchIDChecking] = useState(false);

  // Notification Type
  const [notificationTypes, setNotificationTypes] = useState<NotificationType[]>([]);
  const [selectedNotificationType, setSelectedNotificationType] = useState("");

  // Delay Time
  const [delayTime, setDelayTime] = useState<number>(10);

  // Form Submission Status
  const [isLoading, setIsLoading] = useState(false);

  // Toast
  const [showToast, setShowToast] = useState<ToastState>({ show: false, message: "", variant: "secondary" });
  type ToastState = {
    show: boolean;
    message: string;
    variant: string;
  };

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
        setShowToast({ show: true, message: t("toast-register-success-header", { ns: "register" }), variant: "primary" });
      } else {
        setShowToast({ show: true, message: t("toast-register-error-header", { ns: "register" }), variant: "danger" });
      }
    } catch (e) {
      console.error(e);
      setShowToast({ show: true, message: t("toast-register-error-header", { ns: "register" }), variant: "danger" });
    } finally {
      setIsLoading(false);
    }
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
        <Form.Group controlId="formTwitchID">
          <Form.Label>{t("label-input-twitch-id", { ns: "register" })}</Form.Label>
          <div className="input-group">
            <Form.Control
              type="text"
              className="rounded"
              value={twitchID}
              onChange={handleTwitchIDChange}
              isInvalid={twitchID.length > 0 && twitchIDChecked && !twitchIDValid}
              isValid={twitchIDChecked && twitchIDValid}
              minLength={4}
              maxLength={25}
              readOnly={twitchIDValid}
              onKeyDown={(event) => {
                if (event.code === "Enter") {
                  handleTwitchIDBlur().then(() => {});
                }
              }}
              required
            />

            {twitchIDValid && (
              <Button className="ml-8 rounded btn-secondary" onClick={clickModifyTwitchIdButton}>
                <FontAwesomeIcon icon={faPen} />
              </Button>
            )}
            {(!twitchIDChecked || !twitchIDValid) && (
              <Button className="ml-8 rounded" onClick={handleTwitchIDBlur}>
                <FontAwesomeIcon icon={faCheck} />
              </Button>
            )}
            {twitchIDChecking && (
              <div className="input-group-append">
                <div className="input-group-text">
                  <Spinner animation="border" size="sm" />
                </div>
              </div>
            )}
            <Form.Control.Feedback key={twitchIDValid ? "valid" : "invalid"} type={twitchIDValid ? "valid" : "invalid"}>
              {t(twitchIDValid ? "feedback-valid-twitch-id" : "feedback-invalid-twitch-id")}
            </Form.Control.Feedback>
          </div>
          <Form.Text className="text-muted">{t("hint-twitch-id", { ns: "register" })}</Form.Text>
        </Form.Group>

        <Form.Group controlId="formNotificationType">
          <Form.Label>{t("label-notification-type", { ns: "register" })}</Form.Label>
          <Form.Control as="select" value={selectedNotificationType} onChange={(e) => setSelectedNotificationType(e.currentTarget.value)}>
            <option value="" disabled>
              {t("select-notification-type", { ns: "register" })}
            </option>
            {notificationTypes.map((notificationType) => (
              <option key={notificationType.value} value={notificationType.name}>
                {notificationType.name}
              </option>
            ))}
          </Form.Control>
        </Form.Group>

        <Form.Group controlId="formdelayTime">
          <Form.Label>{t("label-delay-time", { ns: "register" })}</Form.Label>
          <Form.Control type="number" placeholder={"" ?? t("placeholder-delay-time", { ns: "register" })} value={delayTime} onChange={handleDelayTimeChange} />
        </Form.Group>

        <Button variant="primary" onClick={handleSubmit} disabled={isLoading || !twitchIDValid || !selectedNotificationType}>
          {t("button-register", { ns: "register" })}
          {isLoading && <Spinner animation="border" size="sm" className="ml-2" />}
        </Button>
      </Form>

      <ToastContainer position="bottom-end">
        <Toast bg={showToast.variant} onClose={() => setShowToast({ ...showToast, show: false })} show={showToast.show} delay={3000} className="mt-3" autohide>
          <Toast.Body style={{ color: "#ffffff", whiteSpace: "pre-wrap" }}>{showToast.message}</Toast.Body>
        </Toast>
      </ToastContainer>
    </div>
  );
};

export default RegisterPage;
