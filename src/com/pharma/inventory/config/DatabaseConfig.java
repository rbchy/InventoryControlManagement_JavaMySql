package com.pharma.inventory.config;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Centralized DB configuration.
 *
 * Credentials are NEVER hardcoded here. They are resolved, in order, from:
 *   1. Environment variables:  DB_URL, DB_USER, DB_PASSWORD
 *   2. System properties:     -Ddb.url=... -Ddb.user=... -Ddb.password=...
 *   3. Local fallback defaults (root@localhost, empty password) — dev only.
 *
 * Set the environment variables before running, e.g.:
 *   export DB_URL="jdbc:mysql://localhost:3306/pharma_inventory"
 *   export DB_USER="pharma_app"
 *   export DB_PASSWORD="your-password"
 */
public class DatabaseConfig {
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";

    private static final String DB_URL = resolve("DB_URL", "db.url",
            "jdbc:mysql://localhost:3306/pharma_inventory?useSSL=false&allowPublicKeyRetrieval=true");
    private static final String DB_USER = resolve("DB_USER", "db.user", "root");
    private static final String DB_PASSWORD = resolve("DB_PASSWORD", "db.password", "");

    private static String resolve(String envKey, String propKey, String fallback) {
        String value = System.getenv(envKey);
        if (value == null || value.isEmpty()) {
            value = System.getProperty(propKey);
        }
        return (value == null || value.isEmpty()) ? fallback : value;
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(DB_DRIVER);
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC driver not found on classpath. "
                    + "Confirm mysql-connector-j-*.jar is on -cp.", e);
        } catch (SQLException e) {
            throw new SQLException(diagnose(e) + " (user=" + DB_USER + ", url=" + DB_URL + ")", e);
        }
    }

    /** Turns common MySQL SQLState/error codes into an actionable message. */
    private static String diagnose(SQLException e) {
        int code = e.getErrorCode();
        String state = e.getSQLState() == null ? "" : e.getSQLState();
        if (code == 1045 || "28000".equals(state)) {
            return "Access denied for DB_USER/DB_PASSWORD. Set correct env vars "
                    + "(export DB_USER=..., export DB_PASSWORD=...) or fix the MySQL account's "
                    + "password/auth plugin (see README 'Database setup').";
        }
        if (code == 1049) {
            return "Database does not exist. Run docs/01_database_schema.sql against your MySQL "
                    + "instance first (mysql -u root -p < docs/01_database_schema.sql).";
        }
        if (code == 0 && state.startsWith("08")) {
            return "Could not reach MySQL server. Confirm it is running and reachable at the "
                    + "configured host/port (e.g. `mysql.server start` / `brew services start mysql`).";
        }
        return "Database connection failed: " + e.getMessage();
    }

    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("✓ Connection test passed! (" + DB_URL + " as " + DB_USER + ")");
        } catch (SQLException e) {
            System.out.println("✗ " + e.getMessage());
        }
    }
}
