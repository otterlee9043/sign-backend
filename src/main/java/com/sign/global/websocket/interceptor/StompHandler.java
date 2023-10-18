package com.sign.global.websocket.interceptor;

import com.sign.global.security.authentication.JwtProvider;
import com.sign.global.security.authentication.LoginMember;
import com.sign.global.websocket.service.ChatroomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JwtProvider jwtProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        SimpMessageType messageType = accessor.getMessageType();

        if (messageType.equals(SimpMessageType.CONNECT)) {
            String accessToken = getAccessToken(accessor);
            if (!jwtProvider.isTokenValid(accessToken)) {
                throw new BadCredentialsException("토큰이 유효하지 않음");
            }
            Authentication authentication = jwtProvider.getAuthentication(accessToken);
            jwtProvider.saveAuthentication(authentication);

            accessor.setUser(authentication);
            Message<?> authenticatedMessage =
                    MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
            return ChannelInterceptor.super.postReceive(authenticatedMessage, channel);
        }

        return ChannelInterceptor.super.postReceive(message, channel);
    }

    private String getAccessToken(StompHeaderAccessor accessor) {
        String authorizationHeader = accessor.getFirstNativeHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

}