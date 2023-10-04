package com.sign.global.websocket.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class WebSocketSessionRegistry {
    private final Map<String, Long[]> userSession = new ConcurrentHashMap<>();
    // sessionId, {memberId, roomId}

    public void addSession(String sessionId, Long memberId, Long roomId) {
        userSession.put(sessionId, new Long[]{memberId, roomId});
    }

    public void removeSession(String sessionId) {
        if (userSession.containsKey(sessionId)) {
            Long[] userInfo = userSession.get(sessionId);
            Long memberId = userInfo[0];
            Long roomId = userInfo[1];
            log.info("User {} exited room {}.", memberId, roomId);

            userSession.remove(sessionId);
        } else {
            throw new IllegalArgumentException("Session not found.");
        }
    }

    public Long getMemberId(String sessionId) {
        return userSession.get(sessionId)[0];
    }

    public Long getRoomId(String sessionId) {
        return userSession.get(sessionId)[1];
    }
}
