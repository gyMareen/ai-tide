package com.ai.tide.service;

import com.ai.tide.dto.CommentCreateDTO;
import com.ai.tide.entity.Comment;
import com.ai.tide.entity.Content;
import com.ai.tide.entity.User;
import com.ai.tide.exception.ContentException;
import com.ai.tide.repository.CommentRepository;
import com.ai.tide.repository.ContentRepository;
import com.ai.tide.vo.CommentVO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Comment Service
 */
@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ContentRepository contentRepository;

    private static final String[] PROFANE_WORDS = {"badword1", "badword2"};

    /**
     * Get comments by content
     */
    public List<CommentVO> getCommentsByContent(Long contentId) {
        List<Comment> comments = commentRepository.findByContentIdOrderByCreatedAtAsc(contentId);
        return comments.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * Get root comments by content
     */
    public List<CommentVO> getRootCommentsByContent(Long contentId) {
        List<Comment> comments = commentRepository.findByContentIdAndParentIsNullOrderByCreatedAtAsc(contentId);
        return comments.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * Get comment by ID
     */
    public CommentVO getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ContentException("评论不存在"));
        return convertToVO(comment);
    }

    /**
     * Create comment
     */
    @Transactional
    public CommentVO createComment(Long userId, CommentCreateDTO createDTO, String ipAddress, String userAgent) {
        Content content = contentRepository.findById(createDTO.getContentId())
                .orElseThrow(() -> new ContentException("内容不存在"));

        if (!content.getAllowComments()) {
            throw new ContentException("该内容不允许评论");
        }

        Comment comment = new Comment();
        comment.setContent(createDTO.getContent());
        comment.setContentEntity(content);
        comment.setUser(User.builder().id(userId).build());
        comment.setIpAddress(ipAddress);
        comment.setUserAgent(userAgent);
        comment.setIsApproved(true);

        // Check for profanity
        String filteredContent = filterProfanity(createDTO.getContent());
        comment.setContent(filteredContent);

        // Set parent comment
        if (createDTO.getParentId() != null) {
            Comment parent = commentRepository.findById(createDTO.getParentId())
                    .orElseThrow(() -> new ContentException("父评论不存在"));
            comment.setParent(parent);
        }

        comment = commentRepository.save(comment);

        // Update comment count
        content.setCommentCount(content.getCommentCount() + 1);
        contentRepository.save(content);

        return convertToVO(comment);
    }

    /**
     * Delete comment
     */
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ContentException("评论不存在"));

        // Check if user is comment author
        if (!comment.getUser().getId().equals(userId)) {
            throw new ContentException("无权删除此评论");
        }

        // Update comment count
        Content content = comment.getContentEntity();
        if (content != null && content.getCommentCount() > 0) {
            content.setCommentCount(content.getCommentCount() - 1);
            contentRepository.save(content);
        }

        commentRepository.delete(comment);
    }

    /**
     * Filter profanity
     */
    private String filterProfanity(String content) {
        String filtered = content;
        for (String word : PROFANE_WORDS) {
            filtered = filtered.replaceAll("(?i)" + word, "***");
        }
        return filtered;
    }

    /**
     * Convert Comment to CommentVO
     */
    private CommentVO convertToVO(Comment comment) {
        List<CommentVO> children = comment.getChildren().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        String username = comment.getUser() != null ? comment.getUser().getUsername() : null;
        String avatar = comment.getUser() != null ? comment.getUser().getAvatar() : null;

        return new CommentVO(
                comment.getId(),
                comment.getContent(),
                comment.getContentEntity() != null ? comment.getContentEntity().getId() : null,
                comment.getUser() != null ? comment.getUser().getId() : null,
                username,
                avatar,
                comment.getParent() != null ? comment.getParent().getId() : null,
                children,
                comment.getLikesCount(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}
