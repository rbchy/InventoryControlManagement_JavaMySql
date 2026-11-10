package com.pharma.inventory.model;
public class PurchaseOrder {
    private int poId, supplierId;
    private String poNumber, poDate, expectedDeliveryDate, actualDeliveryDate, status, createdBy;
    private double totalAmount;
    public PurchaseOrder() {}
    public PurchaseOrder(String poNumber, int supplierId, String poDate) {
        this.poNumber = poNumber;
        this.supplierId = supplierId;
        this.poDate = poDate;
        this.status = "PENDING";
    }
    public int getPoId() { return poId; }
    public void setPoId(int poId) { this.poId = poId; }
    public String getPoNumber() { return poNumber; }
    public void setPoNumber(String poNumber) { this.poNumber = poNumber; }
    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }
    public String getPoDate() { return poDate; }
    public void setPoDate(String poDate) { this.poDate = poDate; }
    public String getExpectedDeliveryDate() { return expectedDeliveryDate; }
    public void setExpectedDeliveryDate(String expectedDeliveryDate) { this.expectedDeliveryDate = expectedDeliveryDate; }
    public String getActualDeliveryDate() { return actualDeliveryDate; }
    public void setActualDeliveryDate(String actualDeliveryDate) { this.actualDeliveryDate = actualDeliveryDate; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}
