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
    token: string;
    onClick: (formId: number, token: string) => void;
}

const NotificationCard = ({ formId, profileImage, nickname, loginId, notificationType, intervalMinute, token, onClick }: NotificationCardProps): JSX.Element => {
    const { t } = useTranslation(["push-manage", "common"]);

    return (
        <Card style={{ width: "20rem" }}>
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
                        marginRight: "1rem",
                    }}
                />
                <Card.Body style={{ display: "flex", alignItems: "center", justifyContent: "space-between" }}>
                    <div>
                        <Card.Title>{nickname}({loginId})</Card.Title>
                        <Card.Text>{t("notification-type", { ns: "push-manage" })}{notificationType}</Card.Text>
                        <Card.Text>{t("interval-minute", { ns: "push-manage" })}{intervalMinute}</Card.Text>
                    </div>
                    <div>
                        <Button
                            variant="danger"
                            value={formId}
                            onClick={() => onClick(formId, token)}
                        >
                            <FontAwesomeIcon icon={faX} />
                            Delete
                        </Button>
                    </div>
                </Card.Body>
            </div>
        </Card>
    );
};

export default NotificationCard;
