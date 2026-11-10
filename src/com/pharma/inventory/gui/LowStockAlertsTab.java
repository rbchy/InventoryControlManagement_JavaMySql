package com.pharma.inventory.gui;

import com.pharma.inventory.dao.InventoryDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/** Materials at or below their reorder point, backed by the `low_stock_items` view. */
public class LowStockAlertsTab extends JPanel implements Refreshable {
    private static final long serialVersionUID = 1L;
    private static final String[] COLUMNS = {
            "Code", "Material Name", "On Hand", "Min Level", "Max Level", "Qty to Order", "Supplier", "Reorder Pt"
    };

    private final InventoryDAO inventoryDAO = new InventoryDAO();
    private final DefaultTableModel tableModel;
    private final JLabel countLabel = new JLabel(" ");

    public LowStockAlertsTab() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(UITheme.BACKGROUND);
        top.add(UITheme.titleLabel("Low Stock Alerts"), BorderLayout.WEST);
        countLabel.setFont(UITheme.FONT_BOLD);
        top.add(countLabel, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.setDefaultRenderer(Object.class, new WarningRenderer());
        add(new JScrollPane(table), BorderLayout.CENTER);

        reloadData();
    }

    @Override
    public void reloadData() {
        tableModel.setRowCount(0);
        List<Object[]> rows = inventoryDAO.getLowStockItems();
        for (Object[] row : rows) {
            tableModel.addRow(row);
        }
        countLabel.setText(rows.isEmpty() ? "All materials healthy ✓" : rows.size() + " material(s) need reordering");
        countLabel.setForeground(rows.isEmpty() ? UITheme.STATUS_NORMAL : UITheme.STATUS_CRITICAL);
    }

    private static class WarningRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected,
                                                         boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
            c.setForeground(isSelected ? Color.WHITE : UITheme.STATUS_CRITICAL);
            setFont(UITheme.FONT_BASE);
            return c;
        }
    }
}
