package com.pharma.inventory.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** min 1000 · reorder 3000 · max 10000 (the BLS-001 blister pack in the sample data). */
class StockStatusTest {

    @ParameterizedTest(name = "{0} on hand -> {1}")
    @CsvSource({
            "0,     CRITICAL",
            "999,   CRITICAL",
            "1000,  CRITICAL",   // exactly at min is CRITICAL (old console said NORMAL)
            "1001,  WARNING",
            "3000,  WARNING",    // exactly at the reorder point: time to order
            "3001,  NORMAL",
            "9999,  NORMAL",
            "10000, OVERSTOCK",  // exactly at max (old console said NORMAL)
            "25000, OVERSTOCK"
    })
    void boundariesMatchTheInventorySummaryView(int onHand, StockStatus expected) {
        assertEquals(expected, StockStatus.classify(onHand, 1000, 3000, 10000));
    }

    @Test
    void sampleDataStatuses() {
        // from docs/01_database_schema.sql: on hand vs (min, reorder, max)
        assertEquals(StockStatus.NORMAL, StockStatus.classify(5000, 1000, 3000, 10000));   // BLS-001
        assertEquals(StockStatus.NORMAL, StockStatus.classify(3500, 800, 2000, 6000));     // BOT-001
        assertEquals(StockStatus.NORMAL, StockStatus.classify(2000, 500, 1500, 5000));     // CAR-001
        assertEquals(StockStatus.NORMAL, StockStatus.classify(8000, 2000, 5000, 15000));   // LBL-001
        assertEquals(StockStatus.NORMAL, StockStatus.classify(150, 50, 100, 200));         // ALU-001
    }

    @Test
    void overloadUsesTheMaterialsLevels() {
        Material m = new Material();
        m.setMinStockLevel(50);
        m.setReorderPoint(100);
        m.setMaxStockLevel(200);
        assertEquals(StockStatus.WARNING, StockStatus.classify(75, m));
        assertEquals("🔴", StockStatus.CRITICAL.icon());
    }
}
