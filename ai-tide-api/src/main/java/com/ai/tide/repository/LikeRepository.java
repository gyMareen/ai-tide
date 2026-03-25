package com.ai.tide.repository;

import com.ai.tide.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Like Repository
 */
@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    /**
     * Find like by content and user
     */
    Optional<Like> findByContentIdAndUserId(Long contentId, Long userId);

    /**
     * Check if user liked content
     */
    boolean existsByContentIdAndUserId(Long contentId, Long userId);

    /**
     * Count likes by content
     */
    long countByContentId(Long contentId);

    /**
     * Delete like by content and user
     */
    void deleteByContentIdAndUserId(Long contentId, Long userId);
}
