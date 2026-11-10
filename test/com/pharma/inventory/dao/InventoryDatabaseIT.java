package com.pharma.inventory.dao;

import com.pharma.inventory.config.DatabaseConfig;
import com.pharma.inventory.model.Inventory;
import com.pharma.inventory.model.Material;
import com.pharma.inventory.model.StockStatus;
import com.pharma.inventory.model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Runs against a real MySQL loaded with docs/01_database_schema.sql (CI does this).
 * Skipped when DB_URL is not set, so "mvn verify" without MySQL still passes.
 */
class InventoryDatabaseIT {

    private final MaterialDAO materials = new MaterialDAO();
    private final InventoryDAO inventory = new InventoryDAO();
    private final ReportDAO reports = new ReportDAO();

    @BeforeAll
    static void needsADatabase() throws Exception {
        String url = System.getenv("DB_URL");
        assumeTrue(url != null && !url.isEmpty(), "DB_URL not set - skipping database tests");
        try (Connection c = DatabaseConfig.getConnection()) {
            assertTrue(c.isValid(5));
        }
    }

    private Material byCode(String code) {
        return materials.searchMaterials(code).stream().filter(m -> m.getMaterialCode().equals(code)).findFirst()
                .orElseThrow(() -> new AssertionError("material " + code + " missing"));
    }

    private int onHand(String code) {
        return inventory.getInventoryByMaterialId(byCode(code).getMaterialId()).getCurrentQuantity();
    }

    private static long count(String sql, Object... params) throws Exception {
        try (Connection c = DatabaseConfig.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        }
    }

    private static Material newMaterial(String code, int min, int reorder, int max) {
        Material m = new Material(code, "IT material " + code, 1);
        m.setUnitOfMeasurement("pieces");
        m.setCurrentPrice(1.25);
        m.setSupplierId(1);
        m.setMinStockLevel(min);
        m.setReorderPoint(reorder);
        m.setMaxStockLevel(max);
        m.setLeadTimeDays(5);
        return m;
    }

    // ------------------------------------------------------------- login / users

    @Test
    void theThreeDemoAccountsLogIn() {
        UserDAO users = new UserDAO();
        assertEquals("ADMIN", users.authenticate("admin", "admin123").getRole());
        assertEquals("MANAGER", users.authenticate("inventory_mgr", "pass123").getRole());
        assertEquals("OPERATOR", users.authenticate("store_keeper", "store123").getRole());
        assertNull(users.authenticate("admin", "Admin123"));
        assertEquals("Invalid username or password.", users.getLastError());
        assertNull(users.authenticate("ghost", "admin123"));
        assertEquals("Invalid username or password.", users.getLastError(), "same message: usernames cannot be probed");
    }

    @Test
    void registrationHashesThePasswordAndRejectsADuplicateUsername() throws Exception {
        UserDAO users = new UserDAO();
        String name = "it_" + System.nanoTime();
        User u = new User();
        u.setUsername(name);
        u.setFullName("IT User");
        u.setRole("OPERATOR");
        assertTrue(users.registerUser(u, "Str0ng!pw"), users.getLastError());
        assertEquals(0, count("SELECT COUNT(*) FROM users WHERE username = ? AND password = 'Str0ng!pw'", name),
                "never stored as typed");
        assertNotNull(users.authenticate(name, "Str0ng!pw"));

        User again = new User();
        again.setUsername(name);
        assertFalse(users.registerUser(again, "other"));
        assertEquals("That username is already taken.", users.getLastError());
    }

    // ------------------------------------------------------------- stock movements

    @Test
    void receiptAndReturnAddIssueAndUsedRemove() throws Exception {
        Material m = byCode("CAR-001");
        int start = onHand("CAR-001");
        assertTrue(inventory.recordTransaction(m.getMaterialId(), InventoryDAO.TYPE_RECEIPT, 500, "PO-IT-1", "PURCHASE_ORDER", "it"));
        assertTrue(inventory.recordTransaction(m.getMaterialId(), InventoryDAO.TYPE_ISSUE, 200, "MO-IT-1", "PRODUCTION", "it"));
        assertTrue(inventory.recordTransaction(m.getMaterialId(), InventoryDAO.TYPE_USED, 120, "BATCH-IT-1", "PRODUCTION", "it"));
        assertTrue(inventory.recordTransaction(m.getMaterialId(), InventoryDAO.TYPE_RETURN, 20, "BATCH-IT-1", "it"));
        assertEquals(start + 500 - 200 - 120 + 20, onHand("CAR-001"));
        assertEquals(4, count("SELECT COUNT(*) FROM stock_transactions WHERE material_id = ? AND created_by = 'it'", m.getMaterialId()));
        assertEquals(1, count("SELECT COUNT(*) FROM inventory WHERE material_id = ? AND last_received_date IS NOT NULL "
                + "AND last_issued_date IS NOT NULL", m.getMaterialId()));
    }

    @Test
    void anIssueLargerThanStockIsRefusedAndNothingIsWritten() throws Exception {
        Material m = byCode("ALU-001");
        int start = onHand("ALU-001");
        long txns = count("SELECT COUNT(*) FROM stock_transactions");
        assertFalse(inventory.recordTransaction(m.getMaterialId(), InventoryDAO.TYPE_ISSUE, start + 1, "X", "it"));
        assertEquals("Insufficient stock: only " + start + " on hand.", inventory.getLastError());
        assertEquals(start, onHand("ALU-001"));
        assertEquals(txns, count("SELECT COUNT(*) FROM stock_transactions"), "rolled back: no orphan transaction row");
    }

    @Test
    void badMovementsAreRefused() {
        int id = byCode("LBL-001").getMaterialId();
        int start = onHand("LBL-001");
        assertFalse(inventory.recordTransaction(id, InventoryDAO.TYPE_RECEIPT, 0, "X", "it"));
        assertEquals("Quantity must be greater than zero.", inventory.getLastError());
        assertFalse(inventory.recordTransaction(id, "TRANSFER", 10, "X", "it"));
        assertTrue(inventory.getLastError().startsWith("Unknown transaction type 'TRANSFER'"), inventory.getLastError());
        assertFalse(inventory.recordTransaction(id, null, 10, "X", "it"));
        assertEquals(start, onHand("LBL-001"), "an unknown type used to be treated as an issue and remove stock");
        assertFalse(inventory.recordTransaction(999999, InventoryDAO.TYPE_RECEIPT, 10, "X", "it"));
        assertEquals("No inventory record exists for this material yet.", inventory.getLastError());
    }

    // ------------------------------------------------------------- materials

    @Test
    void addSearchAndSoftDeleteAMaterial() throws Exception {
        String code = "IT" + (System.nanoTime() % 1_000_000_000L);
        Material m = newMaterial(code, 100, 300, 1000);
        assertTrue(materials.addMaterial(m), materials.getLastError());
        assertTrue(inventory.createInventoryRecord(m.getMaterialId(), 250, "Z9-99"));

        // 250 <= reorder point 300 -> it must show up as a low-stock alert
        assertTrue(inventory.getLowStockItems().stream().anyMatch(r -> code.equals(r[0])));

        assertTrue(materials.softDeleteMaterial(m.getMaterialId()));
        assertEquals("INACTIVE", materials.getMaterialById(m.getMaterialId()).getStatus());
        assertFalse(inventory.getLowStockItems().stream().anyMatch(r -> code.equals(r[0])),
                "a deactivated material must not keep raising low-stock alerts");
        assertFalse(inventory.getInventorySummary().stream().anyMatch(r -> code.equals(r[1])),
                "nor appear in the active stock report");
        assertFalse(materials.getAllMaterials().stream().anyMatch(x -> x.getMaterialCode().equals(code)));
    }

    @Test
    void invalidMaterialsAreNotSaved() throws Exception {
        long before = count("SELECT COUNT(*) FROM materials");
        Material m = newMaterial("IT-BAD", 0, 0, 0);
        assertFalse(materials.addMaterial(m));
        assertTrue(materials.getLastError().startsWith("Max stock level"));
        Material existing = byCode("BOT-001");
        existing.setCurrentPrice(-1);
        assertFalse(materials.updateMaterial(existing));
        assertEquals("Current price cannot be negative.", materials.getLastError());
        assertEquals(before, count("SELECT COUNT(*) FROM materials"));
        assertEquals(0.80, byCode("BOT-001").getCurrentPrice(), 0.0001, "the bad update did not reach the database");
    }

    @Test
    void searchTreatsUnderscoreAndPercentLiterally() {
        assertEquals(1, materials.searchMaterials("BLS-001").size());
        assertTrue(materials.searchMaterials("BLS_001").isEmpty(), "'_' is not a wildcard");
        assertTrue(materials.searchMaterials("%").isEmpty(), "'%' is not a wildcard");
        assertFalse(materials.searchMaterials("bottle").isEmpty(), "search is case-insensitive");
    }

    // ------------------------------------------------------------- reports and views

    @Test
    void theJavaStatusAgreesWithTheDatabaseViewForEveryMaterial() throws Exception {
        List<Object[]> summary = inventory.getInventorySummary();
        assertTrue(summary.size() >= 5);
        for (Object[] r : summary) {
            Material m = materials.getMaterialById((Integer) r[0]);
            Inventory inv = inventory.getInventoryByMaterialId(m.getMaterialId());
            StockStatus java = StockStatus.classify(inv.getCurrentQuantity(), m);
            assertEquals(java.name(), r[10], m.getMaterialCode() + " on hand " + inv.getCurrentQuantity());
            assertEquals((Integer) r[4] - (Integer) r[5], (int) (Integer) r[6], "available = on hand - reserved");
        }
    }

    @Test
    void everyReportRuns() {
        for (String type : InventoryDAO.ALL_TRANSACTION_TYPES) {
            ReportResult r = reports.getTransactionReport(type, null, null);
            assertEquals(8, r.getColumns().length, type);
            assertNull(reports.getLastError(), type + ": " + reports.getLastError());
        }
        assertFalse(reports.getStockUpdateReport().getRows().isEmpty());
        reports.getMinLevelReport();
        reports.getMaxLevelReport();
        assertNull(reports.getLastError(), reports.getLastError());
        inventory.getRecentTransactionsView();
        assertNull(inventory.getLastError(), "recent_transactions view: " + inventory.getLastError());
        assertEquals(3, new SupplierDAO().getAllSuppliers().size());
        assertEquals(5, new CategoryDAO().getAllCategories().size());
    }

    @Test
    void theDateFilterOnTransactionReportsIsInclusive() {
        int id = byCode("BLS-001").getMaterialId();
        assertTrue(inventory.recordTransaction(id, InventoryDAO.TYPE_RECEIPT, 10, "DATE-IT", "it"));
        java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
        assertTrue(reports.getTransactionReport(InventoryDAO.TYPE_RECEIPT, today, today).getRows().stream()
                .anyMatch(r -> "DATE-IT".equals(r[5])), "a movement made today is in the today..today report");
        java.sql.Date yesterday = new java.sql.Date(System.currentTimeMillis() - 86_400_000L);
        assertTrue(reports.getTransactionReport(InventoryDAO.TYPE_RECEIPT, null, yesterday).getRows().stream()
                .noneMatch(r -> "DATE-IT".equals(r[5])));
    }
}
