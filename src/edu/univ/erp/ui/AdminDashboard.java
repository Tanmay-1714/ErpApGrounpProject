package edu.univ.erp.ui;

import edu.univ.erp.auth.UserSession;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.util.ThemeUtils;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    private AdminService adminService;
    private JCheckBox maintenanceToggle;
    private JLabel bannerLabel;
    private JPanel navPanel;

    public AdminDashboard(String username) {
        this.adminService = new AdminService();

        setTitle("ADMIN Dashboard - " + username);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));

        // --- Top ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel welcomeLabel = new JLabel("System Administrator");
        welcomeLabel.setFont(ThemeUtils.TITLE_FONT);
        topPanel.add(welcomeLabel, BorderLayout.WEST);

        maintenanceToggle = new JCheckBox("Maintenance Mode");
        topPanel.add(maintenanceToggle, BorderLayout.EAST);

        bannerLabel = new JLabel("", SwingConstants.CENTER);
        bannerLabel.setOpaque(true);
        bannerLabel.setBackground(ThemeUtils.ERROR_RED);
        bannerLabel.setForeground(Color.WHITE);
        bannerLabel.setFont(ThemeUtils.HEADER_FONT);
        bannerLabel.setPreferredSize(new Dimension(10, 30));

        JPanel northContainer = new JPanel(new BorderLayout());
        northContainer.add(topPanel, BorderLayout.NORTH);
        northContainer.add(bannerLabel, BorderLayout.SOUTH);
        add(northContainer, BorderLayout.NORTH);

        // --- Nav ---
        navPanel = new JPanel(new GridLayout(8, 1, 10, 15));
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        navPanel.setPreferredSize(new Dimension(250, 0));

        navPanel.add(new JButton("Manage Users"));
        navPanel.add(new JButton("Create Courses"));
        navPanel.add(new JButton("Create Sections"));
        navPanel.add(new JButton("Assign Instructors"));
        navPanel.add(new JButton("View Enrollments"));
        navPanel.add(new JLabel("")); // Spacer
        navPanel.add(new JButton("Change Password"));
        navPanel.add(new JButton("Logout"));

        add(navPanel, BorderLayout.WEST);

        // --- Center ---
        JLabel centerMsg = new JLabel("Select an option from the menu.", SwingConstants.CENTER);
        centerMsg.setFont(ThemeUtils.HEADER_FONT);
        add(centerMsg, BorderLayout.CENTER);

        initializeMaintenanceMode();
        addListeners();

        // *** APPLY THEME ***
        ThemeUtils.applyTheme(this);
        if(maintenanceToggle.isSelected()) bannerLabel.setBackground(ThemeUtils.ERROR_RED);

        setVisible(true);
    }

    private void addListeners() {
        // 1. Manage Users
        ((JButton)navPanel.getComponent(0)).addActionListener(e -> new ManageUsersDialog(this));
        
        // 2. Create Courses
        ((JButton)navPanel.getComponent(1)).addActionListener(e -> new ManageCoursesDialog(this));
        
        // 3. Create Sections
        ((JButton)navPanel.getComponent(2)).addActionListener(e -> new ManageSectionsDialog(this));
        
        // 4. Assign Instructors
        ((JButton)navPanel.getComponent(3)).addActionListener(e -> new AssignInstructorDialog(this));
        
        // 5. View Enrollments
        ((JButton)navPanel.getComponent(4)).addActionListener(e -> new ViewEnrollmentsDialog(this));

        // 6. Change Password (Index 6)
        ((JButton)navPanel.getComponent(6)).addActionListener(e -> new ChangePasswordDialog(this));

        // 7. Logout (Index 7)
        ((JButton)navPanel.getComponent(7)).addActionListener(e -> {
            UserSession.getInstance().clearSession();
            dispose();
            new LoginWindow();
        });

        // Maintenance Toggle
        maintenanceToggle.addActionListener(e -> {
            boolean newState = maintenanceToggle.isSelected();
            String res = adminService.toggleMaintenanceMode(newState);
            JOptionPane.showMessageDialog(this, res);
            if (!res.startsWith("SUCCESS")) maintenanceToggle.setSelected(!newState);
            updateBanner(newState);
        });
    }

    private void initializeMaintenanceMode() {
        boolean on = adminService.isMaintenanceModeEnabled();
        maintenanceToggle.setSelected(on);
        updateBanner(on);
    }

    private void updateBanner(boolean on) {
        if (on) {
            bannerLabel.setText("!!! MAINTENANCE MODE ON - WRITES BLOCKED !!!");
            bannerLabel.setVisible(true);
        } else {
            bannerLabel.setVisible(false);
        }
        revalidate(); repaint();
    }
}
