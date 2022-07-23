import { useNavigate } from "react-router-dom";
import { useState } from "react";
import Button from "../components/Button";
import styles from "./CreateRoom.module.css";
import StyledButton from "../components/StyledButton";
function CreateRoom() {
  const [roomcode, setRoomcode] = useState("");
  const [roomname, setRoomname] = useState("");
  const [errorMsg, setMessage] = useState("");
  const navigate = useNavigate();
  const createRoom = async () => {
    const opts = {
      method: "POST",
      body: JSON.stringify({
        roomname: roomname,
        roomcode: roomcode,
      }),
      headers: new Headers({
        "content-type": "application/json",
      }),
    };
    try {
      const response = await fetch("/api/createroom", opts);
      if (response.status !== 200) {
        alert("There has been some errors.");
        return false;
      }
      const data = await response.json();
      console.log("This came from the backend", data);
      if (data.msg === "created") {
        //console.log(location.pathname);
        navigate("/");
      } else if (data.msg === "already exist") {
        setMessage("Room name already exists.");
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
        <input
          type="text"
          onChange={(event) => setRoomcode(event.target.value)}
        ></input>
        <p className={styles.label}>방 이름</p>
        <input
          type="text"
          onChange={(event) => setRoomname(event.target.value)}
        ></input>
        {/* {<Button path="" text="방 생성" btnType="room" />} */}
        <StyledButton text="방 생성" handler={createRoom} />
      </div>
    </div>
  );
}

export default CreateRoom;
