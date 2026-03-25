package com.ai.tide.security;

import lombok.Data;

/**
 * Current User Service
 * Thread-local storage for current user context
 */
@Data
public class CurrentUserService {

    private static final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();
    private static final ThreadLocal<String> usernameHolder = new ThreadLocal<>();

    public static void setCurrentUserContext(Long userId, String username) {
        userIdHolder.set(userId);
        usernameHolder.set(username);
    }

    public static Long getCurrentUserId() {
        return userIdHolder.get();
    }

    public static String getCurrentUsername() {
        return usernameHolder.get();
    }

    public static void clear() {
        userIdHolder.remove();
        usernameHolder.remove();
    }
}
