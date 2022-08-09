import React, { useState, useEffect, useContext } from "react";
import styles from "./Classroom.module.css";
import NavBar from "../components/NavBar.js";
import Circle from "../components/Circle.js";
import { useParams } from "react-router-dom";
import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";
import { connect } from "socket.io-client";
// import io from "socket.io-client";
// import TextField from "@material-ui/core/TextField";
// import { withStyles } from "@material-ui/core/styles";

function Room() {
  const params = useParams();
  const [username, setUsername] = useState("");
  const roomId = params.roomId;
  const colors = ["red", "orange", "yellow", "green", "blue"];
  // const seats = new Array(40).fill("empty");

  const [seats, setSeats] = useState(new Array(40).fill("empty"));
  let copiedSeats;
  let sockJS = new SockJS("http://localhost:8080/api/classroom/");
  let stompClient = Stomp.over(sockJS);
  const joinClassroom = async (roomId) => {
    const opts = {
      method: "POST",
      body: JSON.stringify({
        roomId: roomId,
      }),
      headers: new Headers({
        "content-type": "application/json",
      }),
    };
    try {
      const response = await fetch("/api/joinroom/" + roomId, opts);
      if (response.status !== 200) {
        alert("There has been some errors.");
      }
      const data = await response.json();
      console.log("This came from the backend", data);
      if (data.msg === "joined") {
        //navigate("/");
      } else {
      }
    } catch (error) {
      console.error("There has been an error");
    }
  };

  const getUsername = async () => {
    try {
      const response = await fetch("/api/member/username");
      if (response.status !== 200) {
        alert("There has been some errors.");
        return false;
      }
      username = await response.text();
      if (username === "expired") setUsername("");
      else setUsername(username);
      console.log("This came from the backend", username);
    } catch (error) {
      console.error("There has been an error login", error);
    }
  }

  const selectColor = async (color) => {
    console.log(color);
    stompClient.send(
      "/app/chat/message",
      {},
      JSON.stringify({ type: "TALK", roomId: roomId, sender: username, message: color })
    );
  }
  let reconnect = 0;
  useEffect(() => {
    //joinClassroom(roomId);
    function connect() {
      stompClient.connect(
        {},
        (_) => {
          stompClient.subscribe(`/topic/chat/room/${roomId}`, (received) => {
            const message = JSON.parse(received.body);
            color(message);
          });
          stompClient.send(
            "/app/chat/message",
            {},
            JSON.stringify({ type: "ENTER", roomId: roomId, sender: username })
          );
        },
        (error) => {
          if (reconnect++ <= 5) {
            setTimeout(() => {
              console.log("connection reconnect");
              sockJS = new SockJS("http://localhost:8080/api/classroom/");
              stompClient = Stomp.over(sockJS);
              connect();
            }, 10 * 1000);
          }
        }
      );
    }
    connect();
    
  }, []);

  function color(message) {
    console.log(message);
    copiedSeats = seats;
    copiedSeats[10] = message.message;
    setSeats(copiedSeats);
    console.log(seats[10]);
    //seats[10] = message.message;
    // setSeats(seats.filter(seat => seat == ))
  }

  function receiveColor(message) {
    
  }
  return (
    <div>
      <NavBar mode="classroom" />
      <div className={styles.container}>
        <div className={styles.seats}>
          {seats.map((item, index) => (
            <Circle key={index} size="small" state={item} emoji="" />
          ))}
        </div>
      </div>
      <div className={styles.count}>
        {colors.map((color, index) => (
          <span onClick={() => selectColor(color)} key={index}>
            <Circle key={index} size="small" state={color} />
          </span>
        ))}
      </div>
    </div>
  );
  // return (
  //   <div className="card">
  //     <form onSubmit={onMessageSubmit}>
  //       <h1>Message</h1>
  //       <div>
  //         <textarea
  //           name="message"
  //           onChange={(e) => onTextChange(e)}
  //           value={message}
  //           id="outlined-multiline-static"
  //           variant="outlined"
  //           label="Message"
  //         />
  //       </div>
  //       <button type="submit">Send Message</button>
  //     </form>
  //     <div className="render-chat">
  //       <h1>Chat log</h1>
  //       {chat.map(({ message }, index) => (
  //         <div key={index}>
  //           <h3>
  //             <span>{message}</span>
  //           </h3>
  //         </div>
  //       ))}
  //     </div>
  //   </div>
  // );
}

export default Room;
