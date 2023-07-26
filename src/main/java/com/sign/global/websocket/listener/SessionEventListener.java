package com.sign.global.websocket.listener;


import com.sign.global.security.authentication.LoginMember;
import com.sign.global.websocket.service.ChatroomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.*;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.support.NativeMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.*;

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

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Authentication simpUser = (Authentication) accessor.getHeader("simpUser");
        LoginMember loginMember = (LoginMember) simpUser.getPrincipal();
        Long userId = loginMember.getMember().getId();

        chatroomService.enter(sessionId, userId, roomId);
    }

    @EventListener
    public void handleSessionDisconnectEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        chatroomService.exit(sessionId);
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
