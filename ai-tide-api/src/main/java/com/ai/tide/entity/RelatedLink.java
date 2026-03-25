package com.ai.tide.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Related Link Entity
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "related_link", indexes = {
    @Index(name = "idx_content_id", columnList = "content_id"),
    @Index(name = "idx_sort_order", columnList = "sort_order")
})
public class RelatedLink extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "url", nullable = false, length = 500)
    private String url;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;
}
