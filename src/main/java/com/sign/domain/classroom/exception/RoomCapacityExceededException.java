package com.sign.domain.classroom.exception;

public class RoomCapacityExceededException extends RuntimeException {
    public RoomCapacityExceededException() {
        super("입장한 인원이 많아 참여할 수 없습니다.");
    }

    public RoomCapacityExceededException(String message) {
        super(message);
    }
}