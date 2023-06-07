import React, { useState, useEffect, useContext, useCallback } from "react";
import styles from "./Classroom.module.css";
import NavBar from "../components/NavBar.js";
import Circle from "../components/classroom/Circle";
import SeatCircle from "../components/classroom/SeatCircle";
import ColorCircle from "../components/classroom/ColorCircle";
import Chatroom from "../components/classroom/Chatroom";

import { EVENT, colors } from "../utils/classroomUtils";
import { useParams, useNavigate } from "react-router-dom";
import { CurrentUserContext } from "../contexts/CurrentUserContext";
import { useStompConnection } from "../utils/stompConnection";

function Room() {
  const { currentUser, setCurrentUser } = useContext(CurrentUserContext);
  const params = useParams();
  const roomId = parseInt(params.roomId);
  const [visible, setVisible] = useState(false);
  const [seats, setSeats] = useState(new Array(10).fill("empty"));
  const [chat, setChat] = useState([]);

  const { stompClient, stateRef, rowRef, selectColor, changeSeat } = useStompConnection(
    roomId,
    currentUser,
    setSeats,
    setChat
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

  const disconnect = useCallback(() => {
    stompClient.disconnect();
    console.log(stompClient);
  }, [stompClient]);

  // useEffect(() => {
  //   window.onpopstate = disconnect;
  //   window.addEventListener("beforeunload", (event) => {
  //     event.preventDefault();
  //     event.returnValue = "";
  //     disconnect();
  //   });
  // }, []);

  const openChatroom = () => {
    setVisible((visible) => !visible);
  };

  return (
    <div className={styles.wrapper}>
      <NavBar
        mode="classroom"
        roomId={roomId}
        openChatroom={openChatroom}
        disconnect={disconnect}
      />
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
