import React from 'react';
import { Button, Form, Spinner } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faPen, faCheck } from '@fortawesome/free-solid-svg-icons';
import { useTranslation } from "next-i18next";

interface TwitchIDInputProps {
    twitchID: string;
    twitchIDValid: boolean;
    twitchIDChecked: boolean;
    twitchIDChecking: boolean;
    onTwitchIDChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
    onTwitchIDBlur: () => Promise<void>;
    onClickModifyTwitchIdButton: () => void;
}

const TwitchIDInput: React.FC<TwitchIDInputProps> = ({
                                                         twitchID,
                                                         twitchIDValid,
                                                         twitchIDChecked,
                                                         twitchIDChecking,
                                                         onTwitchIDChange,
                                                         onTwitchIDBlur,
                                                         onClickModifyTwitchIdButton,
                                                     }) => {
    const { t } = useTranslation(["register", "common"]);

    return (
        <Form.Group controlId="formTwitchID">
            <Form.Label>{t("register-menu", { ns: "common" })}</Form.Label>
            <div className="input-group">
                <Form.Control
                    type="text"
                    className="rounded"
                    value={twitchID}
                    onChange={onTwitchIDChange}
                    isInvalid={twitchID.length > 0 && twitchIDChecked && !twitchIDValid}
                    isValid={twitchIDChecked && twitchIDValid}
                    minLength={4}
                    maxLength={25}
                    readOnly={twitchIDValid}
                    onKeyDown={(event) => {
                        if (event.code === "Enter") {
                            onTwitchIDBlur().then(() => {});
                        }
                    }}
                    required
                />

                {twitchIDValid && (
                    <Button className="ml-8 rounded btn-secondary" onClick={onClickModifyTwitchIdButton}>
                        <FontAwesomeIcon icon={faPen} />
                    </Button>
                )}
                {(!twitchIDChecked || !twitchIDValid) && (
                    <Button className="ml-8 rounded" onClick={onTwitchIDBlur}>
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
    );
};

export default TwitchIDInput;
