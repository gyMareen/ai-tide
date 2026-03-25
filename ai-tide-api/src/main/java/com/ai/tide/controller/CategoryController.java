package com.ai.tide.controller;

import com.ai.tide.service.CategoryService;
import com.ai.tide.vo.CategoryVO;
import com.ai.tide.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Category Controller
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * Get all categories
     */
    @GetMapping
    public Result<List<CategoryVO>> getAllCategories() {
        List<CategoryVO> categories = categoryService.getAllCategories();
        return Result.success(categories);
    }

    /**
     * Get root categories
     */
    @GetMapping("/root")
    public Result<List<CategoryVO>> getRootCategories() {
        List<CategoryVO> categories = categoryService.getRootCategories();
        return Result.success(categories);
    }

    /**
     * Get category by ID
     */
    @GetMapping("/{id}")
    public Result<CategoryVO> getCategoryById(@PathVariable Long id) {
        CategoryVO categoryVO = categoryService.getCategoryById(id);
        return Result.success(categoryVO);
    }

    /**
     * Get category by slug
     */
    @GetMapping("/slug/{slug}")
    public Result<CategoryVO> getCategoryBySlug(@PathVariable String slug) {
        CategoryVO categoryVO = categoryService.getCategoryBySlug(slug);
        return Result.success(categoryVO);
    }

    /**
     * Create category (Admin only)
     */
    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public Result<CategoryVO> createCategory(
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Long parentId) {
        CategoryVO categoryVO = categoryService.createCategory(name, description, parentId);
        return Result.success("创建成功", categoryVO);
    }

    /**
     * Update category (Admin only)
     */
    @PutMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public Result<CategoryVO> updateCategory(
            @PathVariable Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Integer sortOrder) {
        CategoryVO categoryVO = categoryService.updateCategory(id, name, description, sortOrder);
        return Result.success("更新成功", categoryVO);
    }

    /**
     * Delete category (Admin only)
     */
    @DeleteMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success("删除成功", null);
    }
}
