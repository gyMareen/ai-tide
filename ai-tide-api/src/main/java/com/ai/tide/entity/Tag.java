package com.ai.tide.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Tag Entity
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tag", indexes = {
    @Index(name = "idx_name", columnList = "name"),
    @Index(name = "idx_use_count", columnList = "use_count")
})
public class Tag extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "slug", nullable = false, unique = true, length = 50)
    private String slug;

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "color", length = 20)
    private String color;

    @Column(name = "use_count", nullable = false)
    private Integer useCount = 0;
}
