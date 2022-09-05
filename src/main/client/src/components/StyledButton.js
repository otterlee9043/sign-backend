import styles from "./StyledButton.module.css";

function StyledButton({ text, color, handler }) {
  return (
    <button className={styles.button} onClick={handler}>
      {text}
    </button>
  );
}

export default StyledButton;
