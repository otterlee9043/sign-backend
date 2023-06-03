import React, { useState, useEffect, useContext, useCallback } from "react";
import styles from "./Classroom.module.css";
import NavBar from "../components/NavBar.js";
import Circle from "../components/classroom/Circle";
import SeatCircle from "../components/classroom/SeatCircle";
import ColorCircle from "../components/classroom/ColorCircle";
import Chatroom from "../components/classroom/Chatroom";

import { EVENT, colors, columnNum } from "../utils/classroomUtils";
import { useParams } from "react-router-dom";
import { CurrentUserContext } from "../contexts/CurrentUserContext";

import { useStompConnection } from "../utils/stompConnection";

function Room() {
  const { currentUser } = useContext(CurrentUserContext);
  const params = useParams();
  const roomId = parseInt(params.roomId);
  const [visible, setVisible] = useState(false);

  const [seats, setSeats] = useState(new Array(40).fill("empty"));
  const [chat, setChat] = useState([]);
  const [chatSubscription, setChatSubsription] = useState(null); // stompClient.subscribe의 리턴 값

  const onMessageReceived = (received) => {
    const parsedMsg = JSON.parse(received.body);
    setChat((chat) => {
      return [...chat, parsedMsg];
    });
  };

  const { stompClient, stateRef, rowRef } = useStompConnection(
    roomId,
    currentUser,
    onMessageReceived,
    setChatSubsription,
    setSeats
  );

  const selectColor = useCallback(
    (color) => {
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
    },
    [stompClient, stateRef]
  );

  const changeSeat = useCallback(
    (seatNum) => {
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
        setChat((chat) => {
          chat.forEach((message) => {
            if (message.seatNum === prevSeatNum) {
              message.seatNum = seatNum;
            }
          });
          return chat;
        });
      }
    },
    [stompClient, stateRef, chatSubscription, chat]
  );

  const sendMessage = useCallback(
    (message) => {
      stompClient.send(
        `/app/classroom/${stateRef.current.roomId}/chat/${rowRef.current}`,
        {},
        JSON.stringify({
          type: EVENT.TALK,
          seatNum: stateRef.current.seatNum,
          content: message,
          sender: currentUser.username,
        })
      );
    },
    [stompClient, stateRef]
  );

  useEffect(() => {
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
                <SeatCircle
                  key={index}
                  index={index}
                  color={color}
                  changeSeat={() => changeSeat(index)}
                />
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
        stateRef={stateRef}
        sendMessage={sendMessage}
      ></Chatroom>
    </div>
  );
}

export default Room;
