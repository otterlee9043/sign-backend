import styles from "./RoomCard.module.css";
import axios from "axios";
import NavLinkButton from "./NavLinkButton";
import Button from "./Button";
import { useContext, useState } from "react";
import { CurrentUserContext } from "../contexts/CurrentUserContext";
import { useNavigate } from "react-router";

function RoomCard({ type, id, roomName, hostUsername, hostEmail, handleJoinClick }) {
  const { currentUser } = useContext(CurrentUserContext);
  const [editing, setEditing] = useState(false);
  const [savedRoomName, setSavedRoomName] = useState(roomName);
  const [newRoomName, setNewRoomName] = useState(roomName);

  const navigate = useNavigate();

  const handleButtonClick = () => {
    setEditing(!editing);
    if (roomName !== newRoomName) updateRoomName();
  };

  const updateRoomName = async () => {
    try {
      await axios.put(`/classroom/${id}`, {
        roomName: newRoomName,
      });
      setSavedRoomName(newRoomName);
    } catch (error) {
      console.error("There has been an error", error);
    }
  };

  const deleteRoomName = async () => {
    try {
      await axios.delete(`/classroom/${id}`, opts);
      navigate(0);
    } catch (error) {
      console.error("There has been an error", error);
    }
  };

  return type === "입장" ? (
    editing ? (
      <div className={styles.card} key={id}>
        <div className={styles.container}>
          <div className={styles.roomInfo}>
            <div className={styles.roomName}>
              <input
                size="1"
                type="text"
                className={styles["roomName-input"]}
                value={newRoomName}
                onChange={(event) => setNewRoomName(event.target.value)}
              ></input>
            </div>
            <div className={styles.host}>{hostUsername}</div>
          </div>
          <div className={styles.buttons}>
            <Button text="설정" type="edit" handleClick={handleButtonClick} />
            <br></br>
            <Button text="삭제" type="delete" handleClick={deleteRoomName} />
          </div>
        </div>
      </div>
    ) : (
      <div className={styles.card} key={id}>
        <div className={styles.container}>
          <div className={styles.roomInfo}>
            <div className={styles.roomName}>{savedRoomName}</div>
            <div className={styles.host}>{hostUsername}</div>
          </div>
          <div className={styles.buttons}>
            {hostEmail === currentUser.email ? (
              <Button text="설정" type="edit" handleClick={handleButtonClick} />
            ) : null}
            <br></br>
            {/* <Button text="입장" type="enter" handleClick={handleEnterClick} /> */}
            <NavLinkButton path={`classroom/${id}`} text="입장" type="enter" roomId={id} />
          </div>
        </div>
      </div>
    )
  ) : (
    <div className={styles.card} key={id}>
      <div className={styles.container}>
        <div className={styles.roomInfo}>
          <div className={styles.roomName}>{savedRoomName}</div>
          <div className={styles.host}>{hostUsername}</div>
        </div>
        <div className={styles.buttons}>
          <br></br>
          <Button text="참여" type="room" handleClick={handleJoinClick} />
        </div>
      </div>
    </div>
  );
}

export default RoomCard;
