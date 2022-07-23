import styles from "./Circle.module.css";
import smile from "./emojis/smile.svg";

function Seat({ size, state = "empty", emoji }) {
  const importAll = (require) =>
    require.keys().reduce((acc, next) => {
      acc[next.replace("./", "").split(".")[0]] = require(next);
      return acc;
    }, {});

  const images = importAll(
    require.context("./emojis", false, /\.(png|jpe?g|svg)$/)
  );

  //console.log(state);

  return (
    <div className={`${styles.seat} ${styles[state]} ${styles[size]}`}>
      <span className={styles.emoji}>{emoji}</span>
    </div>
    // <div className={styles.circle}>
    //   <img src={circle} alt="circle" />
    // </div>
  );
}

export default Seat;
