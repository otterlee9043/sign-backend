import styles from "./LoginBar.module.css";
import { useEffect, useContext } from "react";
import { Link } from "react-router-dom";
// import { CurrentUserContext } from "../contexts/CurrentUserContext";
import { CurrentUserContext } from "../routes/Home";

function LoginBar({ onLogout }) {
  const { currentUser, setCurrentUser } = useContext(CurrentUserContext);

  const logout = async () => {
    await fetch("/api/member/logout", { method: "POST" }).then(onLogout);
  };

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
    } catch (error) {
      console.error("There has been an error login", error);
    }
  };

  useEffect(() => {
    getUser();
  }, []);

  return (
    <div className={styles.loginbar}>
      {currentUser ? (
        <span className={styles["user-bar"]}>
          <span>{currentUser.username}</span>
          <Link to="#">
            <div
              className={`${styles["user"]} `}
              style={{ backgroundImage: `url("${currentUser.picture}")` }}
            ></div>
          </Link>
        </span>
      ) : (
        <Link to="/login">
          <span>Log in</span>
        </Link>
      )}
    </div>
  );
}

export default LoginBar;
