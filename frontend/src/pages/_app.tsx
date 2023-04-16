import "bootstrap/dist/css/bootstrap.min.css";
import type { AppProps } from "next/app";
import { FirebaseApp, initializeApp } from "firebase/app";
import getConfig from "next/config";
const { publicRuntimeConfig } = getConfig();

// Firebase configuration
const firebaseConfig: Record<string, string> = {
  apiKey: publicRuntimeConfig.FIREBASE_API_KEY as string,
  authDomain: publicRuntimeConfig.FIREBASE_AUTH_DOMAIN as string,
  projectId: publicRuntimeConfig.FIREBASE_PROJECT_ID as string,
  storageBucket: publicRuntimeConfig.FIREBASE_STORAGE_BUCKET as string,
  messagingSenderId: publicRuntimeConfig.FIREBASE_MESSAGING_SENDER_ID as string,
  appId: publicRuntimeConfig.FIREBASE_APP_ID as string,
  measurementId: publicRuntimeConfig.FIREBASE_MEASUREMENT_ID as string,
};

// Initialize Firebase
const app: FirebaseApp = initializeApp(firebaseConfig);

function MyApp({ Component, pageProps }: AppProps) {
  return <Component {...pageProps} />;
}

export default MyApp;
export { app };
