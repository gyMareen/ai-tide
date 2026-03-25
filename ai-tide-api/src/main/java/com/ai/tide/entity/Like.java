package com.ai.tide.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * Like Entity
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "like_record", indexes = {
    @Index(name = "idx_content_id", columnList = "content_id"),
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_unique_like", columnList = "content_id,user_id", unique = true)
})
public class Like extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;
}
