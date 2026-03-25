package com.ai.tide.service;

import com.ai.tide.entity.Tag;
import com.ai.tide.exception.ContentException;
import com.ai.tide.repository.TagRepository;
import com.ai.tide.vo.TagVO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Tag Service
 */
@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    /**
     * Get all tags
     */
    public List<TagVO> getAllTags() {
        List<Tag> tags = tagRepository.findAll();
        return tags.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * Get popular tags
     */
    public List<TagVO> getPopularTags() {
        List<Tag> tags = tagRepository.findAllByOrderByUseCountDesc();
        return tags.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * Get tag by ID
     */
    public TagVO getTagById(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ContentException("标签不存在"));
        return convertToVO(tag);
    }

    /**
     * Get tag by slug
     */
    public TagVO getTagBySlug(String slug) {
        Tag tag = tagRepository.findBySlug(slug)
                .orElseThrow(() -> new ContentException("标签不存在"));
        return convertToVO(tag);
    }

    /**
     * Create tag
     */
    @Transactional
    public TagVO createTag(String name, String description, String color) {
        if (tagRepository.existsByName(name)) {
            throw new ContentException("标签名称已存在");
        }

        Tag tag = new Tag();
        tag.setName(name);
        tag.setSlug(name.toLowerCase().replaceAll("[^a-z0-9]+", "-"));
        tag.setDescription(description);
        tag.setColor(color);
        tag.setUseCount(0);

        tag = tagRepository.save(tag);

        return convertToVO(tag);
    }

    /**
     * Update tag
     */
    @Transactional
    public TagVO updateTag(Long id, String name, String description, String color) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ContentException("标签不存在"));

        if (name != null && !name.equals(tag.getName())) {
            if (tagRepository.existsByName(name)) {
                throw new ContentException("标签名称已存在");
            }
            tag.setName(name);
            tag.setSlug(name.toLowerCase().replaceAll("[^a-z0-9]+", "-"));
        }

        if (description != null) {
            tag.setDescription(description);
        }

        if (color != null) {
            tag.setColor(color);
        }

        tag = tagRepository.save(tag);

        return convertToVO(tag);
    }

    /**
     * Delete tag
     */
    @Transactional
    public void deleteTag(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ContentException("标签不存在"));
        tagRepository.delete(tag);
    }

    /**
     * Increment tag use count
     */
    @Transactional
    public void incrementUseCount(Long tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ContentException("标签不存在"));
        tag.setUseCount(tag.getUseCount() + 1);
        tagRepository.save(tag);
    }

    /**
     * Decrement tag use count
     */
    @Transactional
    public void decrementUseCount(Long tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ContentException("标签不存在"));
        if (tag.getUseCount() > 0) {
            tag.setUseCount(tag.getUseCount() - 1);
            tagRepository.save(tag);
        }
    }

    /**
     * Convert Tag to TagVO
     */
    private TagVO convertToVO(Tag tag) {
        return new TagVO(
                tag.getId(),
                tag.getName(),
                tag.getSlug(),
                tag.getDescription(),
                tag.getColor(),
                tag.getUseCount()
        );
    }
}
