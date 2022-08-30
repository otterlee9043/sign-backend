package com.sign.domain.websocket;

import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    public enum MessageType {
        ENTER, TALK, EXIT
    }

    private MessageType type;
    private Integer seatNum;
    private String roomId;
    private String sender;
    private String message;
}
