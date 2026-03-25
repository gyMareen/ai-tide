package com.ai.tide.vo;

import com.ai.tide.enums.ContentType;
import com.ai.tide.enums.ContentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Content View Object
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentVO {

    private Long id;
    private String title;
    private String slug;
    private String description;
    private String content;
    private String coverImage;
    private ContentType type;
    private ContentStatus status;
    private Long authorId;
    private String authorName;
    private String authorAvatar;
    private Long categoryId;
    private String categoryName;
    private List<TagVO> tags;
    private Long viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer favoriteCount;
    private Double averageRating;
    private Integer ratingCount;
    private Boolean isFeatured;
    private Boolean isPinned;
    private LocalDateTime publishedAt;
    private Boolean allowComments;
    private String metaTitle;
    private String metaDescription;
    private String metaKeywords;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
