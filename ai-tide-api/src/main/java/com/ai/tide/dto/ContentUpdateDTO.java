package com.ai.tide.dto;

import com.ai.tide.enums.ContentType;
import com.ai.tide.enums.ContentStatus;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Content Update DTO
 */
@Data
public class ContentUpdateDTO {

    @Size(max = 200, message = "标题长度不能超过200")
    private String title;

    @Size(max = 500, message = "描述长度不能超过500")
    private String description;

    private String content;

    @Size(max = 255, message = "封面图片URL长度不能超过255")
    private String coverImage;

    private ContentType type;

    private ContentStatus status;

    private Long categoryId;

    private String tags;

    private Boolean isFeatured;

    private Boolean isPinned;

    @Size(max = 200, message = "SEO标题长度不能超过200")
    private String metaTitle;

    @Size(max = 500, message = "SEO描述长度不能超过500")
    private String metaDescription;

    @Size(max = 255, message = "SEO关键词长度不能超过255")
    private String metaKeywords;
}
