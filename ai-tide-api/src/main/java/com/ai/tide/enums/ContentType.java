package com.ai.tide.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Content Type Enum
 */
public enum ContentType {
    NEWS(0, "AI新闻"),
    TUTORIAL(1, "教程"),
    TOOL(2, "工具/产品"),
    PAPER(3, "论文"),
    VIDEO(4, "视频"),
    CASE_STUDY(5, "案例研究");

    private final Integer code;
    private final String description;

    ContentType(Integer code, String description) {
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

    public static ContentType fromCode(Integer code) {
        for (ContentType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return NEWS;
    }
}
