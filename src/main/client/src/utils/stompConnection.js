import { useState, useEffect, useRef, useCallback } from "react";
import { over } from "stompjs";
import SockJS from "sockjs-client";
import { EVENT, columnNum } from "./classroomUtils";

export const useStompConnection = (roomId, currentUser, setSeats, setChat) => {
  const [stompClient, setStompClient] = useState(null);
  const seatNumRef = useRef();
  const rowRef = useRef();

  const onChatMessageReceived = (received) => {
    const parsedMsg = JSON.parse(received.body);
    setChat((chat) => {
      return [...chat, parsedMsg];
    });
  };

  useEffect(() => {
    const Sock = new SockJS("http://localhost:8080/ws");
    const client = over(Sock);
    setStompClient(client);
  }, [roomId, currentUser]);

  useEffect(() => {
    if (stompClient) {
      const onConnected = () => {
        stompClient.subscribe(`/topic/classroom/${roomId}`, onMessageReceived);

        const queueSub = stompClient.subscribe(
          `/queue/temp/classroom/${roomId}/user/${currentUser.username}`,
          (received) => {
            const classroomInfo = JSON.parse(received.body);
            seatNumRef.current = classroomInfo.seatNum;
            rowRef.current = parseInt(classroomInfo.seatNum / columnNum) + 1;
            setChatSubsription((_) => {
              const subscription = stompClient.subscribe(
                `/topic/classroom/${roomId}/chat/${rowRef.current}`,
                onChatMessageReceived
              );
              stompClient.send(
                `/app/classroom/${roomId}/chat/${rowRef.current}`,
                {},
                JSON.stringify({
                  type: EVENT.ENTER,
                  seatNum: seatNumRef.current,
                  row: rowRef.current,
                  content: null,
                })
              );
              return subscription;
            });
            setSeats((oldSeats) => {
              let newSeats = [...oldSeats];
              for (let seatNum in classroomInfo.classRoomStates) {
                newSeats[seatNum - 1] = classroomInfo.classRoomStates[seatNum];
              }
              return newSeats;
            });

            queueSub.unsubscribe();
          }
        );

        stompClient.send(
          `/app/classroomInfo/${roomId}`,
          {},
          JSON.stringify({
            sender: currentUser.username,
          })
        );
      };

      const onMessageReceived = (received) => {
        const parsedMsg = JSON.parse(received.body);
        switch (parsedMsg.type) {
          case EVENT.ENTER:
            color(parsedMsg.seatNum, "unselected");
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
              newSeats[parseInt(parsedMsg.message) - 1] = oldSeats[parsedMsg.seatNum];
              newSeats[parsedMsg.seatNum - 1] = "empty";
              return newSeats;
            });
            if (parsedMsg.seatNum === seatNumRef.current) {
              seatNumRef.current = parseInt(parsedMsg.message);
              rowRef.current = parseInt(seatNumRef.current / columnNum) + 1;
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

  const selectColor = useCallback(
    (color) => {
      stompClient.send(
        `/app/classroom/${roomId}`,
        {},
        JSON.stringify({
          type: EVENT.TALK,
          roomId: roomId,
          message: color,
          seatNum: seatNumRef.current,
        })
      );
    },
    [stompClient, seatNumRef]
  );

  const [chatSubscription, setChatSubsription] = useState(null);

  const changeSeat = useCallback(
    (seatNum) => {
      const prevSeatNum = seatNumRef.current;
      const newRow = parseInt(seatNum / columnNum) + 1;
      stompClient.send(
        `/app/classroom/${roomId}`,
        {},
        JSON.stringify({
          type: EVENT.CHANGE_SEAT,
          roomId: roomId,
          message: seatNum + 1,
          seatNum: seatNumRef.current,
        })
      );
      if (newRow !== rowRef.current) {
        stompClient.send(
          `/app/classroom/${roomId}/chat/${rowRef.current}`,
          {},
          JSON.stringify({
            type: EVENT.EXIT,
            seatNum: seatNumRef.current,
            content: null,
          })
        );
        chatSubscription.unsubscribe();
        rowRef.current = newRow;
        setChatSubsription((_) => {
          const subscription = stompClient.subscribe(
            `/topic/classroom/${roomId}/chat/${rowRef.current}`,
            onChatMessageReceived
          );
          stompClient.send(
            `/app/classroom/${roomId}/chat/${rowRef.current}`,
            {},
            JSON.stringify({
              type: EVENT.ENTER,
              seatNum: seatNum,
              row: rowRef.current,
              content: null,
            })
          );
          return subscription;
        });
      } else {
        setChat((chat) => {
          chat.forEach((message) => {
            if (message.seatNum === prevSeatNum) {
              message.seatNum = seatNum;
            }
          });
          return chat;
        });
      }
    },
    [stompClient, seatNumRef, chatSubscription]
  );

  const sendMessage = useCallback(
    (message) => {
      stompClient.send(
        `/app/classroom/${roomId}/chat/${rowRef.current}`,
        {},
        JSON.stringify({
          type: EVENT.TALK,
          seatNum: seatNumRef.current,
          content: message,
          sender: currentUser.username,
        })
      );
    },
    [stompClient, seatNumRef]
  );

  const disconnect = useCallback(() => {
    stompClient.disconnect();
  }, [stompClient]);

  return { seatNumRef, selectColor, changeSeat, sendMessage, disconnect };
};
