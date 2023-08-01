package com.sign.domain.classroom.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class RoomCreateRequest {

    @NotEmpty(message = "방 이름은 필수 항목입니다.")
    private String name;

    @NotEmpty(message = "방 코드는 필수 항목입니다.")
    private String code;

    @Min(value = 1, message = "정원은 필수 항목입니다.")
    @Max(value = 100)
    private Integer capacity;
}
