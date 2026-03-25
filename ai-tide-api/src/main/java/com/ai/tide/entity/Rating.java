package com.ai.tide.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Rating Entity
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "rating", indexes = {
    @Index(name = "idx_content_id", columnList = "content_id"),
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_unique_rating", columnList = "content_id,user_id", unique = true)
})
public class Rating extends BaseEntity {

    @Column(name = "score", nullable = false)
    private Integer score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "review", columnDefinition = "TEXT")
    private String review;
