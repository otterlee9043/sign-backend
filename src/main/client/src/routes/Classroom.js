import React, { useState, useEffect, useContext } from "react";
import styles from "./Classroom.module.css";
import NavBar from "../components/NavBar.js";
import Circle from "../components/Circle.js";
import { useParams } from "react-router-dom";
import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";
import { StarPurple500 } from "@mui/icons-material";
// import io from "socket.io-client";
// import TextField from "@material-ui/core/TextField";
// import { withStyles } from "@material-ui/core/styles";

function Room() {
  const params = useParams();
  const [username, setUsername] = useState("");
  const roomId = params.roomId;
  const colors = ["red", "orange", "yellow", "green", "blue"];
  const seats = new Array(40).fill({});
  for (let i = 0; i < 40; i++) {
    const result = useState("empty");
    seats[i] = {
      value: result[0],
      setState: result[1]
    };
    console.log(seats[i]);
  }

  let stompClient = Stomp.over(function () {
    return new SockJS("http://localhost:8080/api/classroom/");
  });

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
      const data = await response.text();
      if (username === "expired") setUsername("");
      else setUsername(data);
      // console.log("This came from the backend", username);
    } catch (error) {
      console.error("There has been an error login", error);
    }
  }

  const selectColor = (color) => {
    console.log(stompClient.connected);
    console.log({ type: "TALK", roomId: roomId, sender: username, message: color });
    stompClient.activate();
    stompClient.send(
      "/app/chat/message",
      {},
      JSON.stringify({ type: "TALK", roomId: roomId, sender: username, message: color })
    );

  }
  let reconnect = 0;

  stompClient.onConnect = (_) => {
    console.log("onConnect");
    stompClient.subscribe(`/topic/chat/room/${roomId}`, (received) => {
      // console.log(received.body);
      // console.log("@subscribe");
      // console.log(stompClient.connected);
      // color(received.body);
    });
    stompClient.send(
      "/app/chat/message",
      {},
      JSON.stringify({ type: "ENTER", roomId: roomId, sender: username })
    );
  };

  stompClient.onChangeState = (state) => {
    console.log("something changed!");
    console.log(state);
  }

  stompClient.onDisconnect = () => {
    console.log("disconnect");
  }

  stompClient.onWebSocketClose = () => {
    console.log("close");
  }
  stompClient.onWebSocketError = () => {
    console.log("onWebSocketError");
  }

  stompClient.onStompError = (frame) => {
      console.log('Broker reported error: ' + frame.headers['message']);
      console.log('Additional details: ' + frame.body);
  };
  

  //
  useEffect(() => {
    getUsername();
      // stompClient.activate();
    //stompClient.connect();
    stompClient.connect(
    {},
    (_) => {
      console.log("connect");
      stompClient.subscribe(`/topic/chat/room/${roomId}`, (received) => {
        color(received.body);
      });
      stompClient.send(
        "/app/chat/message",
        {},
        JSON.stringify({ type: "ENTER", roomId: roomId, sender: username })
      );
    });
  }, []);

  function color(message) {
    console.log("message.message :", message.message);
    seats[10].setState(message.message);
    console.log(seats[10].value);
    // setSeats(seats.filter(seat => seat == ))
  }

  function receiveColor(message) {
    
  }
  return (
    <div>
      <NavBar mode="classroom" />
      <div className={styles.container}>
        <div className={styles.seats}>
          {seats.map((seat, index) => (
            <Circle key={index} size="small" state={seat.val} emoji="" />
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


// {
//     "destination": "/app/chat/message",
//     "headers": {},
//     "body": "{\"type\":\"TALK\",\"roomId\":\"8\",\"sender\":\"sualee\",\"message\":\"green\"}",
//     "skipContentLengthHeader": false
// }
