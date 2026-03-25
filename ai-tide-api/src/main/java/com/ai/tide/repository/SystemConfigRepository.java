package com.ai.tide.repository;

import com.ai.tide.entity.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * System Config Repository
 */
@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long> {

    /**
     * Find config by key
     */
    Optional<SystemConfig> findByConfigKey(String configKey);

    /**
     * Find public configs
     */
    List<SystemConfig> findByIsPublicTrue();

    /**
     * Find configs by category
     */
    List<SystemConfig> findByCategory(String category);
}
