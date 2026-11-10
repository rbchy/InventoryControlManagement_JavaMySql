package com.pharma.inventory.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Salted SHA-256 password hashing. No external dependency (no bcrypt jar on
 * this project's classpath), but this is still a meaningful step up from the
 * plaintext passwords the `users` table previously stored.
 *
 * Stored format: "<16-hex-char salt>:<64-hex-char SHA-256 hash>"
 * hash = SHA256(salt + plainPassword), UTF-8 encoded.
 */
public final class PasswordUtil {
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {}

    /** Hashes a brand-new password with a fresh random salt, ready to store in the `password` column. */
    public static String hashForStorage(String plainPassword) {
        String salt = generateSaltHex();
        return salt + ":" + sha256Hex(salt + plainPassword);
    }

    /** Checks a login attempt's plaintext password against the "salt:hash" value stored in the DB. */
    public static boolean verify(String plainPassword, String stored) {
        if (stored == null || !stored.contains(":")) return false;
        String[] parts = stored.split(":", 2);
        if (parts.length != 2) return false;
        String salt = parts[0];
        String expectedHash = parts[1];
        String actualHash = sha256Hex(salt + plainPassword);
        return constantTimeEquals(expectedHash, actualHash);
    }

    private static String generateSaltHex() {
        byte[] saltBytes = new byte[8];
        RANDOM.nextBytes(saltBytes);
        return toHex(saltBytes);
    }

    private static String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes("UTF-8"));
            return toHex(hashBytes);
        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /** Avoids short-circuit timing leaks when comparing hashes. */
    private static boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) return false;
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}
