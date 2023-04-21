// CustomToast.tsx
import React from 'react';
import { Toast, ToastContainer } from 'react-bootstrap';

interface CustomToastProps {
    showToast: ToastState;
    setShowToast: (state: ToastState) => void;
}

export type ToastState = {
    show: boolean;
    message: string;
    variant: string;
};

const CustomToast: React.FC<CustomToastProps> = ({ showToast, setShowToast }) => {
    return (
        <ToastContainer position="bottom-end">
            <Toast
                bg={showToast.variant}
                onClose={() => setShowToast({ ...showToast, show: false })}
                show={showToast.show}
                delay={3000}
                className="mt-3"
                autohide
            >
                <Toast.Body style={{ color: '#ffffff', whiteSpace: 'pre-wrap' }}>{showToast.message}</Toast.Body>
            </Toast>
        </ToastContainer>
    );
};

export default CustomToast;
