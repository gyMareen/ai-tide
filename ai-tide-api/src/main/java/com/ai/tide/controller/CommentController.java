package com.ai.tide.controller;

import com.ai.tide.dto.CommentCreateDTO;
import com.ai.tide.security.CurrentUserService;
import com.ai.tide.service.CommentService;
import com.ai.tide.vo.CommentVO;
import com.ai.tide.vo.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Comment Controller
 */
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * Get comments by content
     */
    @GetMapping("/content/{contentId}")
    public Result<List<CommentVO>> getCommentsByContent(@PathVariable Long contentId) {
        List<CommentVO> comments = commentService.getCommentsByContent(contentId);
        return Result.success(comments);
    }

    /**
     * Get root comments by content
     */
    @GetMapping("/content/{contentId}/root")
    public Result<List<CommentVO>> getRootCommentsByContent(@PathVariable Long contentId) {
        List<CommentVO> comments = commentService.getRootCommentsByContent(contentId);
        return Result.success(comments);
    }

    /**
     * Get comment by ID
     */
    @GetMapping("/{id}")
    public Result<CommentVO> getCommentById(@PathVariable Long id) {
        CommentVO commentVO = commentService.getCommentById(id);
        return Result.success(commentVO);
    }

    /**
     * Create comment
     */
    @PostMapping
    public Result<CommentVO> createComment(
            @Valid @RequestBody CommentCreateDTO createDTO,
            HttpServletRequest request) {
        Long userId = CurrentUserService.getCurrentUserId();
        String ipAddress = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        CommentVO commentVO = commentService.createComment(userId, createDTO, ipAddress, userAgent);
        return Result.success("评论成功", commentVO);
    }

    /**
     * Delete comment
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteComment(@PathVariable Long id) {
        Long userId = CurrentUserService.getCurrentUserId();
        commentService.deleteComment(id, userId);
        return Result.success("删除成功", null);
    }

    /**
     * Get client IP address
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
