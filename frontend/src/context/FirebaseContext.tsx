import React, {
  createContext,
  ReactNode,
  useContext,
  useState,
  useEffect,
} from "react";
import { initializeApp } from "firebase/app";
import { getMessaging, Messaging, getToken } from "firebase/messaging";
import getConfig from "next/config";

const { publicRuntimeConfig } = getConfig();

// Initialize Firebase App
const firebaseApp = initializeApp(publicRuntimeConfig.firebase);

const FirebaseContext = createContext({
  app: firebaseApp,
  messaging: null as Messaging | null,
  messagingToken: "" as string, // 수정된 부분: 빈 문자열로 초기화
});

export function useFirebase() {
  const { app, messaging: initialMessaging } = useContext(FirebaseContext);
  const [messaging, setMessaging] = useState(initialMessaging);
  const [messagingToken, setMessagingToken] = useState("TEST");

  useEffect(() => {
    const fetchMessagingToken = async () => {
      if (typeof window !== "undefined" && messaging) {
        try {
          const token = await getToken(messaging);
          if (messagingToken !== token) {
            console.error(
              `messagingToken is modified!!!\nbefore: ${messagingToken} / current: ${token}`
            );
          }
          setMessagingToken(token);
        } catch (error) {
          console.error("토큰을 가져오는 중에 오류가 발생했습니다.", error);
        }
      }
    };

    if (typeof window !== "undefined" && !messaging) {
      setMessaging(getMessaging(app));
    }

    fetchMessagingToken();
  }, [app, messaging]);

  return { app, messaging, messagingToken };
}

interface FirebaseProviderProps {
  children: ReactNode;
}

export function FirebaseProvider({ children }: FirebaseProviderProps) {
  return (
    <FirebaseContext.Provider
      value={{ app: firebaseApp, messaging: null, messagingToken: "" }} // 수정된 부분: 빈 문자열로 초기화
    >
      {children}
    </FirebaseContext.Provider>
  );
}
