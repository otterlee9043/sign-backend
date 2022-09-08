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
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

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
        String username = getHeaderValue(nativeHeaders, "username");
        String sessionId = getSessionId();
        log.info(">>> connected | sessionId={}", sessionId);
        log.info(">>> connected | [before] lastState={}", lastState);
        log.info(">>> connected | [before] seatingCharts={}", seatingCharts);
        if (seatingCharts.containsKey(roomId)) {
            Map seatingChart = seatingCharts.get(roomId);
            Map classroomState = lastState.get(roomId);
            int seatNum = 0;
            for(int i = 0; i < roomSize; i++) {
                if (!classroomState.containsKey(i)){
                    seatNum = i;
                    break;
                }
            }
            seatingChart.put(sessionId, seatNum);

            Map colorInfo = lastState.get(roomId);
            colorInfo.put(seatNum, "empty");
        } else {
            Map seatingChart = new ConcurrentHashMap<>();
            seatingChart.put(sessionId, 0);
            seatingCharts.put(roomId, seatingChart);

            Map colorInfo = new ConcurrentHashMap<>();
            colorInfo.put(0, "empty");
            lastState.put(roomId, colorInfo);
        }
        log.info(">>> connected | [after] lastState={}", lastState);
        log.info(">>> connected | [after] seatingCharts={}", seatingCharts);
    }

    @EventListener
    public void handleSessionDisconnectEvent(SessionDisconnectEvent event){
        String sessionId = getSessionId();
        log.info(">>> disconnected | sessionId={}", sessionId);
        log.info(">>> disconnected | [before] lastState={}", lastState);
        log.info(">>> disconnected | [before] seatingCharts={}", seatingCharts);
        for (Integer roomId : seatingCharts.keySet()) {
            if (seatingCharts.get(roomId).containsKey(sessionId)){
                int seatNum = seatingCharts.get(roomId).get(sessionId);
//                String username = seatingCharts.get(roomId).get(sessionId).getUsername();
                seatingCharts.get(roomId).remove(sessionId);
                if(seatingCharts.get(roomId).isEmpty()) {
                    seatingCharts.remove(roomId);
                }
                lastState.get(roomId).remove(seatNum);
                if(lastState.get(roomId).isEmpty()){
                    lastState.remove(roomId);
                }

                sendingOperations.convertAndSend("/topic/room/" + roomId,
                        new RoomMessage(RoomMessage.MessageType.EXIT, seatNum, roomId, null, null));

                break;
            }
        }
        log.info(">>> disconnected | [after] lastState={}", lastState);
        log.info(">>> disconnected | [after] seatingCharts={}", seatingCharts);

    }

    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event){
        log.info("SessionSubscribeEvent={}", event);
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
//        SeatInfo seatInfo = seatingChart.get(sessionId);
//        seatInfo.setSeatNum(newSeatNum);
        seatingChart.put(sessionId, newSeatNum);
    }
}
