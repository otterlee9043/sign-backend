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
import org.springframework.web.context.request.RequestContextHolder;
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
    private final Map<String, Map<Integer, String>> lastState = new ConcurrentHashMap<>();
    //roomId, seatNum, color
    private final Map<String, Map<String, Integer>> connectedUser = new ConcurrentHashMap<>();
    //roomId, sessionId, seatNum
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event){
        log.info(">>> connected: {}", event);

        String roomId = getRoomId(event);
        String sessionId = getSessionId();

        if(connectedUser.containsKey(roomId)){
            //방에 무조건 차례대로 앉게 하는 경우
            Map seatInfo = connectedUser.get(roomId);
            seatInfo.put(sessionId, seatInfo.size());

            Map colorInfo = lastState.get(roomId);
            colorInfo.put(seatInfo.size(), "empty");
        } else {
            //방에 첫번째로 접속하는 경우
            Map seatInfo = new ConcurrentHashMap<>();
            seatInfo.put(sessionId, 0);
            connectedUser.put(roomId, seatInfo);

            Map colorInfo = new ConcurrentHashMap<>();
            colorInfo.put(0, "empty");
            lastState.put(roomId, colorInfo);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event){
        log.info(">>> disconnected: {}", event);
        log.info("  sessionId={}", event.getSessionId());
    }

    public Map<Integer, String> getRoomStatesByRoomId(String roomId){
        return lastState.get(roomId);
    }

    public Map<String, Integer> getConnectedUsersByRoomId(String roomId){
        return connectedUser.get(roomId);
    }

    private String getSessionId() {
        SimpAttributes simpAttributes = SimpAttributesContextHolder.currentAttributes();
        String sessionId = simpAttributes.getSessionId();
        return sessionId;
    }

    private String getRoomId(SessionConnectedEvent event) {
        MessageHeaderAccessor accessor = NativeMessageHeaderAccessor.getAccessor(event.getMessage(), SimpMessageHeaderAccessor.class);
        GenericMessage generic = (GenericMessage) accessor.getHeader("simpConnectMessage");
        Map nativeHeaders = (Map) generic.getHeaders().get("nativeHeaders");
        String roomId = (String)((List) nativeHeaders.get("roomId")).get(0);
        return roomId;
    }

    public Integer getMySeatPosition(String roomId) {
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        log.info("sessionId by RequestContextHolder={}", sessionId);
        return connectedUser.get(roomId).get(sessionId);
    }

    public void color(String roomId, String sessionId, String color){
//        lastState.get(roomId).put(sessionId, color);
//        log.info("lastState={}", lastState);
    }


}
