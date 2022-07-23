import { useNavigate } from "react-router-dom";
import { useState } from "react";
import StyledButton from "../components/StyledButton";
import styles from "./EnterRoom.module.css";

function EnterRoom() {
  const [roomcode, setRoomcode] = useState("");
  const [roomname, setRoomname] = useState("");
  const navigate = useNavigate();
  const [errorMsg, setMessage] = useState("");
  const enterRoom = async () => {
    const opts = {
      method: "POST",
      body: JSON.stringify({
        roomcode: roomcode,
      }),
      headers: new Headers({
        "content-type": "application/json",
      }),
    };
    try {
      const response = await fetch("/api/enterroom", opts);
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
      <div>
        {errorMsg == "" ? null : (
          <div className={styles.errorMsg}>{errorMsg}</div>
        )}
        <p className={styles.label}>입장 코드</p>
        <input type="text"></input>
        <StyledButton text="방 참가" handler={enterRoom} />
      </div>
    </div>
  );
}

export default EnterRoom;
