package com.sign.global.websocket.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SeatingChartManager {

    private final Map<Long, Map<Long, Integer>> seatingCharts = new ConcurrentHashMap<>();
    //roomId, {memberId, seatNum}

    public Map<Long, Integer> getSeatingChart(Long roomId) {
        return seatingCharts.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>());
    }

    public void updateSeatingChart(Long roomId, Long memberId, int seatNum) {
        Map<Long, Integer> seatingChart = getSeatingChart(roomId);
        seatingChart.put(memberId, seatNum);
        seatingCharts.put(roomId, seatingChart);
    }

    public Long getJoiningRoomId(Long memberId) {
        return seatingCharts.keySet().stream()
                .filter(roomId -> seatingCharts.get(roomId).containsKey(memberId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Member not found."));
    }

    public int getSeatNum(Long roomId, Long memberId) {
        return seatingCharts.get(roomId).get(memberId);
    }

    public void emptySeat(Long roomId, Long memberId) {
        seatingCharts.get(roomId).remove(memberId);

        if (seatingCharts.get(roomId).isEmpty()) {
            seatingCharts.remove(roomId);
        }
    }

    public boolean doesSeatingChartExist(Long roomId) {
        return seatingCharts.containsKey(roomId);
    }

    public boolean doesMemberExist(Long roomId, Long memberId) {
        return seatingCharts.get(roomId).containsKey(memberId);
    }
}
