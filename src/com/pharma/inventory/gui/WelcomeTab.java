package com.pharma.inventory.gui;

import com.pharma.inventory.dao.InventoryDAO;
import com.pharma.inventory.dao.MaterialDAO;
import com.pharma.inventory.dao.SupplierDAO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/** Home screen: a pharma-themed banner with live quick-stats and a feature summary. */
public class WelcomeTab extends JPanel implements Refreshable {
    private static final long serialVersionUID = 1L;

    private final MaterialDAO materialDAO = new MaterialDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();
    private final InventoryDAO inventoryDAO = new InventoryDAO();

    private final JLabel materialsValue = new JLabel("—");
    private final JLabel suppliersValue = new JLabel("—");
    private final JLabel lowStockValue = new JLabel("—");

    public WelcomeTab() {
        setLayout(new BorderLayout());

        BackgroundPanel banner = new BackgroundPanel();
        banner.setPreferredSize(new Dimension(0, 340));
        banner.setLayout(new BorderLayout());

        JPanel textBlock = new JPanel();
        textBlock.setOpaque(false);
        textBlock.setLayout(new BoxLayout(textBlock, BoxLayout.Y_AXIS));
        textBlock.setBorder(BorderFactory.createEmptyBorder(46, 50, 0, 50));

        JLabel eyebrow = new JLabel("PHARMACEUTICAL PACKAGING");
        eyebrow.setFont(new Font("Segoe UI", Font.BOLD, 13));
        eyebrow.setForeground(new Color(255, 255, 255, 210));

        JLabel title = new JLabel("Inventory Control Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(Color.WHITE);

        JLabel tagline = new JLabel("Real-time stock tracking & supply chain visibility for your packaging line.");
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tagline.setForeground(new Color(255, 255, 255, 225));

        textBlock.add(eyebrow);
        textBlock.add(Box.createVerticalStrut(8));
        textBlock.add(title);
        textBlock.add(Box.createVerticalStrut(6));
        textBlock.add(tagline);

        banner.add(textBlock, BorderLayout.NORTH);

        JPanel statsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        statsRow.setOpaque(false);
        statsRow.setBorder(BorderFactory.createEmptyBorder(0, 44, 30, 44));
        statsRow.add(statCard(materialsValue, "Active Materials"));
        statsRow.add(statCard(suppliersValue, "Suppliers"));
        statsRow.add(statCard(lowStockValue, "Need Reordering"));
        banner.add(statsRow, BorderLayout.SOUTH);

        add(banner, BorderLayout.NORTH);
        add(buildFeatureSummary(), BorderLayout.CENTER);

        reloadData();
    }

    private JPanel statCard(JLabel valueLabel, String caption) {
        RoundedPanel card = new RoundedPanel(new Color(255, 255, 255, 235), 14);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(14, 22, 14, 22));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valueLabel.setForeground(UITheme.PRIMARY_DARK);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel captionLabel = new JLabel(caption);
        captionLabel.setFont(UITheme.FONT_BASE);
        captionLabel.setForeground(UITheme.TEXT_MUTED);
        captionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(valueLabel);
        card.add(captionLabel);
        return card;
    }

    private JScrollPane buildFeatureSummary() {
        JPanel body = new JPanel();
        body.setBackground(UITheme.BACKGROUND);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createEmptyBorder(24, 50, 30, 50));

        body.add(UITheme.sectionLabel("What you can do here"));
        body.add(Box.createVerticalStrut(10));

        JTextArea info = new JTextArea(
                "• View Materials – live stock dashboard with color-coded status\n"
                + "• Add / Update / Delete Material – manage the material catalog\n"
                + "• Search Material – find any material by code or name\n"
                + "• Suppliers – view and register suppliers\n"
                + "• Transactions – record Receipts, Issues, Used, and Returns for a production batch\n"
                + "• Reports – Inventory In / Out / Used / Return, Stock Update, Min Level, Max Level, exportable to CSV\n"
                + "• Low Stock Alerts – materials at or below their reorder point\n"
                + "• Refresh – reload every tab from the database\n");
        info.setEditable(false);
        info.setFocusable(false);
        info.setOpaque(false);
        info.setFont(UITheme.FONT_BASE);
        info.setForeground(UITheme.TEXT_DARK);
        info.setAlignmentX(Component.LEFT_ALIGNMENT);

        body.add(info);

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    @Override
    public void reloadData() {
        materialsValue.setText(String.valueOf(materialDAO.getAllMaterials().size()));
        suppliersValue.setText(String.valueOf(supplierDAO.getAllSuppliers().size()));
        lowStockValue.setText(String.valueOf(inventoryDAO.getLowStockItems().size()));
    }

    /** A JPanel that paints a translucent rounded-rectangle background instead of a plain rectangle. */
    private static class RoundedPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private final Color fill;
        private final int arc;

        RoundedPanel(Color fill, int arc) {
            this.fill = fill;
            this.arc = arc;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fill);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), arc, arc));
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
