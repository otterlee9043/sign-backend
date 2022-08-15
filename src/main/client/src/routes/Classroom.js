import React, { useState, useEffect, useContext } from "react";
import styles from "./Classroom.module.css";
import NavBar from "../components/NavBar.js";
import Circle from "../components/Circle.js";
import { useParams } from "react-router-dom";
import SockJS from "sockjs-client";
import { Stomp, Client } from "@stomp/stompjs";
import { StarPurple500 } from "@mui/icons-material";
// import io from "socket.io-client";
// import TextField from "@material-ui/core/TextField";
// import { withStyles } from "@material-ui/core/styles";

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
  let subscription = false;

  let stompClient = new Client({
    brokerURL: "ws://localhost:3000/ws",
    webSocketFactory: function () {
      console.log("Stomp.over");
      return new SockJS("http://localhost:8080/ws");
    },
    onConnect: (_) => {
      console.log("======== onConnect");
      if (!connected) {
        stompClient.subscribe(`/topic/chat/room/${roomId}`, (received) => {
          console.log(received.body);
          const parsedMsg = JSON.parse(received.body);
          if (parsedMsg.type == "TALK") color(parsedMsg.message);
          // subscription = true;
          setConnected(true);
          console.log("connected: ", connected);
        });
        stompClient.publish({
          destination: "/app/chat/message",
          body: JSON.stringify({ type: "ENTER", roomId: roomId, sender: username }),
        });
      }
    },
    onChangeState: (state) => {
      console.log("something changed!");
      console.log(state);
    },
    onDisconnect: () => {
      console.log("disconnect");
    },
    onWebSocketClose: () => {
      console.log("close");
    },
    debug: () => {},
    onWebSocketError: () => {
      console.log("onWebSocketError");
    },
    onStompError: (frame) => {
      console.log("Broker reported error: " + frame.headers["message"]);
      console.log("Additional details: " + frame.body);
    },
    reconnectDelay: 100000,
  });

  function color(receivedColor) {
    seats[10].setState(receivedColor);
    console.log(seats[10].value);
    // setSeats(seats.filter(seat => seat == ))
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

  const selectColor = (color) => {
    // console.log({ type: "TALK", roomId: roomId, sender: username, message: color });
    // stompClient.activate();
    console.log(stompClient.connected);
    stompClient.publish({
      destination: "/app/chat/message",
      body: JSON.stringify({ type: "TALK", roomId: roomId, sender: username, message: color }),
    });
  };

  //
  useEffect(() => {
    getUsername();
    let reconnect = 0;
  }, []);

  useEffect(() => {
    stompClient.activate();
    console.log("activate in useEffect");
  }, [stompClient]);
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
