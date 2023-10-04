package com.sign.global.websocket.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatroomService {

    private final WebSocketSessionRegistry webSocketSessionRegistry;

    private final SeatingChartManager seatingChartManager;

    private final RoomStateManager roomStateManager;


    public void enter(String sessionId, Long memberId, Long roomId) {
        webSocketSessionRegistry.addSession(sessionId, memberId, roomId);
        log.info("User {} entered room {}.", memberId, roomId);
    }

    public int sit(Long memberId, Long roomId) {
        int seatNum = roomStateManager.getAvailableSeatNum(roomId);
        roomStateManager.updateRoomState(roomId, seatNum, new String[]{"unselected", ""});
        seatingChartManager.updateSeatingChart(roomId, memberId, seatNum);

        return seatNum;
    }

    public void color(Long roomId, int seatNum, String color) {
        String[] state = roomStateManager.getSeatState(roomId, seatNum);
        state[0] = color;
        roomStateManager.updateRoomState(roomId, seatNum, state);
    }

    public void drawEmoji(Long roomId, int seatNum, String emoji) {
        String[] state = roomStateManager.getSeatState(roomId, seatNum);
        state[1] = emoji;
        roomStateManager.updateRoomState(roomId, seatNum, state);
    }

    public void leave(Long memberId) {
        Long roomId = seatingChartManager.getJoiningRoomId(memberId);

        seatingChartManager.emptySeat(roomId, memberId);
        int seatNum = seatingChartManager.getSeatNum(roomId, memberId);

        roomStateManager.removeSeatState(roomId, seatNum);
    }

    public void exit(String sessionId) {
        Long memberId = webSocketSessionRegistry.getMemberId(sessionId);
        leave(memberId);

        webSocketSessionRegistry.removeSession(sessionId);
    }

    public void changeSeat(Long roomId, String sessionId, int oldSeatNum, int newSeatNum) {
        Long memberId = webSocketSessionRegistry.getMemberId(sessionId);

        String[] oldSeatState = roomStateManager.getSeatState(roomId, oldSeatNum);
        roomStateManager.updateRoomState(roomId, newSeatNum, oldSeatState);
        roomStateManager.getSeatState(roomId, oldSeatNum);

        seatingChartManager.updateSeatingChart(roomId, memberId, newSeatNum);
    }

    public int getSeatNum(String sessionId) {
        Long memberId = webSocketSessionRegistry.getMemberId(sessionId);
        Long roomId = webSocketSessionRegistry.getMemberId(sessionId);
        return seatingChartManager.getSeatNum(roomId, memberId);
    }

    public Map<Integer, String[]> getRoomStateByRoomId(Long roomId) {
        return roomStateManager.getRoomState(roomId);
    }

    public boolean isConnected(Long roomId, Long memberId) {
        return seatingChartManager.doesSeatingChartExist(roomId)
                && seatingChartManager.doesMemberExist(roomId, memberId);
    }
}
