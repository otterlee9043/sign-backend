import React, { createContext, useState } from "react";

// Create the context
const CurrentUserContext = createContext();

// Create a provider component
const CurrentUserContextProvider = ({ children }) => {
  const [value, setValue] = useState({});

  return (
    <CurrentUserContext.Provider value={{ value, setValue }}>
      {children}
    </CurrentUserContext.Provider>
  );
};

export { CurrentUserContext, CurrentUserContextProvider };
