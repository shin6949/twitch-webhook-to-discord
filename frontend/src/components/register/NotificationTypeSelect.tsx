import React from "react";
import { Form } from "react-bootstrap";
import { useTranslation } from "next-i18next";
import { NotificationType } from "../../interface/RegisterInterface";

interface NotificationTypeSelectProps {
  notificationTypes: NotificationType[];
  selectedNotificationType: string;
  onNotificationTypeChange: (
    event: React.ChangeEvent<HTMLInputElement>
  ) => void;
}

const NotificationTypeSelect: React.FC<NotificationTypeSelectProps> = ({
  notificationTypes,
  selectedNotificationType,
  onNotificationTypeChange,
}) => {
  const { t } = useTranslation(["register", "common"]);

  return (
    <Form.Group controlId="formNotificationType">
      <Form.Label>
        {t("label-notification-type", { ns: "register" })}
      </Form.Label>
      <Form.Control
        as="select"
        value={selectedNotificationType}
        onChange={onNotificationTypeChange}
      >
        <option value="" disabled>
          {t("select-notification-type", { ns: "register" })}
        </option>
        {notificationTypes.map((notificationType) => (
          <option key={notificationType.name} value={notificationType.value}>
            {notificationType.name}
          </option>
        ))}
      </Form.Control>
    </Form.Group>
  );
};

export default NotificationTypeSelect;
