package com.ai.tide.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Operation Log Type Enum
 */
public enum OperationType {
    CREATE(0, "创建"),
    UPDATE(1, "更新"),
    DELETE(2, "删除"),
    QUERY(3, "查询"),
    LOGIN(4, "登录"),
    LOGOUT(5, "登出"),
    PUBLISH(6, "发布"),
    ARCHIVE(7, "归档"),
    RESTORE(8, "恢复"),
    IMPORT(9, "导入"),
    EXPORT(10, "导出");

    private final Integer code;
    private final String description;

    OperationType(Integer code, String description) {
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

    public static OperationType fromCode(Integer code) {
        for (OperationType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return QUERY;
    }
}
