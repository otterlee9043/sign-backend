package com.sign.domain.websocket;

import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class RoomMessage {

    public enum MessageType {
        ENTER, TALK, EXIT, CHANGE_SEAT
    }

    private MessageType type;
    private Integer seatNum;
    private Integer roomId;
    private String sender;
    private String message;
}
