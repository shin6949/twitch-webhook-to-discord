import React from 'react';
import { Form } from 'react-bootstrap';
import { useTranslation } from "next-i18next";

interface DelayTimeInputProps {
    delayTime: number,
    onDelayTimeChange: (event: React.ChangeEvent<HTMLInputElement>)  => void
}

const DelayTimeInput: React.FC<DelayTimeInputProps> = ({ delayTime,
                                                           onDelayTimeChange }) => {
    const { t } = useTranslation(["register", "common"]);

    return (
        <Form.Group controlId="formdelayTime">
            <Form.Label>{t("label-delay-time", {ns: "register"})}</Form.Label>
            <Form.Control type="number" placeholder={"" ?? t("placeholder-delay-time", {ns: "register"})}
                          value={delayTime} onChange={onDelayTimeChange}/>
        </Form.Group>
    );
};

export default DelayTimeInput;
