import { useContext } from "react";
import { EVENT, columnNum } from "../../utils/classroomUtils";
import styles from "./Chatroom.module.css";
import ChatMessages from "./ChatMessages";
import ChatInputBar from "./ChatInputBar";
import { CurrentUserContext } from "../../contexts/CurrentUserContext";

function Chatroom({ visible, chat, stompClient, stateRef }) {
  const row = parseInt(stateRef.current.seatNum / columnNum) + 1;
  const { currentUser } = useContext(CurrentUserContext);
  const sendMessage = (message) => {
    stompClient.send(
      `/app/classroom/${stateRef.current.roomId}/chat/${row}`,
      {},
      JSON.stringify({
        type: EVENT.TALK,
        seatNum: stateRef.current.seatNum,
        content: message,
        sender: currentUser.username,
      })
    );
  };
  return (
    <div className={visible ? styles.chatroom : styles.hidden}>
      <ChatMessages chat={chat} stateRef={stateRef} />
      <ChatInputBar sendMessage={sendMessage} />
    </div>
  );
}

export default Chatroom;
