package com.sign.domain.classroom.controller.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RoomErrorResult {
    private String code;
    private String message;
}
