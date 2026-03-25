package com.ai.tide.repository;

import com.ai.tide.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Category Repository
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find category by slug
     */
    Optional<Category> findBySlug(String slug);

    /**
     * Check if slug exists
     */
    boolean existsBySlug(String slug);

    /**
     * Find root categories (no parent)
     */
    List<Category> findByParentIsNull();

    /**
     * Find categories by parent
     */
    List<Category> findByParentId(Long parentId);

    /**
     * Find all categories sorted by order
     */
    List<Category> findAllByOrderBySortOrderAsc();

    /**
     * Find categories by level
     */
    List<Category> findByLevel(Integer level);
}
