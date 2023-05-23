import React, {
  createContext,
  ReactNode,
  useContext,
  useState,
  useEffect,
} from "react";
import { initializeApp } from "firebase/app";
import { getMessaging, Messaging } from "firebase/messaging";
import getConfig from "next/config";

const { publicRuntimeConfig } = getConfig();

// Initialize Firebase App
const firebaseApp = initializeApp(publicRuntimeConfig.firebase);

const FirebaseContext = createContext({
  app: firebaseApp,
  messaging: null as Messaging | null,
});

export function useFirebase() {
  const { app, messaging: initialMessaging } = useContext(FirebaseContext);
  const [messaging, setMessaging] = useState(initialMessaging);

  useEffect(() => {
    if (typeof window !== "undefined" && !messaging) {
      setMessaging(getMessaging(app));
    }
  }, [app, messaging]);

  return { app, messaging };
}

interface FirebaseProviderProps {
  children: ReactNode;
}

export function FirebaseProvider({ children }: FirebaseProviderProps) {
  return (
    <FirebaseContext.Provider value={{ app: firebaseApp, messaging: null }}>
      {children}
    </FirebaseContext.Provider>
  );
}
