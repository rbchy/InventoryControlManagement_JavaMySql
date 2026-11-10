package com.pharma.inventory.gui;

/** Implemented by any tab whose table data should reload after another tab changes the DB. */
public interface Refreshable {
    void reloadData();
}
