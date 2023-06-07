import { useState, useEffect, useRef, useCallback } from "react";
import { over } from "stompjs";
import SockJS from "sockjs-client";
import { EVENT, columnNum } from "./classroomUtils";

export const useStompConnection = (roomId, currentUser, setCurrentUser, setSeats, setChat) => {
  const [stompClient, setStompClient] = useState(null);
  const stateRef = useRef({});
  const rowRef = useRef();

  const onMessageReceived = (received) => {
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

  useEffect(async () => {
    try {
      const response = await fetch("/api/member/userInfo");
      if (!response.ok) {
        return;
      }
      const userInfo = await response.json();
      setCurrentUser(userInfo);
    } catch (error) {
      console.error("There has been an error login", error);
    }
  }, [currentUser]);

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
              newSeats[parseInt(parsedMsg.message) - 1] = oldSeats[parsedMsg.seatNum];
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

  const selectColor = useCallback(
    (color) => {
      stompClient.send(
        `/app/classroom/${roomId}`,
        {},
        JSON.stringify({
          type: EVENT.TALK,
          roomId: stateRef.current.roomId,
          message: color,
          seatNum: stateRef.current.seatNum,
        })
      );
    },
    [stompClient, stateRef]
  );

  const [chatSubscription, setChatSubsription] = useState(null);

  const changeSeat = useCallback(
    (seatNum) => {
      console.log(stateRef.current);
      const prevSeatNum = stateRef.current.seatNum;
      const newRow = parseInt(seatNum / columnNum) + 1;
      stompClient.send(
        `/app/classroom/${stateRef.current.roomId}`,
        {},
        JSON.stringify({
          type: EVENT.CHANGE_SEAT,
          roomId: stateRef.current.roomId,
          message: seatNum + 1,
          seatNum: stateRef.current.seatNum,
        })
      );
      if (newRow !== rowRef.current) {
        stompClient.send(
          `/app/classroom/${roomId}/chat/${rowRef.current}`,
          {},
          JSON.stringify({
            type: EVENT.EXIT,
            seatNum: stateRef.current.seatNum,
            content: null,
          })
        );
        chatSubscription.unsubscribe();
        rowRef.current = newRow;
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
    [stompClient, stateRef, chatSubscription]
  );

  return { stompClient, stateRef, rowRef, selectColor, changeSeat };
};
