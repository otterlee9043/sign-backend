import styles from "./Login.module.css";
import { useState, useContext } from "react";
import { Link, useNavigate } from "react-router-dom";
import { CurrentUserContext } from "../contexts/CurrentUserContext";

function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [errorMsg, setMessage] = useState("");
  const navigate = useNavigate();
  const { setCurrentUser } = useContext(CurrentUserContext);

  const handleClick = async () => {
    const opts = {
      method: "POST",
      body: JSON.stringify({
        email: email,
        password: password,
      }),
      headers: new Headers({
        "content-type": "application/json",
      }),
    };
    try {
      const response = await fetch("/api/member/login", opts);
      if (response.ok) {
        getUser().then(() => {
          navigate("/home");
        });
      } else {
        alert("There has been some errors.");
        return false;
      }
    } catch (error) {
      console.error("There has been an error login", error);
    }
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

  const handleOnKeyPress = (event) => {
    if (event.key === "Enter") handleClick();
  };

  return (
    <div>
      <div className={styles.wrapper}>
        <div className={styles.login}>
          <span className={styles.loginText}>Login</span>
          {errorMsg === "" ? null : <div className={styles.errorMsg}>{errorMsg}</div>}
          <div>
            <input
              className={styles.input}
              type="text"
              name="email"
              placeholder="email"
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
              onKeyDown={handleOnKeyPress}
            />
            <button
              className={`${styles["login-button"]} ${styles["login-button-blue"]}`}
              onClick={handleClick}
            >
              Sign In
            </button>
            <br></br>
            <a href="http://localhost:8080/oauth2/authorization/google">
              <button className={`${styles["login-button"]} ${styles["login-google"]}`}>
                Google 로그인
              </button>
            </a>
            <a href="http://localhost:8080/oauth2/authorization/kakao">
              <button className={`${styles["login-button"]} ${styles["login-kakao"]}`}>
                카카오 로그인
              </button>
            </a>

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
