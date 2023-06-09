package com.sign.domain.websocket;


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
    private final Map<Integer, Map<Integer, String>> lastState = new ConcurrentHashMap<>();
    //roomId, seatNum, color
    private final Map<Integer, Map<String, Integer>> seatingCharts = new ConcurrentHashMap<>();
    //roomId, sessionId, seatNum

    private static final int roomSize = 40;
    @EventListener
    public void handleSessionConnectedEvent(SessionConnectedEvent event) {
        Map nativeHeaders = getNativeHeaders(event);
        Integer roomId = Integer.parseInt(getHeaderValue(nativeHeaders, "roomId"));
        String sessionId = getSessionId();
        sit(sessionId, roomId);
    }


    @EventListener
    public void handleSessionDisconnectEvent(SessionDisconnectEvent event){
        String sessionId = getSessionId();
        leave(sessionId);
    }


    private void sit(String sessionId, Integer roomId) {
        if (seatingCharts.containsKey(roomId)) {
            // 입장한 사람이 있는 경우
            log.info("입장한 사람이 있는 경우");
            Map seatingChart = seatingCharts.get(roomId);
            Map classroomState = lastState.get(roomId);
            int seatNum = 1;
            for(int i = 1; i <= roomSize; i++) {
                if (!classroomState.containsKey(i)){
                    seatNum = i;
                    break;
                }
            }
            seatingChart.put(sessionId, seatNum);

            Map colorInfo = lastState.get(roomId);
            colorInfo.put(seatNum, "empty");
        } else {
            // 처음 입장하는 경우
            log.info("처음 입장하는 경우");
            Map seatingChart = new ConcurrentHashMap<>();
            seatingChart.put(sessionId, 1);
            seatingCharts.put(roomId, seatingChart);

            Map colorInfo = new ConcurrentHashMap<>();
            colorInfo.put(1, "empty");
            lastState.put(roomId, colorInfo);
        }
    }

    private void leave(String sessionId) {
        for (Integer roomId : seatingCharts.keySet()) {
            if (seatingCharts.get(roomId).containsKey(sessionId)){
                int seatNum = seatingCharts.get(roomId).get(sessionId);
                seatingCharts.get(roomId).remove(sessionId);

                if (seatingCharts.get(roomId).isEmpty()) {
                    seatingCharts.remove(roomId);
                }

                lastState.get(roomId).remove(seatNum);
                if (lastState.get(roomId).isEmpty()){
                    lastState.remove(roomId);
                }

                sendingOperations.convertAndSend("/topic/room/" + roomId,
                        new RoomMessage(MessageType.EXIT, seatNum, roomId, null, null));

                break;
            }
        }
    }


    public Map<Integer, String> getRoomStatesByRoomId(Integer roomId){
        return lastState.get(roomId);
    }


    public Map<String, Integer> getSeatingChartByRoomId(String roomId){
        return seatingCharts.get(roomId);
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


    public Integer getMySeatPosition(Integer roomId, String sessionId) {
        log.info("seatingCharts={}", seatingCharts);
        return seatingCharts.get(roomId).get(sessionId);
    }


    public void color(Integer roomId, int seatNum, String color){
        lastState.get(roomId).put(seatNum, color);
    }


    public String getColor(int roomId, int seatNum){
        return lastState.get(roomId).get(seatNum);
    }


    public void changeSeat(Integer roomId, String sessionId, int oldSeatNum, int newSeatNum){
        Map<Integer, String> classroomState = lastState.get(roomId);
        Map<String, Integer> seatingChart = seatingCharts.get(roomId);
        classroomState.put(newSeatNum, classroomState.get(oldSeatNum));
        classroomState.remove(oldSeatNum);
        seatingChart.put(sessionId, newSeatNum);
    }
}
