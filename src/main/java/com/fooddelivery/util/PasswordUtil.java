package com.fooddelivery.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

/**
 * Utility class for password hashing and token generation.
 * Uses SHA-256 with a random salt for password storage.
 */
public class PasswordUtil {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private PasswordUtil() { /* utility class */ }

    /**
     * Hashes a plaintext password with a randomly generated salt.
     * Format: salt:hash
     */
    public static String hashPassword(String plaintext) {
        byte[] saltBytes = new byte[16];
        SECURE_RANDOM.nextBytes(saltBytes);
        String salt = Base64.getEncoder().encodeToString(saltBytes);
        String hash = sha256(salt + plaintext);
        return salt + ":" + hash;
    }

    /**
     * Verifies a plaintext password against a stored hash (salt:hash format).
     */
    public static boolean verifyPassword(String plaintext, String storedHash) {
        if (storedHash == null || !storedHash.contains(":")) {
            return false;
        }
        String[] parts = storedHash.split(":", 2);
        String salt = parts[0];
        String expectedHash = parts[1];
        String actualHash = sha256(salt + plaintext);
        return expectedHash.equals(actualHash);
    }

    /**
     * Generates a secure random reset token (UUID-based).
     */
    public static String generateResetToken() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

    /**
     * Generates a random OTP (6 digits).
     */
    public static String generateOtp() {
        int otp = 100000 + SECURE_RANDOM.nextInt(900000);
        return String.valueOf(otp);
    }

    private static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
