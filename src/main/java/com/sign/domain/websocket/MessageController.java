package com.sign.domain.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessageSendingOperations sendingOperations;
    private final ChatEventListener chatEventListener;
    @MessageMapping("/classroom/{roomId}")
    public void enter (ClassroomMessage message, @DestinationVariable Integer roomId, @Header("simpSessionId") String sessionId){
        log.info("@DestinationVariable.roomId: {}", roomId);
        log.info("@Header.sessionId: {}", sessionId);
        switch (message.getType()){
            case ENTER, EXIT:
                break;
            case TALK:
                chatEventListener.color(roomId, message.getSeatNum(), message.getMessage());
                break;
            case CHANGE_SEAT:
                chatEventListener.changeSeat(roomId, sessionId, message.getSeatNum(), Integer.parseInt(message.getMessage()));
                break;
        }

        log.info("message={}", message);
        sendingOperations.convertAndSend("/topic/classroom/" + roomId, message) ;
    }

    @MessageMapping("/classroom/{roomId}/chat/{row}")
    public void chat(ChatroomMessage message, @DestinationVariable Integer roomId, @DestinationVariable Integer row){
        sendingOperations.convertAndSend("/topic/classroom/" + roomId + "/chat/" + row, message) ;
    }

    @SubscribeMapping("/queue/temp/classroom/{roomId}")
    public void sendClassroomInfo(@DestinationVariable Integer roomId){
        log.info("@SubscribeMapping.roomId={}", roomId);

    }
}
