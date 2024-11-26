import React, { createContext, useState, useEffect } from "react";
import { getToken, storeToken, removeToken } from "../utils/Auth";


export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(null);


  useEffect(() => {
    const loadToken = async () => {
      const storedToken = await getToken();
      if (storedToken) {
        setToken(storedToken);
      }
    };
    loadToken();
  }, []);

  const saveToken = async (newToken) => {
    await storeToken(newToken);
    setToken(newToken);
  };

  const removeUserToken = async () => {
    await removeToken();
    setToken(null);
  };

  return (
    <AuthContext.Provider value={{ token, saveToken, removeUserToken }}>
      {children}
    </AuthContext.Provider>
  );
};
