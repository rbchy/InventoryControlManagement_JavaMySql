package com.pharma.inventory.model;

/**
 * Stock status of one material — the single definition used by the console dashboard,
 * the GUI and (as the same CASE expression) the {@code inventory_summary} view in
 * docs/01_database_schema.sql. Checked in that order:
 *
 * <pre>
 *   on hand &lt;= min stock level   -&gt; CRITICAL  (reorder urgently)
 *   on hand &lt;= reorder point     -&gt; WARNING   (place a purchase order)
 *   on hand &gt;= max stock level   -&gt; OVERSTOCK
 *   otherwise                    -&gt; NORMAL
 * </pre>
 *
 * Before this class the console used "&lt; min = LOW, &gt; max = HIGH", so a material sitting
 * exactly on its minimum was NORMAL in the console but CRITICAL in the GUI and reports.
 */
public enum StockStatus {
    CRITICAL("🔴"), WARNING("🟠"), NORMAL("🟢"), OVERSTOCK("🟡");

    private final String icon;

    StockStatus(String icon) {
        this.icon = icon;
    }

    public String icon() {
        return icon;
    }

    public static StockStatus classify(int onHand, int minLevel, int reorderPoint, int maxLevel) {
        if (onHand <= minLevel) return CRITICAL;
        if (onHand <= reorderPoint) return WARNING;
        if (onHand >= maxLevel) return OVERSTOCK;
        return NORMAL;
    }

    public static StockStatus classify(int onHand, Material m) {
        return classify(onHand, m.getMinStockLevel(), m.getReorderPoint(), m.getMaxStockLevel());
    }
}
