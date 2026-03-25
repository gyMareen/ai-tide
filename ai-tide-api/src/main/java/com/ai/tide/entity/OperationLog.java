package com.ai.tide.entity;

import com.ai.tide.enums.OperationType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Operation Log Entity
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "operation_log", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_operation_type", columnList = "operation_type"),
    @Index(name = "idx_module", columnList = "module"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
public class OperationLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "operation_type", nullable = false)
    private OperationType operationType;

    @Column(name = "module", length = 50)
    private String module;

    @Column(name = "operation", length = 100)
    private String operation;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "request_method", length = 10)
    private String requestMethod;

    @Column(name = "request_url", length = 500)
    private String requestUrl;

    @Column(name = "request_params", columnDefinition = "TEXT")
    private String requestParams;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "execution_time")
    private Long executionTime;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
}
