package com.sign.domain.websocket;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class ChatroomMessageTest {

    @Test
    void getCurrentTime(){
        //        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy.MM.dd a H:mm:ss", Locale.KOREA);
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("a h:mm", Locale.KOREA));
        System.out.println("currentTime = " + currentTime);
    }

}