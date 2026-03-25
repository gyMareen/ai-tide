package com.ai.tide.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * Password Utility Class (BCrypt)
 */
public class PasswordUtil {

    /**
     * Hash password
     */
    public static String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    /**
     * Verify password
     */
    public static boolean verifyPassword(String password, String hashedPassword) {
        return BCrypt.verifyer().verify(password.toCharArray(), hashedPassword).verified;
    }
}
