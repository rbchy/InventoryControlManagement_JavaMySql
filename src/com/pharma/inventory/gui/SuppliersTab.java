package com.pharma.inventory.gui;

import com.pharma.inventory.dao.SupplierDAO;
import com.pharma.inventory.model.Supplier;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/** Views existing suppliers and registers new ones. */
public class SuppliersTab extends JPanel implements Refreshable {
    private static final long serialVersionUID = 1L;
    private static final String[] COLUMNS = {
            "ID", "Name", "Contact Person", "Email", "Phone", "City", "Payment Terms", "Rating"
    };

    private final SupplierDAO supplierDAO = new SupplierDAO();
    private final DefaultTableModel tableModel;

    private final JTextField nameField = new JTextField(18);
    private final JTextField contactField = new JTextField(18);
    private final JTextField emailField = new JTextField(18);
    private final JTextField phoneField = new JTextField(18);
    private final JTextField addressField = new JTextField(18);
    private final JTextField cityField = new JTextField(18);
    private final JTextField countryField = new JTextField(18);
    private final JTextField termsField = new JTextField(18);
    private final JSpinner ratingSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 5, 1));
    private final JLabel statusMessage = new JLabel(" ");
    private Runnable onSupplierAdded;

    public SuppliersTab() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        add(UITheme.titleLabel("Suppliers"), BorderLayout.NORTH);

        UITheme.styleTextField(nameField);
        UITheme.styleTextField(contactField);
        UITheme.styleTextField(emailField);
        UITheme.styleTextField(phoneField);
        UITheme.styleTextField(addressField);
        UITheme.styleTextField(cityField);
        UITheme.styleTextField(countryField);
        UITheme.styleTextField(termsField);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(tableModel);
        UITheme.styleTable(table);

        JPanel form = UITheme.card();
        form.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel row1 = fieldRow("Name:", nameField, "Contact Person:", contactField);
        JPanel row2 = fieldRow("Email:", emailField, "Phone:", phoneField);
        JPanel row3 = fieldRow("Address:", addressField, "City:", cityField);
        JPanel row4 = fieldRow("Country:", countryField, "Payment Terms:", termsField);

        JPanel row5 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row5.setBackground(UITheme.CARD_BG);
        JLabel ratingLbl = new JLabel("Rating (1-5):");
        ratingLbl.setFont(UITheme.FONT_BOLD);
        row5.add(ratingLbl);
        row5.add(ratingSpinner);
        JButton addBtn = new JButton("Add Supplier");
        UITheme.stylePrimaryButton(addBtn);
        addBtn.addActionListener(e -> addSupplier());
        row5.add(addBtn);

        JPanel formStack = new JPanel();
        formStack.setBackground(UITheme.CARD_BG);
        formStack.setLayout(new BoxLayout(formStack, BoxLayout.Y_AXIS));
        formStack.add(UITheme.sectionLabel("Register New Supplier"));
        formStack.add(row1);
        formStack.add(row2);
        formStack.add(row3);
        formStack.add(row4);
        formStack.add(row5);
        statusMessage.setFont(UITheme.FONT_BASE);
        formStack.add(statusMessage);

        JPanel south = new JPanel(new BorderLayout());
        south.setBackground(UITheme.BACKGROUND);
        south.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        south.add(formStack, BorderLayout.CENTER);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(UITheme.BACKGROUND);
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        center.add(south, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);

        reloadData();
    }

    private JPanel fieldRow(String label1, JTextField field1, String label2, JTextField field2) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(UITheme.CARD_BG);
        JLabel l1 = new JLabel(label1);
        l1.setFont(UITheme.FONT_BOLD);
        JLabel l2 = new JLabel(label2);
        l2.setFont(UITheme.FONT_BOLD);
        panel.add(l1);
        panel.add(field1);
        panel.add(l2);
        panel.add(field2);
        return panel;
    }

    public void setOnSupplierAdded(Runnable callback) {
        this.onSupplierAdded = callback;
    }

    @Override
    public void reloadData() {
        tableModel.setRowCount(0);
        List<Supplier> suppliers = supplierDAO.getAllSuppliers();
        for (Supplier s : suppliers) {
            tableModel.addRow(new Object[] {
                    s.getSupplierId(), s.getSupplierName(), s.getContactPerson(), s.getEmail(),
                    s.getPhone(), s.getCity(), s.getPaymentTerms(), s.getRating()
            });
        }
    }

    private void addSupplier() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            statusMessage.setText("Supplier name is required.");
            statusMessage.setForeground(UITheme.STATUS_WARNING);
            return;
        }
        Supplier s = new Supplier();
        s.setSupplierName(name);
        s.setContactPerson(contactField.getText().trim());
        s.setEmail(emailField.getText().trim());
        s.setPhone(phoneField.getText().trim());
        s.setAddress(addressField.getText().trim());
        s.setCity(cityField.getText().trim());
        s.setCountry(countryField.getText().trim());
        s.setPaymentTerms(termsField.getText().trim());
        s.setRating((Integer) ratingSpinner.getValue());

        if (supplierDAO.addSupplier(s)) {
            statusMessage.setText("✓ Supplier '" + name + "' added.");
            statusMessage.setForeground(UITheme.STATUS_NORMAL);
            nameField.setText("");
            contactField.setText("");
            emailField.setText("");
            phoneField.setText("");
            addressField.setText("");
            cityField.setText("");
            countryField.setText("");
            termsField.setText("");
            ratingSpinner.setValue(5);
            reloadData();
            if (onSupplierAdded != null) onSupplierAdded.run();
        } else {
            statusMessage.setText("✗ " + supplierDAO.getLastError());
            statusMessage.setForeground(UITheme.STATUS_CRITICAL);
        }
    }
}
