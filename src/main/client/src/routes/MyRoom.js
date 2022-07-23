import NavBar from "../components/NavBar";
import styles from "./MyRoom.module.css";
import Circle from "../components/Circle";

function Face() {
  const emojis = ["😢", "🤔", "😯", "🙂", "😀"];
  const colors = ["red", "orange", "yellow", "green", "blue"];
  return (
    <div>
      <NavBar mode="mystate" />
      <div className={styles.container}>
        <Circle size="large" state="blue" emoji="🙂" />
      </div>
      <div className={styles.expression}>
        <div>
          <div>
            {emojis.map((item, index) => (
              <a className={styles.emoji} key={index}>
                {item}
              </a>
            ))}
          </div>
          <div>
            {colors.map((item, index) => (
              <Circle key={index} size="small" state={item} />
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}

export default Face;
