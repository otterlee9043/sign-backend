import { useNavigate } from "react-router-dom";
import { useState } from "react";
import Button from "../components/Button";
import styles from "./RoomForm.module.css";

function EnterRoom() {
  const [roomcode, setRoomcode] = useState("");
  const navigate = useNavigate();
  const [errorMsg, setMessage] = useState("");
  const findRoomByRoomcode = async () => {
    try {
      const response = await fetch(`/api/classrooms/${roomcode}`);
      const result = await response.text();
      if (result === "no room") {
        window.location.reload();
        return;
      }
      return result;
    } catch (error) {
      console.error("There has been error while getting room Id", error);
      return;
    }
  };

  const enterRoom = async () => {
    const roomId = await findRoomByRoomcode();
    const opts = {
      method: "POST",
      body: JSON.stringify({
        roomCode: roomcode,
      }),
      headers: new Headers({
        "content-type": "application/json",
      }),
    };

    try {
      const response = await fetch(`/api/classroom/${roomId}/join`, opts);
      if (response.status !== 200) {
        alert("There has been some errors.");
        return false;
      }
      const data = await response.json();
      console.log("This came from the backend", data);
      if (data.msg === "entered") {
        navigate("/");
      } else if (data.msg === "doesn't exist") {
        setMessage("Room doesn't exist.");
      } else if (data.msg === "already entered.") {
        setMessage("You already entered the room.");
      }
    } catch (error) {
      console.error("There has been an error login");
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.wrapper}>
        {errorMsg == "" ? null : <div className={styles.errorMsg}>{errorMsg}</div>}
        <p className={styles.label}>입장 코드</p>
        <input
          type="text"
          className={styles.input}
          onChange={(event) => setRoomcode(event.target.value)}
        ></input>
        <Button text="방 참가" type="room" handleClick={enterRoom} />
      </div>
    </div>
  );
}

export default EnterRoom;
