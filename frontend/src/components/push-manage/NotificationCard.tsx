import { Button, Card } from "react-bootstrap";
import { useTranslation } from "next-i18next";
import { faX } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import React from "react";

interface NotificationCardProps {
  formId: number;
  profileImage: string;
  nickname: string;
  loginId: string;
  notificationType: string;
  intervalMinute: number;
  notificationCount: number;
  latestNotificationTime: Date;
  token: string;
  onClick: (formId: number, token: string) => void;
}

const NotificationCard = ({
  formId,
  profileImage,
  nickname,
  loginId,
  notificationType,
  intervalMinute,
  token,
  notificationCount,
  latestNotificationTime,
  onClick,
}: NotificationCardProps): JSX.Element => {
  const { t } = useTranslation(["push-manage", "common"]);

  const formatDate = (isoDateString: Date) => {
    const date = new Date(isoDateString);

    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0"); // month is 0-indexed
    const day = String(date.getDate()).padStart(2, "0");

    const hours = String(date.getHours()).padStart(2, "0");
    const minutes = String(date.getMinutes()).padStart(2, "0");
    const seconds = String(date.getSeconds()).padStart(2, "0");

    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
  };

  return (
    <Card
      style={{
        width: "100%",
        height: "13rem",
        position: "relative",
        overflow: "auto",
        paddingBottom: "3rem",
      }}
    >
      <div
        style={{
          display: "flex",
          alignItems: "center",
        }}
      >
        <Card.Img
          variant="top"
          src={profileImage}
          style={{
            width: 64,
            height: 64,
            borderRadius: "50%",
            objectFit: "cover",
            margin: "1rem",
          }}
        />
        <Card.Body
          style={{
            display: "flex",
            alignItems: "center",
            justifyContent: "space-between",
          }}
        >
          <div>
            <Card.Title>
              {nickname === loginId ? loginId : `${nickname}(${loginId})`}
            </Card.Title>
            <Card.Text>
              {t("notification-type", {
                ns: "push-manage",
                notificationType: notificationType,
              })}
              <br />
              {t("interval-minute", {
                ns: "push-manage",
                minute: intervalMinute,
              })}
              <br />
              {t("notification-count", {
                ns: "push-manage",
                notificationCount: notificationCount,
              })}
              {latestNotificationTime && (
                <>
                  <br />
                  {t("latest-notification-time", {
                    ns: "push-manage",
                    latestNotificationTime: formatDate(latestNotificationTime),
                  })}
                </>
              )}
            </Card.Text>
          </div>
        </Card.Body>
      </div>
      <div style={{ position: "absolute", bottom: "1rem", right: "1rem" }}>
        <Button
          variant="danger"
          value={formId}
          onClick={() => onClick(formId, token)}
        >
          <FontAwesomeIcon icon={faX} style={{ marginRight: "0.5rem" }} />
          {t("push-manage:delete-button", { ns: "push-manage" })}
        </Button>
      </div>
    </Card>
  );
};

export default NotificationCard;
