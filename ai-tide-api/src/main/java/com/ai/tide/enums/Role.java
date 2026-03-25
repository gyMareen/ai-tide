package com.ai.tide.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * User Role Enum
 */
public enum Role {
    ADMIN(0, "管理员"),
    EDITOR(1, "编辑"),
    USER(2, "普通用户");

    private final Integer code;
    private final String description;

    Role(Integer code, String description) {
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

    public static Role fromCode(Integer code) {
        for (Role role : values()) {
            if (role.code.equals(code)) {
                return role;
            }
        }
        return USER;
    }
}
