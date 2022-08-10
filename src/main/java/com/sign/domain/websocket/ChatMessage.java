package com.sign.domain.websocket;

import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class ChatMessage {

    public enum MessageType {
        ENTER, TALK
    }

    private MessageType type;
    private String roomId;
    private String sender;
    private String message;
}
