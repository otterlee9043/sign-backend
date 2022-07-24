package com.sign.classroom;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Classroom {
    private Long id;
    private String roomName;
    private Long hostId;
    private String roomCode;
    private Date created;
}
