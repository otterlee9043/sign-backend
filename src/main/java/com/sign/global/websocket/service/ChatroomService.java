package com.sign.global.websocket.service;

import com.sign.domain.classroom.service.RoomService;
import com.sign.global.websocket.dto.MessageType;
import com.sign.global.websocket.dto.RoomMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatroomService {


    private final RoomService roomService;


    private final Map<Long, Map<Integer, String[]>> lastState = new ConcurrentHashMap<>();
    //roomId, seatNum, [color, emoji]
    private final Map<Long, Map<String, Integer>> seatingCharts = new ConcurrentHashMap<>();
    //roomId, sessionId, seatNum
//    private final Map<Long, Map<String, Long>> userSession = new ConcurrentHashMap<>();
    private final Map<String, Long[]> userSession = new ConcurrentHashMap<>();
    // sessionId, {userId, roomId}
    public void sit(String sessionId, Long roomId) {
        Integer capacity = roomService.getRoomCapacity(roomId);
        if (seatingCharts.containsKey(roomId)) {
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
            colorInfo.put(seatNum, new String[]{"unselected", ""});
        } else {
            Map seatingChart = new ConcurrentHashMap<>();
            seatingChart.put(sessionId, 1);
            seatingCharts.put(roomId, seatingChart);

            Map colorInfo = new ConcurrentHashMap<>();
            colorInfo.put(1, new String[]{"unselected", ""});
            lastState.put(roomId, colorInfo);
        }
    }

    public RoomMessage leave(String sessionId) {
        Integer seatNum = null;
        Long leavingRoomId = null;
        for (Long roomId : seatingCharts.keySet()) {
            if (seatingCharts.get(roomId).containsKey(sessionId)){
                seatNum = seatingCharts.get(roomId).get(sessionId);
                leavingRoomId = roomId;
                seatingCharts.get(roomId).remove(sessionId);

                if (seatingCharts.get(roomId).isEmpty()) {
                    seatingCharts.remove(roomId);
                }

                lastState.get(roomId).remove(seatNum);
                if (lastState.get(roomId).isEmpty()){
                    lastState.remove(roomId);
                }
                break;
            }
        }
        return new RoomMessage(MessageType.EXIT, seatNum, leavingRoomId, null, null);
    }

    public void color(Long roomId, int seatNum, String color){
        String[] state = lastState.get(roomId).get(seatNum);
        state[0] = color;
        lastState.get(roomId).put(seatNum, state);
    }

    public void drawEmoji(Long roomId, int seatNum, String emoji){
        String[] state = lastState.get(roomId).get(seatNum);
        state[1] = emoji;
        lastState.get(roomId).put(seatNum, state);
    }

    public void enter(String sessionId, Long userId, Long roomId){
        userSession.put(sessionId,  new Long[]{userId, roomId});
        log.info("User {} entered room {}.", userId, roomId);
        sit(sessionId, roomId);
    }

    public void exit(String sessionId){
        Long[] userInfo = userSession.get(sessionId);
        Long userId = userInfo[0];
        Long roomId = userInfo[1];
        userSession.remove(sessionId);
        log.info("User {} exited room {}.", userId, roomId);
        leave(sessionId);
    }

    public void changeSeat(Long roomId, String sessionId, int oldSeatNum, int newSeatNum){
        Map<Integer, String[]> classroomState = lastState.get(roomId);
        Map<String, Integer> seatingChart = seatingCharts.get(roomId);
        classroomState.put(newSeatNum, classroomState.get(oldSeatNum));
        classroomState.remove(oldSeatNum);
        seatingChart.put(sessionId, newSeatNum);
    }

    public boolean isRoomAccessible(Long roomId) {
        Integer capacity = roomService.getRoomCapacity(roomId);
        return !(seatingCharts.get(roomId).size() >= capacity);
    }

    public Integer getMySeatPosition(Long roomId, String sessionId) {
        return seatingCharts.get(roomId).get(sessionId);
    }

    public Map<Integer, String[]> getRoomStatesByRoomId(Long roomId){
        return lastState.get(roomId);
    }
}
