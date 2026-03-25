package com.ai.tide.exception;

import lombok.Getter;

/**
 * Content Exception
 */
@Getter
public class ContentException extends RuntimeException {

    private Integer code;
    private String message;

    public ContentException(String message) {
        super(message);
        this.code = 400;
        this.message = message;
    }

    public ContentException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}
