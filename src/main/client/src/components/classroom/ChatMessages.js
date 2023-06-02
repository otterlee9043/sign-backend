import { useContext } from "react";
import Message from "./Message";
import styles from "./ChatMessages.module.css";
import { CurrentUserContext } from "../../contexts/CurrentUserContext";

function ChatMessages({ chat, stateRef }) {
  const { currentUser } = useContext(CurrentUserContext);
  const EVENT = {
    ENTER: "ENTER",
    EXIT: "EXIT",
    TALK: "TALK",
    CHANGE_SEAT: "CHANGE_SEAT",
  };
  console.log(chat);

  return (
    <ul className={styles.msgContainer}>
      {chat.map((data, index) => {
        switch (data.type) {
          case EVENT.ENTER:
            return (
              <div key={index} className={styles.announcWrapper}>
                {data.sender === currentUser.username ? (
                  <div>
                    {index !== 0 ? <hr></hr> : null}
                    <span>{data.row}번째 줄 대화방</span>
                    <br></br>
                  </div>
                ) : null}
                <span className={styles.announcement}>{data.seatNum}번 좌석님이 들어왔습니다.</span>
              </div>
            );
          case EVENT.EXIT:
            return (
              <div key={index} className={styles.announcWrapper}>
                <span className={styles.announcement}>{data.seatNum}번 좌석님이 나갔습니다.</span>
              </div>
            );
          case EVENT.TALK:
            return stateRef.current.seatNum == data.seatNum ? (
              <Message key={index} myMessage={true} data={data}></Message>
            ) : (
              <Message key={index} myMessage={false} data={data}></Message>
            );
        }
      })}
    </ul>
  );
}

export default ChatMessages;
