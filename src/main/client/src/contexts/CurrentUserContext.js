import { createContext, useState, useEffect } from "react";
import CircularProgress from "@mui/material/CircularProgress";

export const CurrentUserContext = createContext(null);

const injectContext = (PassedComponent) => {
  const StoreWrapper = (props) => {
    const [currentUser, setCurrentUser] = useState(null);
    const [loading, setLoading] = useState(true);
    useEffect(() => {
      const getUser = async () => {
        try {
          const response = await fetch("/api/member/userInfo");
          if (!response.ok) {
            if (response.status === 401) {
              return;
            }
          }
          const userInfo = await response.json();
          setCurrentUser(userInfo);
          console.log("setCurrentUser");
        } catch (error) {
          console.error("There has been an error login", error);
        } finally {
          setLoading(false);
        }
      };

      getUser();
    }, []);

    if (loading) {
      return <CircularProgress />;
    }

    return (
      <CurrentUserContext.Provider value={{ currentUser, setCurrentUser }}>
        <PassedComponent {...props} />
      </CurrentUserContext.Provider>
    );
  };
  return StoreWrapper;
};

export default injectContext;
