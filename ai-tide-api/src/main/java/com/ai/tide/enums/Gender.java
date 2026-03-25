package com.ai.tide.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * User Gender Enum
 */
public enum Gender {
    MALE(0, "男"),
    FEMALE(1, "女"),
    OTHER(2, "其他");

    private final Integer code;
    private final String description;

    Gender(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @JsonValue
    public Integer getValue() {
        return code;
    }

    public static Gender fromCode(Integer code) {
        for (Gender gender : values()) {
            if (gender.code.equals(code)) {
                return gender;
            }
        }
        return OTHER;
    }
}
