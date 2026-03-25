package com.ai.tide.controller;

import com.ai.tide.service.TagService;
import com.ai.tide.vo.Result;
import com.ai.tide.vo.TagVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Tag Controller
 */
@RestController
@RequestMapping("/api/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    /**
     * Get all tags
     */
    @GetMapping
    public Result<List<TagVO>> getAllTags() {
        List<TagVO> tags = tagService.getAllTags();
        return Result.success(tags);
    }

    /**
     * Get popular tags
     */
    @GetMapping("/popular")
    public Result<List<TagVO>> getPopularTags() {
        List<TagVO> tags = tagService.getPopularTags();
        return Result.success(tags);
    }

    /**
     * Get tag by ID
     */
    @GetMapping("/{id}")
    public Result<TagVO> getTagById(@PathVariable Long id) {
        TagVO tagVO = tagService.getTagById(id);
        return Result.success(tagVO);
    }

    /**
     * Get tag by slug
     */
    @GetMapping("/slug/{slug}")
    public Result<TagVO> getTagBySlug(@PathVariable String slug) {
        TagVO tagVO = tagService.getTagBySlug(slug);
        return Result.success(tagVO);
    }

    /**
     * Create tag (Admin only)
     */
    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public Result<TagVO> createTag(
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String color) {
        TagVO tagVO = tagService.createTag(name, description, color);
        return Result.success("创建成功", tagVO);
    }

    /**
     * Update tag (Admin only)
     */
    @PutMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public Result<TagVO> updateTag(
            @PathVariable Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String color) {
        TagVO tagVO = tagService.updateTag(id, name, description, color);
        return Result.success("更新成功", tagVO);
    }

    /**
     * Delete tag (Admin only)
     */
    @DeleteMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return Result.success("删除成功", null);
    }
}
