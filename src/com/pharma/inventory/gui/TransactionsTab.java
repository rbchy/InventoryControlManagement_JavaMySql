package com.pharma.inventory.gui;

import com.pharma.inventory.dao.InventoryDAO;
import com.pharma.inventory.dao.MaterialDAO;
import com.pharma.inventory.model.Material;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** Shows recent stock movements and records new receipts/issues. */
public class TransactionsTab extends JPanel implements Refreshable {
    private static final long serialVersionUID = 1L;
    private static final String[] COLUMNS = {
            "Txn ID", "Code", "Material Name", "Type", "Quantity", "Date", "Reference", "By"
    };

    private final InventoryDAO inventoryDAO = new InventoryDAO();
    private final MaterialDAO materialDAO = new MaterialDAO();
    private final List<Refreshable> refreshTargets = new ArrayList<>();
    private final DefaultTableModel tableModel;

    private final JTextField codeField = new JTextField(12);
    private final JComboBox<String> typeCombo = new JComboBox<>(InventoryDAO.ALL_TRANSACTION_TYPES);
    private final JComboBox<String> refTypeCombo = new JComboBox<>(
            new String[] {"MANUAL", "PRODUCTION", "PURCHASE_ORDER", "ADJUSTMENT"});
    private final JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1_000_000, 1));
    private final JTextField referenceField = new JTextField(14);
    private final JTextField byField = new JTextField(14);
    private final JLabel statusMessage = new JLabel(" ");
    private final JLabel typeHint = new JLabel(" ");

    public TransactionsTab() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        add(UITheme.titleLabel("Stock Transactions"), BorderLayout.NORTH);

        UITheme.styleTextField(codeField);
        UITheme.styleTextField(referenceField);
        UITheme.styleTextField(byField);
        UITheme.styleComboBox(typeCombo);
        UITheme.styleComboBox(refTypeCombo);
        typeHint.setFont(UITheme.FONT_BASE);
        typeHint.setForeground(UITheme.TEXT_MUTED);
        typeCombo.addActionListener(e -> updateTypeHint());
        updateTypeHint();

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(tableModel);
        UITheme.styleTable(table);

        JPanel form = UITheme.card();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.add(UITheme.sectionLabel("Record Stock Movement"));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row1.setBackground(UITheme.CARD_BG);
        row1.add(boldLabel("Material Code:"));
        row1.add(codeField);
        row1.add(boldLabel("Type:"));
        row1.add(typeCombo);
        row1.add(boldLabel("Quantity:"));
        row1.add(qtySpinner);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row2.setBackground(UITheme.CARD_BG);
        row2.add(boldLabel("Batch / Reference #:"));
        row2.add(referenceField);
        row2.add(boldLabel("Reference Type:"));
        row2.add(refTypeCombo);
        row2.add(boldLabel("Recorded By:"));
        row2.add(byField);

        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row3.setBackground(UITheme.CARD_BG);
        JButton recordBtn = new JButton("Record Transaction");
        UITheme.stylePrimaryButton(recordBtn);
        recordBtn.addActionListener(e -> record());
        row3.add(recordBtn);

        statusMessage.setFont(UITheme.FONT_BASE);

        form.add(row1);
        form.add(typeHint);
        form.add(row2);
        form.add(row3);
        form.add(statusMessage);

        JPanel south = new JPanel(new BorderLayout());
        south.setBackground(UITheme.BACKGROUND);
        south.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        south.add(form, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(UITheme.BACKGROUND);
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        center.add(south, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);

        reloadData();
    }

    private JLabel boldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.FONT_BOLD);
        return l;
    }

    private void updateTypeHint() {
        String type = (String) typeCombo.getSelectedItem();
        if (InventoryDAO.TYPE_RECEIPT.equals(type)) {
            typeHint.setText("RECEIPT adds stock — use when new material arrives from a supplier.");
        } else if (InventoryDAO.TYPE_ISSUE.equals(type)) {
            typeHint.setText("ISSUE removes stock — use when material is sent to production for a batch.");
        } else if (InventoryDAO.TYPE_USED.equals(type)) {
            typeHint.setText("USED removes stock — material consumed directly (no separate issue step). "
                    + "For batch reconciliation, set Reference Type to PRODUCTION.");
        } else if (InventoryDAO.TYPE_RETURN.equals(type)) {
            typeHint.setText("RETURN adds stock back — use for unused material returned once a batch finishes.");
        } else {
            typeHint.setText(" ");
        }
    }

    public void setRefreshTargets(Refreshable... targets) {
        for (Refreshable r : targets) refreshTargets.add(r);
    }

    @Override
    public void reloadData() {
        tableModel.setRowCount(0);
        List<Object[]> rows = inventoryDAO.getRecentTransactionsView();
        for (Object[] row : rows) {
            tableModel.addRow(row);
        }
    }

    private void record() {
        String code = codeField.getText().trim();
        if (code.isEmpty()) {
            statusMessage.setText("Material code is required.");
            statusMessage.setForeground(UITheme.STATUS_WARNING);
            return;
        }
        Material material = null;
        for (Material m : materialDAO.searchMaterials(code)) {
            if (m.getMaterialCode().equalsIgnoreCase(code)) {
                material = m;
                break;
            }
        }
        if (material == null) {
            statusMessage.setText("No material found with code '" + code + "'.");
            statusMessage.setForeground(UITheme.STATUS_CRITICAL);
            return;
        }
        String type = (String) typeCombo.getSelectedItem();
        String refType = (String) refTypeCombo.getSelectedItem();
        int qty = (Integer) qtySpinner.getValue();
        String reference = referenceField.getText().trim();
        String by = byField.getText().trim().isEmpty() ? System.getProperty("user.name", "system") : byField.getText().trim();

        if (inventoryDAO.recordTransaction(material.getMaterialId(), type, qty, reference, refType, by)) {
            statusMessage.setText("✓ " + type + " of " + qty + " " + material.getUnitOfMeasurement()
                    + " recorded for " + material.getMaterialName() + ".");
            statusMessage.setForeground(UITheme.STATUS_NORMAL);
            codeField.setText("");
            referenceField.setText("");
            qtySpinner.setValue(1);
            reloadData();
            for (Refreshable r : refreshTargets) r.reloadData();
        } else {
            statusMessage.setText("✗ " + inventoryDAO.getLastError());
            statusMessage.setForeground(UITheme.STATUS_CRITICAL);
        }
    }
}
