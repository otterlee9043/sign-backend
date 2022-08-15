package com.sign.domain.classroom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class ClassroomEnterForm {
    
    @NotEmpty(message = "방 코드는 필수 항목입니다.")
    private String roomCode;
}
