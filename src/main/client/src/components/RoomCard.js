import styles from "./RoomCard.module.css";
import axios from "axios";
import NavLinkButton from "./NavLinkButton";
import Button from "./Button";
import { useContext, useState } from "react";
import { CurrentUserContext } from "../contexts/CurrentUserContext";
import { useNavigate } from "react-router";

export function RoomCard({ roomInfo }) {
  const { currentUser } = useContext(CurrentUserContext);
  const [editing, setEditing] = useState(false);
  const [isDeleted, setIsDeleted] = useState(false);
  const [savedRoomName, setSavedRoomName] = useState(roomInfo.roomName);
  const [newRoomName, setNewRoomName] = useState(roomInfo.roomName);
  const isHost = roomInfo.hostEmail === currentUser.email;
  const navigate = useNavigate();

  const updateRoomName = async () => {
    setEditing((editing) => !editing);
    if (roomInfo.roomName !== newRoomName) {
      try {
        await axios.put(`/classroom/${roomInfo.id}`, {
          roomName: newRoomName,
        });
        setSavedRoomName(newRoomName);
      } catch (error) {
        console.error("There has been an error", error);
      }
    }
  };

  const deleteRoomName = async () => {
    try {
      await axios.delete(`/classroom/${roomInfo.id}`);
      // navigate(0);
      setIsDeleted(true);
    } catch (error) {
      console.error("There has been an error", error);
    }
  };

  return (
    !isDeleted && (
      <div className={styles.card}>
        {editing ? (
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
              <div className={styles.host}>{roomInfo.hostUsername}</div>
            </div>
            <div className={styles.buttons}>
              <Button text="설정" type="edit" handleClick={updateRoomName} />
              <br></br>
              <Button text="삭제" type="delete" handleClick={deleteRoomName} />
            </div>
          </div>
        ) : (
          <div className={styles.container}>
            <div className={styles.roomInfo}>
              <div className={styles.roomName}>{savedRoomName}</div>
              <div className={styles.host}>{roomInfo.hostUsername}</div>
            </div>
            <div className={styles.buttons}>
              {isHost ? <Button text="설정" type="edit" handleClick={updateRoomName} /> : null}
              <br></br>
              <NavLinkButton
                path={`classroom/${roomInfo.id}`}
                text="입장"
                type="enter"
                roomId={roomInfo.id}
              />
            </div>
          </div>
        )}
      </div>
    )
  );
}

export function EnterRoomCard({ roomInfo, handleJoinClick }) {
  return (
    <div className={styles.card}>
      <div className={styles.container}>
        <div className={styles.roomInfo}>
          <div className={styles.roomName}>{roomInfo.roomName}</div>
          <div className={styles.host}>{roomInfo.hostUsername}</div>
        </div>
        <div className={styles.buttons}>
          <br></br>
          <Button text="참여" type="room" handleClick={handleJoinClick} />
        </div>
      </div>
    </div>
  );
}
