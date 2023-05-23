import React, { useState, useEffect, useContext, createContext } from "react";
import NavLinkButton from "../components/NavLinkButton.js";
import LoginBar from "../components/LoginBar.js";
import RoomCard from "../components/RoomCard.js";
import { useNavigate } from "react-router-dom";
import styles from "./Home.module.css";

export const CurrentUserContext = createContext(null);

function Home() {
  const [rooms, setRooms] = useState([]);
  const navigate = useNavigate();
  const [currentUser, setCurrentUser] = useState(null);

  useEffect(() => {
    async function getRooms() {
      try {
        const response = await fetch("/api/classrooms");
        if (response.status !== 200) {
          console.log("There has been some errors.");
          return false;
        }
        const roomsJson = await response.json();
        setRooms(roomsJson);
      } catch (error) {
        console.log("There has been an error login", error);
      }
    }
    getRooms();
  }, []);

  const handleLogout = () => {
    navigate("/");
  };

  return (
    <CurrentUserContext.Provider
      value={{
        currentUser,
        setCurrentUser,
      }}
    >
      <div>
        <LoginBar onLogout={handleLogout} />
        <div className={styles.right}>
          <NavLinkButton path="createroom" text="방 생성" btnType="room" />
          <NavLinkButton path="enterroom" text="방 참여" btnType="room" />
        </div>
        <div className="room-container">
          <h2 className={styles.left}>참여한 방</h2>
          <div className="room">
            {rooms.length > 0 ? (
              rooms.map((room) => (
                <RoomCard
                  key={room.id}
                  id={room.id}
                  roomName={room.roomName}
                  hostUsername={room.hostUsername}
                  hostEmail={room.hostEmail}
                />
              ))
            ) : (
              <p className={styles["center"]}>참여한 방이 없습니다.</p>
            )}
          </div>
        </div>
      </div>
    </CurrentUserContext.Provider>
  );
}

export default Home;
