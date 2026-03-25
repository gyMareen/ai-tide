package com.ai.tide.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * System Config Entity
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "system_config", indexes = {
    @Index(name = "idx_key", columnList = "config_key", unique = true)
})
public class SystemConfig extends BaseEntity {

    @Column(name = "config_key", nullable = false, unique = true, length = 100)
    private String configKey;

    @Column(name = "config_value", columnDefinition = "TEXT")
    private String configValue;

    @Column(name = "value_type", length = 20)
    private String valueType = "string";

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = false;
}
