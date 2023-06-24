package com.sign.global.websocket.listener;


import com.sign.global.websocket.service.ChatroomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.*;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.support.NativeMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SessionEventListener {
    private final ChatroomService chatroomService;

    @EventListener
    public void handleSessionConnectedEvent(SessionConnectedEvent event) {
        Map nativeHeaders = getNativeHeaders(event);
        Long roomId = Long.parseLong(getHeaderValue(nativeHeaders, "roomId"));
        String sessionId = getSessionId();
        chatroomService.sit(sessionId, roomId);
    }

    @EventListener
    public void handleSessionDisconnectEvent(SessionDisconnectEvent event) {
        String sessionId = getSessionId();
        chatroomService.leave(sessionId);
    }

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        log.info("SessionSubscribeEvent: {}", event);
    }

    private Long getRoomIdFromDestination(String destination) {
        String[] parts = destination.split("/");
        String roomIdStr = parts[parts.length - 1];
        return Long.parseLong(roomIdStr);
    }

    @EventListener
    public void handleSessionConnectEvent(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, List<String>> nativeHeaders = accessor.toNativeHeaderMap();
        Long roomId = Long.parseLong(nativeHeaders.get("roomId").get(0));
        String sessionId = accessor.getSessionId();
    }

    private String getSessionId() {
        SimpAttributes simpAttributes = SimpAttributesContextHolder.currentAttributes();
        String sessionId = simpAttributes.getSessionId();
        return sessionId;
    }

    private String getHeaderValue(Map nativeHeaders, String headerName) {
        return (String)((List) nativeHeaders.get(headerName)).get(0);
    }

    private Map getNativeHeaders(SessionConnectedEvent event) {
        MessageHeaderAccessor accessor = NativeMessageHeaderAccessor.getAccessor(event.getMessage(), SimpMessageHeaderAccessor.class);
        GenericMessage generic = (GenericMessage) accessor.getHeader("simpConnectMessage");
        Map nativeHeaders = (Map) generic.getHeaders().get("nativeHeaders");
        return nativeHeaders;
    }
}