package com.sign.domain.websocket;

import com.sign.domain.classroom.exception.RoomCapacityExceededException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class TopicSubscriptionInterceptor implements ChannelInterceptor {

    private final RoomEventHandler roomEventHandler;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String destination = accessor.getDestination();
        log.info("accessor: {}", accessor);
        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            log.info("if (StompCommand.SUBSCRIBE.equals(accessor.getCommand()))");
            Map<String, List<String>> nativeHeaders = accessor.toNativeHeaderMap();
            log.info("nativeHeaders.get(\"roomId\"): {}", nativeHeaders.get("roomId"));

            Long roomId = Long.parseLong(nativeHeaders.get("roomId").get(0));
            boolean roomAccessible = roomEventHandler.isRoomAccessible(roomId);
            log.info("roomAccessible: {}", roomAccessible);
            throw new RoomCapacityExceededException();

        }
        return message;
    }
}
