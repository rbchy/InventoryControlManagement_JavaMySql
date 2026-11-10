package com.pharma.inventory.gui;

import javax.swing.*;
import java.awt.*;

/** One button that reloads every data-backed tab from the database. */
public class RefreshTab extends JPanel {
    private static final long serialVersionUID = 1L;

    public RefreshTab(Refreshable... targets) {
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        add(UITheme.titleLabel("Refresh"), BorderLayout.NORTH);

        JPanel card = UITheme.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        JLabel info = new JLabel("Reload the Dashboard, Suppliers, Transactions and Low Stock Alerts tabs from the database.");
        info.setFont(UITheme.FONT_BASE);

        JButton refreshBtn = new JButton("Refresh All Data");
        UITheme.stylePrimaryButton(refreshBtn);
        JLabel status = new JLabel(" ");
        status.setFont(UITheme.FONT_BASE);
        refreshBtn.addActionListener(e -> {
            for (Refreshable r : targets) {
                if (r != null) r.reloadData();
            }
            status.setText("✓ Refreshed at " + java.time.LocalTime.now().withNano(0));
            status.setForeground(UITheme.STATUS_NORMAL);
        });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnRow.setBackground(UITheme.CARD_BG);
        btnRow.add(refreshBtn);

        card.add(info);
        card.add(Box.createVerticalStrut(10));
        card.add(btnRow);
        card.add(status);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UITheme.BACKGROUND);
        wrapper.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        wrapper.add(card, BorderLayout.NORTH);
        add(wrapper, BorderLayout.CENTER);
    }
}
