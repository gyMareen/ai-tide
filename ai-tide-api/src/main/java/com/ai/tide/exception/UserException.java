package com.ai.tide.exception;

import lombok.Getter;

/**
 * User Exception
 */
@Getter
public class UserException extends RuntimeException {

    private Integer code;
    private String message;

    public UserException(String message) {
        super(message);
        this.code = 400;
        this.message = message;
    }

    public UserException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}
