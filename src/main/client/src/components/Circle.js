import styles from "./Circle.module.css";
import smile from "./emojis/smile.svg";

function Seat({
  size,
  state = "empty",
  emoji,
  mySeat = false,
  handler = null,
}) {
  const handleOnclick = () => {
    console.log("click");
  };
  return (
    <div
      className={`${styles.seat} ${styles[state]} ${styles[size]}`}
      onClick={handler}
    >
      <span className={`${styles.float} ${styles.emoji}`}>{emoji}</span>
      {mySeat ? <span className={`${styles.float} ${styles.I}`}>I</span> : null}
    </div>
  );
}

export default Seat;
