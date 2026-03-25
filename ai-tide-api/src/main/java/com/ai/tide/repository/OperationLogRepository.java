package com.ai.tide.repository;

import com.ai.tide.entity.OperationLog;
import com.ai.tide.enums.OperationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Operation Log Repository
 */
@Repository
public interface OperationLogRepository extends JpaRepository<OperationLog, Long> {

    /**
     * Find logs by user
     */
    Page<OperationLog> findByUserId(Long userId, Pageable pageable);

    /**
     * Find logs by operation type
     */
    Page<OperationLog> findByOperationType(OperationType operationType, Pageable pageable);

    /**
     * Find logs by module
     */
    Page<OperationLog> findByModule(String module, Pageable pageable);

    /**
     * Find logs by time range
     */
    Page<OperationLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    /**
     * Find logs by user and module
     */
    Page<OperationLog> findByUserIdAndModule(Long userId, String module, Pageable pageable);

    /**
     * Find recent logs
     */
    List<OperationLog> findTop100ByOrderByCreatedAtDesc();
}
