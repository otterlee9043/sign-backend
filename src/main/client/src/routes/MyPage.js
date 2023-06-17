import styles from "./RoomForm.module.css";
import NavBar from "../components/NavBar.js";
import { useNavigate } from "react-router-dom";

function MyPage() {
  const navigate = useNavigate();
  const logout = async () => {
    fetch("/api/member/logout", { method: "POST" })
      .then(() => {
        navigate("/");
      })
      .catch((error) => {
        console.log(error);
      });
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
