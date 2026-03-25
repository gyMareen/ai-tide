package com.ai.tide.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Comment Entity
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "comment", indexes = {
    @Index(name = "idx_content_id", columnList = "content_id"),
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_parent_id", columnList = "parent_id"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
public class Comment extends BaseEntity {

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private Content contentEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<Comment> children = new ArrayList<>();

    @Column(name = "likes_count", nullable = false)
    private Integer likesCount = 0;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "is_approved", nullable = false)
    private Boolean isApproved = true;
}
