package com.sign.domain.classroom.controller.dto;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class RoomCreateRequest {

    @NotEmpty(message = "방 이름은 필수 항목입니다.")
    private String roomName;

    @NotEmpty(message = "방 코드는 필수 항목입니다.")
    private String roomCode;

    @Min(value = 1, message = "정원은 필수 항목입니다.")
    @Max(value = 100)
    private Integer capacity;
}
