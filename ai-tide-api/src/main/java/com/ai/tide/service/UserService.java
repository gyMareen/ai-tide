package com.ai.tide.service;

import com.ai.tide.dto.ChangePasswordDTO;
import com.ai.tide.dto.LoginDTO;
import com.ai.tide.dto.RegisterDTO;
import com.ai.tide.dto.UpdateProfileDTO;
import com.ai.tide.entity.User;
import com.ai.tide.enums.AccountStatus;
import com.ai.tide.enums.Role;
import com.ai.tide.exception.AuthException;
import com.ai.tide.exception.UserException;
import com.ai.tide.repository.UserRepository;
import com.ai.tide.utils.JwtUtil;
import com.ai.tide.utils.PasswordUtil;
import com.ai.tide.utils.RedisUtil;
import com.ai.tide.vo.LoginVO;
import com.ai.tide.vo.UserVO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * User Service
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisUtil redisUtil;

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCK_DURATION_MINUTES = 30;
    private static final String TOKEN_PREFIX = "auth:token:";
    private static final String LOCK_PREFIX = "auth:lock:";

    @Value("${jwt.expiration:7200000}")
    private Long jwtExpiration;

    /**
     * Register new user
     */
    @Transactional
    public UserVO register(RegisterDTO registerDTO) {
        // Check if password matches confirmation
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new UserException("密码和确认密码不匹配");
        }

        // Check if username already exists
        if (userRepository.existsByUsername(registerDTO.getUsername())) {
            throw new UserException("用户名已存在");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            throw new UserException("邮箱已被使用");
        }

        // Create new user
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(PasswordUtil.hashPassword(registerDTO.getPassword()));
        user.setEmail(registerDTO.getEmail());
        user.setNickname(registerDTO.getNickname() != null ? registerDTO.getNickname() : registerDTO.getUsername());
        user.setRole(Role.USER);
        user.setStatus(AccountStatus.ACTIVE);
        user.setEmailVerified(false);
        user.setNotificationEnabled(true);

        user = userRepository.save(user);

        return convertToVO(user);
    }

    /**
     * User login
     */
    @Transactional
    public LoginVO login(LoginDTO loginDTO, String ipAddress) {
        // Find user by username or email
        User user = userRepository.findByUsernameOrEmail(loginDTO.getUsername(), loginDTO.getUsername())
                .orElseThrow(() -> new AuthException("用户名或密码错误"));

        // Check if account is locked
        if (user.getStatus() == AccountStatus.LOCKED) {
            if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
                throw new AuthException("账户已被锁定，请" +
                    java.time.Duration.between(LocalDateTime.now(), user.getLockedUntil()).toMinutes() + "分钟后重试");
            } else {
                // Unlock account if lock expired
                user.setStatus(AccountStatus.ACTIVE);
                user.setFailedLoginAttempts(0);
                user.setLockedUntil(null);
                userRepository.save(user);
            }
        }

        // Check if account is disabled or deleted
        if (user.getStatus() == AccountStatus.DISABLED || user.getDeleted()) {
            throw new AuthException("账户已被禁用或删除");
        }

        // Verify password
        if (!PasswordUtil.verifyPassword(loginDTO.getPassword(), user.getPassword())) {
            // Increment failed attempts
            int failedAttempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(failedAttempts);

            if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
                // Lock account
                user.setStatus(AccountStatus.LOCKED);
                user.setLockedUntil(LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES));
            }

            userRepository.save(user);
            throw new AuthException("用户名或密码错误");
        }

        // Reset failed attempts on successful login
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        user.setLastLoginAt(LocalDateTime.now());
        user.setLastLoginIp(ipAddress);
        userRepository.save(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        // Store token in Redis
        String tokenKey = TOKEN_PREFIX + user.getId();
        redisUtil.set(tokenKey, token);
        redisUtil.expire(tokenKey, jwtExpiration, TimeUnit.MILLISECONDS);

        return new LoginVO(token, convertToVO(user));
    }

    /**
     * User logout
     */
    @Transactional
    public void logout(Long userId) {
        String tokenKey = TOKEN_PREFIX + userId;
        redisUtil.delete(tokenKey);
    }

    /**
     * Get user by ID
     */
    public UserVO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException("用户不存在"));
        return convertToVO(user);
    }

    /**
     * Get current user
     */
    public UserVO getCurrentUser(Long userId) {
        return getUserById(userId);
    }

    /**
     * Update user profile
     */
    @Transactional
    public UserVO updateProfile(Long userId, UpdateProfileDTO updateDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("用户不存在"));

        if (updateDTO.getNickname() != null) {
            user.setNickname(updateDTO.getNickname());
        }
        if (updateDTO.getAvatar() != null) {
            user.setAvatar(updateDTO.getAvatar());
        }
        if (updateDTO.getBio() != null) {
            user.setBio(updateDTO.getBio());
        }
        if (updateDTO.getPhone() != null) {
            user.setPhone(updateDTO.getPhone());
        }
        if (updateDTO.getEmail() != null && !user.getEmail().equals(updateDTO.getEmail())) {
            if (userRepository.existsByEmail(updateDTO.getEmail())) {
                throw new UserException("邮箱已被使用");
            }
            user.setEmail(updateDTO.getEmail());
            user.setEmailVerified(false);
        }
        if (updateDTO.getWebsite() != null) {
            user.setWebsite(updateDTO.getWebsite());
        }
        if (updateDTO.getLocation() != null) {
            user.setLocation(updateDTO.getLocation());
        }

        user = userRepository.save(user);

        return convertToVO(user);
    }

    /**
     * Change password
     */
    @Transactional
    public void changePassword(Long userId, ChangePasswordDTO changePasswordDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("用户不存在"));

        // Verify old password
        if (!PasswordUtil.verifyPassword(changePasswordDTO.getOldPassword(), user.getPassword())) {
            throw new AuthException("原密码错误");
        }

        // Check if new password matches confirmation
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            throw new UserException("新密码和确认密码不匹配");
        }

        // Update password
        user.setPassword(PasswordUtil.hashPassword(changePasswordDTO.getNewPassword()));
        userRepository.save(user);

        // Invalidate existing tokens
        String tokenKey = TOKEN_PREFIX + userId;
        redisUtil.delete(tokenKey);
    }

    /**
     * Search users
     */
    public Page<UserVO> searchUsers(String keyword, Pageable pageable) {
        Page<User> users = userRepository.searchUsers(keyword, pageable);
        return users.map(this::convertToVO);
    }

    /**
     * Get users by role
     */
    public Page<UserVO> getUsersByRole(Role role, Pageable pageable) {
        Page<User> users = userRepository.findByRole(role, pageable);
        return users.map(this::convertToVO);
    }

    /**
     * Validate JWT token
     */
    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    /**
     * Convert User to UserVO
     */
    private UserVO convertToVO(User user) {
        return new UserVO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getNickname(),
                user.getAvatar(),
                user.getBio(),
                user.getRole(),
                user.getStatus(),
                user.getGender(),
                user.getBirthday(),
                user.getPhone(),
                user.getWebsite(),
                user.getLocation(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getEmailVerified()
        );
    }
}
