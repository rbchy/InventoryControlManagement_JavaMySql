package com.pharma.inventory.model;
public class Material {
    private int materialId, categoryId, minStockLevel, maxStockLevel, reorderPoint, supplierId, leadTimeDays;
    private String materialCode, materialName, description, status, unitOfMeasurement;
    private double currentPrice;
    public Material() {}
    public Material(String materialCode, String materialName, int categoryId) {
        this.materialCode = materialCode;
        this.materialName = materialName;
        this.categoryId = categoryId;
    }
    public int getMaterialId() { return materialId; }
    public void setMaterialId(int materialId) { this.materialId = materialId; }
    public String getMaterialCode() { return materialCode; }
    public void setMaterialCode(String materialCode) { this.materialCode = materialCode; }
    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getMinStockLevel() { return minStockLevel; }
    public void setMinStockLevel(int minStockLevel) { this.minStockLevel = minStockLevel; }
    public int getMaxStockLevel() { return maxStockLevel; }
    public void setMaxStockLevel(int maxStockLevel) { this.maxStockLevel = maxStockLevel; }
    public int getReorderPoint() { return reorderPoint; }
    public void setReorderPoint(int reorderPoint) { this.reorderPoint = reorderPoint; }
    public double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(double currentPrice) { this.currentPrice = currentPrice; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }
    public int getLeadTimeDays() { return leadTimeDays; }
    public void setLeadTimeDays(int leadTimeDays) { this.leadTimeDays = leadTimeDays; }
    public String getUnitOfMeasurement() { return unitOfMeasurement; }
    public void setUnitOfMeasurement(String unitOfMeasurement) { this.unitOfMeasurement = unitOfMeasurement; }
}
