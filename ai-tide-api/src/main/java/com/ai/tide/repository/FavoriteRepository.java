package com.ai.tide.repository;

import com.ai.tide.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Favorite Repository
 */
@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    /**
     * Find favorite by content and user
     */
    Optional<Favorite> findByContentIdAndUserId(Long contentId, Long userId);

    /**
     * Check if user favorited content
     */
    boolean existsByContentIdAndUserId(Long contentId, Long userId);

    /**
     * Find favorites by user with pagination
     */
    Page<Favorite> findByUserId(Long userId, Pageable pageable);

    /**
     * Count favorites by content
     */
    long countByContentId(Long contentId);

    /**
     * Count favorites by user
     */
    long countByUserId(Long userId);

    /**
     * Delete favorite by content and user
     */
    void deleteByContentIdAndUserId(Long contentId, Long userId);
}
