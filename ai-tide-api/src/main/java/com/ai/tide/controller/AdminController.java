package com.ai.tide.controller;

import com.ai.tide.entity.Content;
import com.ai.tide.entity.OperationLog;
import com.ai.tide.entity.SystemConfig;
import com.ai.tide.entity.User;
import com.ai.tide.enums.AccountStatus;
import com.ai.tide.enums.ContentStatus;
import com.ai.tide.enums.Role;
import com.ai.tide.repository.ContentRepository;
import com.ai.tide.repository.OperationLogRepository;
import com.ai.tide.repository.SystemConfigRepository;
import com.ai.tide.repository.UserRepository;
import com.ai.tide.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Admin Controller
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private OperationLogRepository operationLogRepository;

    @Autowired
    private SystemConfigRepository systemConfigRepository;

    // User Management

    /**
     * Get all users
     */
    @GetMapping("/users")
    public Result<Page<User>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<User> users = userRepository.findAll(pageable);
        return Result.success(users);
    }

    /**
     * Search users
     */
    @GetMapping("/users/search")
    public Result<Page<User>> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<User> users = userRepository.searchUsers(keyword, pageable);
        return Result.success(users);
    }

    /**
     * Update user role
     */
    @PutMapping("/users/{id}/role")
    public Result<Void> updateUserRole(@PathVariable Long id, @RequestParam Role role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setRole(role);
        userRepository.save(user);
        return Result.success("角色更新成功", null);
    }

    /**
     * Enable/Disable user
     */
    @PutMapping("/users/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestParam AccountStatus status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setStatus(status);
        userRepository.save(user);
        return Result.success("状态更新成功", null);
    }

    /**
     * Get user statistics
     */
    @GetMapping("/users/stats")
    public Result<Map<String, Long>> getUserStats() {
        Map<String, Long> stats = Map.of(
                "total", userRepository.count(),
                "admin", userRepository.countByRole(Role.ADMIN),
                "editor", userRepository.countByRole(Role.EDITOR),
                "user", userRepository.countByRole(Role.USER),
                "active", userRepository.countByStatus(AccountStatus.ACTIVE),
                "locked", userRepository.countByStatus(AccountStatus.LOCKED),
                "disabled", userRepository.countByStatus(AccountStatus.DISABLED)
        );
        return Result.success(stats);
    }

    // Content Management

    /**
     * Get all content
     */
    @GetMapping("/contents")
    public Result<Page<Content>> getAllContents(
            @RequestParam(required = false) ContentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Content> contents;
        if (status != null) {
            contents = contentRepository.findByStatus(status, pageable);
        } else {
            contents = contentRepository.findAll(pageable);
        }
        return Result.success(contents);
    }

    /**
     * Update content status (Admin override)
     */
    @PutMapping("/contents/{id}/status")
    public Result<Void> updateContentStatus(@PathVariable Long id, @RequestParam ContentStatus status) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("内容不存在"));
        content.setStatus(status);
        contentRepository.save(content);
        return Result.success("状态更新成功", null);
    }

    /**
     * Get content statistics
     */
    @GetMapping("/contents/stats")
    public Result<Map<String, Long>> getContentStats() {
        Map<String, Long> stats = Map.of(
                "total", contentRepository.count(),
                "published", contentRepository.countByStatus(ContentStatus.PUBLISHED),
                "draft", contentRepository.countByStatus(ContentStatus.DRAFT),
                "archived", contentRepository.countByStatus(ContentStatus.ARCHIVED)
        );
        return Result.success(stats);
    }

    // Operation Logs

    /**
     * Get operation logs
     */
    @GetMapping("/logs")
    public Result<Page<OperationLog>> getOperationLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<OperationLog> logs = operationLogRepository.findAll(pageable);
        return Result.success(logs);
    }

    /**
     * Get recent operation logs
     */
    @GetMapping("/logs/recent")
    public Result<List<OperationLog>> getRecentLogs() {
        List<OperationLog> logs = operationLogRepository.findTop100ByOrderByCreatedAtDesc();
        return Result.success(logs);
    }

    // System Config

    /**
     * Get all system configs
     */
    @GetMapping("/configs")
    public Result<List<SystemConfig>> getAllConfigs() {
        List<SystemConfig> configs = systemConfigRepository.findAll();
        return Result.success(configs);
    }

    /**
     * Get public system configs
     */
    @GetMapping("/configs/public")
    public Result<List<SystemConfig>> getPublicConfigs() {
        List<SystemConfig> configs = systemConfigRepository.findByIsPublicTrue();
        return Result.success(configs);
    }

    /**
     * Update system config
     */
    @PutMapping("/configs/{key}")
    public Result<Void> updateConfig(@PathVariable String key, @RequestParam String value) {
        SystemConfig config = systemConfigRepository.findByConfigKey(key)
                .orElseThrow(() -> new RuntimeException("配置不存在"));
        config.setConfigValue(value);
        systemConfigRepository.save(config);
        return Result.success("配置更新成功", null);
    }

    // Dashboard Statistics

    /**
     * Get dashboard statistics
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = Map.of(
                "users", Map.of(
                        "total", userRepository.count(),
                        "active", userRepository.countByStatus(AccountStatus.ACTIVE)
                ),
                "contents", Map.of(
                        "total", contentRepository.count(),
                        "published", contentRepository.countByStatus(ContentStatus.PUBLISHED),
                        "draft", contentRepository.countByStatus(ContentStatus.DRAFT)
                ),
                "recentLogs", operationLogRepository.findTop10ByOrderByCreatedAtDesc()
        );
        return Result.success(stats);
    }
}
