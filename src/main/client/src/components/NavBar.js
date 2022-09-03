import styles from "./NavBar.module.css";
import { MdArrowBack, MdPerson, MdQuestionAnswer, MdGroups } from "react-icons/md";
import { Link } from "react-router-dom";

function NavBar({ mode, roomId, handler = null }) {
  console.log();
  console.log(`/classroom/${roomId}/chat`);
  return (
    <div className={styles.navBar}>
      <div className={styles.left}>
        <a path={mode == "classroom" ? "/" : `/classroom/${roomId}`}>
          <MdArrowBack size="40" />
        </a>
      </div>
      <div className={styles.right}>
        {mode == "classroom" ? (
          <span onClick={handler}>
            <MdQuestionAnswer size="40" />
          </span>
        ) : null}
      </div>
    </div>
  );
}

export default NavBar;
