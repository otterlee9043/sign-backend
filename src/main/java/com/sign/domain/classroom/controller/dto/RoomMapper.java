package com.sign.domain.classroom.controller.dto;

import com.sign.domain.classroom.entity.Room;

public class RoomMapper {
    public static RoomResponse toRoomInfo(Room room) {
        RoomResponse roomResponse = RoomResponse.builder()
                .id(room.getId())
                .roomName(room.getName())
                .capacity(room.getCapacity())
                .hostUsername(room.getHost().getUsername())
                .hostEmail(room.getHost().getEmail())
                .build();
        return roomResponse;
    }
}
