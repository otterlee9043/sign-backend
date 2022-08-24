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

        Map nativeHeaders = getNativeHeaders(event);
        String roomId = getHeaderValue(nativeHeaders, "roomId");
        String username = getHeaderValue(nativeHeaders, "username");

        if(connectedUser.containsKey(roomId)){
            //방에 무조건 차례대로 앉게 하는 경우
            Map seatInfo = connectedUser.get(roomId);
            seatInfo.put(username, seatInfo.size());

            Map colorInfo = lastState.get(roomId);
            colorInfo.put(seatInfo.size(), "empty");
        } else {
            //방에 첫번째로 접속하는 경우
            Map seatInfo = new ConcurrentHashMap<>();
            seatInfo.put(username, 0);
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


    private String getHeaderValue(Map nativeHeaders, String headerName) {
        return (String)((List) nativeHeaders.get(headerName)).get(0);
    }

    private Map getNativeHeaders(SessionConnectedEvent event) {
        MessageHeaderAccessor accessor = NativeMessageHeaderAccessor.getAccessor(event.getMessage(), SimpMessageHeaderAccessor.class);
        GenericMessage generic = (GenericMessage) accessor.getHeader("simpConnectMessage");
        Map nativeHeaders = (Map) generic.getHeaders().get("nativeHeaders");
        return nativeHeaders;
    }

    public Integer getMySeatPosition(String roomId, String username) {
        log.info("connectedUser={}", connectedUser);
        return connectedUser.get(roomId).get(username);
    }

    public void color(String roomId, String sessionId, String color){
//        lastState.get(roomId).put(sessionId, color);
//        log.info("lastState={}", lastState);
    }


}
