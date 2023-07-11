import { useNavigate } from "react-router-dom";
import { useState } from "react";
import axios from "axios";
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
      const response = await axios.get(`/classrooms/byCode/${roomcode}`);
      const result = response.data;
      console.log("result", result);
      setErrorMessage(null);
      setFoundRoom(result);
    } catch (error) {
      setFoundRoom(null);
      if (error.response && error.response.status === 404) {
        const result = await response.json();
        setErrorMessage(result["message"]);
      } else {
        console.log(response);
      }
    }
  };

  const enterRoom = async () => {
    try {
      await axios.post(`/classroom/${foundRoom["id"]}/join`, {
        roomCode: roomcode,
      });
      navigate("/home");
    } catch (error) {
      if (error.response && error.response.status === 409) {
        setErrorMessage(error.response.data["message"]);
      } else {
        console.log(response);
      }
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
