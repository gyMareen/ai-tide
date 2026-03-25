package com.ai.tide.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Account Status Enum
 */
public enum AccountStatus {
    ACTIVE(0, "正常"),
    LOCKED(1, "已锁定"),
    DISABLED(2, "已禁用"),
    DELETED(3, "已删除");

    private final Integer code;
    private final String description;

    AccountStatus(Integer code, String description) {
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

    public static AccountStatus fromCode(Integer code) {
        for (AccountStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return ACTIVE;
    }
}
