package com.ai.tide.repository;

import com.ai.tide.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Comment Repository
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Find comments by content
     */
    List<Comment> findByContentIdOrderByCreatedAtAsc(Long contentId);

    /**
     * Find root comments (no parent) by content
     */
    List<Comment> findByContentIdAndParentIsNullOrderByCreatedAtAsc(Long contentId);

    /**
     * Find comments by content with pagination
     */
    Page<Comment> findByContentId(Long contentId, Pageable pageable);

    /**
     * Find comments by user
     */
    List<Comment> findByUserId(Long userId);

    /**
     * Find comments by parent
     */
    List<Comment> findByParentId(Long parentId);

    /**
     * Count comments by content
     */
    long countByContentId(Long contentId);

    /**
     * Count comments by user
     */
    long countByUserId(Long userId);
}
