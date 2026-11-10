package com.pharma.inventory.gui;

import com.pharma.inventory.dao.UserDAO;
import com.pharma.inventory.model.User;
import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/** Login form shown inside AuthScreen. */
public class LoginPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final UserDAO userDAO = new UserDAO();
    private final JTextField usernameField = new JTextField(18);
    private final JPasswordField passwordField = new JPasswordField(18);
    private final JLabel statusMessage = new JLabel(" ");

    public LoginPanel(Consumer<User> onLoginSuccess, Runnable onSwitchToRegister) {
        setLayout(new GridBagLayout());
        setBackground(UITheme.BACKGROUND);

        JPanel card = UITheme.elevatedCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setMaximumSize(new Dimension(420, 500));

        JLabel appName = new JLabel("Pharma Inventory");
        appName.setFont(UITheme.FONT_TITLE);
        appName.setForeground(UITheme.TEXT_DARK);
        appName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tagline = new JLabel("Sign in to manage your stock");
        tagline.setFont(UITheme.FONT_SUBTITLE);
        tagline.setForeground(UITheme.TEXT_MUTED);
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        UITheme.styleTextField(usernameField);
        UITheme.styleTextField(passwordField);
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        JLabel userLbl = fieldLabel("Username");
        JLabel passLbl = fieldLabel("Password");

        JButton loginBtn = new JButton("Log In");
        UITheme.stylePrimaryButton(loginBtn);
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        loginBtn.addActionListener(e -> attemptLogin(onLoginSuccess));
        passwordField.addActionListener(e -> attemptLogin(onLoginSuccess));

        statusMessage.setFont(UITheme.FONT_BASE);
        statusMessage.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton registerLink = new JButton("New here? Create an account");
        UITheme.styleLinkButton(registerLink);
        registerLink.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerLink.addActionListener(e -> onSwitchToRegister.run());

        card.add(appName);
        card.add(Box.createVerticalStrut(4));
        card.add(tagline);
        card.add(Box.createVerticalStrut(24));
        card.add(userLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(14));
        card.add(passLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(18));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(10));
        card.add(statusMessage);
        card.add(Box.createVerticalStrut(6));
        card.add(registerLink);

        add(card);
    }

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.FONT_BOLD);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void attemptLogin(Consumer<User> onLoginSuccess) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            statusMessage.setText("Enter both username and password.");
            statusMessage.setForeground(UITheme.STATUS_WARNING);
            return;
        }
        User user = userDAO.authenticate(username, password);
        if (user != null) {
            statusMessage.setText(" ");
            passwordField.setText("");
            onLoginSuccess.accept(user);
        } else {
            statusMessage.setText("✗ " + userDAO.getLastError());
            statusMessage.setForeground(UITheme.STATUS_CRITICAL);
        }
    }

    /** Called by AuthScreen when switching back to this panel, so old input doesn't linger. */
    public void reset() {
        usernameField.setText("");
        passwordField.setText("");
        statusMessage.setText(" ");
    }

    /** Called right after a successful registration so the user can log straight in. */
    public void prefillAfterRegistration(String username) {
        usernameField.setText(username);
        passwordField.setText("");
        statusMessage.setText("✓ Account created — log in below.");
        statusMessage.setForeground(UITheme.STATUS_NORMAL);
    }
}
