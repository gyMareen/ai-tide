package com.ai.tide.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Category View Object
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryVO {

    private Long id;
    private String name;
    private String slug;
    private String description;
    private String icon;
    private String color;
    private Integer sortOrder;
    private Integer level;
    private String path;
    private Long parentId;
    private List<CategoryVO> children;
    private Integer contentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
