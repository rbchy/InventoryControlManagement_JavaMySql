package com.pharma.inventory.gui;

import com.pharma.inventory.dao.MaterialDAO;
import com.pharma.inventory.model.Material;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** Soft-deletes (deactivates) a material by code. Materials are never hard-deleted. */
public class DeleteMaterialTab extends JPanel {
    private static final long serialVersionUID = 1L;

    private final MaterialDAO materialDAO = new MaterialDAO();
    private final List<Refreshable> refreshTargets = new ArrayList<>();

    private final JTextField codeField = new JTextField(20);
    private final JLabel foundLabel = new JLabel(" ");
    private final JButton deleteBtn = new JButton("Deactivate Material");
    private Material found;

    public DeleteMaterialTab() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        add(UITheme.titleLabel("Delete Material"), BorderLayout.NORTH);

        UITheme.styleTextField(codeField);

        JPanel card = UITheme.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchRow.setBackground(UITheme.CARD_BG);
        JLabel lbl = new JLabel("Material Code:");
        lbl.setFont(UITheme.FONT_BOLD);
        JButton findBtn = new JButton("Find");
        UITheme.stylePrimaryButton(findBtn);
        findBtn.addActionListener(e -> find());
        searchRow.add(lbl);
        searchRow.add(codeField);
        searchRow.add(findBtn);

        foundLabel.setFont(UITheme.FONT_BASE);
        UITheme.styleDangerButton(deleteBtn);
        deleteBtn.setEnabled(false);
        deleteBtn.addActionListener(e -> delete());

        JPanel deleteRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        deleteRow.setBackground(UITheme.CARD_BG);
        deleteRow.add(deleteBtn);

        card.add(searchRow);
        card.add(foundLabel);
        card.add(deleteRow);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UITheme.BACKGROUND);
        wrapper.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        wrapper.add(card, BorderLayout.NORTH);
        add(wrapper, BorderLayout.CENTER);
    }

    public void setRefreshTargets(Refreshable... targets) {
        for (Refreshable r : targets) refreshTargets.add(r);
    }

    private void find() {
        String code = codeField.getText().trim();
        found = null;
        deleteBtn.setEnabled(false);
        if (code.isEmpty()) {
            foundLabel.setText("Enter a material code first.");
            foundLabel.setForeground(UITheme.STATUS_WARNING);
            return;
        }
        List<Material> results = materialDAO.searchMaterials(code);
        for (Material m : results) {
            if (m.getMaterialCode().equalsIgnoreCase(code)) {
                found = m;
                break;
            }
        }
        if (found == null) {
            foundLabel.setText("No material found with code '" + code + "'.");
            foundLabel.setForeground(UITheme.STATUS_CRITICAL);
        } else if ("INACTIVE".equals(found.getStatus())) {
            foundLabel.setText("Found: " + found.getMaterialName() + " — already INACTIVE.");
            foundLabel.setForeground(UITheme.STATUS_WARNING);
        } else {
            foundLabel.setText("Found: " + found.getMaterialName() + " (status: " + found.getStatus() + ")");
            foundLabel.setForeground(UITheme.TEXT_DARK);
            deleteBtn.setEnabled(true);
        }
    }

    private void delete() {
        if (found == null) return;
        int confirm = JOptionPane.showConfirmDialog(this,
                "Deactivate '" + found.getMaterialName() + "' (" + found.getMaterialCode() + ")?\n"
                        + "It will be hidden from active dashboards but its history is kept.",
                "Confirm Deactivation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        if (materialDAO.softDeleteMaterial(found.getMaterialId())) {
            foundLabel.setText("✓ '" + found.getMaterialName() + "' deactivated.");
            foundLabel.setForeground(UITheme.STATUS_NORMAL);
            deleteBtn.setEnabled(false);
            codeField.setText("");
            found = null;
            for (Refreshable r : refreshTargets) r.reloadData();
        } else {
            foundLabel.setText("✗ " + materialDAO.getLastError());
            foundLabel.setForeground(UITheme.STATUS_CRITICAL);
        }
    }
}
