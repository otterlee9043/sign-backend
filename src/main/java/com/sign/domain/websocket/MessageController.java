package com.sign.domain.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.config.WebSocketMessageBrokerStats;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessageSendingOperations sendingOperations;
    private final WebSocketMessageBrokerStats stats;
    @MessageMapping("/chat/message")
    public void enter (ChatMessage message){
        if (ChatMessage.MessageType.ENTER.equals(message.getType())){
            //message.setMessage(message.getSender() );
        }
        log.info("message={}", message);
        sendingOperations.convertAndSend("/topic/chat/room/" + message.getRoomId(), message) ;
        log.info("stats={}",stats.getWebSocketSessionStatsInfo());
    }
}
