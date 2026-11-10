package com.pharma.inventory.model;
public class StockTransaction {
    private int transactionId, materialId, quantity;
    private String transactionType, transactionDate, referenceNumber, referenceType, createdBy;
    public StockTransaction() {}
    public StockTransaction(int materialId, String transactionType, int quantity, String referenceNumber) {
        this.materialId = materialId;
        this.transactionType = transactionType;
        this.quantity = quantity;
        this.referenceNumber = referenceNumber;
    }
    public int getTransactionId() { return transactionId; }
    public void setTransactionId(int transactionId) { this.transactionId = transactionId; }
    public int getMaterialId() { return materialId; }
    public void setMaterialId(int materialId) { this.materialId = materialId; }
    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getTransactionDate() { return transactionDate; }
    public void setTransactionDate(String transactionDate) { this.transactionDate = transactionDate; }
    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    public String getReferenceType() { return referenceType; }
    public void setReferenceType(String referenceType) { this.referenceType = referenceType; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}
