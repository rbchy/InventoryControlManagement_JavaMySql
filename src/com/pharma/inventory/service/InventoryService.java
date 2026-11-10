package com.pharma.inventory.service;
import com.pharma.inventory.dao.MaterialDAO;
import com.pharma.inventory.dao.InventoryDAO;
import com.pharma.inventory.dao.SupplierDAO;
import com.pharma.inventory.model.*;
import java.util.List;
public class InventoryService {
    private MaterialDAO materialDAO;
    private InventoryDAO inventoryDAO;
    private SupplierDAO supplierDAO;
    public InventoryService() {
        this.materialDAO = new MaterialDAO();
        this.inventoryDAO = new InventoryDAO();
        this.supplierDAO = new SupplierDAO();
    }
    public void displayStockStatus() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("📊 স্টক অবস্থা রিপোর্ট");
        System.out.println("=".repeat(80));
        List<Material> materials = materialDAO.getAllMaterials();
        for (Material material : materials) {
            Inventory inventory = inventoryDAO.getInventoryByMaterialId(material.getMaterialId());
            if (inventory != null) {
                StockStatus st = StockStatus.classify(inventory.getCurrentQuantity(), material);
                String status = st.icon() + " " + st;
                System.out.printf("%s | %-20s | বর্তমান: %d | প্রয়োজন: %d | %s%n",
                    material.getMaterialCode(), material.getMaterialName(),
                    inventory.getCurrentQuantity(), material.getMinStockLevel(), status);
            }
        }
        System.out.println("=".repeat(80) + "\n");
    }
    public void displayTransactions() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("📝 সাম্প্রতিক লেনদেন");
        System.out.println("=".repeat(80));
        List<StockTransaction> transactions = inventoryDAO.getAllTransactions();
        for (StockTransaction transaction : transactions) {
            System.out.printf("ID: %d | ম্যাটেরিয়াল: %d | ধরন: %s | পরিমাণ: %d%n",
                transaction.getTransactionId(), transaction.getMaterialId(),
                transaction.getTransactionType(), transaction.getQuantity());
        }
        System.out.println("=".repeat(80) + "\n");
    }
    public void displaySuppliers() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("🏢 সরবরাহকারী তালিকা");
        System.out.println("=".repeat(80));
        List<Supplier> suppliers = supplierDAO.getAllSuppliers();
        for (Supplier supplier : suppliers) {
            System.out.printf("ID: %d | নাম: %-30s | শহর: %s | রেটিং: %d⭐%n",
                supplier.getSupplierId(), supplier.getSupplierName(),
                supplier.getCity(), supplier.getRating());
        }
        System.out.println("=".repeat(80) + "\n");
    }
}
