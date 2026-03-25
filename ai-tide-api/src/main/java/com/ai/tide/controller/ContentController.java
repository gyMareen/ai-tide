package com.ai.tide.controller;

import com.ai.tide.dto.ContentCreateDTO;
import com.ai.tide.dto.ContentUpdateDTO;
import com.ai.tide.enums.ContentType;
import com.ai.tide.security.CurrentUserService;
import com.ai.tide.service.ContentService;
import com.ai.tide.vo.ContentVO;
import com.ai.tide.vo.PageVO;
import com.ai.tide.vo.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

/**
 * Content Controller
 */
@RestController
@RequestMapping("/api/contents")
public class ContentController {

    @Autowired
    private ContentService contentService;

    /**
     * Get published content with pagination
     */
    @GetMapping
    public Result<PageVO<ContentVO>> getPublishedContent(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        PageVO<ContentVO> contents = contentService.getPublishedContent(pageable);
        return Result.success(contents);
    }

    /**
     * Get content by ID
     */
    @GetMapping("/{id}")
    public Result<ContentVO> getContentById(@PathVariable Long id) {
        ContentVO contentVO = contentService.getContentById(id);
        contentService.incrementViewCount(id);
        return Result.success(contentVO);
    }

    /**
     * Get content by slug
     */
    @GetMapping("/slug/{slug}")
    public Result<ContentVO> getContentBySlug(@PathVariable String slug) {
        ContentVO contentVO = contentService.getContentBySlug(slug);
        return Result.success(contentVO);
    }

    /**
     * Create content
     */
    @PostMapping
    public Result<ContentVO> createContent(@Valid @RequestBody ContentCreateDTO createDTO) {
        Long userId = CurrentUserService.getCurrentUserId();
        ContentVO contentVO = contentService.createContent(userId, createDTO);
        return Result.success("创建成功", contentVO);
    }

    /**
     * Update content
     */
    @PutMapping("/{id}")
    public Result<ContentVO> updateContent(
            @PathVariable Long id,
            @Valid @RequestBody ContentUpdateDTO updateDTO) {
        Long userId = CurrentUserService.getCurrentUserId();
        ContentVO contentVO = contentService.updateContent(id, userId, updateDTO);
        return Result.success("更新成功", contentVO);
    }

    /**
     * Delete content
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteContent(@PathVariable Long id) {
        Long userId = CurrentUserService.getCurrentUserId();
        contentService.deleteContent(id, userId);
        return Result.success("删除成功", null);
    }

    /**
     * Publish content
     */
    @PostMapping("/{id}/publish")
    public Result<ContentVO> publishContent(@PathVariable Long id) {
        Long userId = CurrentUserService.getCurrentUserId();
        ContentVO contentVO = contentService.publishContent(id, userId);
        return Result.success("发布成功", contentVO);
    }

    /**
     * Archive content
     */
    @PostMapping("/{id}/archive")
    public Result<ContentVO> archiveContent(@PathVariable Long id) {
        Long userId = CurrentUserService.getCurrentUserId();
        ContentVO contentVO = contentService.archiveContent(id, userId);
        return Result.success("归档成功", contentVO);
    }

    /**
     * Search content
     */
    @GetMapping("/search")
    public Result<PageVO<ContentVO>> searchContent(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        PageVO<ContentVO> contents = contentService.searchContent(keyword, pageable);
        return Result.success(contents);
    }

    /**
     * Search content with filters
     */
    @GetMapping("/search/filters")
    public Result<PageVO<ContentVO>> searchContentWithFilters(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) ContentType type,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        PageVO<ContentVO> contents = contentService.searchContentWithFilters(keyword, type, categoryId, pageable);
        return Result.success(contents);
    }

    /**
     * Get content by author
     */
    @GetMapping("/author/{authorId}")
    public Result<PageVO<ContentVO>> getContentByAuthor(
            @PathVariable Long authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        PageVO<ContentVO> contents = contentService.getContentByAuthor(authorId, pageable);
        return Result.success(contents);
    }

    /**
     * Get content by category
     */
    @GetMapping("/category/{categoryId}")
    public Result<PageVO<ContentVO>> getContentByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        PageVO<ContentVO> contents = contentService.getContentByCategory(categoryId, pageable);
        return Result.success(contents);
    }

    /**
     * Get content by tag
     */
    @GetMapping("/tag/{tagId}")
    public Result<PageVO<ContentVO>> getContentByTag(
            @PathVariable Long tagId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        PageVO<ContentVO> contents = contentService.getContentByTag(tagId, pageable);
        return Result.success(contents);
    }

    /**
     * Get featured content
     */
    @GetMapping("/featured")
    public Result<PageVO<ContentVO>> getFeaturedContent(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        PageVO<ContentVO> contents = contentService.getFeaturedContent(pageable);
        return Result.success(contents);
    }

    /**
     * Get popular content
     */
    @GetMapping("/popular")
    public Result<PageVO<ContentVO>> getPopularContent(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("viewCount").descending());
        PageVO<ContentVO> contents = contentService.getPopularContent(pageable);
        return Result.success(contents);
    }

    /**
     * Get trending content
     */
    @GetMapping("/trending")
    public Result<PageVO<ContentVO>> getTrendingContent(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        PageVO<ContentVO> contents = contentService.getTrendingContent(pageable);
        return Result.success(contents);
    }
}
