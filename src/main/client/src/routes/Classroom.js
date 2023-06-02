import React, { useState, useEffect, useRef, useContext } from "react";
import styles from "./Classroom.module.css";
import NavBar from "../components/NavBar.js";
import Circle from "../components/classroom/Circle";
import SeatCircle from "../components/classroom/SeatCircle";
import ColorCircle from "../components/classroom/ColorCircle";

import { connectToWebSocket } from "../utils/websocket";
import { onConnected, onColorReceived } from "../utils/messageHandler";
import { EVENT, colors, columnNum } from "../utils/classroomUtils";
import { useParams } from "react-router-dom";
import SockJS from "sockjs-client";
import { over } from "stompjs";
import { CurrentUserContext } from "../contexts/CurrentUserContext";
import Chatroom from "../components/classroom/Chatroom";
let stompClient = null;

// sender 언제 쓰지?

function Room() {
  const { currentUser, setCurrentUser } = useContext(CurrentUserContext);
  const params = useParams();
  const roomId = parseInt(params.roomId);
  const [visible, setVisible] = useState(false);

  const [seats, setSeats] = useState(new Array(40).fill("empty"));
  const [chat, setChat] = useState([]);
  const [chatSubscription, setChatSubsription] = useState(null); // stompClient.subscribe의 리턴 값

  const stateRef = useRef({}); // 좌석 번호, onConnected에서 호출
  const chatRef = useRef(); // 채팅 메시지 리스트, onMessageReceived에서 호출
  const rowRef = useRef(); // 행 숫자, onConnected(입장한 순간)에서, changeSeat(좌석 변경)에서 호출

  chatRef.current = chat;

  const connect = async () => {
    let Sock = new SockJS("http://localhost:8080/ws");
    stompClient = over(Sock);
  };

  const onConnected = () => {
    stompClient.subscribe(`/topic/classroom/${roomId}`, onColorReceived);

    const queueSub = stompClient.subscribe(
      `/queue/temp/classroom/${roomId}/user/${currentUser.username}`,
      (received) => {
        const classroomInfo = JSON.parse(received.body);
        // 1. classInfo -> 좌석번호, 행 번호 설정
        stateRef.current = {
          seatNum: classroomInfo.seatNum,
          roomId: roomId,
        };
        rowRef.current = parseInt(classroomInfo.seatNum / columnNum) + 1;
        setChatSubsription((_) => {
          // 2. 채팅 방 subscribe
          const subscription = stompClient.subscribe(
            `/topic/classroom/${roomId}/chat/${rowRef.current}`,
            onMessageReceived
          );
          // 3. 채팅 방 입장 메시지 전송
          stompClient.send(
            `/app/classroom/${roomId}/chat/${rowRef.current}`,
            {},
            JSON.stringify({
              type: EVENT.ENTER,
              seatNum: stateRef.current.seatNum,
              row: rowRef.current,
              content: null,
            })
          );
          // 4. 채팅 방 set
          return subscription;
        });
        setSeats((oldSeats) => {
          // 5. classInfo.classRoomStates -> seats
          let newSeats = [...oldSeats];
          for (let seatNum in classroomInfo.classRoomStates) {
            console.log("seatNum: ", seatNum);
            newSeats[seatNum - 1] = classroomInfo.classRoomStates[seatNum];
          }
          return newSeats;
        });
        queueSub.unsubscribe();
      }
    );
    stompClient.send(
      `/app/classroom/${roomId}`,
      { roomId: roomId },
      JSON.stringify({
        type: EVENT.ENTER,
        roomId: roomId,
      })
    );
    stompClient.send(
      `/app/classroomInfo/${roomId}`,
      {},
      JSON.stringify({
        sender: currentUser.username,
      })
    );
  };

  const onColorReceived = (received) => {
    console.log("onColorReceived: ", received);
    const parsedMsg = JSON.parse(received.body);
    switch (parsedMsg.type) {
      case EVENT.ENTER:
        console.log(parsedMsg);
        break;
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
          newSeats[parsedMsg.seatNum - 1] = "empty";
          return newSeats;
        });
        if (parsedMsg.seatNum === stateRef.current.seatNum) {
          stateRef.current.seatNum = parseInt(parsedMsg.message);
          rowRef.current = parseInt(stateRef.current.seatNum / columnNum) + 1;
        }
        break;
    }
  };

  const color = (seatNum, receivedColor) => {
    setSeats((oldSeats) => {
      let newSeats = [...oldSeats];
      newSeats[seatNum - 1] = receivedColor;
      return newSeats;
    });
  };

  const selectColor = (color) => {
    stompClient.send(
      `/app/classroom/${roomId}`,
      {},
      JSON.stringify({
        type: EVENT.TALK,
        roomId: roomId,
        message: color,
        seatNum: stateRef.current.seatNum,
      })
    );
  };

  const changeSeat = (seatNum) => {
    console.log(stateRef.current);
    const prevSeatNum = stateRef.current.seatNum;
    const newRow = parseInt(seatNum / columnNum) + 1;
    stompClient.send(
      `/app/classroom/${roomId}`,
      {},
      JSON.stringify({
        type: EVENT.CHANGE_SEAT,
        roomId: roomId,
        message: seatNum + 1,
        seatNum: stateRef.current.seatNum,
      })
    );
    if (newRow !== rowRef.current) {
      stompClient.send(
        `/app/classroom/${roomId}/chat/${rowRef.current}`,
        {},
        JSON.stringify({
          type: EVENT.EXIT,
          seatNum: stateRef.current.seatNum,
          content: null,
        })
      );
      chatSubscription.unsubscribe();
      rowRef.current = newRow;
      setChatSubsription((_) => {
        const subscription = stompClient.subscribe(
          `/topic/classroom/${roomId}/chat/${rowRef.current}`,
          onMessageReceived
        );
        stompClient.send(
          `/app/classroom/${roomId}/chat/${rowRef.current}`,
          {},
          JSON.stringify({
            type: EVENT.ENTER,
            seatNum: seatNum,
            row: rowRef.current,
            content: null,
          })
        );
        return subscription;
      });
    } else {
      console.log(chat);
      setChat((chat) => {
        chat.forEach((message) => {
          if (message.seatNum === prevSeatNum) {
            message.seatNum = seatNum;
          }
        });
        return chat;
      });
    }
  };

  const onMessageReceived = (received) => {
    const parsedMsg = JSON.parse(received.body);
    setChat([...chatRef.current, parsedMsg]);
    console.log("onMessageReceived:");
    // setChat([...chat, parsedMsg]);
  };

  useEffect(() => {
    const getUsername = async () => {
      connect().then(() => {
        stompClient.connect(
          { roomId: roomId, username: currentUser.username },
          onConnected,
          (error) => {
            console.error(error);
          }
        );
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
              index === stateRef.current.seatNum - 1 ? (
                <Circle key={index} size="small" state={color} emoji="" mySeat={true} />
              ) : color !== "empty" ? (
                <Circle key={index} size="small" state={color} emoji="" />
              ) : (
                <SeatCircle key={index} index={index} color={color} changeSeat={changeSeat} />
              )
            )}
          </div>
        </div>
        <div className={styles.colors}>
          {colors.map((color, index) => (
            <ColorCircle key={index} color={color} selectColor={selectColor} />
          ))}
        </div>
      </div>
      <Chatroom
        visible={visible}
        chat={chat}
        stompClient={stompClient}
        stateRef={stateRef}
      ></Chatroom>
    </div>
  );
}

export default Room;
