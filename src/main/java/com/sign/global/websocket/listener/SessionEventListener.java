package com.sign.global.websocket.listener;


import com.sign.global.security.authentication.LoginMember;
import com.sign.global.websocket.service.ChatroomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpAttributes;
import org.springframework.messaging.simp.SimpAttributesContextHolder;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
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
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(event.getMessage());
        MessageHeaderAccessor messageHeaderAccessor =
                NativeMessageHeaderAccessor.getAccessor(event.getMessage(), SimpMessageHeaderAccessor.class);

        Long memberId = getMemberId(stompHeaderAccessor);
        Long roomId = Long.parseLong(getNativeHeader(messageHeaderAccessor, "roomId"));
        String sessionId = getSessionId();

        chatroomService.enter(sessionId, memberId, roomId);
    }

    @EventListener
    public void handleSessionDisconnectEvent(SessionDisconnectEvent event) {
        chatroomService.exit(getSessionId());
    }

    private String getSessionId() {
        SimpAttributes simpAttributes = SimpAttributesContextHolder.currentAttributes();
        return simpAttributes.getSessionId();
    }

    private Long getMemberId(StompHeaderAccessor accessor) {
        Authentication simpUser = (Authentication) accessor.getHeader("simpUser");
        LoginMember loginMember = (LoginMember) simpUser.getPrincipal();
        return loginMember.getId();
    }

    private String getNativeHeader(MessageHeaderAccessor accessor, String header) {
        GenericMessage generic = (GenericMessage) accessor.getHeader("simpConnectMessage");
        Map nativeHeaders = (Map) generic.getHeaders().get("nativeHeaders");
        return (String) ((List) nativeHeaders.get(header)).get(0);
    }

}
