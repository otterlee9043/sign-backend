package com.sign.domain.websocket;

import com.sign.domain.classroom.ClassroomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpAttributes;
import org.springframework.messaging.simp.SimpAttributesContextHolder;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.support.NativeMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatEventListener {
    private final SimpMessageSendingOperations sendingOperations;
    private final Map<String, Map<String, String>> lastState = new ConcurrentHashMap<>();

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event){
        log.info(">>> connected: {}", event);
        //private Map<String, Map<String, String>> lastState = new ConcurrentHashMap<>();
        //roomId, sessionId, color
        MessageHeaderAccessor accessor = NativeMessageHeaderAccessor.getAccessor(event.getMessage(), SimpMessageHeaderAccessor.class);
        GenericMessage generic = (GenericMessage) accessor.getHeader("simpConnectMessage");
        Map nativeHeaders = (Map) generic.getHeaders().get("nativeHeaders");
        String chatRoomId = (String)((List) nativeHeaders.get("roomId")).get(0);

        SimpAttributes simpAttributes = SimpAttributesContextHolder.currentAttributes();
        String sessionId = simpAttributes.getSessionId();

        if(lastState.containsKey(chatRoomId)){
            lastState.get(chatRoomId).put(sessionId, "empty");
        } else {
            lastState.put(chatRoomId, new ConcurrentHashMap<>());
        }

    }

    public void color(String roomId, String sessionId, String color){
        lastState.get(roomId).put(sessionId, color);
        log.info("lastState={}", lastState);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event){
        log.info(">>> disconnected: {}", event);
        log.info("  sessionId={}", event.getSessionId());
    }
}
