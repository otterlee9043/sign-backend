import styles from "./RoomForm.module.css";
import axios from "axios";
import NavBar from "../components/NavBar.js";
import { useNavigate } from "react-router-dom";
import { useContext } from "react";
import { CurrentUserContext } from "../contexts/CurrentUserContext";

function MyPage() {
  const { setCurrentUser } = useContext(CurrentUserContext);
  const navigate = useNavigate();
  const logout = async () => {
    try {
      await axios.post("/member/logout");
      setCurrentUser(null);
      navigate("/");
    } catch {
      console.log(error);
    }
  };
  return (
    <div className={styles.container}>
      <NavBar mode="default" />
      <div className={styles.wrapper}>
        <div onClick={logout}>로그아웃</div>
      </div>
    </div>
  );
}

export default MyPage;
