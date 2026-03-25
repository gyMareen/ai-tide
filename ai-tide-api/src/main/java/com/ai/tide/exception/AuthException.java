package com.ai.tide.exception;

import lombok.Getter;

/**
 * Authentication Exception
 */
@Getter
public class AuthException extends RuntimeException {

    private Integer code;
    private String message;

    public AuthException(String message) {
        super(message);
        this.code = 401;
        this.message = message;
    }

    public AuthException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}
