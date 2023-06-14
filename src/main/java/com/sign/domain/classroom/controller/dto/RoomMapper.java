package com.sign.domain.classroom.controller.dto;

import com.sign.domain.classroom.entity.Room;

public class RoomMapper {
    public static RoomInfo toRoomInfo(Room room) {
        RoomInfo roomInfo = RoomInfo.builder()
                .id(room.getId())
                .roomName(room.getName())
                .capacity(room.getCapacity())
                .hostUsername(room.getHost().getUsername())
                .hostEmail(room.getHost().getEmail())
                .build();
        return roomInfo;
    }
}
