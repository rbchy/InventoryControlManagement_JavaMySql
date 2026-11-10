package com.pharma.inventory.dao;

import com.pharma.inventory.config.DatabaseConfig;
import com.pharma.inventory.model.User;
import com.pharma.inventory.util.PasswordUtil;
import java.sql.*;

public class UserDAO {
    private String lastError;
    public String getLastError() { return lastError; }

    /**
     * Verifies credentials and, on success, stamps last_login and returns the User.
     * Returns null on any failure; the message is intentionally generic
     * ("Invalid username or password") so a failed attempt can't be used to
     * probe which usernames exist.
     */
    public User authenticate(String username, String plainPassword) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    lastError = "Invalid username or password.";
                    return null;
                }
                String stored = rs.getString("password");
                boolean active = rs.getBoolean("is_active");
                if (!active) {
                    lastError = "This account has been deactivated.";
                    return null;
                }
                if (!PasswordUtil.verify(plainPassword, stored)) {
                    lastError = "Invalid username or password.";
                    return null;
                }
                User user = mapRow(rs);
                touchLastLogin(conn, user.getUserId());
                return user;
            }
        } catch (SQLException e) {
            lastError = e.getMessage();
            System.out.println("✗ Error: " + lastError);
            return null;
        }
    }

    private void touchLastLogin(Connection conn, int userId) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(
                "UPDATE users SET last_login = NOW() WHERE user_id = ?")) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Registers a new account. Rejects duplicate usernames (relies on the
     * unique constraint + MySQL error 1062 rather than a separate
     * check-then-insert, so it can't race with a concurrent registration).
     */
    public boolean registerUser(User user, String plainPassword) {
        String sql = "INSERT INTO users (username, password, full_name, email, role, department, is_active) "
                + "VALUES (?, ?, ?, ?, ?, ?, TRUE)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, PasswordUtil.hashForStorage(plainPassword));
            pstmt.setString(3, user.getFullName());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getRole());
            pstmt.setString(6, user.getDepartment());
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) user.setUserId(keys.getInt(1));
                }
                user.setActive(true);
            }
            return affected > 0;
        } catch (SQLException e) {
            lastError = e.getErrorCode() == 1062 ? "That username is already taken." : e.getMessage();
            System.out.println("✗ Error: " + lastError);
            return false;
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setRole(rs.getString("role"));
        user.setDepartment(rs.getString("department"));
        user.setActive(rs.getBoolean("is_active"));
        return user;
    }
}
