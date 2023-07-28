package com.sign.domain.classroom.controller.dto;

import com.sign.domain.classroom.entity.Room;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RoomResponse {
    private Long id;
    private String roomName;
    private Integer capacity;
    private String hostUsername;
    private String hostEmail;

    public static RoomResponse from(Room room) {
        return RoomResponse.builder()
                .id(room.getId())
                .roomName(room.getName())
                .capacity(room.getCapacity())
                .hostEmail(room.getHost().getEmail())
                .hostUsername(room.getHost().getUsername())
                .build();
    }

}
