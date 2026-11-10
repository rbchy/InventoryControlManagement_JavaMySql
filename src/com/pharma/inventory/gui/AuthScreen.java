package com.pharma.inventory.gui;

import com.pharma.inventory.model.User;
import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/** Holds the Login and Register panels and flips between them with a CardLayout. */
public class AuthScreen extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final String LOGIN = "LOGIN";
    private static final String REGISTER = "REGISTER";

    private final CardLayout cardLayout = new CardLayout();
    private final LoginPanel loginPanel;
    private final RegisterPanel registerPanel;

    public AuthScreen(Consumer<User> onLoginSuccess) {
        setLayout(cardLayout);
        setBackground(UITheme.BACKGROUND);

        loginPanel = new LoginPanel(onLoginSuccess, this::showRegister);
        registerPanel = new RegisterPanel(this::showLogin, username -> {
            showLogin();
            loginPanel.prefillAfterRegistration(username);
        });

        add(loginPanel, LOGIN);
        add(registerPanel, REGISTER);
        cardLayout.show(this, LOGIN);
    }

    private void showLogin() {
        cardLayout.show(this, LOGIN);
    }

    private void showRegister() {
        registerPanel.reset();
        cardLayout.show(this, REGISTER);
    }

    /** Called on logout so a fresh visit to this screen starts with clean fields. */
    public void resetToLogin() {
        loginPanel.reset();
        showLogin();
    }
}
