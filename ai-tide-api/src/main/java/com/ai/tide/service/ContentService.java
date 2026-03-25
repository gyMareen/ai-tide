package com.ai.tide.service;

import com.ai.tide.dto.ContentCreateDTO;
import com.ai.tide.dto.ContentUpdateDTO;
import com.ai.tide.entity.Content;
import com.ai.tide.entity.Tag;
import com.ai.tide.enums.ContentStatus;
import com.ai.tide.enums.ContentType;
import com.ai.tide.exception.ContentException;
import com.ai.tide.repository.CategoryRepository;
import com.ai.tide.repository.ContentRepository;
import com.ai.tide.repository.TagRepository;
import com.ai.tide.vo.ContentVO;
import com.ai.tide.vo.PageVO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Content Service
 */
@Service
public class ContentService {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;

    /**
     * Get content by ID
     */
    public ContentVO getContentById(Long id) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new ContentException("内容不存在"));
        return convertToVO(content);
    }

    /**
     * Get content by slug
     */
    public ContentVO getContentBySlug(String slug) {
        Content content = contentRepository.findBySlug(slug)
                .orElseThrow(() -> new ContentException("内容不存在"));
        return convertToVO(content);
    }

    /**
     * Increment view count
     */
    @Transactional
    public void incrementViewCount(Long contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ContentException("内容不存在"));
        content.setViewCount(content.getViewCount() + 1);
        contentRepository.save(content);
    }

    /**
     * Create content
     */
    @Transactional
    public ContentVO createContent(Long authorId, ContentCreateDTO createDTO) {
        Content content = new Content();
        content.setTitle(createDTO.getTitle());
        content.setDescription(createDTO.getDescription());
        content.setContent(createDTO.getContent());
        content.setCoverImage(createDTO.getCoverImage());
        content.setType(createDTO.getType());
        content.setStatus(ContentStatus.DRAFT);
        content.setPublishedAt(null);

        // Set author
        content.setAuthor(com.ai.tide.entity.User.builder().id(authorId).build());

        // Set category
        if (createDTO.getCategoryId() != null) {
            categoryRepository.findById(createDTO.getCategoryId())
                    .ifPresent(content::setCategory);
        }

        // Set tags
        if (StringUtils.hasText(createDTO.getTags())) {
            Set<String> tagNames = Set.of(createDTO.getTags().split(","));
            List<Tag> tags = tagNames.stream()
                    .map(name -> {
                        Tag tag = tagRepository.findByName(name.trim());
                        if (tag == null) {
                            tag = new Tag();
                            tag.setName(name.trim());
                            tag.setSlug(name.trim().toLowerCase().replaceAll("[^a-z0-9]+", "-"));
                            tag.setUseCount(0);
                            tag = tagRepository.save(tag);
                        }
                        return tag;
                    })
                    .collect(Collectors.toList());
            content.setTags(tags);
        }

        // Set SEO fields
        content.setMetaTitle(createDTO.getMetaTitle());
        content.setMetaDescription(createDTO.getMetaDescription());
        content.setMetaKeywords(createDTO.getMetaKeywords());

        content = contentRepository.save(content);

        return convertToVO(content);
    }

    /**
     * Update content
     */
    @Transactional
    public ContentVO updateContent(Long id, Long authorId, ContentUpdateDTO updateDTO) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new ContentException("内容不存在"));

        // Check if user is the author or admin
        if (!content.getAuthor().getId().equals(authorId)) {
            throw new ContentException("无权修改此内容");
        }

        if (updateDTO.getTitle() != null) {
            content.setTitle(updateDTO.getTitle());
        }
        if (updateDTO.getDescription() != null) {
            content.setDescription(updateDTO.getDescription());
        }
        if (updateDTO.getContent() != null) {
            content.setContent(updateDTO.getContent());
        }
        if (updateDTO.getCoverImage() != null) {
            content.setCoverImage(updateDTO.getCoverImage());
        }
        if (updateDTO.getType() != null) {
            content.setType(updateDTO.getType());
        }
        if (updateDTO.getStatus() != null) {
            content.setStatus(updateDTO.getStatus());
            if (updateDTO.getStatus() == ContentStatus.PUBLISHED && content.getPublishedAt() == null) {
                content.setPublishedAt(LocalDateTime.now());
            }
        }
        if (updateDTO.getCategoryId() != null) {
            categoryRepository.findById(updateDTO.getCategoryId())
                    .ifPresent(content::setCategory);
        }
        if (updateDTO.getIsFeatured() != null) {
            content.setIsFeatured(updateDTO.getIsFeatured());
        }
        if (updateDTO.getIsPinned() != null) {
            content.setIsPinned(updateDTO.getIsPinned());
        }
        if (updateDTO.getMetaTitle() != null) {
            content.setMetaTitle(updateDTO.getMetaTitle());
        }
        if (updateDTO.getMetaDescription() != null) {
            content.setMetaDescription(updateDTO.getMetaDescription());
        }
        if (updateDTO.getMetaKeywords() != null) {
            content.setMetaKeywords(updateDTO.getMetaKeywords());
        }

        content = contentRepository.save(content);

        return convertToVO(content);
    }

    /**
     * Delete content (soft delete)
     */
    @Transactional
    public void deleteContent(Long id, Long authorId) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new ContentException("内容不存在"));

        // Check if user is the author
        if (!content.getAuthor().getId().equals(authorId)) {
            throw new ContentException("无权删除此内容");
        }

        content.setDeleted(true);
        contentRepository.save(content);
    }

    /**
     * Publish content
     */
    @Transactional
    public ContentVO publishContent(Long id, Long authorId) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new ContentException("内容不存在"));

        // Check if user is the author
        if (!content.getAuthor().getId().equals(authorId)) {
            throw new ContentException("无权发布此内容");
        }

        content.setStatus(ContentStatus.PUBLISHED);
        if (content.getPublishedAt() == null) {
            content.setPublishedAt(LocalDateTime.now());
        }

        content = contentRepository.save(content);

        return convertToVO(content);
    }

    /**
     * Archive content
     */
    @Transactional
    public ContentVO archiveContent(Long id, Long authorId) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new ContentException("内容不存在"));

        // Check if user is the author
        if (!content.getAuthor().getId().equals(authorId)) {
            throw new ContentException("无权归档此内容");
        }

        content.setStatus(ContentStatus.ARCHIVED);
        content = contentRepository.save(content);

        return convertToVO(content);
    }

    /**
     * Get published content with pagination
     */
    public PageVO<ContentVO> getPublishedContent(Pageable pageable) {
        Page<Content> contents = contentRepository.findPublishedContent(pageable);
        return convertToPageVO(contents);
    }

    /**
     * Get featured content
     */
    public PageVO<ContentVO> getFeaturedContent(Pageable pageable) {
        Page<Content> contents = contentRepository.findFeaturedContent(pageable);
        return convertToPageVO(contents);
    }

    /**
     * Search content
     */
    public PageVO<ContentVO> searchContent(String keyword, Pageable pageable) {
        Page<Content> contents = contentRepository.searchContent(keyword, pageable);
        return convertToPageVO(contents);
    }

    /**
     * Search content with filters
     */
    public PageVO<ContentVO> searchContentWithFilters(String keyword, ContentType type, Long categoryId, Pageable pageable) {
        Page<Content> contents = contentRepository.searchContentWithFilters(keyword, type, categoryId, pageable);
        return convertToPageVO(contents);
    }

    /**
     * Get content by author
     */
    public PageVO<ContentVO> getContentByAuthor(Long authorId, Pageable pageable) {
        Page<Content> contents = contentRepository.findByAuthorId(authorId, pageable);
        return convertToPageVO(contents);
    }

    /**
     * Get content by category
     */
    public PageVO<ContentVO> getContentByCategory(Long categoryId, Pageable pageable) {
        Page<Content> contents = contentRepository.findByCategoryId(categoryId, pageable);
        return convertToPageVO(contents);
    }

    /**
     * Get content by tag
     */
    public PageVO<ContentVO> getContentByTag(Long tagId, Pageable pageable) {
        Page<Content> contents = contentRepository.findByTagId(tagId, pageable);
        return convertToPageVO(contents);
    }

    /**
     * Get popular content
     */
    public PageVO<ContentVO> getPopularContent(Pageable pageable) {
        Page<Content> contents = contentRepository.findPopularContent(pageable);
        return convertToPageVO(contents);
    }

    /**
     * Get trending content
     */
    public PageVO<ContentVO> getTrendingContent(Pageable pageable) {
        LocalDateTime since = LocalDateTime.now().minusDays(7);
        Page<Content> contents = contentRepository.findTrendingContent(since, pageable);
        return convertToPageVO(contents);
    }

    /**
     * Convert Content to ContentVO
     */
    private ContentVO convertToVO(Content content) {
        List<com.ai.tide.vo.TagVO> tagVOs = content.getTags().stream()
                .map(tag -> new com.ai.tide.vo.TagVO(
                        tag.getId(),
                        tag.getName(),
                        tag.getSlug(),
                        tag.getDescription(),
                        tag.getColor(),
                        tag.getUseCount()
                ))
                .collect(Collectors.toList());

        String categoryName = content.getCategory() != null ? content.getCategory().getName() : null;
        Long categoryId = content.getCategory() != null ? content.getCategory().getId() : null;
        String authorName = content.getAuthor() != null ? content.getAuthor().getNickname() : null;
        String authorAvatar = content.getAuthor() != null ? content.getAuthor().getAvatar() : null;

        return new ContentVO(
                content.getId(),
                content.getTitle(),
                content.getSlug(),
                content.getDescription(),
                content.getContent(),
                content.getCoverImage(),
                content.getType(),
                content.getStatus(),
                content.getAuthor() != null ? content.getAuthor().getId() : null,
                authorName,
                authorAvatar,
                categoryId,
                categoryName,
                tagVOs,
                content.getViewCount(),
                content.getLikeCount(),
                content.getCommentCount(),
                content.getFavoriteCount(),
                content.getAverageRating(),
                content.getRatingCount(),
                content.getIsFeatured(),
                content.getIsPinned(),
                content.getPublishedAt(),
                content.getAllowComments(),
                content.getMetaTitle(),
                content.getMetaDescription(),
                content.getMetaKeywords(),
                content.getCreatedAt(),
                content.getUpdatedAt()
        );
    }

    /**
     * Convert Page to PageVO
     */
    private PageVO<ContentVO> convertToPageVO(Page<Content> page) {
        List<ContentVO> contentVOs = page.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return new PageVO<>(
                contentVOs,
                page.getTotalElements(),
                (long) page.getTotalPages(),
                (long) page.getNumber(),
                (long) page.getSize()
        );
    }
}
