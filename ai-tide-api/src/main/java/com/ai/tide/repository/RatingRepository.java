package com.ai.tide.repository;

import com.ai.tide.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Rating Repository
 */
@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    /**
     * Find rating by content and user
     */
    Optional<Rating> findByContentIdAndUserId(Long contentId, Long userId);

    /**
     * Check if user rated content
     */
    boolean existsByContentIdAndUserId(Long contentId, Long userId);

    /**
     * Count ratings by content
     */
    long countByContentId(Long contentId);

    /**
     * Calculate average rating for content
     */
    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.contentId = :contentId")
    Double calculateAverageRating(@Param("contentId") Long contentId);

    /**
     * Calculate rating sum for content
     */
    @Query("SELECT SUM(r.score) FROM Rating r WHERE r.contentId = :contentId")
    Integer calculateRatingSum(@Param("contentId") Long contentId);
}
