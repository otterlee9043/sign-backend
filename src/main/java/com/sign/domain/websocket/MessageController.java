package com.sign.domain.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessageSendingOperations sendingOperations;
    private final ChatEventListener chatEventListener;
    @MessageMapping("/classroom/{roomId}")
    public void enter (RoomMessage message, @DestinationVariable Integer roomId, @Header("simpSessionId") String sessionId){
        if (message.getType().equals(MessageType.TALK)) {
            chatEventListener.color(roomId, message.getSeatNum(), message.getMessage());
        } else if (message.getType().equals(MessageType.CHANGE_SEAT)) {
            chatEventListener.changeSeat(roomId, sessionId, message.getSeatNum(),
                    Integer.parseInt(message.getMessage()));
        }
        sendingOperations.convertAndSend("/topic/classroom/" + roomId, message) ;
    }

    @MessageMapping("/classroom/{roomId}/chat/{row}")
    public void chat(ChatroomMessage message, @DestinationVariable Integer roomId, @DestinationVariable Integer row){
        sendingOperations.convertAndSend("/topic/classroom/" + roomId + "/chat/" + row, message) ;
    }

    @MessageMapping("/classroomInfo/{roomId}")
    public void sendRoomInfo(RoomMessage message, @DestinationVariable Integer roomId, @Header("simpSessionId") String sessionId){
        RoomInfo classroomInfo = new RoomInfo();
        classroomInfo.setClassRoomStates(chatEventListener.getRoomStatesByRoomId(roomId));
        classroomInfo.setSeatNum(chatEventListener.getMySeatPosition(roomId, sessionId));
        sendingOperations.convertAndSend("/queue/temp/classroom/" + roomId + "/user/" + message.getSender(), classroomInfo) ;

        RoomMessage roomMessage = RoomMessage.builder()
                .roomId(roomId)
                .type(MessageType.ENTER)
                .seatNum(classroomInfo.getSeatNum())
                .build();
        sendingOperations.convertAndSend("/topic/classroom/" + roomId, roomMessage) ;
    }
}
