package com.pharma.inventory.gui;

import com.pharma.inventory.dao.MaterialDAO;
import com.pharma.inventory.model.Material;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/** Search materials by code or name, across every status (including deactivated ones). */
public class SearchMaterialTab extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final String[] COLUMNS = {
            "ID", "Code", "Name", "Unit", "Price", "Min", "Max", "Reorder Pt", "Status"
    };

    private final MaterialDAO materialDAO = new MaterialDAO();
    private final DefaultTableModel tableModel;
    private final JTextField searchField = new JTextField(24);

    public SearchMaterialTab() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        add(UITheme.titleLabel("Search Material"), BorderLayout.NORTH);

        UITheme.styleTextField(searchField);

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchRow.setBackground(UITheme.BACKGROUND);
        searchRow.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        JLabel lbl = new JLabel("Code or Name contains:");
        lbl.setFont(UITheme.FONT_BOLD);
        JButton searchBtn = new JButton("Search");
        UITheme.stylePrimaryButton(searchBtn);
        searchBtn.addActionListener(e -> search());
        searchField.addActionListener(e -> search());
        searchRow.add(lbl);
        searchRow.add(searchField);
        searchRow.add(searchBtn);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(tableModel);
        UITheme.styleTable(table);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(UITheme.BACKGROUND);
        center.add(searchRow, BorderLayout.NORTH);
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
    }

    private void search() {
        tableModel.setRowCount(0);
        String keyword = searchField.getText().trim();
        List<Material> results = materialDAO.searchMaterials(keyword);
        for (Material m : results) {
            tableModel.addRow(new Object[] {
                    m.getMaterialId(), m.getMaterialCode(), m.getMaterialName(), m.getUnitOfMeasurement(),
                    m.getCurrentPrice(), m.getMinStockLevel(), m.getMaxStockLevel(), m.getReorderPoint(),
                    m.getStatus()
            });
        }
    }
}
