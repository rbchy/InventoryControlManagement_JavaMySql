package com.pharma.inventory.dao;
import com.pharma.inventory.config.DatabaseConfig;
import com.pharma.inventory.model.MaterialCategory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    private String lastError;
    public String getLastError() { return lastError; }

    public List<MaterialCategory> getAllCategories() {
        List<MaterialCategory> categories = new ArrayList<>();
        String sql = "SELECT category_id, category_name FROM material_categories ORDER BY category_name";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                categories.add(new MaterialCategory(rs.getInt("category_id"), rs.getString("category_name")));
            }
        } catch (SQLException e) {
            lastError = e.getMessage();
            System.out.println("✗ Error: " + lastError);
        }
        return categories;
    }
}
