package com.sign.domain.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoomEventHandler {
    private final SimpMessageSendingOperations sendingOperations;
    //roomId, sessionId, seatNum
    private final Map<Long, Map<Integer, String>> lastState = new ConcurrentHashMap<>();
    //roomId, seatNum, color
    private final Map<Long, Map<String, Integer>> seatingCharts = new ConcurrentHashMap<>();
    private void sit(String sessionId, Long roomId) {
        Integer capacity = roomService.getRoomCapacity(roomId);
        if (seatingCharts.containsKey(roomId)) {
            // 입장한 사람이 있는 경우
            log.info("입장한 사람이 있는 경우");
            Map seatingChart = seatingCharts.get(roomId);
            Map classroomState = lastState.get(roomId);
            int seatNum = 1;
            for(int i = 1; i <= capacity; i++) {
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
        for (Long roomId : seatingCharts.keySet()) {
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

    public void color(Integer roomId, int seatNum, String color){
        lastState.get(roomId).put(seatNum, color);
    }


    public void changeSeat(Integer roomId, String sessionId, int oldSeatNum, int newSeatNum){
        Map<Integer, String> classroomState = lastState.get(roomId);
        Map<String, Integer> seatingChart = seatingCharts.get(roomId);
        classroomState.put(newSeatNum, classroomState.get(oldSeatNum));
        classroomState.remove(oldSeatNum);
        seatingChart.put(sessionId, newSeatNum);
    }

    public Integer getMySeatPosition(Long roomId, String sessionId) {
        log.info("seatingCharts={}", seatingCharts);
        return seatingCharts.get(roomId).get(sessionId);
    }

    public Map<Integer, String> getRoomStatesByRoomId(Long roomId){
        return lastState.get(roomId);
    }

}
