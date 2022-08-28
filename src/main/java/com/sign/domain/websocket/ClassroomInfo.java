package com.sign.domain.websocket;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class ClassroomInfo {
    private Integer seatNum;
    private Map<Integer, String> classRoomStates;
}
