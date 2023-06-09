package com.sign.domain.websocket;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomMessage {

    private MessageType type;
    private Integer seatNum;
    private Integer roomId;
    private String sender;
    private String message;
}
