import React, { useState, useEffect } from "react";
import { Toast } from "react-bootstrap";
import { CSSProperties } from "react";

export type ToastState = {
  show: boolean;
  message: string;
  variant: string;
};

type Props = {
  showToast: ToastState;
  setShowToast: React.Dispatch<React.SetStateAction<ToastState>>;
};

const CustomToast: React.FC<Props> = ({ showToast }): JSX.Element => {
  const [toasts, setToasts] = useState<ToastState[]>([]);

  useEffect(() => {
    if (showToast.show) {
      setToasts((prevToasts) => [...prevToasts, showToast]);
    }
  }, [showToast]);

  useEffect(() => {
    const timer = setTimeout(() => {
      if (toasts.length > 0) {
        handleToastClose(0);
      }
    }, 1000);

    return () => {
      clearTimeout(timer);
    };
  }, [toasts]);

  const handleToastClose = (index: number) => {
    setToasts((prevToasts) => {
      const newToasts = [...prevToasts];
      newToasts.splice(index, 1);
      return newToasts;
    });
  };

  const containerStyle: CSSProperties = {
    position: "fixed",
    bottom: "16px",
    right: "16px",
    display: "flex",
    flexDirection: "column-reverse",
    alignItems: "flex-end",
  };

  const toastStyle: CSSProperties = {
    marginBottom: "8px",
  };

  return (
    <div style={containerStyle}>
      {toasts.map((toast, index) => (
        <Toast
          key={index}
          show={true}
          onClose={() => handleToastClose(index)}
          delay={3000}
          bg={toast.variant}
          style={toastStyle}
          // autohide
        >
          <Toast.Body style={{ color: "#ffffff", whiteSpace: "pre-wrap" }}>
            {toast.message}
          </Toast.Body>
        </Toast>
      ))}
    </div>
  );
};

export default CustomToast;
