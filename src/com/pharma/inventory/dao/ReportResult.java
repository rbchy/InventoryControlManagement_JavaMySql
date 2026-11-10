package com.pharma.inventory.dao;

import java.util.List;

/** A generic (columns + rows) bundle so one ReportsTab table can render any report type. */
public class ReportResult {
    private final String[] columns;
    private final List<Object[]> rows;

    public ReportResult(String[] columns, List<Object[]> rows) {
        this.columns = columns;
        this.rows = rows;
    }

    public String[] getColumns() { return columns; }
    public List<Object[]> getRows() { return rows; }
}
