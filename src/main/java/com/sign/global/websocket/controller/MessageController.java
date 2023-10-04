package com.sign.global.websocket.controller;

import com.sign.global.websocket.service.ChatroomService;
import com.sign.global.websocket.dto.MessageType;
import com.sign.global.websocket.dto.RoomInfo;
import com.sign.global.websocket.dto.RoomMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;


@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessageSendingOperations sendingOperations;

    private final ChatroomService chatroomService;

    @MessageMapping("/topic/classroom/{roomId}")
    public void handleMessage(RoomMessage message,
                              @DestinationVariable Long roomId,
                              @Header("simpSessionId") String sessionId){
        MessageType type = message.getType();
        if (type.equals(MessageType.COLOR)) {
            chatroomService.color(roomId, message.getSeatNum(), message.getMessage());
        } else if (type.equals(MessageType.DRAW_EMOJI)) {
            chatroomService.drawEmoji(roomId, message.getSeatNum(), message.getMessage());
        } else if (type.equals(MessageType.CHANGE_SEAT)) {
            chatroomService.changeSeat(
                    roomId,
                    sessionId,
                    message.getSeatNum(),
                    Integer.parseInt(message.getMessage())
            );
        }
    }


    @SubscribeMapping("/queue/temp/classroom/{roomId}/member/{memberId}")
    public void getState(@DestinationVariable Long roomId,
                         @DestinationVariable Long memberId) {
        int seatNum = chatroomService.sit(memberId, roomId);
        RoomInfo classroomInfo = RoomInfo.builder()
                .seatNum(seatNum)
                .classRoomStates(chatroomService.getRoomStateByRoomId(roomId))
                .build();
        sendingOperations.convertAndSend(
                "/queue/temp/classroom/" + roomId + "/member/" + memberId,
                classroomInfo);
    }
}
