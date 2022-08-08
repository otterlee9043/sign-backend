import React, { useState, useEffect, useContext } from "react";
import Button from "../components/Button.js";
import LoginBar from "../components/LoginBar.js";
import RoomCard from "../components/RoomCard.js";
import { Context } from "../store/appContext.js";
import styles from "./Home.module.css";

function Home() {
  const [rooms, setRooms] = useState([]);

  useEffect(() => {
    async function getRooms() {
      try {
        const response = await fetch("/api/classrooms");
        if (response.status !== 200) {
          alert("There has been some errors.");
          return false;
        }
        const roomsJson = await response.json();

        console.log(roomsJson)
        setRooms(roomsJson);
        // if (username === "expired") setRooms([]);
        // else setRooms([]);

      } catch (error) {
        console.error("There has been an error login", error);
      }
    }
    getRooms();
  }, []);

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
            roomName={room.roomName}
            host={room.host.username}
          />
        ))}
      </div>
      {/* <span>{store.message}</span> */}
    </div>
  );
}

export default Home;
