import React, { useState, useEffect } from "react";
import axios from "axios";
import NavLinkButton from "../components/NavLinkButton.js";
import LoginBar from "../components/LoginBar.js";
import RoomCard from "../components/RoomCard.js";
import styles from "./Home.module.css";

function Home() {
  const [rooms, setRooms] = useState(null);
  useEffect(() => {
    async function getRooms() {
      try {
        const response = await axios.get("/classrooms");
        const roomsJson = response.data;
        setRooms(roomsJson);
      } catch (error) {
        console.log("There has been an error login", error);
      }
    }
    getRooms();
  }, []);

  return (
    <div>
      <LoginBar />
      <div className={styles.right}>
        <NavLinkButton path="createroom" text="방 생성" type="room" />
        <NavLinkButton path="enterroom" text="방 참여" type="room" />
      </div>
      <div className="room-container">
        <h2 className={styles.left}>참여한 방</h2>
        <div className="room">
          {rooms ? (
            rooms.length > 0 ? (
              rooms.map((room) => (
                <RoomCard
                  key={room.id}
                  id={room.id}
                  type="입장"
                  roomName={room.roomName}
                  hostUsername={room.hostUsername}
                  hostEmail={room.hostEmail}
                />
              ))
            ) : (
              <p className={styles["center"]}>참여한 방이 없습니다.</p>
            )
          ) : null}
        </div>
      </div>
    </div>
  );
}

export default Home;
