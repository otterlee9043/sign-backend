import React, { useState, useEffect, useContext } from "react";
import styles from "./Classroom.module.css";
import NavBar from "../components/NavBar.js";
import Circle from "../components/Circle.js";
import { useParams } from "react-router-dom";
import io from "socket.io-client";
import TextField from "@material-ui/core/TextField";
import { withStyles } from "@material-ui/core/styles";

function Room() {
  // const params = useParams();
  // const roomId = params.roomId;
  // const colors = ["red", "orange", "yellow", "green", "blue"];
  // const status = useScript("../socketio.js");

  // const joinClassroom = async (roomId) => {
  //   const opts = {
  //     method: "POST",
  //     body: JSON.stringify({
  //       roomId: roomId,
  //     }),
  //     headers: new Headers({
  //       "content-type": "application/json",
  //     }),
  //   };
  //   try {
  //     const response = await fetch("/api/joinroom/" + roomId, opts);
  //     if (response.status !== 200) {
  //       alert("There has been some errors.");
  //     }
  //     const data = await response.json();
  //     console.log("This came from the backend", data);
  //     if (data.msg === "joined") {
  //       //navigate("/");
  //     } else {
  //     }
  //   } catch (error) {
  //     console.error("There has been an error");
  //   }
  // };
  // useEffect(() => {
  //   joinClassroom(roomId);
  // }, []);

  const [message, setMessage] = useState("");
  const [chat, setChat] = useState([]);

  useEffect(() => {
    socket.on("connect", function () {
      socket.emit("joined", {});
    });
  });

  const onTextChange = (e) => {
    setMessage(e.target.value);
  };

  const onMessageSubmit = (e) => {
    e.preventDefault();
    socket.emit("message", { message });
    //setState({ message: "", name });
    setChat([...chat, { message }]);
  };

  // return (
  //   <div>
  //     <NavBar mode="classroom" />
  //     <div className={styles.container}>
  //       <div className={styles.seats}>
  //         {Array.from(Array(40).keys()).map((item, index) => (
  //           <Circle key={index} size="small" state="empty" emoji="😀" />
  //         ))}
  //       </div>
  //     </div>
  //     <div className={styles.count}>
  //       {colors.map((item, index) => (
  //         <Circle key={index} size="small" state={item} />
  //       ))}
  //     </div>
  //   </div>
  // );
  return (
    <div className="card">
      <form onSubmit={onMessageSubmit}>
        <h1>Message</h1>
        <div>
          <textarea
            name="message"
            onChange={(e) => onTextChange(e)}
            value={message}
            id="outlined-multiline-static"
            variant="outlined"
            label="Message"
          />
        </div>
        <button type="submit">Send Message</button>
      </form>
      <div className="render-chat">
        <h1>Chat log</h1>
        {chat.map(({ message }, index) => (
          <div key={index}>
            <h3>
              <span>{message}</span>
            </h3>
          </div>
        ))}
      </div>
    </div>
  );
}

export default Room;
