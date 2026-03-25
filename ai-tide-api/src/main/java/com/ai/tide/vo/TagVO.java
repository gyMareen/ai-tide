package com.ai.tide.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Tag View Object
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagVO {

    private Long id;
    private String name;
    private String slug;
    private String description;
    private String color;
    private Integer useCount;
}
