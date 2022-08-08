import React from "react";
import styles from "./Login.module.css";
import { useContext, useState } from "react";
import { Link, useNavigate, useLocation } from "react-router-dom";
import { Context } from "../store/appContext";

function Login({ msg }) {
  const { store, actions } = useContext(Context);
  // const context = useContext(Context);
  // console.log(context);
  // const store = context.store;
  // const actions = context.actions;
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [errorMsg, setMessage] = useState("");
  //const token = sessionStorage.getItem("token");
  const location = useLocation();
  const navigate = useNavigate();

  const handleClick = async () => {
    const opts = {
      method: "POST",
      body: new URLSearchParams({
        username: username,
        password: password,
      }),
      headers: new Headers({
        "content-type": "application/x-www-form-urlencoded",
      }),
    };
    try {
      const response = await fetch("/api/member/login", opts);
      if (response.status !== 200) {
        alert("There has been some errors.");
        return false;
      }
       navigate("/");
      return true;
    } catch (error) {
      console.error("There has been an error login", error);
    }
  };

  return (
    <div>
      <div className={styles.wrapper}>
        <div className={styles.login}>
          <span className={styles.loginText}>Login</span>
          {errorMsg == "" ? null : (
            <div className={styles.errorMsg}>{errorMsg}</div>
          )}
          <div>
            <input
              className={styles.input}
              type="text"
              name="username"
              placeholder="username"
              value={username}
              onChange={(event) => setUsername(event.target.value)}
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
              Sign In
            </button>
            <div>
              <Link to="/signup">Sign up</Link>
              {" / "}
              <a>Find password</a>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Login;
