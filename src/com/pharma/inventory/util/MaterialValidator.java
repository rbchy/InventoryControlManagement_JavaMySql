package com.pharma.inventory.util;

import com.pharma.inventory.model.Material;

/**
 * Business rules a material must meet before it is saved. MaterialDAO checks them on both
 * add and update, so every screen gets the same rules. Previously nothing stopped, for example,
 * a negative price or a Max Stock Level of 0 (the Add form's default), which made every such
 * material show as OVERSTOCK.
 */
public final class MaterialValidator {

    private MaterialValidator() {
    }

    /** @return null when the material is valid, otherwise a message for the user */
    public static String validate(Material m) {
        return validate(m, true);
    }

    /** @param checkCode false on update, where the (read-only) code is not saved */
    public static String validate(Material m, boolean checkCode) {
        if (m == null) return "Material is missing.";
        if (checkCode && isBlank(m.getMaterialCode())) return "Material code is required.";
        if (checkCode && m.getMaterialCode().trim().length() > 20) return "Material code must be at most 20 characters.";
        if (isBlank(m.getMaterialName())) return "Material name is required.";
        if (Double.isNaN(m.getCurrentPrice()) || Double.isInfinite(m.getCurrentPrice()) || m.getCurrentPrice() < 0) {
            return "Current price cannot be negative.";
        }
        if (m.getMinStockLevel() < 0) return "Min stock level cannot be negative.";
        if (m.getMaxStockLevel() <= m.getMinStockLevel()) {
            return "Max stock level (" + m.getMaxStockLevel() + ") must be greater than min stock level ("
                    + m.getMinStockLevel() + ").";
        }
        if (m.getReorderPoint() < m.getMinStockLevel() || m.getReorderPoint() > m.getMaxStockLevel()) {
            return "Reorder point (" + m.getReorderPoint() + ") must be between min (" + m.getMinStockLevel()
                    + ") and max (" + m.getMaxStockLevel() + ").";
        }
        if (m.getLeadTimeDays() < 0) return "Lead time cannot be negative.";
        return null;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
