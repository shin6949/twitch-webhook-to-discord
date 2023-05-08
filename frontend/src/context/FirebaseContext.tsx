import { createContext, useContext } from "react";
import { FirebaseApp } from "firebase/app";

const FirebaseContext = createContext<FirebaseApp | null>(null);

export const useFirebaseApp = () => {
  return useContext(FirebaseContext);
};

export default FirebaseContext;
