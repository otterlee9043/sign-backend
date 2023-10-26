package com.sign.global.exception;

public class InvalidRefreshTokenException extends RuntimeException{

    public InvalidRefreshTokenException() {
    }

    public InvalidRefreshTokenException(String message) {
        super(message);
    }


}
