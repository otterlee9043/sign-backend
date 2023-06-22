package com.sign.global.websocket.dto;

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
    private Long roomId;
    private String sender;
    private String message;
}
