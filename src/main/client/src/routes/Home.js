import React, { useState, useEffect, useContext } from "react";
import Button from "../components/Button.js";
import LoginBar from "../components/LoginBar.js";
import RoomCard from "../components/RoomCard.js";
import { Context } from "../store/appContext.js";
import styles from "./Home.module.css";

function Home() {
  const [rooms, setRooms] = useState([]);
  const getNames = async () => {
    // const response = await fetch("/api/rooms");
    // const json = await response.json();
    // console.log(json);
    // setRooms(json);
    setRooms([]);
  };
  useEffect(() => {
    getNames();
    //console.log(data);
  }, []);
  const { store, actions } = useContext(Context);
  useEffect(() => {
    if (store.token && store.token !== "" && store.token !== undefined)
      actions.getMessage();
  }, [store.token]);

  return (
    <div>
      <LoginBar />
      <br></br>
      <div className={styles.right}>
        <Button path="createroom" text="방 생성" btnType="room" />
        <Button path="enterroom" text="방 참여" btnType="room" />
      </div>
      <div className="rooms">
        <h2 className="left">참여한 방</h2>
      </div>

      <div>
        {rooms.map((room) => (
          <RoomCard
            key={room.id}
            id={room.id}
            roomName={room.room_name}
            host={room.host_name}
          />
        ))}
      </div>
      {/* <span>{store.message}</span> */}
    </div>
  );
}

export default Home;
