package com.ai.tide.repository;

import com.ai.tide.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Tag Repository
 */
@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    /**
     * Find tag by slug
     */
    Optional<Tag> findBySlug(String slug);

    /**
     * Find tag by name
     */
    Optional<Tag> findByName(String name);

    /**
     * Check if slug exists
     */
    boolean existsBySlug(String slug);

    /**
     * Check if name exists
     */
    boolean existsByName(String name);

    /**
     * Find popular tags (by use count)
     */
    List<Tag> findAllByOrderByUseCountDesc();

    /**
     * Search tags by name
     */
    @Query("SELECT t FROM Tag t WHERE t.name LIKE %:keyword% AND t.deleted = false")
    List<Tag> searchByName(@Param("keyword") String keyword);
}
