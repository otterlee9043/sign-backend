package com.sign.domain.websocket;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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
    private Integer row;
    private String sender;
    private String content;
    private String sentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("a h:mm", Locale.KOREA));
}
