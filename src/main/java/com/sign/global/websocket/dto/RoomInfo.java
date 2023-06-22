package com.sign.global.websocket.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class RoomInfo {
    private Integer seatNum;
    private Map<Integer, String[]> classRoomStates;
}
