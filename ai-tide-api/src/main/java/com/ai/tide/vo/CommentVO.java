package com.ai.tide.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Comment View Object
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentVO {

    private Long id;
    private String content;
    private Long contentId;
    private Long userId;
    private String username;
    private String avatar;
    private Long parentId;
    private List<CommentVO> children;
    private Integer likesCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
