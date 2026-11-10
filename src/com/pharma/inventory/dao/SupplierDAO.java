package com.pharma.inventory.dao;
import com.pharma.inventory.config.DatabaseConfig;
import com.pharma.inventory.model.Supplier;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class SupplierDAO {
    private String lastError;
    public String getLastError() { return lastError; }

    public List<Supplier> getAllSuppliers() {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM suppliers ORDER BY supplier_name";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Supplier supplier = new Supplier();
                supplier.setSupplierId(rs.getInt("supplier_id"));
                supplier.setSupplierName(rs.getString("supplier_name"));
                supplier.setContactPerson(rs.getString("contact_person"));
                supplier.setEmail(rs.getString("email"));
                supplier.setPhone(rs.getString("phone"));
                supplier.setAddress(rs.getString("address"));
                supplier.setCity(rs.getString("city"));
                supplier.setCountry(rs.getString("country"));
                supplier.setPaymentTerms(rs.getString("payment_terms"));
                supplier.setRating(rs.getInt("rating"));
                suppliers.add(supplier);
            }
            System.out.println("✓ Retrieved " + suppliers.size() + " suppliers");
        } catch (SQLException e) {
            lastError = e.getMessage();
            System.out.println("✗ Error: " + lastError);
        }
        return suppliers;
    }

    public Supplier getSupplierById(int supplierId) {
        String sql = "SELECT * FROM suppliers WHERE supplier_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, supplierId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Supplier supplier = new Supplier();
                    supplier.setSupplierId(rs.getInt("supplier_id"));
                    supplier.setSupplierName(rs.getString("supplier_name"));
                    supplier.setContactPerson(rs.getString("contact_person"));
                    supplier.setEmail(rs.getString("email"));
                    supplier.setPhone(rs.getString("phone"));
                    supplier.setCity(rs.getString("city"));
                    return supplier;
                }
            }
        } catch (SQLException e) {
            lastError = e.getMessage();
            System.out.println("✗ Error: " + lastError);
        }
        return null;
    }

    public boolean addSupplier(Supplier s) {
        String sql = "INSERT INTO suppliers (supplier_name, contact_person, email, phone, address, city, country, "
                + "payment_terms, rating) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, s.getSupplierName());
            pstmt.setString(2, s.getContactPerson());
            pstmt.setString(3, s.getEmail());
            pstmt.setString(4, s.getPhone());
            pstmt.setString(5, s.getAddress());
            pstmt.setString(6, s.getCity());
            pstmt.setString(7, s.getCountry());
            pstmt.setString(8, s.getPaymentTerms());
            pstmt.setInt(9, s.getRating() <= 0 ? 5 : s.getRating());
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) s.setSupplierId(keys.getInt(1));
                }
            }
            return affected > 0;
        } catch (SQLException e) {
            lastError = e.getMessage();
            System.out.println("✗ Error: " + lastError);
            return false;
        }
    }
}
