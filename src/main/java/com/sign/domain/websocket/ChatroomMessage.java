package com.sign.domain.websocket;

import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class ChatroomMessage {
    public enum MessageType {
        ENTER, TALK, EXIT, CHANGE_SEAT
    }

    private MessageType type;
    private Integer seatNum;
    private String sender;
    private String content;
}
