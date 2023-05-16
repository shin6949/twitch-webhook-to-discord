import React from 'react';
import { Form } from 'react-bootstrap';
import { useTranslation } from "next-i18next";

interface IntervalMinuteInputProps {
    intervalMinute: number,
    onIntervalMinuteChange: (event: React.ChangeEvent<HTMLInputElement>)  => void
}

const IntervalMinuteInput: React.FC<IntervalMinuteInputProps> = ({ intervalMinute,
                                                                     onIntervalMinuteChange }) => {
    const { t } = useTranslation(["register", "common"]);

    return (
        <Form.Group controlId="formdelayTime">
            <Form.Label>{t("label-delay-time", {ns: "register"})}</Form.Label>
            <Form.Control type="number" placeholder={"" ?? t("placeholder-delay-time", {ns: "register"})}
                          value={intervalMinute} onChange={onIntervalMinuteChange}/>
        </Form.Group>
    );
};

export default IntervalMinuteInput;
