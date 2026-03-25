package com.ai.tide.repository;

import com.ai.tide.entity.User;
import com.ai.tide.enums.AccountStatus;
import com.ai.tide.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * User Repository
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username or email
     */
    Optional<User> findByUsernameOrEmail(String username, String email);

    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Find users by role
     */
    List<User> findByRole(Role role);

    /**
     * Find users by role with pagination
     */
    Page<User> findByRole(Role role, Pageable pageable);

    /**
     * Find users by status
     */
    List<User> findByStatus(AccountStatus status);

    /**
     * Search users by username or email or nickname
     */
    @Query("SELECT u FROM User u WHERE (u.username LIKE %:keyword% OR u.email LIKE %:keyword% OR u.nickname LIKE %:keyword%) AND u.deleted = false")
    Page<User> searchUsers(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Find users created after date
     */
    List<User> findByCreatedAtAfter(LocalDateTime createdAt);

    /**
     * Find locked users
     */
    List<User> findByStatusAndLockedUntilAfter(AccountStatus status, LocalDateTime lockedUntil);

    /**
     * Count users by role
     */
    long countByRole(Role role);

    /**
     * Count users by status
     */
    long countByStatus(AccountStatus status);
}
