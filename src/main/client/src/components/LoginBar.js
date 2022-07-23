import styles from "./LoginBar.module.css";
import { Context } from "../store/appContext";
import { useContext, useState, useEffect } from "react";
import { Link } from "react-router-dom";

function LoginBar() {
  const [username, setUsername] = useState("");
  // const get_user = async () => {
  //   try {
  //     // const response = await fetch("/api/user");
  //     // if (response.status !== 200) {
  //     //   alert("There has been some errors.");
  //     //   return false;
  //     // }
  //     // const data = await response.json();
  //     // console.log("This came from the backend", data);
  //     // setUsername(data.username);

  //     setUsername("username")
  //   } catch (error) {
  //     console.error("There has been an error login");
  //   }
  // };

  const logout = async () => {
    try {
      // const response = await fetch("/api/logout");
      // if (response.status !== 200) {
      //   alert("There has been some errors.");
      //   return false;
      // }
      setUsername("");
    } catch (error) {
      console.error("There has been an error login");
    }
  };

  useEffect(() => {
    async function getUser(){
      try {
      // const response = await fetch("/api/user");
      // if (response.status !== 200) {
      //   alert("There has been some errors.");
      //   return false;
      // }
      // const data = await response.json();
      // console.log("This came from the backend", data);
      // setUsername(data.username);
        setUsername("username")
      } catch (error) {
        console.error("There has been an error login");
      }
    }
    getUser();
  }, []);

  const { store, actions } = useContext(Context);
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
