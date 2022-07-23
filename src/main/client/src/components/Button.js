import styles from "./Button.module.css";
import { Link } from "react-router-dom";

function Button({ path, text, btnType, roomId = null }) {
  return (
    <Link to={`/${path}`} className={`${styles.btn} ${styles[btnType]}`}>
      {text}
    </Link>
  );
}

export default Button;
