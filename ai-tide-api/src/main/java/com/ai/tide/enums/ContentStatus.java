package com.ai.tide.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Content Status Enum
 */
public enum ContentStatus {
    DRAFT(0, "草稿"),
    PUBLISHED(1, "已发布"),
    ARCHIVED(2, "已归档"),
    DELETED(3, "已删除");

    private final Integer code;
    private final String description;

    ContentStatus(Integer code, String description) {
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

    public static ContentStatus fromCode(Integer code) {
        for (ContentStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return DRAFT;
    }
}
