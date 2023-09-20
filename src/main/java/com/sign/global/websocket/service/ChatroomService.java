package com.sign.global.websocket.service;

import com.sign.domain.classroom.entity.Room;
import com.sign.domain.classroom.service.RoomService;
import com.sign.global.websocket.dto.MessageType;
import com.sign.global.websocket.dto.RoomInfo;
import com.sign.global.websocket.dto.RoomMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatroomService {

    private final Map<Long, Map<Integer, String[]>> lastState = new ConcurrentHashMap<>();
    //roomId, seatNum, [color, emoji]
    private final Map<Long, Map<Long, Integer>> seatingCharts = new ConcurrentHashMap<>();
    //roomId, {memberId, seatNum}

    private final Map<String, Long[]> userSession = new ConcurrentHashMap<>();
    // sessionId, {memberId, roomId}

    private final RoomService roomService;


    public void enter(String sessionId, Long memberId, Long roomId) {
        userSession.put(sessionId, new Long[]{memberId, roomId});
        log.info("User {} entered room {}.", memberId, roomId);
    }

    public int sit(Long memberId, Long roomId) {
        Map<Long, Integer> seatingChart = seatingCharts.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>());
        Map<Integer, String[]> colorInfo = lastState.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>());

        Room room = roomService.getRoom(roomId);
        int seatNum = IntStream.rangeClosed(1, room.getCapacity())
                .filter(i -> !colorInfo.containsKey(i))
                .findFirst()
                .orElse(1);

        seatingChart.put(memberId, seatNum);
        seatingCharts.put(roomId, seatingChart);

        colorInfo.put(seatNum, new String[]{"unselected", ""});
        lastState.put(roomId, colorInfo);

        return seatNum;
    }

    public void color(Long roomId, int seatNum, String color) {
        String[] state = lastState.get(roomId).get(seatNum);
        state[0] = color;
        lastState.get(roomId).put(seatNum, state);
    }

    public void drawEmoji(Long roomId, int seatNum, String emoji) {
        String[] state = lastState.get(roomId).get(seatNum);
        state[1] = emoji;
        lastState.get(roomId).put(seatNum, state);
    }

    public void leave(Long memberId) {
        for (Long roomId : seatingCharts.keySet()) {
            if (seatingCharts.get(roomId).containsKey(memberId)) {
                int seatNum = seatingCharts.get(roomId).get(memberId);
                seatingCharts.get(roomId).remove(memberId);

                if (seatingCharts.get(roomId).isEmpty()) {
                    seatingCharts.remove(roomId);
                }

                lastState.get(roomId).remove(seatNum);
                if (lastState.get(roomId).isEmpty()) {
                    lastState.remove(roomId);
                }
                break;
            }
        }
    }

    public void exit(String sessionId) {
        if (userSession.containsKey(sessionId)) {
            Long[] userInfo = userSession.get(sessionId);
            Long memberId = userInfo[0];
            Long roomId = userInfo[1];
            log.info("User {} exited room {}.", memberId, roomId);

            userSession.remove(sessionId);
            leave(memberId);
        }
    }

    public void changeSeat(Long roomId, String sessionId, int oldSeatNum, int newSeatNum) {
        Long memberId = userSession.get(sessionId)[0];

        Map<Integer, String[]> classroomState = lastState.get(roomId);
        Map<Long, Integer> seatingChart = seatingCharts.get(roomId);

        classroomState.put(newSeatNum, classroomState.get(oldSeatNum));
        classroomState.remove(oldSeatNum);

        seatingChart.put(memberId, newSeatNum);
    }

    public int getSeatNum(String sessionId) {
        Long memberId = userSession.get(sessionId)[0];
        Long roomId = userSession.get(sessionId)[1];
        return seatingCharts.get(roomId).get(memberId);
    }

    public Long getRoomId(String sessionId) {
        return userSession.get(sessionId)[1];
    }

    public Map<Integer, String[]> getRoomStatesByRoomId(Long roomId) {
        return lastState.get(roomId);
    }

    public boolean isConnected(Long roomId, Long memberId) {
        return seatingCharts.containsKey(roomId) && seatingCharts.get(roomId).containsKey(memberId);
    }
}
