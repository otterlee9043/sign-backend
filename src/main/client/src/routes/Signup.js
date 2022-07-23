import React from "react";
import styles from "./Login.module.css";
import { useContext, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Context } from "../store/appContext";

function Signup() {
  const { store, actions } = useContext(Context);
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  //const token = sessionStorage.getItem("token");
  const navigate = useNavigate();

  const handleClick = async () => {
    actions.signup(username, email, password).then(() => {
      navigate("/");
    });
  };

  return (
    <div>
      <div className={styles.wrapper}>
        <div className={styles.login}>
          <span className={styles.loginText}>Sign up</span>
          {store.token && store.token != "" && store.token != undefined ? (
            <div>You are logged in with {store.token}</div>
          ) : (
            <div>
              <input
                className={styles.input}
                type="text"
                name="usename"
                placeholder="Username"
                value={username}
                onChange={(event) => setUsername(event.target.value)}
              />
              <input
                className={styles.input}
                type="text"
                name="email"
                placeholder="Email"
                value={email}
                onChange={(event) => setEmail(event.target.value)}
              />
              <input
                className={styles.input}
                type="password"
                name="password"
                placeholder="Password"
                value={password}
                onChange={(event) => setPassword(event.target.value)}
              />
              <button className={styles.loginBtn} onClick={handleClick}>
                Sign up
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default Signup;
