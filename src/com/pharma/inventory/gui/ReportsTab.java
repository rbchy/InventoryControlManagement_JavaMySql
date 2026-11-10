package com.pharma.inventory.gui;

import com.pharma.inventory.dao.InventoryDAO;
import com.pharma.inventory.dao.ReportDAO;
import com.pharma.inventory.dao.ReportResult;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.List;

/**
 * One-stop reporting screen: Inventory In / Out / Used / Return (transaction-based,
 * with an optional date range), Stock Update, Min Level, and Max Level reports.
 * Results can be exported to CSV.
 */
public class ReportsTab extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final String IN = "Inventory In (Receipts)";
    private static final String OUT = "Inventory Out (Issues)";
    private static final String USED = "Inventory Used";
    private static final String RETURNED = "Inventory Return";
    private static final String STOCK_UPDATE = "Stock Update (Current Levels)";
    private static final String MIN_LEVEL = "Min Level Report";
    private static final String MAX_LEVEL = "Max Level Report";

    private final ReportDAO reportDAO = new ReportDAO();
    private final JComboBox<String> reportTypeCombo = new JComboBox<>(
            new String[] {IN, OUT, USED, RETURNED, STOCK_UPDATE, MIN_LEVEL, MAX_LEVEL});
    private final JTextField fromDateField = new JTextField(10);
    private final JTextField toDateField = new JTextField(10);
    private final JLabel dateHint = new JLabel("Date range applies to In / Out / Used / Return only.");
    private final JLabel statusMessage = new JLabel(" ");
    private final DefaultTableModel tableModel = new DefaultTableModel();
    private final JTable table = new JTable(tableModel);

    public ReportsTab() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        add(UITheme.titleLabel("Reports"), BorderLayout.NORTH);

        UITheme.styleComboBox(reportTypeCombo);
        UITheme.styleTextField(fromDateField);
        UITheme.styleTextField(toDateField);
        dateHint.setFont(UITheme.FONT_BASE);
        dateHint.setForeground(UITheme.TEXT_MUTED);

        JPanel controls = UITheme.card();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row1.setBackground(UITheme.CARD_BG);
        row1.add(boldLabel("Report:"));
        row1.add(reportTypeCombo);
        row1.add(boldLabel("From (yyyy-mm-dd):"));
        row1.add(fromDateField);
        row1.add(boldLabel("To (yyyy-mm-dd):"));
        row1.add(toDateField);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row2.setBackground(UITheme.CARD_BG);
        JButton generateBtn = new JButton("Generate Report");
        UITheme.stylePrimaryButton(generateBtn);
        generateBtn.addActionListener(e -> generate());
        JButton exportBtn = new JButton("Export to CSV");
        UITheme.stylePrimaryButton(exportBtn);
        exportBtn.addActionListener(e -> exportCsv());
        row2.add(generateBtn);
        row2.add(exportBtn);

        controls.add(row1);
        controls.add(dateHint);
        controls.add(Box.createVerticalStrut(8));
        controls.add(row2);
        controls.add(statusMessage);

        UITheme.styleTable(table);

        JPanel north = new JPanel(new BorderLayout());
        north.setBackground(UITheme.BACKGROUND);
        north.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        north.add(controls, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(UITheme.BACKGROUND);
        center.add(north, BorderLayout.NORTH);
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        statusMessage.setFont(UITheme.FONT_BASE);
        generate();
    }

    private JLabel boldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.FONT_BOLD);
        return l;
    }

    private void generate() {
        String selected = (String) reportTypeCombo.getSelectedItem();
        Date fromDate = null;
        Date toDate = null;
        boolean needsDates = IN.equals(selected) || OUT.equals(selected) || USED.equals(selected) || RETURNED.equals(selected);
        if (needsDates) {
            try {
                if (!fromDateField.getText().trim().isEmpty()) fromDate = Date.valueOf(fromDateField.getText().trim());
                if (!toDateField.getText().trim().isEmpty()) toDate = Date.valueOf(toDateField.getText().trim());
            } catch (IllegalArgumentException ex) {
                statusMessage.setText("Dates must be in yyyy-mm-dd format.");
                statusMessage.setForeground(UITheme.STATUS_WARNING);
                return;
            }
        }

        ReportResult result;
        if (IN.equals(selected)) {
            result = reportDAO.getTransactionReport(InventoryDAO.TYPE_RECEIPT, fromDate, toDate);
        } else if (OUT.equals(selected)) {
            result = reportDAO.getTransactionReport(InventoryDAO.TYPE_ISSUE, fromDate, toDate);
        } else if (USED.equals(selected)) {
            result = reportDAO.getTransactionReport(InventoryDAO.TYPE_USED, fromDate, toDate);
        } else if (RETURNED.equals(selected)) {
            result = reportDAO.getTransactionReport(InventoryDAO.TYPE_RETURN, fromDate, toDate);
        } else if (STOCK_UPDATE.equals(selected)) {
            result = reportDAO.getStockUpdateReport();
        } else if (MIN_LEVEL.equals(selected)) {
            result = reportDAO.getMinLevelReport();
        } else {
            result = reportDAO.getMaxLevelReport();
        }

        tableModel.setColumnIdentifiers(result.getColumns());
        tableModel.setRowCount(0);
        for (Object[] row : result.getRows()) {
            tableModel.addRow(row);
        }
        statusMessage.setText(result.getRows().size() + " row(s).");
        statusMessage.setForeground(UITheme.TEXT_MUTED);
    }

    private void exportCsv() {
        if (tableModel.getRowCount() == 0) {
            statusMessage.setText("Nothing to export — generate a report first.");
            statusMessage.setForeground(UITheme.STATUS_WARNING);
            return;
        }
        JFileChooser chooser = new JFileChooser();
        String suggestedName = ((String) reportTypeCombo.getSelectedItem())
                .toLowerCase().replaceAll("[^a-z0-9]+", "_") + ".csv";
        chooser.setSelectedFile(new java.io.File(suggestedName));
        int choice = chooser.showSaveDialog(this);
        if (choice != JFileChooser.APPROVE_OPTION) return;

        try (PrintWriter writer = new PrintWriter(new FileWriter(chooser.getSelectedFile()))) {
            int colCount = tableModel.getColumnCount();
            StringBuilder header = new StringBuilder();
            for (int c = 0; c < colCount; c++) {
                if (c > 0) header.append(',');
                header.append(csvEscape(tableModel.getColumnName(c)));
            }
            writer.println(header);
            for (int r = 0; r < tableModel.getRowCount(); r++) {
                StringBuilder line = new StringBuilder();
                for (int c = 0; c < colCount; c++) {
                    if (c > 0) line.append(',');
                    Object value = tableModel.getValueAt(r, c);
                    line.append(csvEscape(value == null ? "" : value.toString()));
                }
                writer.println(line);
            }
            statusMessage.setText("✓ Exported to " + chooser.getSelectedFile().getName());
            statusMessage.setForeground(UITheme.STATUS_NORMAL);
        } catch (IOException e) {
            statusMessage.setText("✗ Export failed: " + e.getMessage());
            statusMessage.setForeground(UITheme.STATUS_CRITICAL);
        }
    }

    private String csvEscape(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
