import React, { useState, useEffect, useContext } from "react";
import styles from "./Classroom.module.css";
import NavBar from "../components/NavBar.js";
import Circle from "../components/Circle.js";
import { useParams } from "react-router-dom";
import SockJS from "sockjs-client";
import { over } from "stompjs";
import { Stomp, Client } from "@stomp/stompjs";
import { ReportProblem, StarPurple500 } from "@mui/icons-material";
// import io from "socket.io-client";
// import TextField from "@material-ui/core/TextField";
// import { withStyles } from "@material-ui/core/styles";
let stompClient = null;

function Room() {
  const params = useParams();
  const [username, setUsername] = useState("");
  const [connected, setConnected] = useState(false);
  const roomId = params.roomId;
  const colors = ["red", "orange", "yellow", "green", "blue"];
  let seats = new Array(40).fill({});
  for (let i = 0; i < 40; i++) {
    const result = useState("empty");
    seats[i] = {
      value: result[0],
      setState: result[1],
    };
  }

  const connect = async () => {
    let Sock = new SockJS("http://localhost:8080/ws");
    stompClient = over(Sock);
  };

  const onConnected = () => {
    stompClient.subscribe(`/topic/chat/room/${roomId}`, onMessageReceived);
    stompClient.send(
      "/app/chat/message",
      { roomId: roomId },
      JSON.stringify({ type: "ENTER", roomId: roomId, sender: username })
    );
    console.log(`=> sessionId: `, stompClient);
    getCurrentState();
    getMyPosition();
  };

  const onMessageReceived = (received) => {
    console.log(received);
    const parsedMsg = JSON.parse(received.body);
    if (parsedMsg.type === "TALK") color(parsedMsg.message);
  };

  const onError = (err) => {
    console.log(err);
  };

  function color(receivedColor) {
    seats[10].setState(receivedColor);
    console.log(seats[10].value);
  }

  const getUsername = async () => {
    try {
      const response = await fetch("/api/member/username");
      if (response.status !== 200) {
        alert("There has been some errors.");
        return false;
      }
      const data = await response.text();
      if (username === "expired") setUsername("");
      else setUsername(data);
      // console.log("This came from the backend", username);
    } catch (error) {
      console.error("There has been an error login", error);
    }
  };

  const getCurrentState = async () => {
    try {
      const response = await fetch(`/api/classroom/${roomId}/states`);
      if (response.status !== 200) {
        alert("There has been some errors.");
        return false;
      }
      const data = await response.json();
      console.log(data);
      //if (username === "expired") setUsername("");
      //else setUsername(data);
      // console.log("This came from the backend", username);
    } catch (error) {
      console.error("There has been an error login", error);
    }
  };

  const getMyPosition = async () => {
    try {
      const response = await fetch(`/api/classroom/${roomId}/mySeat`);
      if (response.status !== 200) {
        alert("There has been some errors.");
        return false;
      }
      const data = await response.text();
      console.log(data);
      //if (username === "expired") setUsername("");
      //else setUsername(data);
      // console.log("This came from the backend", username);
    } catch (error) {
      console.error("There has been an error login", error);
    }
  };

  const selectColor = (color) => {
    console.log(stompClient.connected);
    stompClient.send(
      "/app/chat/message",
      {},
      JSON.stringify({ type: "TALK", roomId: roomId, sender: username, message: color })
    );
  };

  //
  useEffect(() => {
    getUsername();
    let reconnect = 0;
    connect().then(() => {
      stompClient.connect({ roomId: roomId }, onConnected, onError);
    });
  }, []);

  return (
    <div>
      <NavBar mode="classroom" />
      <div className={styles.container}>
        <div className={styles.seats}>
          {seats.map((seat, index) => (
            <Circle key={index} size="small" state={seat.value} emoji="" />
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
      <span onClick={() => stompClient.disconnect()}>disconnect</span>
    </div>
  );
}

export default Room;
