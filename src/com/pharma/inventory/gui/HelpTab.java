package com.pharma.inventory.gui;

import javax.swing.*;
import java.awt.*;

/** Static in-app documentation for how to use the inventory system. */
public class HelpTab extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final String HELP_HTML =
            "<html><body style='font-family: Segoe UI, sans-serif; font-size: 10.5pt; color: #22282A; padding: 6px 10px;'>"
            + "<h1 style='color:#165C51;'>Using Pharma Inventory</h1>"
            + "<p>This system tracks packaging materials, suppliers, and stock movements for a "
            + "pharmaceutical packaging operation. Here's what each tab does and how to use it.</p>"

            + "<h2 style='color:#1F7A6C;'>Stock Dashboard (View Materials)</h2>"
            + "<p>Shows every active material with its on-hand quantity and a <b>Status</b> column:</p>"
            + "<ul>"
            + "<li><b style='color:#D93B3B;'>CRITICAL</b> &ndash; at or below the minimum stock level, reorder urgently</li>"
            + "<li><b style='color:#E08E1D;'>WARNING</b> &ndash; at or below the reorder point</li>"
            + "<li><b style='color:#2E8B4A;'>NORMAL</b> &ndash; healthy stock level</li>"
            + "<li><b style='color:#2D6CC4;'>OVERSTOCK</b> &ndash; above the maximum stock level</li>"
            + "</ul>"
            + "<p>Type in the Filter box to search by code, name, or category. Click any row to open it in "
            + "<b>Update Material</b>.</p>"

            + "<h2 style='color:#1F7A6C;'>Add / Update / Delete Material</h2>"
            + "<p><b>Add Material</b> registers a brand-new item and creates its starting inventory record in one step. "
            + "<b>Update Material</b> edits price, stock thresholds, supplier, or status for an item you selected from "
            + "the dashboard. <b>Delete Material</b> deactivates an item by code &mdash; it is never permanently removed, "
            + "so past transactions and purchase orders stay intact.</p>"

            + "<h2 style='color:#1F7A6C;'>Search Material</h2>"
            + "<p>Looks up materials by code or name across <i>every</i> status, including deactivated ones &mdash; "
            + "useful for finding something Delete Material hid from the main dashboard.</p>"

            + "<h2 style='color:#1F7A6C;'>Suppliers</h2>"
            + "<p>View registered suppliers and register new ones. Every material must be linked to a supplier.</p>"

            + "<h2 style='color:#1F7A6C;'>Transactions</h2>"
            + "<p>Records stock movements. Choose <b>RECEIPT</b> when stock arrives (adds to on-hand quantity) or "
            + "<b>ISSUE</b> when stock leaves for production (subtracts from it). The system will refuse an ISSUE "
            + "that would take a material below zero.</p>"

            + "<h2 style='color:#1F7A6C;'>Low Stock Alerts</h2>"
            + "<p>A live list of everything at or under its reorder point, with the supplier to contact and how much "
            + "to order to reach the maximum stock level.</p>"

            + "<h2 style='color:#1F7A6C;'>Refresh</h2>"
            + "<p>Reloads the Dashboard, Suppliers, Transactions, and Low Stock Alerts tabs from the database in one "
            + "click &mdash; handy if changes were made elsewhere.</p>"

            + "<h2 style='color:#1F7A6C;'>Accounts</h2>"
            + "<p>Self-registered accounts are created as OPERATOR or MANAGER. Use <b>Logout</b> in the top bar to "
            + "sign out and return to the login screen.</p>"

            + "<h2 style='color:#1F7A6C;'>Troubleshooting</h2>"
            + "<p>If a tab shows a database error instead of data, the app couldn't reach MySQL. Check that "
            + "the <code>DB_URL</code>, <code>DB_USER</code>, and <code>DB_PASSWORD</code> environment variables are "
            + "set correctly and that MySQL is running, then use the Refresh tab.</p>"
            + "</body></html>";

    public HelpTab() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        add(UITheme.titleLabel("Help"), BorderLayout.NORTH);

        JEditorPane editorPane = new JEditorPane("text/html", HELP_HTML);
        editorPane.setEditable(false);
        editorPane.setOpaque(false);
        editorPane.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        JScrollPane scroll = new JScrollPane(editorPane);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }
}
