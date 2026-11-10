package com.pharma.inventory.gui;

import com.pharma.inventory.dao.UserDAO;
import com.pharma.inventory.model.User;
import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/** New-account registration form shown inside AuthScreen. */
public class RegisterPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final UserDAO userDAO = new UserDAO();
    private final JTextField usernameField = new JTextField(18);
    private final JTextField fullNameField = new JTextField(18);
    private final JTextField emailField = new JTextField(18);
    private final JTextField departmentField = new JTextField(18);
    // Public self-registration is intentionally limited to non-admin roles.
    private final JComboBox<String> roleCombo = new JComboBox<>(new String[] {"OPERATOR", "MANAGER"});
    private final JPasswordField passwordField = new JPasswordField(18);
    private final JPasswordField confirmField = new JPasswordField(18);
    private final JLabel statusMessage = new JLabel(" ");

    public RegisterPanel(Runnable onSwitchToLogin, Consumer<String> onRegistered) {
        setLayout(new GridBagLayout());
        setBackground(UITheme.BACKGROUND);

        JPanel card = UITheme.elevatedCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setMaximumSize(new Dimension(460, 700));

        JLabel title = new JLabel("Create Your Account");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tagline = new JLabel("Join Pharma Inventory to start tracking stock");
        tagline.setFont(UITheme.FONT_SUBTITLE);
        tagline.setForeground(UITheme.TEXT_MUTED);
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(tagline);
        card.add(Box.createVerticalStrut(20));

        card.add(labeledField("Full Name", fullNameField));
        card.add(labeledField("Username", usernameField));
        card.add(labeledField("Email", emailField));
        card.add(labeledField("Department", departmentField));
        card.add(labeledCombo("Role", roleCombo));
        card.add(labeledField("Password", passwordField));
        card.add(labeledField("Confirm Password", confirmField));

        JButton registerBtn = new JButton("Register");
        UITheme.stylePrimaryButton(registerBtn);
        registerBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        registerBtn.addActionListener(e -> submit(onRegistered));

        statusMessage.setFont(UITheme.FONT_BASE);
        statusMessage.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton backLink = new JButton("Already have an account? Log in");
        UITheme.styleLinkButton(backLink);
        backLink.setAlignmentX(Component.LEFT_ALIGNMENT);
        backLink.addActionListener(e -> onSwitchToLogin.run());

        card.add(Box.createVerticalStrut(6));
        card.add(registerBtn);
        card.add(Box.createVerticalStrut(10));
        card.add(statusMessage);
        card.add(Box.createVerticalStrut(6));
        card.add(backLink);

        JScrollPane scroll = new JScrollPane(card);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll);
    }

    private JPanel labeledField(String label, JTextField field) {
        UITheme.styleTextField(field);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        return wrap(label, field);
    }

    private JPanel labeledCombo(String label, JComboBox<?> combo) {
        UITheme.styleComboBox(combo);
        combo.setAlignmentX(Component.LEFT_ALIGNMENT);
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        return wrap(label, combo);
    }

    private JPanel wrap(String label, JComponent field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UITheme.CARD_BG);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel l = new JLabel(label);
        l.setFont(UITheme.FONT_BOLD);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(l);
        panel.add(Box.createVerticalStrut(4));
        panel.add(field);
        panel.add(Box.createVerticalStrut(12));
        return panel;
    }

    private void submit(Consumer<String> onRegistered) {
        String username = usernameField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String department = departmentField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmField.getPassword());

        if (username.isEmpty() || fullName.isEmpty() || password.isEmpty()) {
            statusMessage.setText("Full name, username and password are required.");
            statusMessage.setForeground(UITheme.STATUS_WARNING);
            return;
        }
        if (password.length() < 6) {
            statusMessage.setText("Password must be at least 6 characters.");
            statusMessage.setForeground(UITheme.STATUS_WARNING);
            return;
        }
        if (!password.equals(confirm)) {
            statusMessage.setText("Passwords do not match.");
            statusMessage.setForeground(UITheme.STATUS_WARNING);
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setDepartment(department);
        user.setRole((String) roleCombo.getSelectedItem());

        if (userDAO.registerUser(user, password)) {
            statusMessage.setText("✓ Account created. Redirecting to login…");
            statusMessage.setForeground(UITheme.STATUS_NORMAL);
            String createdUsername = username;
            clearForm();
            onRegistered.accept(createdUsername);
        } else {
            statusMessage.setText("✗ " + userDAO.getLastError());
            statusMessage.setForeground(UITheme.STATUS_CRITICAL);
        }
    }

    private void clearForm() {
        usernameField.setText("");
        fullNameField.setText("");
        emailField.setText("");
        departmentField.setText("");
        passwordField.setText("");
        confirmField.setText("");
        roleCombo.setSelectedIndex(0);
    }

    public void reset() {
        clearForm();
        statusMessage.setText(" ");
    }
}
