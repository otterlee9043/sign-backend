package com.sign.domain.member.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class DataDuplicateException extends RuntimeException{
    List<String> fields;
    public DataDuplicateException() {
        super();
    }

    public DataDuplicateException(String message, List<String> fields) {
        super(message);
        this.fields = fields;
    }
}
