package com.ai.tide.repository;

import com.ai.tide.entity.RelatedLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Related Link Repository
 */
@Repository
public interface RelatedLinkRepository extends JpaRepository<RelatedLink, Long> {

    /**
     * Find links by content
     */
    List<RelatedLink> findByContentIdOrderBySortOrderAsc(Long contentId);
}
