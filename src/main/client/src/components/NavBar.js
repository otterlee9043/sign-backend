import styles from "./NavBar.module.css";
import {
  MdArrowBack,
  MdPerson,
  MdQuestionAnswer,
  MdGroups,
} from "react-icons/md";
import { Link } from "react-router-dom";

function NavBar({ mode }) {
  return (
    <div className={styles.navBar}>
      <div className={styles.left}>
        <a path="#">
          <MdArrowBack size="40" />
        </a>
      </div>
      <div className={styles.right}>
        {mode == "classroom" ? (
          <Link to="/mystate">
            <MdPerson size="40" />
          </Link>
        ) : (
          <Link to="/classroom">
            <MdGroups size="40" />
          </Link>
        )}
        <a path="#">
          <MdQuestionAnswer size="40" />
        </a>
      </div>
    </div>
  );
}

export default NavBar;
