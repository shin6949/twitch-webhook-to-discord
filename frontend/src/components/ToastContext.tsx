import React, {
  createContext,
  useContext,
  useState,
  PropsWithChildren,
} from "react";
import { ToastState } from "./CustomToast";

type ToastContextData = {
  showToast: ToastState;
  setShowToast: React.Dispatch<React.SetStateAction<ToastState>>;
};

const ToastContext = createContext<ToastContextData>({
  showToast: { show: false, message: "", variant: "secondary" },
  setShowToast: () => {},
});

const useToast = () => useContext(ToastContext);

const ToastProvider = ({ children }: PropsWithChildren<{}>) => {
  const [showToast, setShowToast] = useState<ToastState>({
    show: false,
    message: "",
    variant: "secondary",
  });

  return (
    <ToastContext.Provider value={{ showToast, setShowToast }}>
      {children}
    </ToastContext.Provider>
  );
};

export { ToastProvider, useToast };
