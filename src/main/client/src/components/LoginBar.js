import styles from "./LoginBar.module.css";
import { useState, useEffect } from "react";
import { Link } from "react-router-dom";

function LoginBar({ onLogout }) {
  const [username, setUsername] = useState("");

  const logout = async () => {
    await fetch("/api/member/logout", { method: "POST" }).then(onLogout);
  };

  const getUser = async () => {
    try {
      const response = await fetch("/api/member/userInfo");
      if (!response.ok) {
        if (response.status === 401) {
          setUsername("");
          return;
        }
      }
      const userInfo = await response.json();
      console.log(userInfo);
      const username = userInfo["username"];
      setUsername(username);
    } catch (error) {
      console.error("There has been an error login", error);
    }
  };

  useEffect(() => {
    getUser();
  }, []);

  return (
    <div className={styles.loginbar}>
      {username === "" ? (
        <Link to="/login">
          <span>Log in</span>
        </Link>
      ) : (
        <Link to="#">
          <span>{username}, </span>
          <span onClick={logout}>Logout</span>
        </Link>
      )}
    </div>
  );
}

export default LoginBar;
