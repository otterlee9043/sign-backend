import React, { useState, useEffect, useContext, useCallback } from "react";
import styles from "./Classroom.module.css";
import NavBar from "../components/NavBar.js";
import Circle from "../components/classroom/Circle";
import SeatCircle from "../components/classroom/SeatCircle";
import ColorCircle from "../components/classroom/ColorCircle";
import Chatroom from "../components/classroom/Chatroom";

import CircularProgress from "@mui/material/CircularProgress";

import { colors } from "../utils/classroomUtils";
import { useParams } from "react-router-dom";
import { CurrentUserContext } from "../contexts/CurrentUserContext";
import { useStompConnection } from "../utils/stompConnection";

const InitDataFetcher = ({ children }) => {
  const params = useParams();
  const roomId = parseInt(params.roomId);

  const { currentUser, setCurrentUser } = useContext(CurrentUserContext);
  const [roomInfo, setRoomInfo] = useState(null);
  useEffect(() => {
    if (!currentUser) {
      fetch("/api/member/userInfo")
        .then((response) => response.json())
        .then((data) => {
          setCurrentUser(data);
        })
        .catch((error) => {
          console.error("Error fetching data:", error);
        });
    }
    fetch(`/api/classroom/${roomId}`)
      .then((response) => response.json())
      .then((data) => {
        setRoomInfo(data);
      })
      .catch((error) => {
        console.error("Error fetching data:", error);
      });
  }, []);

  return currentUser && roomInfo ? (
    React.cloneElement(children, { currentUser, roomInfo })
  ) : (
    <CircularProgress />
  );
};

function Room({ currentUser, roomInfo }) {
  console.log(roomInfo);
  const params = useParams();
  const roomId = parseInt(params.roomId);
  const [visible, setVisible] = useState(false);
  const [seats, setSeats] = useState(new Array(roomInfo["capacity"]).fill("empty"));
  const [chat, setChat] = useState([]);

  const { seatNumRef, selectColor, changeSeat, sendMessage, disconnect } = useStompConnection(
    roomId,
    currentUser,
    setSeats,
    setChat
  );

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
              index === seatNumRef.current - 1 ? (
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
          <div className={styles.colors}>
            {colors.map((color, index) => (
              <ColorCircle key={index} color={color} selectColor={selectColor} />
            ))}
          </div>
        </div>
      </div>
      <Chatroom
        visible={visible}
        chat={chat}
        stateRef={seatNumRef}
        sendMessage={sendMessage}
      ></Chatroom>
    </div>
  );
}

const ClassRoom = () => {
  return (
    <InitDataFetcher>
      <Room />
    </InitDataFetcher>
  );
};

export default ClassRoom;
