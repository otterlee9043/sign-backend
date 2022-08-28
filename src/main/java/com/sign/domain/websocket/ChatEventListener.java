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
    private final Map<String, Map<String, SeatInfo>> seatingCharts = new ConcurrentHashMap<>();
    //roomId, sessionId, classroomInfo(seatNum, username)
    private final Map<String, String> connectedUser = new ConcurrentHashMap<>();
    //username, sessionId

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        log.info(">>> connected: {}", event);

        Map nativeHeaders = getNativeHeaders(event);
        String roomId = getHeaderValue(nativeHeaders, "roomId");
        String username = getHeaderValue(nativeHeaders, "username");
        String sessionId = getSessionId();
        connectedUser.put(username, sessionId);
        if (seatingCharts.containsKey(roomId)) {
            //방에 무조건 차례대로 앉게 하는 경우
            Map seatingChart = seatingCharts.get(roomId);
//            seatInfo.put(username, seatInfo.size());
            int seatNum = seatingChart.size();
            seatingChart.put(sessionId, new SeatInfo(seatNum, username));

            Map colorInfo = lastState.get(roomId);
            colorInfo.put(seatNum, "empty");
            log.info("handleWebSocketConnectListener.seatingCharts={}", seatingCharts);
        } else {
            //방에 첫번째로 접속하는 경우
            Map seatingChart = new ConcurrentHashMap<>();
//            seatInfo.put(username, 0);
            seatingChart.put(sessionId, new SeatInfo(0, username));
            seatingCharts.put(roomId, seatingChart);

            Map colorInfo = new ConcurrentHashMap<>();
            colorInfo.put(0, "empty");
            lastState.put(roomId, colorInfo);
            log.info("handleWebSocketConnectListener.seatingCharts={}", seatingCharts);
        }
        log.info("connect.seatingCharts={}", seatingCharts);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event){
        log.info(">>> disconnected: {}", event);
        log.info("  sessionId={}", event.getSessionId());
        String sessionId = getSessionId();
        for (String roomId : seatingCharts.keySet()) {
            if (seatingCharts.get(roomId).containsKey(sessionId)){
                int seatNum = seatingCharts.get(roomId).get(sessionId).getSeatNum();
                seatingCharts.get(roomId).remove(sessionId);
                if(seatingCharts.get(roomId).isEmpty()) {
                    seatingCharts.remove(roomId);
                }
                lastState.get(roomId).remove(seatNum);
                if(lastState.get(roomId).isEmpty()){
                    lastState.remove(roomId);
                }
                break;
            }
        }
        connectedUser.remove(sessionId);
        log.info("disconnected.seatingCharts={}", seatingCharts);
        /**
         * TODO 해당 사용자가 disconnect되었으므로
         * 클라이언트에서도 방에서 퇴장했다는 조치를 해야한다.
         * seat을 empty로 처리하도록
         */
    }

    public Map<Integer, String> getRoomStatesByRoomId(String roomId){
        return lastState.get(roomId);
    }

    public Map<String, SeatInfo> getSeatingChartByRoomId(String roomId){
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

    public Integer getMySeatPosition(String roomId, String username) {
        String sessionId = connectedUser.get(username);
        log.info("seatingCharts={}", seatingCharts);
        return seatingCharts.get(roomId).get(sessionId).getSeatNum();
    }

    public void color(String roomId, String sessionId, String color){
//        lastState.get(roomId).put(sessionId, color);
//        log.info("lastState={}", lastState);
    }


}
