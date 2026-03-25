package com.ai.tide.service;

import com.ai.tide.dto.RatingDTO;
import com.ai.tide.entity.Content;
import com.ai.tide.entity.Favorite;
import com.ai.tide.entity.Like;
import com.ai.tide.entity.Rating;
import com.ai.tide.entity.User;
import com.ai.tide.exception.ContentException;
import com.ai.tide.repository.ContentRepository;
import com.ai.tide.repository.FavoriteRepository;
import com.ai.tide.repository.LikeRepository;
import com.ai.tide.repository.RatingRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Interaction Service
 */
@Service
public class InteractionService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private ContentRepository contentRepository;

    /**
     * Like content
     */
    @Transactional
    public void likeContent(Long userId, Long contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ContentException("内容不存在"));

        if (likeRepository.existsByContentIdAndUserId(contentId, userId)) {
            throw new ContentException("已经点赞过");
        }

        Like like = new Like();
        like.setContent(content);
        like.setUser(User.builder().id(userId).build());
        likeRepository.save(like);

        // Update like count
        content.setLikeCount(content.getLikeCount() + 1);
        contentRepository.save(content);
    }

    /**
     * Unlike content
     */
    @Transactional
    public void unlikeContent(Long userId, Long contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ContentException("内容不存在"));

        if (!likeRepository.existsByContentIdAndUserId(contentId, userId)) {
            throw new ContentException("未点赞过");
        }

        likeRepository.deleteByContentIdAndUserId(contentId, userId);

        // Update like count
        if (content.getLikeCount() > 0) {
            content.setLikeCount(content.getLikeCount() - 1);
            contentRepository.save(content);
        }
    }

    /**
     * Check if user liked content
     */
    public boolean isLiked(Long userId, Long contentId) {
        return likeRepository.existsByContentIdAndUserId(contentId, userId);
    }

    /**
     * Favorite content
     */
    @Transactional
    public void favoriteContent(Long userId, Long contentId, String notes) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ContentException("内容不存在"));

        if (favoriteRepository.existsByContentIdAndUserId(contentId, userId)) {
            throw new ContentException("已经收藏过");
        }

        Favorite favorite = new Favorite();
        favorite.setContent(content);
        favorite.setUser(User.builder().id(userId).build());
        favorite.setNotes(notes);
        favoriteRepository.save(favorite);

        // Update favorite count
        content.setFavoriteCount(content.getFavoriteCount() + 1);
        contentRepository.save(content);
    }

    /**
     * Unfavorite content
     */
    @Transactional
    public void unfavoriteContent(Long userId, Long contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ContentException("内容不存在"));

        if (!favoriteRepository.existsByContentIdAndUserId(contentId, userId)) {
            throw new ContentException("未收藏过");
        }

        favoriteRepository.deleteByContentIdAndUserId(contentId, userId);

        // Update favorite count
        if (content.getFavoriteCount() > 0) {
            content.setFavoriteCount(content.getFavoriteCount() - 1);
            contentRepository.save(content);
        }
    }

    /**
     * Check if user favorited content
     */
    public boolean isFavorited(Long userId, Long contentId) {
        return favoriteRepository.existsByContentIdAndUserId(contentId, userId);
    }

    /**
     * Get user's favorites
     */
    public Page<Content> getUserFavorites(Long userId, Pageable pageable) {
        Page<Favorite> favorites = favoriteRepository.findByUserId(userId, pageable);
        return favorites.map(Favorite::getContent);
    }

    /**
     * Rate content
     */
    @Transactional
    public void rateContent(Long userId, RatingDTO ratingDTO) {
        Content content = contentRepository.findById(ratingDTO.getContentId())
                .orElseThrow(() -> new ContentException("内容不存在"));

        Rating existingRating = ratingRepository.findByContentIdAndUserId(ratingDTO.getContentId(), userId);

        if (existingRating != null) {
            // Update existing rating
            existingRating.setScore(ratingDTO.getScore());
            existingRating.setReview(ratingDTO.getReview());
            ratingRepository.save(existingRating);
        } else {
            // Create new rating
            Rating rating = new Rating();
            rating.setScore(ratingDTO.getScore());
            rating.setReview(ratingDTO.getReview());
            rating.setContent(content);
            rating.setUser(User.builder().id(userId).build());
            ratingRepository.save(rating);
        }

        // Update rating statistics
        long ratingCount = ratingRepository.countByContentId(content.getId());
        Integer ratingSum = ratingRepository.calculateRatingSum(content.getId());
        Double averageRating = ratingRepository.calculateAverageRating(content.getId());

        content.setRatingCount((int) ratingCount);
        content.setRatingSum(ratingSum != null ? ratingSum : 0);
        content.setAverageRating(averageRating != null ? averageRating : 0.0);
        contentRepository.save(content);
    }

    /**
     * Get user's rating for content
     */
    public Rating getUserRating(Long userId, Long contentId) {
        return ratingRepository.findByContentIdAndUserId(contentId, userId).orElse(null);
    }

    /**
     * Delete rating
     */
    @Transactional
    public void deleteRating(Long userId, Long contentId) {
        Rating rating = ratingRepository.findByContentIdAndUserId(contentId, userId);
        if (rating != null) {
            ratingRepository.delete(rating);

            // Update rating statistics
            Content content = contentRepository.findById(contentId).orElse(null);
            if (content != null) {
                long ratingCount = ratingRepository.countByContentId(content.getId());
                Integer ratingSum = ratingRepository.calculateRatingSum(content.getId());
                Double averageRating = ratingRepository.calculateAverageRating(content.getId());

                content.setRatingCount((int) ratingCount);
                content.setRatingSum(ratingSum != null ? ratingSum : 0);
                content.setAverageRating(averageRating != null ? averageRating : 0.0);
                contentRepository.save(content);
            }
        }
    }
}
