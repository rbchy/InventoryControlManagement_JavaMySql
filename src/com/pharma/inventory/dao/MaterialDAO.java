package com.pharma.inventory.dao;

import com.pharma.inventory.util.MaterialValidator;
import com.pharma.inventory.config.DatabaseConfig;
import com.pharma.inventory.model.Material;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class MaterialDAO {
    private String lastError;
    public String getLastError() { return lastError; }

    private Material mapRow(ResultSet rs) throws SQLException {
        Material material = new Material();
        material.setMaterialId(rs.getInt("material_id"));
        material.setMaterialCode(rs.getString("material_code"));
        material.setMaterialName(rs.getString("material_name"));
        material.setCategoryId(rs.getInt("category_id"));
        material.setUnitOfMeasurement(rs.getString("unit_of_measurement"));
        material.setSupplierId(rs.getInt("supplier_id"));
        material.setMinStockLevel(rs.getInt("min_stock_level"));
        material.setMaxStockLevel(rs.getInt("max_stock_level"));
        material.setReorderPoint(rs.getInt("reorder_point"));
        material.setLeadTimeDays(rs.getInt("lead_time_days"));
        material.setCurrentPrice(rs.getDouble("current_price"));
        material.setStatus(rs.getString("status"));
        return material;
    }

    public List<Material> getAllMaterials() {
        List<Material> materials = new ArrayList<>();
        String sql = "SELECT * FROM materials WHERE status = 'ACTIVE' ORDER BY material_name";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                materials.add(mapRow(rs));
            }
            System.out.println("✓ Retrieved " + materials.size() + " materials");
        } catch (SQLException e) {
            lastError = e.getMessage();
            System.out.println("✗ Error: " + lastError);
        }
        return materials;
    }

    public Material getMaterialById(int materialId) {
        String sql = "SELECT * FROM materials WHERE material_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, materialId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            lastError = e.getMessage();
            System.out.println("✗ Error: " + lastError);
        }
        return null;
    }

    /** Search by code or name, across all statuses (used by the Search tab). */
    public List<Material> searchMaterials(String keyword) {
        List<Material> materials = new ArrayList<>();
        String sql = "SELECT * FROM materials WHERE material_code LIKE ? ESCAPE '!' OR material_name LIKE ? ESCAPE '!' "
                + "ORDER BY material_name";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Escape LIKE wildcards so "BLS_001" finds that code, not every "BLS?001".
            String like = "%" + escapeLike(keyword == null ? "" : keyword.trim()) + "%";
            pstmt.setString(1, like);
            pstmt.setString(2, like);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    materials.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            lastError = e.getMessage();
            System.out.println("✗ Error: " + lastError);
        }
        return materials;
    }

    public boolean addMaterial(Material m) {
        String invalid = MaterialValidator.validate(m);
        if (invalid != null) {
            lastError = invalid;
            return false;
        }
        String sql = "INSERT INTO materials (material_code, material_name, category_id, unit_of_measurement, "
                + "current_price, supplier_id, min_stock_level, max_stock_level, reorder_point, lead_time_days, status) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, m.getMaterialCode());
            pstmt.setString(2, m.getMaterialName());
            pstmt.setInt(3, m.getCategoryId());
            pstmt.setString(4, m.getUnitOfMeasurement());
            pstmt.setDouble(5, m.getCurrentPrice());
            pstmt.setInt(6, m.getSupplierId());
            pstmt.setInt(7, m.getMinStockLevel());
            pstmt.setInt(8, m.getMaxStockLevel());
            pstmt.setInt(9, m.getReorderPoint());
            pstmt.setInt(10, m.getLeadTimeDays());
            pstmt.setString(11, m.getStatus() == null || m.getStatus().isEmpty() ? "ACTIVE" : m.getStatus());
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) m.setMaterialId(keys.getInt(1));
                }
            }
            return affected > 0;
        } catch (SQLException e) {
            lastError = e.getMessage();
            System.out.println("✗ Error: " + lastError);
            return false;
        }
    }

    public boolean updateMaterial(Material m) {
        // The code cannot be edited on update (it is not in the SET list), so only the other rules apply.
        String invalid = MaterialValidator.validate(m, false);
        if (invalid != null) {
            lastError = invalid;
            return false;
        }
        String sql = "UPDATE materials SET material_name=?, category_id=?, unit_of_measurement=?, current_price=?, "
                + "supplier_id=?, min_stock_level=?, max_stock_level=?, reorder_point=?, lead_time_days=?, status=? "
                + "WHERE material_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, m.getMaterialName());
            pstmt.setInt(2, m.getCategoryId());
            pstmt.setString(3, m.getUnitOfMeasurement());
            pstmt.setDouble(4, m.getCurrentPrice());
            pstmt.setInt(5, m.getSupplierId());
            pstmt.setInt(6, m.getMinStockLevel());
            pstmt.setInt(7, m.getMaxStockLevel());
            pstmt.setInt(8, m.getReorderPoint());
            pstmt.setInt(9, m.getLeadTimeDays());
            pstmt.setString(10, m.getStatus());
            pstmt.setInt(11, m.getMaterialId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            lastError = e.getMessage();
            System.out.println("✗ Error: " + lastError);
            return false;
        }
    }

    /** Soft delete: materials are never hard-deleted (would break FK history in transactions/POs). */
    public boolean softDeleteMaterial(int materialId) {
        String sql = "UPDATE materials SET status = 'INACTIVE' WHERE material_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, materialId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            lastError = e.getMessage();
            System.out.println("✗ Error: " + lastError);
            return false;
        }
    }

    static String escapeLike(String s) {
        return s.replace("!", "!!").replace("%", "!%").replace("_", "!_");
    }
}
