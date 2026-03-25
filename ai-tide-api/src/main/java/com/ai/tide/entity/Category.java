package com.ai.tide.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Category Entity
 * Supports hierarchical structure with parent-child relationships
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "category", indexes = {
    @Index(name = "idx_parent_id", columnList = "parent_id"),
    @Index(name = "idx_name", columnList = "name"),
    @Index(name = "idx_sort_order", columnList = "sort_order")
})
public class Category extends BaseEntity {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "slug", nullable = false, unique = true, length = 100)
    private String slug;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "icon", length = 50)
    private String icon;

    @Column(name = "color", length = 20)
    private String color;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @Column(name = "level", nullable = false)
    private Integer level = 0;

    @Column(name = "path", length = 500)
    private String path;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<Category> children = new ArrayList<>();

    @Column(name = "content_count", nullable = false)
    private Integer contentCount = 0;
}
