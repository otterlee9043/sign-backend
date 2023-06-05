import styles from "./NavBar.module.css";
import { MdArrowBack, MdQuestionAnswer } from "react-icons/md";
import { useNavigate } from "react-router-dom";

function NavBar({ mode, openChatroom, disconnect }) {
  const navigate = useNavigate();
  return (
    <div className={styles.navBar}>
      <div className={styles.left}>
        <span
          onClick={() => {
            disconnect();
            navigate("/home");
          }}
        >
          <MdArrowBack size="40" />
        </span>
      </div>
      <div className={styles.right}>
        {mode == "classroom" ? (
          <span onClick={openChatroom}>
            <MdQuestionAnswer size="40" />
          </span>
        ) : null}
      </div>
    </div>
  );
}

export default NavBar;
