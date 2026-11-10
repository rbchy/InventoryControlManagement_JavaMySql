package com.pharma.inventory.dao;
import com.pharma.inventory.config.DatabaseConfig;
import com.pharma.inventory.model.Inventory;
import com.pharma.inventory.model.StockTransaction;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class InventoryDAO {
    public static final String TYPE_RECEIPT = "RECEIPT";
    public static final String TYPE_ISSUE = "ISSUE";
    public static final String TYPE_USED = "USED";
    public static final String TYPE_RETURN = "RETURN";
    public static final String[] ALL_TRANSACTION_TYPES = {TYPE_RECEIPT, TYPE_ISSUE, TYPE_USED, TYPE_RETURN};

    /** RECEIPT and RETURN add to on-hand stock; ISSUE and USED remove from it. */
    private static boolean isInbound(String type) {
        return TYPE_RECEIPT.equals(type) || TYPE_RETURN.equals(type);
    }

    private String lastError;
    public String getLastError() { return lastError; }

    public Inventory getInventoryByMaterialId(int materialId) {
        String sql = "SELECT * FROM inventory WHERE material_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, materialId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Inventory inventory = new Inventory();
                    inventory.setInventoryId(rs.getInt("inventory_id"));
                    inventory.setMaterialId(rs.getInt("material_id"));
                    inventory.setCurrentQuantity(rs.getInt("current_quantity"));
                    inventory.setReservedQuantity(rs.getInt("reserved_quantity"));
                    inventory.setWarehouseLocation(rs.getString("warehouse_location"));
                    return inventory;
                }
            }
        } catch (SQLException e) {
            lastError = e.getMessage();
            System.out.println("✗ Error: " + lastError);
        }
        return null;
    }

    public List<StockTransaction> getAllTransactions() {
        List<StockTransaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM stock_transactions ORDER BY transaction_id DESC LIMIT 10";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                StockTransaction transaction = new StockTransaction();
                transaction.setTransactionId(rs.getInt("transaction_id"));
                transaction.setMaterialId(rs.getInt("material_id"));
                transaction.setTransactionType(rs.getString("transaction_type"));
                transaction.setQuantity(rs.getInt("quantity"));
                transaction.setTransactionDate(rs.getString("transaction_date"));
                transaction.setReferenceNumber(rs.getString("reference_number"));
                transactions.add(transaction);
            }
            System.out.println("✓ Retrieved " + transactions.size() + " transactions");
        } catch (SQLException e) {
            lastError = e.getMessage();
            System.out.println("✗ Error: " + lastError);
        }
        return transactions;
    }

    /**
     * Read-friendly transaction feed (joins material code/name) backed by the
     * `recent_transactions` view in docs/01_database_schema.sql.
     * Columns: transaction_id, material_code, material_name, transaction_type,
     *          quantity, transaction_date, reference_number, created_by
     */
    public List<Object[]> getRecentTransactionsView() {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT transaction_id, material_code, material_name, transaction_type, quantity, "
                + "transaction_date, reference_number, created_by FROM recent_transactions";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                rows.add(new Object[] {
                        rs.getInt("transaction_id"), rs.getString("material_code"), rs.getString("material_name"),
                        rs.getString("transaction_type"), rs.getInt("quantity"), rs.getTimestamp("transaction_date"),
                        rs.getString("reference_number"), rs.getString("created_by")
                });
            }
        } catch (SQLException e) {
            lastError = e.getMessage();
            System.out.println("✗ Error: " + lastError);
        }
        return rows;
    }

    /**
     * Full stock picture backed by the `inventory_summary` view.
     * Columns: material_id, material_code, material_name, category_name, current_quantity,
     *          reserved_quantity, available_quantity, min_stock_level, max_stock_level,
     *          stock_value, stock_status (CRITICAL/WARNING/NORMAL/OVERSTOCK)
     */
    public List<Object[]> getInventorySummary() {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT material_id, material_code, material_name, category_name, current_quantity, "
                + "reserved_quantity, available_quantity, min_stock_level, max_stock_level, stock_value, stock_status "
                + "FROM inventory_summary ORDER BY material_name";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                rows.add(new Object[] {
                        rs.getInt("material_id"), rs.getString("material_code"), rs.getString("material_name"),
                        rs.getString("category_name"), rs.getInt("current_quantity"), rs.getInt("reserved_quantity"),
                        rs.getInt("available_quantity"), rs.getInt("min_stock_level"), rs.getInt("max_stock_level"),
                        rs.getDouble("stock_value"), rs.getString("stock_status")
                });
            }
        } catch (SQLException e) {
            lastError = e.getMessage();
            System.out.println("✗ Error: " + lastError);
        }
        return rows;
    }

    /**
     * Materials at/under their reorder point, backed by the `low_stock_items` view.
     * Columns: material_code, material_name, current_quantity, min_stock_level,
     *          max_stock_level, quantity_to_order, supplier_name, reorder_point
     */
    public List<Object[]> getLowStockItems() {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT material_code, material_name, current_quantity, min_stock_level, max_stock_level, "
                + "quantity_to_order, supplier_name, reorder_point FROM low_stock_items ORDER BY current_quantity ASC";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                rows.add(new Object[] {
                        rs.getString("material_code"), rs.getString("material_name"), rs.getInt("current_quantity"),
                        rs.getInt("min_stock_level"), rs.getInt("max_stock_level"), rs.getInt("quantity_to_order"),
                        rs.getString("supplier_name"), rs.getInt("reorder_point")
                });
            }
        } catch (SQLException e) {
            lastError = e.getMessage();
            System.out.println("✗ Error: " + lastError);
        }
        return rows;
    }

    /** Creates the inventory row for a newly-added material. */
    public boolean createInventoryRecord(int materialId, int initialQuantity, String warehouseLocation) {
        String sql = "INSERT INTO inventory (material_id, current_quantity, reserved_quantity, warehouse_location) "
                + "VALUES (?, ?, 0, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, materialId);
            pstmt.setInt(2, initialQuantity);
            pstmt.setString(3, warehouseLocation);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            lastError = e.getMessage();
            System.out.println("✗ Error: " + lastError);
            return false;
        }
    }

    /** Convenience overload: MANUAL reference type, for simple one-off receipts/issues. */
    public boolean recordTransaction(int materialId, String type, int quantity, String referenceNumber,
                                      String createdBy) {
        return recordTransaction(materialId, type, quantity, referenceNumber, "MANUAL", createdBy);
    }

    /**
     * Records a stock movement and adjusts on-hand quantity atomically.
     * type must be one of ALL_TRANSACTION_TYPES: RECEIPT/RETURN add to stock,
     * ISSUE/USED remove from it. referenceType is a free-form tag such as
     * "MANUAL", "PRODUCTION" (batch work), "PURCHASE_ORDER", or "ADJUSTMENT" —
     * used to group batch-related Used/Return entries for reporting.
     * Returns false (with getLastError() set) if the material has no inventory
     * row yet, or an outbound movement would take current_quantity negative.
     */
    public boolean recordTransaction(int materialId, String type, int quantity, String referenceNumber,
                                      String referenceType, String createdBy) {
        if (quantity <= 0) {
            lastError = "Quantity must be greater than zero.";
            return false;
        }
        if (!java.util.Arrays.asList(ALL_TRANSACTION_TYPES).contains(type)) {
            // Anything that is not RECEIPT/RETURN used to be treated as an outbound movement.
            lastError = "Unknown transaction type '" + type + "'. Use one of "
                    + String.join(", ", ALL_TRANSACTION_TYPES) + ".";
            return false;
        }
        String selectSql = "SELECT current_quantity FROM inventory WHERE material_id = ? FOR UPDATE";
        String updateSql = "UPDATE inventory SET current_quantity = ?, "
                + (isInbound(type) ? "last_received_date = NOW()" : "last_issued_date = NOW()")
                + " WHERE material_id = ?";
        String insertSql = "INSERT INTO stock_transactions (material_id, transaction_type, quantity, "
                + "transaction_date, reference_number, reference_type, created_by) VALUES (?, ?, ?, NOW(), ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int currentQty;
                try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                    selectStmt.setInt(1, materialId);
                    try (ResultSet rs = selectStmt.executeQuery()) {
                        if (!rs.next()) {
                            lastError = "No inventory record exists for this material yet.";
                            conn.rollback();
                            return false;
                        }
                        currentQty = rs.getInt("current_quantity");
                    }
                }
                int newQty = isInbound(type) ? currentQty + quantity : currentQty - quantity;
                if (newQty < 0) {
                    lastError = "Insufficient stock: only " + currentQty + " on hand.";
                    conn.rollback();
                    return false;
                }
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, newQty);
                    updateStmt.setInt(2, materialId);
                    updateStmt.executeUpdate();
                }
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, materialId);
                    insertStmt.setString(2, type);
                    insertStmt.setInt(3, quantity);
                    insertStmt.setString(4, referenceNumber);
                    insertStmt.setString(5, referenceType == null || referenceType.isEmpty() ? "MANUAL" : referenceType);
                    insertStmt.setString(6, createdBy);
                    insertStmt.executeUpdate();
                }
                conn.commit();
                return true;
            } catch (SQLException inner) {
                conn.rollback();
                throw inner;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            lastError = e.getMessage();
            System.out.println("✗ Error: " + lastError);
            return false;
        }
    }
}
