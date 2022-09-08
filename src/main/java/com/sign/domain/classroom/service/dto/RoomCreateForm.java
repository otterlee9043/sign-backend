package com.sign.domain.classroom.service.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class RoomCreateForm {

    @NotEmpty(message = "방 이름은 필수 항목입니다.")
    private String roomName;

    @NotEmpty(message = "방 코드는 필수 항목입니다.")
    private String roomCode;
}
