import styles from "./Circle.module.css";
import smile from "./emojis/smile.svg";

function Seat({ size, state = "empty", emoji, mySeat = false, handler = null }) {
  // const importAll = (require) =>
  //   require.keys().reduce((acc, next) => {
  //     acc[next.replace("./", "").split(".")[0]] = require(next);
  //     return acc;
  //   }, {});

  // const images = importAll(
  //   require.context("./emojis", false, /\.(png|jpe?g|svg)$/)
  // );
  const handleOnclick = () => {
    console.log("click");
  };
  return (
    <div className={`${styles.seat} ${styles[state]} ${styles[size]}`} onClick={handler}>
      <span className={`${styles.float} ${styles.emoji}`}>{emoji}</span>
      {mySeat ? <span className={`${styles.float} ${styles.I}`}>I</span> : null}
    </div>
  );
}

export default Seat;
