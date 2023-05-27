import { useNavigate } from "react-router-dom";
import { useState } from "react";
import styles from "./RoomForm.module.css";
import Button from "../components/Button";
function CreateRoom() {
  const [roomcode, setRoomcode] = useState("");
  const [roomname, setRoomname] = useState("");
  const [errorMsg, setMessage] = useState("");
  const navigate = useNavigate();
  const createRoom = async () => {
    const opts = {
      method: "POST",
      body: JSON.stringify({
        roomName: roomname,
        roomCode: roomcode,
      }),
      headers: new Headers({
        "content-type": "application/json",
      }),
    };
    try {
      const response = await fetch("/api/classrooms", opts);
      if (response.status !== 200) {
        alert("There has been some errors.");
        return false;
      }
      const data = await response.text();
      console.log("This came from the backend", data);
      if (data === "classroom successfully created") {
        //console.log(location.pathname);
        navigate("/home");
      } else if (data === "classroom successfully created") {
        setMessage("Room name already exists.");
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
          className={styles.input}
          type="text"
          onChange={(event) => setRoomcode(event.target.value)}
        ></input>
        <p className={styles.label}>방 이름</p>
        <input
          className={styles.input}
          type="text"
          onChange={(event) => setRoomname(event.target.value)}
        ></input>
        <Button text="방 생성" type="room" handleClick={createRoom} />
      </div>
    </div>
  );
}

export default CreateRoom;
