package com.pharma.inventory.gui;

import com.pharma.inventory.config.DatabaseConfig;
import com.pharma.inventory.model.User;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Swing front-end for the Pharmaceutical Packaging Inventory Management System.
 * Root layout is a CardLayout with two screens: AUTH (login/register, shown first)
 * and APP (top bar + sidebar + tabbed pane, shown after a successful login).
 */
public class PharmaInventoryGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final String AUTH_CARD = "AUTH";
    private static final String APP_CARD = "APP";

    private final CardLayout rootLayout = new CardLayout();
    private final JPanel rootPanel = new JPanel();
    private JTabbedPane tabbedPane;
    private AuthScreen authScreen;
    private TopBar topBar;

    public PharmaInventoryGUI() {
        setTitle("Pharmaceutical Packaging Inventory Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.BACKGROUND);

        checkDbConnection();

        rootPanel.setLayout(rootLayout);
        authScreen = new AuthScreen(this::onLoginSuccess);
        JPanel appPanel = buildAppPanel();

        rootPanel.add(authScreen, AUTH_CARD);
        rootPanel.add(appPanel, APP_CARD);
        rootLayout.show(rootPanel, AUTH_CARD);

        add(rootPanel);
    }

    private void onLoginSuccess(User user) {
        topBar.setUser(user);
        rootLayout.show(rootPanel, APP_CARD);
        tabbedPane.setSelectedIndex(0);
    }

    private void onLogout() {
        topBar.setUser(null);
        authScreen.resetToLogin();
        rootLayout.show(rootPanel, AUTH_CARD);
    }

    private JPanel buildAppPanel() {
        JPanel appPanel = new JPanel(new BorderLayout());
        appPanel.setBackground(UITheme.BACKGROUND);

        topBar = new TopBar(this::onLogout);
        appPanel.add(topBar, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();

        // Constructed in dependency order so setter-injection can wire the
        // circular refresh relationships afterwards (View <-> Update, etc).
        WelcomeTab welcomeTab = new WelcomeTab();
        UpdateMaterialTab updateTab = new UpdateMaterialTab();
        ViewMaterialsTab viewTab = new ViewMaterialsTab(updateTab, tabbedPane);
        AddMaterialTab addTab = new AddMaterialTab();
        DeleteMaterialTab deleteTab = new DeleteMaterialTab();
        SearchMaterialTab searchTab = new SearchMaterialTab();
        SuppliersTab suppliersTab = new SuppliersTab();
        TransactionsTab transactionsTab = new TransactionsTab();
        LowStockAlertsTab lowStockTab = new LowStockAlertsTab();
        ReportsTab reportsTab = new ReportsTab();

        addTab.setRefreshTargets(viewTab, lowStockTab, welcomeTab);
        updateTab.setRefreshTargets(viewTab, lowStockTab, welcomeTab);
        deleteTab.setRefreshTargets(viewTab, lowStockTab, welcomeTab);
        transactionsTab.setRefreshTargets(viewTab, lowStockTab, welcomeTab);
        suppliersTab.setOnSupplierAdded(() -> {
            addTab.refreshDropdowns();
            updateTab.refreshDropdowns();
        });

        RefreshTab refreshTab = new RefreshTab(welcomeTab, viewTab, suppliersTab, transactionsTab, lowStockTab);

        tabbedPane.addTab("Welcome", welcomeTab);
        tabbedPane.addTab("Add Material", addTab);
        tabbedPane.addTab("View Materials", viewTab);
        tabbedPane.addTab("Update Material", updateTab);
        tabbedPane.addTab("Delete Material", deleteTab);
        tabbedPane.addTab("Search Material", searchTab);
        tabbedPane.addTab("Suppliers", suppliersTab);
        tabbedPane.addTab("Transactions", transactionsTab);
        tabbedPane.addTab("Low Stock Alerts", lowStockTab);
        tabbedPane.addTab("Reports", reportsTab);
        tabbedPane.addTab("Help", new HelpTab());
        tabbedPane.addTab("Refresh", refreshTab);
        tabbedPane.setFont(UITheme.FONT_BOLD);

        JPanel sidebar = createSidebar();
        appPanel.add(sidebar, BorderLayout.WEST);
        appPanel.add(tabbedPane, BorderLayout.CENTER);
        return appPanel;
    }

    private void checkDbConnection() {
        try (Connection conn = DatabaseConfig.getConnection()) {
            System.out.println("LOG: Connected to MySQL!");
        } catch (SQLException e) {
            System.err.println("ERROR: Cannot connect to DB: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Database connection failed:\n" + e.getMessage()
                            + "\n\nThe app will still open, but login and every tab will show this error "
                            + "until the DB_URL / DB_USER / DB_PASSWORD environment variables are correct.",
                    "Connection Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UITheme.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(190, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel brand = new JLabel("Navigation");
        brand.setForeground(Color.WHITE);
        brand.setFont(new Font("Segoe UI", Font.BOLD, 13));
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        brand.setBorder(BorderFactory.createEmptyBorder(0, 16, 14, 16));
        sidebar.add(brand);

        String[][] labelsWithIcons = {
                {"Welcome", "🏠"}, {"Add Material", "➕"}, {"View Materials", "📊"},
                {"Update Material", "✏️"}, {"Delete Material", "🗑️"}, {"Search Material", "🔍"},
                {"Suppliers", "🏢"}, {"Transactions", "🔄"}, {"Low Stock Alerts", "⚠️"},
                {"Reports", "📋"}, {"Help", "❓"}, {"Refresh", "⟳"}
        };
        for (String[] entry : labelsWithIcons) {
            String label = entry[0];
            JButton btn = new JButton(entry[1] + "  " + label);
            UITheme.styleSidebarButton(btn);
            btn.addActionListener(e -> {
                int idx = tabbedPane.indexOfTab(label);
                if (idx != -1) tabbedPane.setSelectedIndex(idx);
            });
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(2));
        }

        sidebar.add(Box.createVerticalGlue());

        JButton exitBtn = new JButton("⏻  Exit");
        UITheme.styleDangerButton(exitBtn);
        exitBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        exitBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        exitBtn.addActionListener(e -> System.exit(0));
        sidebar.add(exitBtn);

        return sidebar;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            UITheme.applyGlobalFont();
            new PharmaInventoryGUI().setVisible(true);
        });
    }
}
