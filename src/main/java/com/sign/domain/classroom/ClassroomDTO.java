package com.sign.domain.classroom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomDTO {
    private String roomName;
    private String hostId;
    private String roomCode;

    public Classroom toEntity(){
        return new Classroom(roomName, null, roomCode);
    }
}
