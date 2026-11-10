package com.pharma.inventory.model;
public class Inventory {
    private int inventoryId, materialId, currentQuantity, reservedQuantity;
    private String warehouseLocation, lastReceivedDate;
    public Inventory() {}
    public Inventory(int materialId, int currentQuantity) {
        this.materialId = materialId;
        this.currentQuantity = currentQuantity;
        this.reservedQuantity = 0;
    }
    public int getInventoryId() { return inventoryId; }
    public void setInventoryId(int inventoryId) { this.inventoryId = inventoryId; }
    public int getMaterialId() { return materialId; }
    public void setMaterialId(int materialId) { this.materialId = materialId; }
    public int getCurrentQuantity() { return currentQuantity; }
    public void setCurrentQuantity(int currentQuantity) { this.currentQuantity = currentQuantity; }
    public int getReservedQuantity() { return reservedQuantity; }
    public void setReservedQuantity(int reservedQuantity) { this.reservedQuantity = reservedQuantity; }
    public int getAvailableQuantity() { return currentQuantity - reservedQuantity; }
    public String getWarehouseLocation() { return warehouseLocation; }
    public void setWarehouseLocation(String warehouseLocation) { this.warehouseLocation = warehouseLocation; }
    public String getLastReceivedDate() { return lastReceivedDate; }
    public void setLastReceivedDate(String lastReceivedDate) { this.lastReceivedDate = lastReceivedDate; }
}
