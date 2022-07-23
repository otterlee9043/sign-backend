import styles from "./StyledButton.module.css";

function StyledButton({ text, color, handler }) {
  return <button onClick={handler}>{text}</button>;
}

export default StyledButton;
