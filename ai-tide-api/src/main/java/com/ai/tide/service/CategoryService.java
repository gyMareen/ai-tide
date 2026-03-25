package com.ai.tide.service;

import com.ai.tide.entity.Category;
import com.ai.tide.exception.ContentException;
import com.ai.tide.repository.CategoryRepository;
import com.ai.tide.repository.ContentRepository;
import com.ai.tide.vo.CategoryVO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Category Service
 */
@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ContentRepository contentRepository;

    /**
     * Get all categories
     */
    public List<CategoryVO> getAllCategories() {
        List<Category> categories = categoryRepository.findAllByOrderBySortOrderAsc();
        return categories.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * Get root categories
     */
    public List<CategoryVO> getRootCategories() {
        List<Category> categories = categoryRepository.findByParentIsNull();
        return categories.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * Get category by ID
     */
    public CategoryVO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ContentException("分类不存在"));
        return convertToVO(category);
    }

    /**
     * Get category by slug
     */
    public CategoryVO getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ContentException("分类不存在"));
        return convertToVO(category);
    }

    /**
     * Create category
     */
    @Transactional
    public CategoryVO createCategory(String name, String description, Long parentId) {
        if (categoryRepository.existsByName(name)) {
            throw new ContentException("分类名称已存在");
        }

        Category category = new Category();
        category.setName(name);
        category.setSlug(name.toLowerCase().replaceAll("[^a-z0-9]+", "-"));
        category.setDescription(description);
        category.setSortOrder(0);

        if (parentId != null) {
            Category parent = categoryRepository.findById(parentId)
                    .orElseThrow(() -> new ContentException("父分类不存在"));
            category.setParent(parent);
            category.setLevel(parent.getLevel() + 1);
            category.setPath(parent.getPath() + "/" + category.getSlug());
        } else {
            category.setLevel(0);
            category.setPath(category.getSlug());
        }

        category = categoryRepository.save(category);

        return convertToVO(category);
    }

    /**
     * Update category
     */
    @Transactional
    public CategoryVO updateCategory(Long id, String name, String description, Integer sortOrder) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ContentException("分类不存在"));

        if (name != null && !name.equals(category.getName())) {
            if (categoryRepository.existsByName(name)) {
                throw new ContentException("分类名称已存在");
            }
            category.setName(name);
            category.setSlug(name.toLowerCase().replaceAll("[^a-z0-9]+", "-"));
        }

        if (description != null) {
            category.setDescription(description);
        }

        if (sortOrder != null) {
            category.setSortOrder(sortOrder);
        }

        category = categoryRepository.save(category);

        return convertToVO(category);
    }

    /**
     * Delete category
     */
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ContentException("分类不存在"));

        // Check if category has children
        if (!category.getChildren().isEmpty()) {
            throw new ContentException("分类包含子分类，无法删除");
        }

        // Check if category has content
        long contentCount = contentRepository.countByCategoryId(id);
        if (contentCount > 0) {
            throw new ContentException("分类包含内容，无法删除");
        }

        categoryRepository.delete(category);
    }

    /**
     * Convert Category to CategoryVO
     */
    private CategoryVO convertToVO(Category category) {
        List<CategoryVO> children = category.getChildren().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return new CategoryVO(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getDescription(),
                category.getIcon(),
                category.getColor(),
                category.getSortOrder(),
                category.getLevel(),
                category.getPath(),
                category.getParent() != null ? category.getParent().getId() : null,
                children,
                category.getContentCount(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}
