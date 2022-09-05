import React, { useState, useEffect, useRef } from "react";
import styles from "./Classroom.module.css";
import NavBar from "../components/NavBar.js";
import Circle from "../components/Circle.js";
import { useParams } from "react-router-dom";
import SockJS from "sockjs-client";
import { over } from "stompjs";
import axios from "axios";
import { Card } from "react-bootstrap";
let stompClient = null;

function Room() {
  const params = useParams();
  const [username, setUsername] = useState("");
  const [mySeat, setMySeat] = useState(-1);
  const [myRow, setMyRow] = useState(-1);
  const [visible, setVisible] = useState(false);
  const [seats, setSeats] = useState(new Array(40).fill("empty"));
  const [chat, setChat] = useState([]);
  const roomId = parseInt(params.roomId);
  const colors = ["red", "orange", "yellow", "green", "blue"];
  const columnNum = 5;
  const CLASSROOM_EVENT = {
    ENTER: "ENTER",
    EXIT: "EXIT",
    TALK: "TALK",
    CHANGE_SEAT: "CHANGE_SEAT",
  };
  const stateRef = useRef();
  const messageRef = useRef();
  const chatRef = useRef();
  stateRef.current = mySeat;
  chatRef.current = chat;

  const connect = async () => {
    let Sock = new SockJS("http://localhost:8080/ws");
    stompClient = over(Sock);
  };

  const onConnected = () => {
    stompClient.subscribe(`/topic/classroom/${roomId}`, onColorReceived);
    stompClient.send(
      `/app/classroom/${roomId}`,
      { roomId: roomId },
      JSON.stringify({ type: CLASSROOM_EVENT.ENTER, roomId: roomId, sender: username })
    );
    getClassroomInfo();
  };

  const onColorReceived = (received) => {
    const parsedMsg = JSON.parse(received.body);
    switch (parsedMsg.type) {
      case CLASSROOM_EVENT.TALK:
        color(parsedMsg.seatNum, parsedMsg.message);
        break;
      case CLASSROOM_EVENT.EXIT:
        color(parsedMsg.seatNum, "empty");
        break;
      case CLASSROOM_EVENT.CHANGE_SEAT:
        setSeats((oldSeats) => {
          let newSeats = [...oldSeats];
          newSeats[parseInt(parsedMsg.message)] = oldSeats[parsedMsg.seatNum];
          newSeats[parsedMsg.seatNum] = "empty";
          return newSeats;
        });
        if (isMySeat(parsedMsg.seatNum)) {
          setMySeat(parseInt(parsedMsg.message));
          setMyRow(parseInt(mySeat / columnNum));
        }
        break;
    }
  };

  const onMessageReceived = (received) => {
    console.log(chatRef.current);
    const parsedMsg = JSON.parse(received.body);
    setChat([...chatRef.current, parsedMsg]);
    console.log(chat);
  };

  const isMySeat = (seatNum) => {
    return seatNum === stateRef.current;
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
      `/app/classroom/${roomId}`,
      {},
      JSON.stringify({
        type: CLASSROOM_EVENT.TALK,
        roomId: roomId,
        sender: username,
        message: color,
        seatNum: mySeat,
      })
    );
  };

  const changeSeat = (seatNum) => {
    stompClient.send(
      `/app/classroom/${roomId}`,
      {},
      JSON.stringify({
        type: CLASSROOM_EVENT.CHANGE_SEAT,
        roomId: roomId,
        sender: username,
        message: seatNum,
        seatNum: mySeat,
      })
    );
  };

  const sendMessage = (message) => {
    stompClient.send(
      `/app/classroom/${roomId}/chat/${myRow}`,
      {},
      JSON.stringify({
        type: CLASSROOM_EVENT.TALK,
        seatNum: mySeat + 1,
        sender: username,
        content: message,
      })
    );
  };

  const getClassroomInfo = async () => {
    axios
      .get(`/api/classroom/${roomId}/classroomInfo`)
      .then((res) => {
        const classroomInfo = res.data;
        setMySeat(classroomInfo.seatNum);
        setMyRow(() => {
          const row = parseInt(classroomInfo.seatNum / columnNum);
          stompClient.subscribe(`/topic/classroom/${roomId}/chat/${row}`, onMessageReceived);
          return row;
        });
        setSeats((oldSeats) => {
          let newSeats = [...oldSeats];
          for (let seatNum in classroomInfo.classRoomStates) {
            newSeats[seatNum] = classroomInfo.classRoomStates[seatNum];
          }
          return newSeats;
        });

        console.log(`/topic/classroom/${roomId}/chat/${myRow}`);
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
      console.log(event);
      console.log(document.activeElement.href);
      event.preventDefault();
      event.returnValue = "";
      stompClient.disconnect();
    });
  }, []);

  const openChatroom = () => {
    setVisible((visible) => !visible);
  };
  return (
    <div className={styles.wrapper}>
      <NavBar mode="classroom" roomId={roomId} handler={openChatroom} />
      <div className={styles.classroom}>
        <div className={styles.container}>
          <div className={styles.seats}>
            {seats.map((color, index) =>
              index === mySeat ? (
                <Circle key={index} size="small" state={color} emoji="" mySeat={true} />
              ) : color !== "empty" ? (
                <Circle key={index} size="small" state={color} emoji="" />
              ) : (
                <span key={index} onClick={() => changeSeat(index)}>
                  <Circle key={index} size="small" state={color} emoji="" />
                </span>
              )
            )}
          </div>
        </div>
        <div className={styles.colors}>
          {colors.map((color, index) => (
            <span onClick={() => selectColor(color)} key={index}>
              <Circle key={index} size="small" state={color} />
            </span>
          ))}
        </div>
      </div>
      <div className={visible ? styles.chatroom : styles.hidden}>
        <ul className={styles.msgContainer}>
          {chat.map((data, index) => 
            stateRef.current + 1 == data.seatNum ? (
              <div key={index} className={`${styles.msgWrapper} ${styles.right}`}>
                <span className={styles.sentTime}>{data.sentTime}</span>
                <li className={styles.message}>
                  {data.content}
                </li>
              </div>
            ) : (
              <div key={index} className={`${styles.msgWrapper} ${styles.left}`}>
                <span className={styles.sender}>{data.seatNum}번 좌석</span>
                <li className={styles.message}>
                  <span>{data.content}</span>
                </li>
                <span className={styles.sentTime}>{data.sentTime}</span>
              </div>
            )
          )}
        </ul>
        <div className={styles.inputBar}>
          <form
            onSubmit={(event) => {
              event.preventDefault();
              if (messageRef.current.value != "") sendMessage(messageRef.current.value);
              messageRef.current.value = "";
            }}
          >
            <input className={styles.input} ref={messageRef} />
            <button className={styles.button} type="submit">
              전송
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}

export default Room;
