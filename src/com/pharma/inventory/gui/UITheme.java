package com.pharma.inventory.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;

/**
 * Central place for the GUI's look & feel: colors, fonts, and small helpers
 * so every tab looks consistent without repeating styling code.
 */
public final class UITheme {
    private UITheme() {}

    public static final Color BACKGROUND = new Color(0xF4, 0xF7, 0xF6);
    public static final Color SIDEBAR_BG = new Color(0x1F, 0x3A, 0x3A);
    public static final Color SIDEBAR_BTN = new Color(0x2A, 0x4D, 0x4A);
    public static final Color SIDEBAR_BTN_HOVER = new Color(0x36, 0x63, 0x5F);
    public static final Color PRIMARY = new Color(0x1F, 0x7A, 0x6C);
    public static final Color PRIMARY_DARK = new Color(0x16, 0x5C, 0x51);
    public static final Color CARD_BG = Color.WHITE;
    public static final Color TEXT_DARK = new Color(0x22, 0x2B, 0x2A);
    public static final Color TEXT_MUTED = new Color(0x6B, 0x76, 0x74);

    public static final Color STATUS_CRITICAL = new Color(0xD9, 0x3B, 0x3B);
    public static final Color STATUS_WARNING = new Color(0xE0, 0x8E, 0x1D);
    public static final Color STATUS_NORMAL = new Color(0x2E, 0x8B, 0x4A);
    public static final Color STATUS_OVERSTOCK = new Color(0x2D, 0x6C, 0xC4);

    public static final Font FONT_BASE = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_TABLE_HEADER = new Font("Segoe UI", Font.BOLD, 13);

    public static void styleSidebarButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setFont(FONT_BASE);
        btn.setForeground(Color.WHITE);
        btn.setBackground(SIDEBAR_BTN);
        btn.setBorder(new EmptyBorder(10, 14, 10, 14));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.getModel().addChangeListener(e -> {
            ButtonModel m = btn.getModel();
            btn.setBackground(m.isRollover() || m.isPressed() ? SIDEBAR_BTN_HOVER : SIDEBAR_BTN);
        });
    }

    public static void stylePrimaryButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setFont(FONT_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setBackground(PRIMARY);
        btn.setBorder(new EmptyBorder(8, 18, 8, 18));
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.getModel().addChangeListener(e -> {
            ButtonModel m = btn.getModel();
            btn.setBackground(m.isRollover() || m.isPressed() ? PRIMARY_DARK : PRIMARY);
        });
    }

    public static void styleDangerButton(JButton btn) {
        stylePrimaryButton(btn);
        btn.setBackground(STATUS_CRITICAL);
        btn.getModel().addChangeListener(e -> {
            ButtonModel m = btn.getModel();
            btn.setBackground(m.isRollover() || m.isPressed() ? STATUS_CRITICAL.darker() : STATUS_CRITICAL);
        });
    }

    public static void styleTextField(JTextComponent field) {
        field.setFont(FONT_BASE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD3, 0xDA, 0xD8), 1, true),
                new EmptyBorder(6, 10, 6, 10)));
    }

    public static void styleComboBox(JComboBox<?> combo) {
        combo.setFont(FONT_BASE);
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD3, 0xDA, 0xD8), 1, true),
                new EmptyBorder(2, 6, 2, 6)));
    }

    /** Flat, no-chrome button used for "switch mode" links (e.g. Login <-> Register). */
    public static void styleLinkButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setFont(FONT_BOLD);
        btn.setForeground(PRIMARY);
        btn.setBackground(BACKGROUND);
        btn.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.getModel().addChangeListener(e -> {
            ButtonModel m = btn.getModel();
            btn.setForeground(m.isRollover() || m.isPressed() ? PRIMARY_DARK : PRIMARY);
        });
    }

    /** Slightly heavier card for auth screens / anything meant to look "raised". */
    public static JPanel elevatedCard() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0xDD, 0xE3, 0xE1), 1, true),
                        BorderFactory.createLineBorder(new Color(0xEC, 0xF0, 0xEF), 4, true)),
                new EmptyBorder(28, 32, 28, 32)));
        return panel;
    }

    public static void styleTable(JTable table) {
        table.setFont(FONT_BASE);
        table.setRowHeight(26);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(0xE3, 0xE8, 0xE7));
        table.setSelectionBackground(new Color(0xD8, 0xEC, 0xE7));
        table.setSelectionForeground(TEXT_DARK);
        table.getTableHeader().setFont(FONT_TABLE_HEADER);
        table.getTableHeader().setBackground(new Color(0xE9, 0xF0, 0xEE));
        table.getTableHeader().setForeground(TEXT_DARK);
        table.setFillsViewportHeight(true);
    }

    public static JLabel titleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_TITLE);
        label.setForeground(TEXT_DARK);
        return label;
    }

    public static JLabel sectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_BOLD);
        label.setForeground(TEXT_DARK);
        return label;
    }

    public static JPanel card() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE3, 0xE8, 0xE7), 1, true),
                new EmptyBorder(16, 16, 16, 16)));
        return panel;
    }

    public static Color statusColor(String status) {
        if (status == null) return TEXT_DARK;
        switch (status) {
            case "CRITICAL": return STATUS_CRITICAL;
            case "WARNING": return STATUS_WARNING;
            case "OVERSTOCK": return STATUS_OVERSTOCK;
            default: return STATUS_NORMAL;
        }
    }

    public static void applyGlobalFont() {
        java.util.Enumeration<?> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, new javax.swing.plaf.FontUIResource(FONT_BASE));
            }
        }
    }
}
