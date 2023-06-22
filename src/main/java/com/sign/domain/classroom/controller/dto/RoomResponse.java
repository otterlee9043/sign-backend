package com.sign.domain.classroom.controller.dto;

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
}
