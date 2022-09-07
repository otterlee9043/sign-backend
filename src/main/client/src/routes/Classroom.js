import React, { useState, useEffect, useRef } from "react";
import styles from "./Classroom.module.css";
import NavBar from "../components/NavBar.js";
import Circle from "../components/Circle.js";
import { useParams } from "react-router-dom";
import SockJS from "sockjs-client";
import { over } from "stompjs";
import axios from "axios";
import Message from "../components/Message";
import { switchClasses } from "@mui/material";
let stompClient = null;

function Room() {
  const params = useParams();
  // const [username, setUsername] = useState("");
  const [visible, setVisible] = useState(false);
  const [seats, setSeats] = useState(new Array(40).fill("empty"));
  const [chat, setChat] = useState([]);
  const [chatSubscription, setChatSubs] = useState(null);
  const roomId = parseInt(params.roomId);
  const colors = ["red", "orange", "yellow", "green", "blue"];
  const columnNum = 5;
  const EVENT = {
    ENTER: "ENTER",
    EXIT: "EXIT",
    TALK: "TALK",
    CHANGE_SEAT: "CHANGE_SEAT",
  };
  const stateRef = useRef();
  const messageRef = useRef();
  const chatRef = useRef();
  const chatSubsRef = useRef();
  const queueSubsRef = useRef();
  const rowRef = useRef();
  const usernameRef = useRef();
  chatRef.current = chat;
  let classSubscription;
  chatSubsRef.current = chatSubscription;
  const connect = async () => {
    let Sock = new SockJS("http://localhost:8080/ws");
    stompClient = over(Sock);
  };

  const onConnected = () => {
    classSubscription = stompClient.subscribe(
      `/topic/classroom/${roomId}`,
      onColorReceived
    );
    queueSubsRef.current = stompClient.subscribe(
      `/queue/temp/classroom/${roomId}/user/${usernameRef.current}`,
      (received) => {
        const classroomInfo = JSON.parse(received.body);
        stateRef.current = classroomInfo.seatNum;
        rowRef.current = parseInt(classroomInfo.seatNum / columnNum);
        setChatSubs((_) => {
          const subscription = stompClient.subscribe(
            `/topic/classroom/${roomId}/chat/${rowRef.current}`,
            onMessageReceived
          );
          stompClient.send(
            `/app/classroom/${roomId}/chat/${rowRef.current}`,
            {},
            JSON.stringify({
              type: EVENT.ENTER,
              seatNum: stateRef.current + 1,
              sender: usernameRef.current,
              row: rowRef.current + 1,
              content: null,
            })
          );
          return subscription;
        });
        setSeats((oldSeats) => {
          let newSeats = [...oldSeats];
          for (let seatNum in classroomInfo.classRoomStates) {
            newSeats[seatNum] = classroomInfo.classRoomStates[seatNum];
          }
          return newSeats;
        });
        queueSubsRef.current.unsubscribe();
      }
    );
    stompClient.send(
      `/app/classroom/${roomId}`,
      { roomId: roomId },
      JSON.stringify({
        type: EVENT.ENTER,
        roomId: roomId,
        sender: usernameRef.current,
      })
    );
    stompClient.send(
      `/app/classroomInfo/${roomId}`,
      {},
      JSON.stringify({
        sender: usernameRef.current,
      })
    );
  };

  const onColorReceived = (received) => {
    const parsedMsg = JSON.parse(received.body);
    switch (parsedMsg.type) {
      case EVENT.TALK:
        color(parsedMsg.seatNum, parsedMsg.message);
        break;
      case EVENT.EXIT:
        color(parsedMsg.seatNum, "empty");
        break;
      case EVENT.CHANGE_SEAT:
        setSeats((oldSeats) => {
          let newSeats = [...oldSeats];
          newSeats[parseInt(parsedMsg.message)] = oldSeats[parsedMsg.seatNum];
          newSeats[parsedMsg.seatNum] = "empty";
          return newSeats;
        });
        if (isMySeat(parsedMsg.seatNum)) {
          stateRef.current = parseInt(parsedMsg.message);
          rowRef.current = parseInt(stateRef.current / columnNum);
        }
        break;
    }
  };

  const onMessageReceived = (received) => {
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
        type: EVENT.TALK,
        roomId: roomId,
        sender: usernameRef.current,
        message: color,
        seatNum: stateRef.current,
      })
    );
  };

  const changeSeat = (seatNum) => {
    stompClient.send(
      `/app/classroom/${roomId}`,
      {},
      JSON.stringify({
        type: EVENT.CHANGE_SEAT,
        roomId: roomId,
        sender: usernameRef.current,
        message: seatNum,
        seatNum: stateRef.current,
      })
    );
    stompClient.send(
      `/app/classroom/${roomId}/chat/${rowRef.current}`,
      {},
      JSON.stringify({
        type: EVENT.EXIT,
        seatNum: stateRef.current + 1,
        sender: usernameRef.current,
        content: null,
      })
    );
    chatSubsRef.current.unsubscribe();
    rowRef.current = parseInt(seatNum / columnNum);
    setChatSubs((_) => {
      const subscription = stompClient.subscribe(
        `/topic/classroom/${roomId}/chat/${rowRef.current}`,
        onMessageReceived
      );
      stompClient.send(
        `/app/classroom/${roomId}/chat/${rowRef.current}`,
        {},
        JSON.stringify({
          type: EVENT.ENTER,
          seatNum: seatNum + 1,
          sender: usernameRef.current,
          row: rowRef.current + 1,
          content: null,
        })
      );
      return subscription;
    });
  };

  const sendMessage = (message) => {
    stompClient.send(
      `/app/classroom/${roomId}/chat/${rowRef.current}`,
      {},
      JSON.stringify({
        type: EVENT.TALK,
        seatNum: stateRef.current + 1,
        sender: usernameRef.current,
        content: message,
      })
    );
  };

  const getClassroomInfo = async () => {
    axios
      .get(`/api/classroom/${roomId}/classroomInfo`)
      .then((res) => {
        const classroomInfo = res.data;
        stateRef.current = classroomInfo.seatNum;
        rowRef.current = parseInt(classroomInfo.seatNum / columnNum);
        setChatSubs((_) => {
          const subscription = stompClient.subscribe(
            `/topic/classroom/${roomId}/chat/${rowRef.current}`,
            onMessageReceived
          );
          stompClient.send(
            `/app/classroom/${roomId}/chat/${rowRef.current}`,
            {},
            JSON.stringify({
              type: EVENT.ENTER,
              seatNum: stateRef.current + 1,
              sender: usernameRef.current,
              row: rowRef.current + 1,
              content: null,
            })
          );
          return subscription;
        });
        setSeats((oldSeats) => {
          let newSeats = [...oldSeats];
          for (let seatNum in classroomInfo.classRoomStates) {
            newSeats[seatNum] = classroomInfo.classRoomStates[seatNum];
          }
          return newSeats;
        });

        console.log(`/topic/classroom/${roomId}/chat/${rowRef.current}`);
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
          if (res.data === "expired") usernameRef.current = "";
          else usernameRef.current = res.data;
          connect().then(() => {
            stompClient.connect(
              { roomId: roomId, username: res.data },
              onConnected,
              onError
            );
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

  let prevRow = null;
  return (
    <div className={styles.wrapper}>
      <NavBar mode="classroom" roomId={roomId} handler={openChatroom} />
      <div className={styles.classroom}>
        <div className={styles.container}>
          <div className={styles.seats}>
            {seats.map((color, index) =>
              index === stateRef.current ? (
                <Circle
                  key={index}
                  size="small"
                  state={color}
                  emoji=""
                  mySeat={true}
                />
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
          {chat.map((data, index) => {
            switch (data.type) {
              case EVENT.ENTER:
                return (
                  <div key={index} className={styles.announcWrapper}>
                    {data.sender === usernameRef.current ? (
                      <div>
                        {index !== 0 ? <hr></hr> : null}
                        <span>{data.row}번째 줄 대화방</span>
                        <br></br>
                      </div>
                    ) : null}

                    <span className={styles.announcement}>
                      {data.seatNum}번 좌석님이 들어왔습니다.
                    </span>
                  </div>
                );
              case EVENT.EXIT:
                return (
                  <div key={index} className={styles.announcWrapper}>
                    <span className={styles.announcement}>
                      {data.seatNum}번 좌석님이 나갔습니다.
                    </span>
                  </div>
                );
              case EVENT.TALK:
                return stateRef.current + 1 == data.seatNum ? (
                  <Message key={index} myMessage={true} data={data}></Message>
                ) : (
                  <Message key={index} myMessage={false} data={data}></Message>
                );
            }
          })}
        </ul>
        <div className={styles.inputBar}>
          <form
            onSubmit={(event) => {
              event.preventDefault();
              if (messageRef.current.value != "")
                sendMessage(messageRef.current.value);
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
