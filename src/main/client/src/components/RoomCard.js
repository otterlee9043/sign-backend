import styles from "./RoomCard.module.css";
import Button from "./Button.js";

function Room({ id, roomName, host }) {
  return (
    <div className={styles.card} key={id}>
      <div className={styles.roomInfo}>
        <div className={styles.roomName}>{roomName}</div>
        <div>{host}</div>
      </div>
      <div className={styles.buttons}>
        <Button path="#" text="삭제" btnType="delete" />
        <br></br>
        <Button
          path={`classroom/${id}`}
          text="입장"
          btnType="enter"
          roomId={id}
        />
      </div>
    </div>
  );
}

export default Room;
