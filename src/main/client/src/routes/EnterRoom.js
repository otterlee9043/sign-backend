import { useNavigate } from "react-router-dom";
import { useState } from "react";
import Button from "../components/Button";
import styles from "./RoomForm.module.css";
import RoomCard from "../components/RoomCard";
import NavBar from "../components/NavBar.js";

function EnterRoom() {
  const [roomcode, setRoomcode] = useState("");
  const navigate = useNavigate();
  const [errorMessage, setErrorMessage] = useState("");
  const [foundRoom, setFoundRoom] = useState(null);

  const findRoomByRoomcode = async () => {
    try {
      const response = await fetch(`/api/classrooms/byCode/${roomcode}`);
      if (response.ok) {
        const result = await response.json();
        console.log("result", result);
        setErrorMessage(null);
        setFoundRoom(result);
      } else {
        setFoundRoom(null);
        if (response.status === 404) {
          const result = await response.json();
          setErrorMessage(result["message"]);
        } else {
          console.log(response);
        }
      }
    } catch (error) {
      console.error("There has been error while getting room Id", error);
    }
  };

  const enterRoom = async () => {
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
      const response = await fetch(`/api/classroom/${foundRoom["id"]}/join`, opts);
      if (response.ok) {
        navigate("/home");
      } else if (response.status === 409) {
        const result = await response.json();
        setErrorMessage(result["message"]);
      } else {
        console.log(response);
      }
    } catch (error) {
      console.error("There has been an error login");
    }
  };

  return (
    <div className={styles.container}>
      <NavBar mode="default" />
      <div className={styles.wrapper}>
        <p className={styles.label}>입장 코드</p>
        <input
          type="text"
          className={styles.input}
          onChange={(event) => setRoomcode(event.target.value)}
        ></input>
        <Button text="방 찾기" type="room" handleClick={findRoomByRoomcode} />

        {foundRoom ? (
          <RoomCard
            key={foundRoom.id}
            id={foundRoom.id}
            type="참여"
            roomName={foundRoom.roomName}
            hostUsername={foundRoom.hostUsername}
            hostEmail={foundRoom.hostEmail}
            handleJoinClick={enterRoom}
          />
        ) : null}
        {errorMessage !== "" ? <div className={styles.errorMsg}>{errorMessage}</div> : null}
      </div>
    </div>
  );
}

export default EnterRoom;
