import styles from "./Button.module.css";

function Button({ text, type, handleClick }) {
  return (
    <button className={`${styles.btn} ${styles[type]}`} onClick={handleClick}>
      {text}
    </button>
  );
}

export default Button;
