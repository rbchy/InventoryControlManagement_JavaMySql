package com.pharma.inventory.dao;

import com.pharma.inventory.config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Read-only reporting queries: Inventory In/Out/Used/Return (by transaction_type,
 * with an optional date range), Stock Update (current levels), and Min/Max Level
 * breach reports. Each method returns a ReportResult so ReportsTab can render any
 * of them in the same table without per-report GUI code.
 */
public class ReportDAO {
    private String lastError;
    public String getLastError() { return lastError; }

    private static final String[] TXN_COLUMNS = {
            "Code", "Material Name", "Type", "Quantity", "Date", "Batch/Reference", "Ref. Type", "Recorded By"
    };

    /**
     * Inventory In (RECEIPT), Out (ISSUE), Used (USED) or Return (RETURN), optionally
     * bounded by [fromDate, toDate] inclusive. Pass null for either bound to leave it open.
     */
    public ReportResult getTransactionReport(String transactionType, java.sql.Date fromDate, java.sql.Date toDate) {
        StringBuilder sql = new StringBuilder(
                "SELECT m.material_code, m.material_name, st.transaction_type, st.quantity, st.transaction_date, "
                + "st.reference_number, st.reference_type, st.created_by "
                + "FROM stock_transactions st JOIN materials m ON st.material_id = m.material_id "
                + "WHERE st.transaction_type = ?");
        if (fromDate != null) sql.append(" AND st.transaction_date >= ?");
        if (toDate != null) sql.append(" AND st.transaction_date < DATE_ADD(?, INTERVAL 1 DAY)");
        sql.append(" ORDER BY st.transaction_date DESC");

        List<Object[]> rows = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            pstmt.setString(idx++, transactionType);
            if (fromDate != null) pstmt.setDate(idx++, fromDate);
            if (toDate != null) pstmt.setDate(idx++, toDate);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    rows.add(new Object[] {
                            rs.getString("material_code"), rs.getString("material_name"),
                            rs.getString("transaction_type"), rs.getInt("quantity"),
                            rs.getTimestamp("transaction_date"), rs.getString("reference_number"),
                            rs.getString("reference_type"), rs.getString("created_by")
                    });
                }
            }
        } catch (SQLException e) {
            lastError = e.getMessage();
            System.out.println("✗ Error: " + lastError);
        }
        return new ReportResult(TXN_COLUMNS, rows);
    }

    /** Current stock snapshot for every active material, backed by the `inventory_summary` view. */
    public ReportResult getStockUpdateReport() {
        String[] columns = {
                "Code", "Material Name", "Category", "On Hand", "Reserved", "Available",
                "Min Level", "Max Level", "Stock Value", "Status"
        };
        String sql = "SELECT material_code, material_name, category_name, current_quantity, reserved_quantity, "
                + "available_quantity, min_stock_level, max_stock_level, stock_value, stock_status "
                + "FROM inventory_summary ORDER BY material_name";
        List<Object[]> rows = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                rows.add(new Object[] {
                        rs.getString("material_code"), rs.getString("material_name"), rs.getString("category_name"),
                        rs.getInt("current_quantity"), rs.getInt("reserved_quantity"), rs.getInt("available_quantity"),
                        rs.getInt("min_stock_level"), rs.getInt("max_stock_level"), rs.getDouble("stock_value"),
                        rs.getString("stock_status")
                });
            }
        } catch (SQLException e) {
            lastError = e.getMessage();
            System.out.println("✗ Error: " + lastError);
        }
        return new ReportResult(columns, rows);
    }

    /** Materials at or below their Min Stock Level (distinct from the reorder-point-based Low Stock Alerts tab). */
    public ReportResult getMinLevelReport() {
        return levelBreachReport("current_quantity <= m.min_stock_level", "min_stock_level", "Min Level", true);
    }

    /** Materials at or above their Max Stock Level (overstock). */
    public ReportResult getMaxLevelReport() {
        return levelBreachReport("current_quantity >= m.max_stock_level", "max_stock_level", "Max Level", false);
    }

    private ReportResult levelBreachReport(String condition, String levelColumn, String levelLabel, boolean ascending) {
        String[] columns = {"Code", "Material Name", "On Hand", levelLabel, "Supplier"};
        String sql = "SELECT m.material_code, m.material_name, i.current_quantity, m." + levelColumn + " AS lvl, "
                + "s.supplier_name FROM materials m "
                + "JOIN inventory i ON m.material_id = i.material_id "
                + "JOIN suppliers s ON m.supplier_id = s.supplier_id "
                + "WHERE m.status = 'ACTIVE' AND i." + condition
                + " ORDER BY i.current_quantity " + (ascending ? "ASC" : "DESC");
        List<Object[]> rows = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                rows.add(new Object[] {
                        rs.getString("material_code"), rs.getString("material_name"),
                        rs.getInt("current_quantity"), rs.getInt("lvl"), rs.getString("supplier_name")
                });
            }
        } catch (SQLException e) {
            lastError = e.getMessage();
            System.out.println("✗ Error: " + lastError);
        }
        return new ReportResult(columns, rows);
    }
}
