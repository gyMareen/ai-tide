package com.ai.tide.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Favorite Entity
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "favorite", indexes = {
    @Index(name = "idx_content_id", columnList = "content_id"),
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_unique_favorite", columnList = "content_id,user_id", unique = true)
})
public class Favorite extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "notes", length = 500)
    private String notes;
}
