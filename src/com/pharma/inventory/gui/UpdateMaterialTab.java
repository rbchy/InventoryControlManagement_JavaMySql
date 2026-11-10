package com.pharma.inventory.gui;

import com.pharma.inventory.dao.CategoryDAO;
import com.pharma.inventory.dao.MaterialDAO;
import com.pharma.inventory.dao.SupplierDAO;
import com.pharma.inventory.model.Material;
import com.pharma.inventory.model.MaterialCategory;
import com.pharma.inventory.model.Supplier;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** Lets the user edit an existing material's details and status. */
public class UpdateMaterialTab extends JPanel {
    private static final long serialVersionUID = 1L;

    private final MaterialDAO materialDAO = new MaterialDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();
    private final List<Refreshable> refreshTargets = new ArrayList<>();

    private int loadedMaterialId = -1;
    private final JLabel idValue = new JLabel("—");
    private final JLabel codeValue = new JLabel("—");
    private final JTextField nameField = new JTextField(24);
    private final JComboBox<MaterialCategory> categoryCombo = new JComboBox<>();
    private final JTextField unitField = new JTextField(24);
    private final JTextField priceField = new JTextField(24);
    private final JComboBox<Supplier> supplierCombo = new JComboBox<>();
    private final JSpinner minSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 1_000_000, 10));
    private final JSpinner maxSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 1_000_000, 10));
    private final JSpinner reorderSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 1_000_000, 10));
    private final JSpinner leadTimeSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 365, 1));
    private final JComboBox<String> statusCombo = new JComboBox<>(new String[] {"ACTIVE", "INACTIVE", "DISCONTINUED"});
    private final JLabel statusMessage = new JLabel(" ");

    public UpdateMaterialTab() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(UITheme.titleLabel("Update Material"), BorderLayout.NORTH);

        UITheme.styleTextField(nameField);
        UITheme.styleTextField(unitField);
        UITheme.styleTextField(priceField);
        UITheme.styleComboBox(categoryCombo);
        UITheme.styleComboBox(supplierCombo);
        UITheme.styleComboBox(statusCombo);

        JPanel form = UITheme.card();
        form.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        row = addRow(form, gbc, row, "Material ID:", idValue);
        row = addRow(form, gbc, row, "Material Code:", codeValue);
        row = addRow(form, gbc, row, "Name:", nameField);
        row = addRow(form, gbc, row, "Category:", categoryCombo);
        row = addRow(form, gbc, row, "Unit of Measurement:", unitField);
        row = addRow(form, gbc, row, "Current Price:", priceField);
        row = addRow(form, gbc, row, "Supplier:", supplierCombo);
        row = addRow(form, gbc, row, "Min Stock Level:", minSpinner);
        row = addRow(form, gbc, row, "Max Stock Level:", maxSpinner);
        row = addRow(form, gbc, row, "Reorder Point:", reorderSpinner);
        row = addRow(form, gbc, row, "Lead Time (days):", leadTimeSpinner);
        row = addRow(form, gbc, row, "Status:", statusCombo);

        JButton saveBtn = new JButton("Save Changes");
        UITheme.stylePrimaryButton(saveBtn);
        saveBtn.addActionListener(e -> save());

        gbc.gridx = 1;
        gbc.gridy = row;
        form.add(saveBtn, gbc);

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

    public void refreshDropdowns() {
        categoryCombo.removeAllItems();
        for (MaterialCategory c : categoryDAO.getAllCategories()) categoryCombo.addItem(c);
        supplierCombo.removeAllItems();
        for (Supplier s : supplierDAO.getAllSuppliers()) supplierCombo.addItem(s);
    }

    /** Called by ViewMaterialsTab when a row is selected. */
    public void loadSelectedRow(int materialId) {
        refreshDropdowns();
        Material m = materialDAO.getMaterialById(materialId);
        if (m == null) {
            statusMessage.setText("Could not load material #" + materialId);
            statusMessage.setForeground(UITheme.STATUS_CRITICAL);
            return;
        }
        loadedMaterialId = m.getMaterialId();
        idValue.setText(String.valueOf(m.getMaterialId()));
        codeValue.setText(m.getMaterialCode());
        nameField.setText(m.getMaterialName());
        unitField.setText(m.getUnitOfMeasurement());
        priceField.setText(String.valueOf(m.getCurrentPrice()));
        minSpinner.setValue(m.getMinStockLevel());
        maxSpinner.setValue(m.getMaxStockLevel());
        reorderSpinner.setValue(m.getReorderPoint());
        leadTimeSpinner.setValue(m.getLeadTimeDays());
        statusCombo.setSelectedItem(m.getStatus() == null ? "ACTIVE" : m.getStatus());
        selectComboById(categoryCombo, m.getCategoryId());
        selectComboById(supplierCombo, m.getSupplierId());
        statusMessage.setText(" ");
    }

    private void selectComboById(JComboBox<?> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            Object item = combo.getItemAt(i);
            if (item instanceof MaterialCategory && ((MaterialCategory) item).getCategoryId() == id) {
                combo.setSelectedIndex(i);
                return;
            }
            if (item instanceof Supplier && ((Supplier) item).getSupplierId() == id) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void save() {
        if (loadedMaterialId == -1) {
            statusMessage.setText("Select a material from 'View Materials' first.");
            statusMessage.setForeground(UITheme.STATUS_WARNING);
            return;
        }
        if (nameField.getText().trim().isEmpty() || categoryCombo.getSelectedItem() == null
                || supplierCombo.getSelectedItem() == null) {
            statusMessage.setText("Name, category and supplier are required.");
            statusMessage.setForeground(UITheme.STATUS_WARNING);
            return;
        }
        double price;
        try {
            price = Double.parseDouble(priceField.getText().trim());
        } catch (NumberFormatException ex) {
            statusMessage.setText("Current Price must be a number.");
            statusMessage.setForeground(UITheme.STATUS_WARNING);
            return;
        }
        Material m = new Material();
        m.setMaterialId(loadedMaterialId);
        m.setMaterialName(nameField.getText().trim());
        m.setCategoryId(((MaterialCategory) categoryCombo.getSelectedItem()).getCategoryId());
        m.setUnitOfMeasurement(unitField.getText().trim());
        m.setCurrentPrice(price);
        m.setSupplierId(((Supplier) supplierCombo.getSelectedItem()).getSupplierId());
        m.setMinStockLevel((Integer) minSpinner.getValue());
        m.setMaxStockLevel((Integer) maxSpinner.getValue());
        m.setReorderPoint((Integer) reorderSpinner.getValue());
        m.setLeadTimeDays((Integer) leadTimeSpinner.getValue());
        m.setStatus((String) statusCombo.getSelectedItem());

        if (materialDAO.updateMaterial(m)) {
            statusMessage.setText("✓ Saved.");
            statusMessage.setForeground(UITheme.STATUS_NORMAL);
            for (Refreshable r : refreshTargets) r.reloadData();
        } else {
            statusMessage.setText("✗ " + materialDAO.getLastError());
            statusMessage.setForeground(UITheme.STATUS_CRITICAL);
        }
    }
}
