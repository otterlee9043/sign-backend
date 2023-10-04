package com.sign.global.websocket.service;

import com.sign.domain.classroom.service.RoomService;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class RoomStateManager {

    private final RoomService roomService;

    private final Map<Long, Map<Integer, String[]>> lastState = new ConcurrentHashMap<>();
    //roomId, seatNum, [color, emoji]

    public Map<Integer, String[]> getRoomState(Long roomId) {
        return lastState.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>());
    }

    public int getAvailableSeatNum(Long roomId) {
        Map<Integer, String[]> roomState = getRoomState(roomId);
        int capacity = roomService.getRoom(roomId).getCapacity();

        return IntStream.rangeClosed(1, capacity)
                .filter(i -> !roomState.containsKey(i))
                .findFirst()
                .orElse(1);
    }

    public void updateRoomState(Long roomId, int seatNum, String[] state) {
        Map<Integer, String[]> roomState = getRoomState(roomId);
        roomState.put(seatNum, state);
        lastState.put(roomId, roomState);
    }

    public String[] getSeatState(Long roomId, int seatNum) {
        return lastState.get(roomId).get(seatNum);
    }

    public void removeSeatState(Long roomId, int seatNum) {
        lastState.get(roomId).remove(seatNum);
        if (lastState.get(roomId).isEmpty()) {
            lastState.remove(roomId);
        }
    }

}
