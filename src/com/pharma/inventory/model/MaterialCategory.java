package com.pharma.inventory.model;

public class MaterialCategory {
    private int categoryId;
    private String categoryName;

    public MaterialCategory() {}

    public MaterialCategory(int categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    /** So this drops straight into a JComboBox. */
    @Override
    public String toString() { return categoryName; }
}
