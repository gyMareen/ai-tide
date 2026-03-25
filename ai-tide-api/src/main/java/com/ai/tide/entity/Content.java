package com.ai.tide.entity;

import com.ai.tide.enums.ContentType;
import com.ai.tide.enums.ContentStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Content Entity
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "content", indexes = {
    @Index(name = "idx_title", columnList = "title"),
    @Index(name = "idx_author_id", columnList = "author_id"),
    @Index(name = "idx_category_id", columnList = "category_id"),
    @Index(name = "idx_type", columnList = "type"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_published_at", columnList = "published_at"),
    @Index(name = "idx_view_count", columnList = "view_count")
})
public class Content extends BaseEntity {

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "slug", nullable = false, unique = true, length = 200)
    private String slug;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "cover_image", length = 255)
    private String coverImage;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "type", nullable = false)
    private ContentType type;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status", nullable = false)
    private ContentStatus status = ContentStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "content_tag",
        joinColumns = @JoinColumn(name = "content_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags = new ArrayList<>();

    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;

    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;

    @Column(name = "comment_count", nullable = false)
    private Integer commentCount = 0;

    @Column(name = "favorite_count", nullable = false)
    private Integer favoriteCount = 0;

    @Column(name = "rating_sum")
    private Integer ratingSum = 0;

    @Column(name = "rating_count", nullable = false)
    private Integer ratingCount = 0;

    @Column(name = "average_rating")
    private Double averageRating = 0.0;

    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured = false;

    @Column(name = "is_pinned", nullable = false)
    private Boolean isPinned = false;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "allow_comments", nullable = false)
    private Boolean allowComments = true;

    @Column(name = "meta_title", length = 200)
    private String metaTitle;

    @Column(name = "meta_description", length = 500)
    private String metaDescription;

    @Column(name = "meta_keywords", length = 255)
    private String metaKeywords;

    @PrePersist
    protected void onCreate() {
        if (slug == null) {
            slug = title.toLowerCase().replaceAll("[^a-z0-9]+", "-");
        }
    }
}
