package com.sign.global.websocket.dto;

import lombok.*;

import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class RoomInfo {
    private Integer seatNum;
    private Map<Integer, String[]> classRoomStates;
}
