package com.ai.tide.repository;

import com.ai.tide.entity.Content;
import com.ai.tide.enums.ContentType;
import com.ai.tide.enums.ContentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Content Repository
 */
@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {

    /**
     * Find content by slug
     */
    Optional<Content> findBySlug(String slug);

    /**
     * Check if slug exists
     */
    boolean existsBySlug(String slug);

    /**
     * Find content by author
     */
    Page<Content> findByAuthorId(Long authorId, Pageable pageable);

    /**
     * Find content by category
     */
    Page<Content> findByCategoryId(Long categoryId, Pageable pageable);

    /**
     * Find content by type
     */
    Page<Content> findByType(ContentType type, Pageable pageable);

    /**
     * Find content by status
     */
    Page<Content> findByStatus(ContentStatus status, Pageable pageable);

    /**
     * Find published content
     */
    @Query("SELECT c FROM Content c WHERE c.status = 'PUBLISHED' AND c.deleted = false ORDER BY c.publishedAt DESC")
    Page<Content> findPublishedContent(Pageable pageable);

    /**
     * Find featured content
     */
    @Query("SELECT c FROM Content c WHERE c.isFeatured = true AND c.status = 'PUBLISHED' AND c.deleted = false ORDER BY c.publishedAt DESC")
    Page<Content> findFeaturedContent(Pageable pageable);

    /**
     * Search content by title or description
     */
    @Query("SELECT c FROM Content c WHERE (c.title LIKE %:keyword% OR c.description LIKE %:keyword% OR c.content LIKE %:keyword%) AND c.status = 'PUBLISHED' AND c.deleted = false")
    Page<Content> searchContent(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Search content with filters
     */
    @Query("SELECT c FROM Content c WHERE " +
           "(:keyword IS NULL OR c.title LIKE %:keyword% OR c.description LIKE %:keyword% OR c.content LIKE %:keyword%) AND " +
           "(:type IS NULL OR c.type = :type) AND " +
           "(:categoryId IS NULL OR c.categoryId = :categoryId) AND " +
           "c.status = 'PUBLISHED' AND c.deleted = false " +
           "ORDER BY c.publishedAt DESC")
    Page<Content> searchContentWithFilters(
            @Param("keyword") String keyword,
            @Param("type") ContentType type,
            @Param("categoryId") Long categoryId,
            Pageable pageable
    );

    /**
     * Find popular content (by views)
     */
    @Query("SELECT c FROM Content c WHERE c.status = 'PUBLISHED' AND c.deleted = false ORDER BY c.viewCount DESC")
    Page<Content> findPopularContent(Pageable pageable);

    /**
     * Find content by tags
     */
    @Query("SELECT DISTINCT c FROM Content c JOIN c.tags t WHERE t.id = :tagId AND c.status = 'PUBLISHED' AND c.deleted = false")
    Page<Content> findByTagId(@Param("tagId") Long tagId, Pageable pageable);

    /**
     * Count content by category
     */
    long countByCategoryId(Long categoryId);

    /**
     * Count content by author
     */
    long countByAuthorId(Long authorId);

    /**
     * Count published content
     */
    long countByStatus(ContentStatus status);

    /**
     * Find trending content (recent and popular)
     */
    @Query("SELECT c FROM Content c WHERE c.status = 'PUBLISHED' AND c.deleted = false AND c.publishedAt >= :since ORDER BY (c.viewCount + c.likeCount) DESC")
    Page<Content> findTrendingContent(@Param("since") java.time.LocalDateTime since, Pageable pageable);
}
