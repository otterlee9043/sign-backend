package com.sign.global.websocket.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class WebSocketSessionRegistry {
    private final Map<String, Long[]> webSocketSession = new ConcurrentHashMap<>();
    // sessionId, {memberId, roomId}

    public void addSession(String sessionId, Long memberId, Long roomId) {
        webSocketSession.put(sessionId, new Long[]{memberId, roomId});
    }

    public void removeSession(String sessionId) {
        if (webSocketSession.containsKey(sessionId)) {
            webSocketSession.remove(sessionId);
        } else {
            throw new IllegalArgumentException("Session not found.");
        }
    }

    public Long getMemberId(String sessionId) {
        return webSocketSession.get(sessionId)[0];
    }

    public Long getRoomId(String sessionId) {
        return webSocketSession.get(sessionId)[1];
    }
}
