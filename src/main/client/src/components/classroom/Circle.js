import styles from "./Circle.module.css";

function Circle({ size, state = "empty", emoji, mySeat = false, handler = null }) {
  // return (
  //   <div className={styles[`size-${size}`]}>
  //     <div className={`${styles.seat} ${styles[state]}`} onClick={handler}>
  //       <span className={`${styles.float} ${styles.emoji}`}>{emoji}</span>
  //       {mySeat ? <span className={`${styles.float} ${styles.I}`}>I</span> : null}
  //     </div>
  //   </div>
  // );
  return (
    <div className={`${styles.seat} ${styles[state]} ${styles[`size-${size}`]}`} onClick={handler}>
      <span className={`${styles.float} ${styles.emoji}`}>{emoji}</span>
      {mySeat ? <span className={`${styles.float} ${styles.I}`}>I</span> : null}
    </div>
  );
}

export default Circle;
