package com.pharma.inventory.gui;

import com.pharma.inventory.model.User;
import javax.swing.*;
import java.awt.*;

/** Persistent header shown across the whole app once a user is logged in: branding + who's logged in + Logout. */
public class TopBar extends JPanel {
    private static final long serialVersionUID = 1L;

    private final JLabel userLabel = new JLabel(" ");

    public TopBar(Runnable onLogout) {
        setLayout(new BorderLayout());
        setBackground(UITheme.PRIMARY_DARK);
        setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        setPreferredSize(new Dimension(0, 52));

        JLabel brand = new JLabel("Pharma Inventory");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 17));
        brand.setForeground(Color.WHITE);
        add(brand, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        right.setOpaque(false);

        userLabel.setFont(UITheme.FONT_BASE);
        userLabel.setForeground(Color.WHITE);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFocusPainted(false);
        logoutBtn.setFont(UITheme.FONT_BOLD);
        logoutBtn.setForeground(UITheme.PRIMARY_DARK);
        logoutBtn.setBackground(Color.WHITE);
        logoutBtn.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        logoutBtn.setBorderPainted(false);
        logoutBtn.setOpaque(true);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Log out of Pharma Inventory?",
                    "Confirm Logout", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                onLogout.run();
            }
        });

        right.add(userLabel);
        right.add(logoutBtn);
        add(right, BorderLayout.EAST);
    }

    public void setUser(User user) {
        if (user == null) {
            userLabel.setText(" ");
        } else {
            userLabel.setText(user.getFullName() + "  ·  " + user.getRole());
        }
    }
}
