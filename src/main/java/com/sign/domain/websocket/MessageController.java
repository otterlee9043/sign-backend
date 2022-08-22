package com.sign.domain.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpAttributesContextHolder;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.config.WebSocketMessageBrokerStats;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessageSendingOperations sendingOperations;
    private final ChatEventListener chatEventListener;
    @MessageMapping("/chat/message")
    public void enter (ChatMessage message){
        if (ChatMessage.MessageType.ENTER.equals(message.getType())){
            //message.setMessage(message.getSender() );
        } else {
            String color = message.getMessage();
            String sessionId = SimpAttributesContextHolder.currentAttributes().getSessionId();
            log.info("enter.sessionId={}", sessionId);
            chatEventListener.color(message.getRoomId(), sessionId, color);
        }
        log.info("message={}", message);
        sendingOperations.convertAndSend("/topic/chat/room/" + message.getRoomId(), message) ;
    }
}
