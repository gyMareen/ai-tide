package com.ai.tide.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Rating DTO
 */
@Data
public class RatingDTO {

    @NotNull(message = "内容ID不能为空")
    private Long contentId;

    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最小为1")
    @Max(value = 5, message = "评分最大为5")
    private Integer score;

    @Size(max = 500, message = "评价内容长度不能超过500")
    private String review;
}
