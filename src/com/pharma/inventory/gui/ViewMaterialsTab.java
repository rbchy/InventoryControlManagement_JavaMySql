package com.pharma.inventory.gui;

import com.pharma.inventory.dao.InventoryDAO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

/**
 * Live stock dashboard. Backed by the `inventory_summary` SQL view so the
 * stock_status (CRITICAL/WARNING/NORMAL/OVERSTOCK) and stock_value columns
 * come straight from the database instead of being recomputed here.
 */
public class ViewMaterialsTab extends JPanel implements Refreshable {
    private static final long serialVersionUID = 1L;
    private static final String[] COLUMNS = {
            "ID", "Code", "Material Name", "Category", "On Hand", "Reserved",
            "Available", "Min Level", "Max Level", "Stock Value", "Status"
    };

    private final InventoryDAO inventoryDAO = new InventoryDAO();
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final UpdateMaterialTab updateTab;
    private final JTabbedPane tabbedPane;

    public ViewMaterialsTab(UpdateMaterialTab updateTab, JTabbedPane tabbedPane) {
        this.updateTab = updateTab;
        this.tabbedPane = tabbedPane;
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.setDefaultRenderer(Object.class, new StatusRowRenderer());

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        JTextField search = new JTextField(24);
        search.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() {
                String text = search.getText();
                sorter.setRowFilter(text.isEmpty() ? null : RowFilter.regexFilter("(?i)" + text, 1, 2, 3));
            }
        });

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(UITheme.BACKGROUND);
        top.add(UITheme.titleLabel("Stock Dashboard"), BorderLayout.WEST);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(UITheme.BACKGROUND);
        JLabel searchLabel = new JLabel("Filter: ");
        searchLabel.setFont(UITheme.FONT_BASE);
        searchPanel.add(searchLabel);
        searchPanel.add(search);
        top.add(searchPanel, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int modelRow = table.convertRowIndexToModel(table.getSelectedRow());
                int materialId = (int) tableModel.getValueAt(modelRow, 0);
                if (this.updateTab != null) {
                    this.updateTab.loadSelectedRow(materialId);
                    int idx = this.tabbedPane.indexOfTab("Update Material");
                    if (idx != -1) this.tabbedPane.setSelectedIndex(idx);
                }
            }
        });

        reloadData();
    }

    @Override
    public void reloadData() {
        tableModel.setRowCount(0);
        List<Object[]> rows = inventoryDAO.getInventorySummary();
        for (Object[] row : rows) {
            tableModel.addRow(row);
        }
    }

    /** Colors the Status column text and keeps everything else legible. */
    private class StatusRowRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected,
                                                         boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
            int modelRow = tbl.convertRowIndexToModel(row);
            String status = String.valueOf(tableModel.getValueAt(modelRow, 10));
            if (column == 10) {
                c.setForeground(isSelected ? Color.WHITE : UITheme.statusColor(status));
                setFont(UITheme.FONT_BOLD);
            } else {
                c.setForeground(isSelected ? UITheme.TEXT_DARK : UITheme.TEXT_DARK);
                setFont(UITheme.FONT_BASE);
            }
            return c;
        }
    }
}
