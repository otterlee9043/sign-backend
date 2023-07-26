package com.sign.global.websocket.controller;

import com.sign.global.websocket.service.ChatroomService;
import com.sign.global.websocket.dto.ChatroomMessage;
import com.sign.global.websocket.dto.MessageType;
import com.sign.global.websocket.dto.RoomInfo;
import com.sign.global.websocket.dto.RoomMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;


@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessageSendingOperations sendingOperations;
    private final ChatroomService chatroomService;

    @MessageMapping("/classroom/{roomId}")
    public void handleMessage(RoomMessage message, @DestinationVariable Long roomId, @Header("simpSessionId") String sessionId){
        if (message.getType().equals(MessageType.COLOR)) {
            chatroomService.color(roomId, message.getSeatNum(), message.getMessage());
        } else if (message.getType().equals(MessageType.DRAW_EMOJI)) {
            chatroomService.color(roomId, message.getSeatNum(), message.getMessage());
        } else if (message.getType().equals(MessageType.TALK)) {
            chatroomService.color(roomId, message.getSeatNum(), message.getMessage());
        } else if (message.getType().equals(MessageType.CHANGE_SEAT)) {
            chatroomService.changeSeat(roomId, sessionId, message.getSeatNum(),
                    Integer.parseInt(message.getMessage()));
        }
        sendingOperations.convertAndSend("/topic/classroom/" + roomId, message) ;
    }

    @MessageMapping("/classroom/{roomId}/chat/{row}")
    public void chat(ChatroomMessage message,
                     @DestinationVariable Integer roomId,
                     @DestinationVariable Integer row){
        sendingOperations.convertAndSend("/topic/classroom/" + roomId + "/chat/" + row, message) ;
    }

    @MessageMapping("/classroomInfo/{roomId}")
    public void enter(RoomMessage message,
                     @DestinationVariable Long roomId,
                     @Header("simpSessionId") String sessionId){
        RoomInfo classroomInfo = new RoomInfo();
        classroomInfo.setClassRoomStates(chatroomService.getRoomStatesByRoomId(roomId));
        classroomInfo.setSeatNum(chatroomService.getMySeatPosition(roomId, sessionId));
        sendingOperations.convertAndSend("/queue/temp/classroom/" + roomId + "/user/" + message.getSender(), classroomInfo) ;

        RoomMessage roomMessage = RoomMessage.builder()
                .roomId(roomId)
                .type(MessageType.ENTER)
                .seatNum(classroomInfo.getSeatNum())
                .build();
        sendingOperations.convertAndSend("/topic/classroom/" + roomId, roomMessage) ;
    }
}
