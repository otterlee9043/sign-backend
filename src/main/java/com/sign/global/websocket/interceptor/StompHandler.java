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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final ChatroomService chatroomService;

    private final JwtProvider jwtProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        SimpMessageType messageType = accessor.getMessageType();

        String accessToken = accessor.getFirstNativeHeader("Access-Token");
        if (messageType.equals(SimpMessageType.CONNECT)) {
            if (!jwtProvider.isTokenValid(accessToken) || accessor.getUser() == null) {
                throw new BadCredentialsException("토큰이 유효하지 않음");
            }
            Authentication authentication = jwtProvider.getAuthentication(accessToken);
            jwtProvider.saveAuthentication(authentication);

            Long roomId = Long.parseLong(accessor.getFirstNativeHeader("roomId"));
            Long memberId = getMemberId(accessor);

            if (chatroomService.isConnected(roomId, memberId)) {
                throw new RuntimeException("이미 연결된 세션");
            }
        }
        return ChannelInterceptor.super.postReceive(message, channel);
    }

    private Long getMemberId(StompHeaderAccessor accessor) {
        Authentication simpUser = (Authentication) accessor.getHeader("simpUser");
        LoginMember loginMember = (LoginMember) simpUser.getPrincipal();
        return loginMember.getId();
    }

}