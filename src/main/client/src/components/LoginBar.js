import styles from "./LoginBar.module.css";
import { Context } from "../store/appContext";
import { useContext, useState, useEffect } from "react";
import { Link, Navigate } from "react-router-dom";
import { useNavigate } from "react-router-dom";

function LoginBar() {
  const [username, setUsername] = useState("");
  const navigate = useNavigate();
  const logout = async () => {
    const opts = {
      method: "POST",
    };
    try {
      const response = await fetch("/api/member/logout", opts);
      if (response.status !== 200) {
        alert("There has been some errors.");
        return false;
      }
      navigate("/");
    } catch (error) {
      console.error("There has been an error login", error);
    }
  };

  useEffect(() => {
    async function getUser(){
      try {
        const response = await fetch("/api/member/username");
        if (response.status !== 200) {
          alert("There has been some errors.");
          return false;
        }
        const username = await response.text();
        if (username === "expired") setUsername("");
        else setUsername(username);
        console.log("This came from the backend", username);
        
      } catch (error) {
        console.error("There has been an error login", error);
      }
    }
    getUser();
  }, []);

  return (
    <div className={styles.loginbar}>
      {username === "" ? (
        <Link to="login">
          <span>Log in</span>
        </Link>
      ) : (
        <Link to="#">
          <span>{username}, </span>
          <span
            onClick={() => {
              logout();
            }}
          >
            Logout
          </span>
        </Link>
      )}
      {/* <span>suaguri 님, </span>
      <a className={styles.logout} href="#">
        로그아웃
      </a> */}
    </div>
  );
}

export default LoginBar;
