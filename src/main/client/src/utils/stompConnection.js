import { useState, useEffect, useRef } from "react";
import { over } from "stompjs";
import SockJS from "sockjs-client";
import { EVENT, columnNum } from "./classroomUtils";

export function useStompConnection(
  roomId,
  currentUser,
  onMessageReceived,
  setChatSubsription,
  setSeats
) {
  const [stompClient, setStompClient] = useState(null);
  const stateRef = useRef({});
  const rowRef = useRef();

  useEffect(() => {
    const Sock = new SockJS("http://localhost:8080/ws");
    const client = over(Sock);
    setStompClient(client);
  }, [roomId, currentUser]);

  useEffect(() => {
    if (stompClient) {
      console.log("stompClient connected!!");
      const onConnected = () => {
        stompClient.subscribe(`/topic/classroom/${roomId}`, onColorReceived);

        const queueSub = stompClient.subscribe(
          `/queue/temp/classroom/${roomId}/user/${currentUser.username}`,
          (received) => {
            const classroomInfo = JSON.parse(received.body);
            stateRef.current = {
              seatNum: classroomInfo.seatNum,
              roomId: roomId,
            };
            rowRef.current = parseInt(classroomInfo.seatNum / columnNum) + 1;
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
                  seatNum: stateRef.current.seatNum,
                  row: rowRef.current,
                  content: null,
                })
              );
              return subscription;
            });
            setSeats((oldSeats) => {
              let newSeats = [...oldSeats];
              for (let seatNum in classroomInfo.classRoomStates) {
                console.log("seatNum: ", seatNum);
                newSeats[seatNum - 1] = classroomInfo.classRoomStates[seatNum];
              }
              return newSeats;
            });
            queueSub.unsubscribe();
          }
        );
        stompClient.send(
          `/app/classroom/${roomId}`,
          { roomId: roomId },
          JSON.stringify({
            type: EVENT.ENTER,
            roomId: roomId,
          })
        );
        stompClient.send(
          `/app/classroomInfo/${roomId}`,
          {},
          JSON.stringify({
            sender: currentUser.username,
          })
        );
      };

      const onColorReceived = (received) => {
        console.log("onColorReceived: ", received);
        const parsedMsg = JSON.parse(received.body);
        switch (parsedMsg.type) {
          case EVENT.ENTER:
            console.log(parsedMsg);
            break;
          case EVENT.TALK:
            color(parsedMsg.seatNum, parsedMsg.message);
            break;
          case EVENT.EXIT:
            color(parsedMsg.seatNum, "empty");
            break;
          case EVENT.CHANGE_SEAT:
            setSeats((oldSeats) => {
              let newSeats = [...oldSeats];
              newSeats[parseInt(parsedMsg.message)] = oldSeats[parsedMsg.seatNum];
              newSeats[parsedMsg.seatNum - 1] = "empty";
              return newSeats;
            });
            if (parsedMsg.seatNum === stateRef.current.seatNum) {
              stateRef.current.seatNum = parseInt(parsedMsg.message);
              rowRef.current = parseInt(stateRef.current.seatNum / columnNum) + 1;
            }
            break;
        }
      };

      const color = (seatNum, receivedColor) => {
        setSeats((oldSeats) => {
          let newSeats = [...oldSeats];
          newSeats[seatNum - 1] = receivedColor;
          return newSeats;
        });
      };
      stompClient.connect(
        { roomId: roomId, username: currentUser.username },
        onConnected,
        (error) => {
          console.error(error);
        }
      );
    }
  }, [stompClient]);
  return { stompClient, stateRef, rowRef };
}
