package com.pharma.inventory.util;

import com.pharma.inventory.model.Material;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MaterialValidatorTest {

    private static Material valid() {
        Material m = new Material("BLS-002", "Blister Pack PVC 12 cavity", 1);
        m.setCurrentPrice(2.75);
        m.setMinStockLevel(1000);
        m.setReorderPoint(3000);
        m.setMaxStockLevel(10000);
        m.setLeadTimeDays(10);
        return m;
    }

    @Test
    void aSensibleMaterialPasses() {
        assertNull(MaterialValidator.validate(valid()));
    }

    @Test
    void theAddFormsDefaultsAreRejected() {
        // The Add Material form starts with min = max = reorder = 0; that used to save as OVERSTOCK forever.
        Material m = valid();
        m.setMinStockLevel(0);
        m.setReorderPoint(0);
        m.setMaxStockLevel(0);
        assertTrue(MaterialValidator.validate(m).startsWith("Max stock level"));
    }

    @Test
    void codeAndNameAreRequired() {
        Material m = valid();
        m.setMaterialCode("  ");
        assertEquals("Material code is required.", MaterialValidator.validate(m));
        m = valid();
        m.setMaterialName(null);
        assertEquals("Material name is required.", MaterialValidator.validate(m));
        m = valid();
        m.setMaterialCode("THIS-CODE-IS-TOO-LONG-1");
        assertNotNull(MaterialValidator.validate(m), "materials.material_code is VARCHAR(20)");
    }

    @Test
    void onUpdateTheReadOnlyCodeIsNotChecked() {
        Material m = valid();
        m.setMaterialCode(null);
        assertNull(MaterialValidator.validate(m, false));
    }

    @Test
    void priceCannotBeNegativeOrNotANumber() {
        Material m = valid();
        m.setCurrentPrice(-0.01);
        assertNotNull(MaterialValidator.validate(m));
        m.setCurrentPrice(Double.NaN);
        assertNotNull(MaterialValidator.validate(m));
        m.setCurrentPrice(0);
        assertNull(MaterialValidator.validate(m), "a free sample material is allowed");
    }

    @Test
    void reorderPointMustSitBetweenMinAndMax() {
        Material m = valid();
        m.setReorderPoint(500);
        assertTrue(MaterialValidator.validate(m).startsWith("Reorder point (500)"));
        m.setReorderPoint(10001);
        assertNotNull(MaterialValidator.validate(m));
        m.setReorderPoint(1000);
        assertNull(MaterialValidator.validate(m), "reorder exactly at min is allowed");
        m.setReorderPoint(10000);
        assertNull(MaterialValidator.validate(m), "and exactly at max");
    }

    @Test
    void negativeLevelsAndLeadTimeAreRejected() {
        Material m = valid();
        m.setMinStockLevel(-1);
        assertNotNull(MaterialValidator.validate(m));
        m = valid();
        m.setLeadTimeDays(-3);
        assertEquals("Lead time cannot be negative.", MaterialValidator.validate(m));
        assertEquals("Material is missing.", MaterialValidator.validate(null));
    }
}
