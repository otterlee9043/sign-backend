import styles from "./NavLinkButton.module.css";
import { Link } from "react-router-dom";

function NavLinkButton({ path, text, type, roomId = null }) {
  return (
    <Link to={`/${path}`} className={`${styles.btn} ${styles[type]}`}>
      {text}
    </Link>
  );
}

export default NavLinkButton;