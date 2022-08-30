import React, { useState, useEffect } from "react";
import styles from "./Classroom.module.css";
import NavBar from "../components/NavBar.js";
import Circle from "../components/Circle.js";
import { useParams } from "react-router-dom";
import SockJS from "sockjs-client";
import { over } from "stompjs";
import axios from "axios";
import { useLocation } from "react-router-dom";
let stompClient = null;

function Room() {
  const params = useParams();
  const [username, setUsername] = useState("");
  const [mySeat, setMySeat] = useState();
  const [seats, setSeats] = useState(new Array(40).fill("empty"));
  const roomId = params.roomId;
  const colors = ["red", "orange", "yellow", "green", "blue"];
  let location = useLocation();

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
    getClassroomInfo();
  };

  const onMessageReceived = (received) => {
    console.log(received);
    const parsedMsg = JSON.parse(received.body);
    switch(parsedMsg.type) {
      case "TALK": 
        color(parsedMsg.seatNum, parsedMsg.message);  
        break;
      case "EXIT":
        color(parsedMsg.seatNum, "empty");
        break;
    }
  
  };

  const onError = (err) => {
    console.log(err);
  };

  function color(seatNum, receivedColor) {
    setSeats((oldSeats) => {
      let newSeats = [...oldSeats];
      newSeats[seatNum] = receivedColor;
      return newSeats;
    });
  }

  const selectColor = (color) => {
    stompClient.send(
      "/app/chat/message",
      {},
      JSON.stringify({
        type: "TALK",
        roomId: roomId,
        sender: username,
        message: color,
        seatNum: mySeat,
      })
    );
  };

  const getClassroomInfo = async () => {
    axios
      .get(`/api/classroom/${roomId}/classroomInfo`)
      .then((res) => {
        const classroomInfo = res.data;
        console.log(classroomInfo);
        setMySeat(parseInt(classroomInfo.seatNum));
        setSeats((oldSeats) => {
          let newSeats = [...oldSeats];
          for (let seatNum in Object.entries(classroomInfo.classRoomStates)) {
            newSeats[seatNum] = classroomInfo.classRoomStates[seatNum];
          }
          return newSeats;
        });

        console.log(classroomInfo.classRoomStates);
      })
      .catch((err) => {
        console.log(err);
      });
  };

  useEffect(() => {
    const getUsername = async () => {
      axios
        .get("/api/member/username")
        .then((res) => {
          if (res.status !== 200) {
            alert("There has been some errors.");
          }
          if (username === "expired") setUsername("");
          else setUsername(res.data);
          connect().then(() => {
            stompClient.connect({ roomId: roomId, username: res.data }, onConnected, onError);
          });
        })
        .catch((err) => {
          console.error("There has been an error login", err);
        });
    };

    getUsername();
    window.onpopstate = () => {
      stompClient.disconnect();
    };
    window.addEventListener("beforeunload", (event) => {
      event.preventDefault();
      event.returnValue = "";
      stompClient.disconnect();
    });
  }, []);


  return (
    <div>
      <NavBar mode="classroom" />
      <div className={styles.container}>
        <div className={styles.seats}>
          {seats.map((color, index) =>
            index == mySeat ? (
              <Circle key={index} size="small" state={color} emoji="" mySeat={true} />
            ) : (
              <Circle key={index} size="small" state={color} emoji="" />
            )
          )}
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
