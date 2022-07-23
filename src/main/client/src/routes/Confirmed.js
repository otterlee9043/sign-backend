import React, { useState, useEffect, useContext } from "react";
import { useParams } from "react-router-dom";
import Button from "../components/Button.js";
import LoginBar from "../components/LoginBar.js";
import RoomCard from "../components/RoomCard.js";
import { Context } from "../store/appContext.js";
import styles from "./Home.module.css";

function Confirmed() {
  const params = useParams();
  console.log(params);
  const [confirmed, setData] = useState([]);
  const get_confirmation = async () => {
    const response = await fetch("/confirm/" + params.token);
    const json = await response.json();
    console.log(response);
    console.log(json);
    setData(json.name);
  };

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
        <Button path="#" text="방 생성" btnType="room" />
        <Button path="#" text="방 참여" btnType="room" />
      </div>
      <div className="rooms">
        <h2 className="left">참여한 방</h2>
      </div>

      <div>
        <RoomCard id="" roomName="Sua" host="suaguri" />
      </div>
      {/* <span>{store.message}</span> */}
    </div>
  );
}

export default Confirmed;
