package com.pharma.inventory.gui;

import com.pharma.inventory.dao.CategoryDAO;
import com.pharma.inventory.dao.InventoryDAO;
import com.pharma.inventory.dao.MaterialDAO;
import com.pharma.inventory.dao.SupplierDAO;
import com.pharma.inventory.model.Material;
import com.pharma.inventory.model.MaterialCategory;
import com.pharma.inventory.model.Supplier;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** Registers a brand-new material and seeds its inventory row. */
public class AddMaterialTab extends JPanel {
    private static final long serialVersionUID = 1L;

    private final MaterialDAO materialDAO = new MaterialDAO();
    private final InventoryDAO inventoryDAO = new InventoryDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();
    private final List<Refreshable> refreshTargets = new ArrayList<>();

    private final JTextField codeField = new JTextField(24);
    private final JTextField nameField = new JTextField(24);
    private final JComboBox<MaterialCategory> categoryCombo = new JComboBox<>();
    private final JTextField unitField = new JTextField(24);
    private final JTextField priceField = new JTextField(24);
    private final JComboBox<Supplier> supplierCombo = new JComboBox<>();
    private final JSpinner minSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 1_000_000, 10));
    private final JSpinner maxSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 1_000_000, 10));
    private final JSpinner reorderSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 1_000_000, 10));
    private final JSpinner leadTimeSpinner = new JSpinner(new SpinnerNumberModel(7, 0, 365, 1));
    private final JSpinner initialQtySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 1_000_000, 10));
    private final JTextField warehouseField = new JTextField(24);
    private final JLabel statusMessage = new JLabel(" ");

    public AddMaterialTab() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        add(UITheme.titleLabel("Add New Material"), BorderLayout.NORTH);

        UITheme.styleTextField(codeField);
        UITheme.styleTextField(nameField);
        UITheme.styleTextField(unitField);
        UITheme.styleTextField(priceField);
        UITheme.styleTextField(warehouseField);
        UITheme.styleComboBox(categoryCombo);
        UITheme.styleComboBox(supplierCombo);

        JPanel form = UITheme.card();
        form.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        row = addRow(form, gbc, row, "Material Code:", codeField);
        row = addRow(form, gbc, row, "Name:", nameField);
        row = addRow(form, gbc, row, "Category:", categoryCombo);
        row = addRow(form, gbc, row, "Unit of Measurement:", unitField);
        row = addRow(form, gbc, row, "Current Price:", priceField);
        row = addRow(form, gbc, row, "Supplier:", supplierCombo);
        row = addRow(form, gbc, row, "Min Stock Level:", minSpinner);
        row = addRow(form, gbc, row, "Max Stock Level:", maxSpinner);
        row = addRow(form, gbc, row, "Reorder Point:", reorderSpinner);
        row = addRow(form, gbc, row, "Lead Time (days):", leadTimeSpinner);
        row = addRow(form, gbc, row, "Initial Quantity:", initialQtySpinner);
        row = addRow(form, gbc, row, "Warehouse Location:", warehouseField);

        JButton addBtn = new JButton("Add Material");
        UITheme.stylePrimaryButton(addBtn);
        addBtn.addActionListener(e -> submit());
        gbc.gridx = 1;
        gbc.gridy = row;
        form.add(addBtn, gbc);

        statusMessage.setFont(UITheme.FONT_BASE);
        gbc.gridy = row + 1;
        form.add(statusMessage, gbc);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UITheme.BACKGROUND);
        wrapper.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        wrapper.add(form, BorderLayout.NORTH);
        add(wrapper, BorderLayout.CENTER);

        refreshDropdowns();
    }

    private int addRow(JPanel form, GridBagConstraints gbc, int row, String label, Component field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        JLabel l = new JLabel(label);
        l.setFont(UITheme.FONT_BOLD);
        form.add(l, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        form.add(field, gbc);
        return row + 1;
    }

    public void setRefreshTargets(Refreshable... targets) {
        for (Refreshable r : targets) refreshTargets.add(r);
    }

    /** Public so the "Refresh" tab can re-pull categories/suppliers if new ones were added elsewhere. */
    public void refreshDropdowns() {
        MaterialCategory selectedCat = (MaterialCategory) categoryCombo.getSelectedItem();
        categoryCombo.removeAllItems();
        for (MaterialCategory c : categoryDAO.getAllCategories()) categoryCombo.addItem(c);
        if (selectedCat != null) categoryCombo.setSelectedItem(selectedCat);

        Supplier selectedSup = (Supplier) supplierCombo.getSelectedItem();
        supplierCombo.removeAllItems();
        for (Supplier s : supplierDAO.getAllSuppliers()) supplierCombo.addItem(s);
        if (selectedSup != null) supplierCombo.setSelectedItem(selectedSup);
    }

    private void submit() {
        String code = codeField.getText().trim();
        String name = nameField.getText().trim();
        if (code.isEmpty() || name.isEmpty() || categoryCombo.getSelectedItem() == null
                || supplierCombo.getSelectedItem() == null) {
            statusMessage.setText("Code, name, category and supplier are required.");
            statusMessage.setForeground(UITheme.STATUS_WARNING);
            return;
        }
        double price;
        try {
            price = priceField.getText().trim().isEmpty() ? 0.0 : Double.parseDouble(priceField.getText().trim());
        } catch (NumberFormatException ex) {
            statusMessage.setText("Current Price must be a number.");
            statusMessage.setForeground(UITheme.STATUS_WARNING);
            return;
        }

        Material m = new Material();
        m.setMaterialCode(code);
        m.setMaterialName(name);
        m.setCategoryId(((MaterialCategory) categoryCombo.getSelectedItem()).getCategoryId());
        m.setUnitOfMeasurement(unitField.getText().trim());
        m.setCurrentPrice(price);
        m.setSupplierId(((Supplier) supplierCombo.getSelectedItem()).getSupplierId());
        m.setMinStockLevel((Integer) minSpinner.getValue());
        m.setMaxStockLevel((Integer) maxSpinner.getValue());
        m.setReorderPoint((Integer) reorderSpinner.getValue());
        m.setLeadTimeDays((Integer) leadTimeSpinner.getValue());
        m.setStatus("ACTIVE");

        if (!materialDAO.addMaterial(m)) {
            statusMessage.setText("✗ " + materialDAO.getLastError());
            statusMessage.setForeground(UITheme.STATUS_CRITICAL);
            return;
        }

        int initialQty = (Integer) initialQtySpinner.getValue();
        String warehouse = warehouseField.getText().trim();
        if (!inventoryDAO.createInventoryRecord(m.getMaterialId(), initialQty, warehouse)) {
            statusMessage.setText("Material saved, but inventory row failed: " + inventoryDAO.getLastError());
            statusMessage.setForeground(UITheme.STATUS_WARNING);
        } else {
            statusMessage.setText("✓ Material '" + name + "' added.");
            statusMessage.setForeground(UITheme.STATUS_NORMAL);
        }
        clearForm();
        for (Refreshable r : refreshTargets) r.reloadData();
    }

    private void clearForm() {
        codeField.setText("");
        nameField.setText("");
        unitField.setText("");
        priceField.setText("");
        minSpinner.setValue(0);
        maxSpinner.setValue(0);
        reorderSpinner.setValue(0);
        leadTimeSpinner.setValue(7);
        initialQtySpinner.setValue(0);
        warehouseField.setText("");
    }
}
