package com.ai.tide.controller;

import com.ai.tide.dto.RatingDTO;
import com.ai.tide.entity.Content;
import com.ai.tide.entity.Rating;
import com.ai.tide.security.CurrentUserService;
import com.ai.tide.service.InteractionService;
import com.ai.tide.vo.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

/**
 * Interaction Controller
 */
@RestController
@RequestMapping("/api/interactions")
public class InteractionController {

    @Autowired
    private InteractionService interactionService;

    /**
     * Like content
     */
    @PostMapping("/like/{contentId}")
    public Result<Void> likeContent(@PathVariable Long contentId) {
        Long userId = CurrentUserService.getCurrentUserId();
        interactionService.likeContent(userId, contentId);
        return Result.success("点赞成功", null);
    }

    /**
     * Unlike content
     */
    @PostMapping("/unlike/{contentId}")
    public Result<Void> unlikeContent(@PathVariable Long contentId) {
        Long userId = CurrentUserService.getCurrentUserId();
        interactionService.unlikeContent(userId, contentId);
        return Result.success("取消点赞成功", null);
    }

    /**
     * Check if user liked content
     */
    @GetMapping("/liked/{contentId}")
    public Result<Boolean> isLiked(@PathVariable Long contentId) {
        Long userId = CurrentUserService.getCurrentUserId();
        boolean liked = interactionService.isLiked(userId, contentId);
        return Result.success(liked);
    }

    /**
     * Favorite content
     */
    @PostMapping("/favorite/{contentId}")
    public Result<Void> favoriteContent(
            @PathVariable Long contentId,
            @RequestParam(required = false) String notes) {
        Long userId = CurrentUserService.getCurrentUserId();
        interactionService.favoriteContent(userId, contentId, notes);
        return Result.success("收藏成功", null);
    }

    /**
     * Unfavorite content
     */
    @PostMapping("/unfavorite/{contentId}")
    public Result<Void> unfavoriteContent(@PathVariable Long contentId) {
        Long userId = CurrentUserService.getCurrentUserId();
        interactionService.unfavoriteContent(userId, contentId);
        return Result.success("取消收藏成功", null);
    }

    /**
     * Check if user favorited content
     */
    @GetMapping("/favorited/{contentId}")
    public Result<Boolean> isFavorited(@PathVariable Long contentId) {
        Long userId = CurrentUserService.getCurrentUserId();
        boolean favorited = interactionService.isFavorited(userId, contentId);
        return Result.success(favorited);
    }

    /**
     * Get user's favorites
     */
    @GetMapping("/favorites")
    public Result<Page<Content>> getUserFavorites(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = CurrentUserService.getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Content> favorites = interactionService.getUserFavorites(userId, pageable);
        return Result.success(favorites);
    }

    /**
     * Rate content
     */
    @PostMapping("/rate")
    public Result<Void> rateContent(@Valid @RequestBody RatingDTO ratingDTO) {
        Long userId = CurrentUserService.getCurrentUserId();
        interactionService.rateContent(userId, ratingDTO);
        return Result.success("评分成功", null);
    }

    /**
     * Get user's rating for content
     */
    @GetMapping("/rating/{contentId}")
    public Result<Rating> getUserRating(@PathVariable Long contentId) {
        Long userId = CurrentUserService.getCurrentUserId();
        Rating rating = interactionService.getUserRating(userId, contentId);
        return Result.success(rating);
    }

    /**
     * Delete rating
     */
    @DeleteMapping("/rating/{contentId}")
    public Result<Void> deleteRating(@PathVariable Long contentId) {
        Long userId = CurrentUserService.getCurrentUserId();
        interactionService.deleteRating(userId, contentId);
        return Result.success("删除评分成功", null);
    }
}
