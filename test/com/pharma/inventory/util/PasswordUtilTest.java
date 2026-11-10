package com.pharma.inventory.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {

    @Test
    void roundTrip() {
        String stored = PasswordUtil.hashForStorage("Inv@2026");
        assertTrue(stored.matches("[0-9a-f]{16}:[0-9a-f]{64}"), stored);
        assertTrue(PasswordUtil.verify("Inv@2026", stored));
        assertFalse(PasswordUtil.verify("inv@2026", stored));
    }

    @Test
    void eachHashGetsItsOwnSalt() {
        assertNotEquals(PasswordUtil.hashForStorage("same"), PasswordUtil.hashForStorage("same"));
    }

    @Test
    void theDemoAccountsInTheSchemaUseTheDocumentedPasswords() {
        assertTrue(PasswordUtil.verify("admin123",
                "a1b2c3d4e5f60718:9879a6c081970a685052bdaf0cba84347009c2f6f758c7a05257f5dcd6485ca8"));
        assertTrue(PasswordUtil.verify("pass123",
                "1122334455667788:590ec48c23c1b62655d6038b71ef151baf71426ae0a472050e59c844690ec84e"));
        assertTrue(PasswordUtil.verify("store123",
                "99aabbccddeeff00:0c5622c2828e0f2179df78e423dcf2a6c3080f1824f8904cd6d9c92dc2ec1fce"));
    }

    @Test
    void plaintextOrMalformedStoredValuesNeverMatch() {
        assertFalse(PasswordUtil.verify("admin123", "admin123"), "a plaintext column value must not log in");
        assertFalse(PasswordUtil.verify("x", null));
        assertFalse(PasswordUtil.verify("x", "abc:"));
    }
}
