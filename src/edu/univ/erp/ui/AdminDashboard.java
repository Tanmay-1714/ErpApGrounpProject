// File: edu.univ.erp.ui.AdminDashboard.java (FINAL CORRECT VERSION)

package edu.univ.erp.ui;

import edu.univ.erp.service.AdminService;
import javax.swing.*;
import java.awt.*;

// --- REQUIRED IMPORTS FOR ALL ADMIN DIALOGS ---
import edu.univ.erp.ui.ManageUsersDialog;
import edu.univ.erp.ui.ManageCoursesDialog;
import edu.univ.erp.ui.ManageSectionsDialog;
import edu.univ.erp.ui.AssignInstructorDialog;
// ---------------------------------------------

public class AdminDashboard extends JFrame {

    private AdminService adminService;
    private JCheckBox maintenanceToggle;
    private JLabel bannerLabel;
    private JPanel navPanel;

    public AdminDashboard(String username) {
        this.adminService = new AdminService();

        setTitle("ADMINISTRATOR Dashboard - " + username);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // --- 1. Top Panel Setup (Banner and Toggle) ---
        JPanel topPanel = new JPanel(new BorderLayout());

        // Welcome Header
        JLabel welcomeLabel = new JLabel("System Administrator - " + username, SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 22));
        topPanel.add(welcomeLabel, BorderLayout.CENTER);

        // Maintenance Mode Toggle Panel
        JPanel maintenancePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        maintenanceToggle = new JCheckBox("Maintenance Mode (View-Only)");
        maintenancePanel.add(maintenanceToggle);
        topPanel.add(maintenancePanel, BorderLayout.EAST);

        // Maintenance Banner
        bannerLabel = new JLabel("", SwingConstants.CENTER);
        bannerLabel.setOpaque(true);
        bannerLabel.setBackground(Color.RED);
        bannerLabel.setForeground(Color.WHITE);
        bannerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        bannerLabel.setPreferredSize(new Dimension(10, 25));

        // Initial state check
        initializeMaintenanceMode();

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(topPanel, BorderLayout.NORTH);
        northPanel.add(bannerLabel, BorderLayout.SOUTH);

        add(northPanel, BorderLayout.NORTH);

        // --- 2. Side Panel (Navigation - West) ---
        navPanel = new JPanel();
        navPanel.setLayout(new GridLayout(8, 1, 10, 10));
        navPanel.setBorder(BorderFactory.createTitledBorder("Admin Management Tools"));

        // Admin Features (BUTTONS)
        navPanel.add(new JButton("Manage Users (Add/View)"));     // Index 0
        navPanel.add(new JButton("Create Courses"));               // Index 1
        navPanel.add(new JButton("Create Sections"));              // Index 2
        navPanel.add(new JButton("Assign Instructors"));           // Index 3
        navPanel.add(new JButton("View All Enrollments"));
        navPanel.add(new JButton("System Settings/Logs"));
        navPanel.add(new JButton("Change Password"));
        navPanel.add(new JButton("Logout"));

        add(navPanel, BorderLayout.WEST);

        // --- 3. Main Content Area (Center) ---
        JLabel mainContent = new JLabel("Use the menu to perform system-wide management tasks.", SwingConstants.CENTER);
        add(mainContent, BorderLayout.CENTER);

        // Add Listeners
        addToggleListener();
        addNavigationListeners();

        setVisible(true);
    }

    /**
     * Adds action listeners to the side navigation buttons.
     */
    private void addNavigationListeners() {
        // Link "Manage Users (Add/View)" button (Index 0)
        JButton manageUsersButton = (JButton) navPanel.getComponent(0);
        manageUsersButton.addActionListener(e -> {
            ManageUsersDialog dialog = new ManageUsersDialog(this);
            dialog.setVisible(true);
        });

        // Link: "Create Courses" button (Index 1)
        JButton createCoursesButton = (JButton) navPanel.getComponent(1);
        createCoursesButton.addActionListener(e -> {
            ManageCoursesDialog dialog = new ManageCoursesDialog(this);
            dialog.setVisible(true);
        });

        // Link: "Create Sections" button (Index 2)
        JButton createSectionsButton = (JButton) navPanel.getComponent(2);
        createSectionsButton.addActionListener(e -> {
            ManageSectionsDialog dialog = new ManageSectionsDialog(this);
            dialog.setVisible(true);
        });

        // Link: "Assign Instructors" button (Index 3)
        JButton assignInstructorsButton = (JButton) navPanel.getComponent(3);
        assignInstructorsButton.addActionListener(e -> {
            AssignInstructorDialog dialog = new AssignInstructorDialog(this);
            dialog.setVisible(true);
        });

        // TODO: Link the remaining utility buttons (View All Enrollments, System Settings/Logs, etc.)
    }


    /**
     * Initializes the toggle state and banner based on the current DB setting.
     */
    private void initializeMaintenanceMode() {
        boolean isEnabled = adminService.isMaintenanceModeEnabled();
        maintenanceToggle.setSelected(isEnabled);
        updateMaintenanceBanner(isEnabled);
    }

    /**
     * Updates the visual banner based on the state.
     */
    private void updateMaintenanceBanner(boolean isEnabled) {
        if (isEnabled) {
            bannerLabel.setText("!!! SYSTEM IS IN MAINTENANCE MODE: ALL WRITES ARE BLOCKED FOR STUDENTS/INSTRUCTORS !!!");
            bannerLabel.setBackground(Color.RED);
            bannerLabel.setVisible(true);
        } else {
            bannerLabel.setText("");
            bannerLabel.setVisible(false);
        }
        // Force the layout to update after showing/hiding the banner
        this.revalidate();
        this.repaint();
    }

    /**
     * Attaches the action listener to the maintenance toggle checkbox.
     */
    private void addToggleListener() {
        maintenanceToggle.addActionListener(e -> {
            boolean newState = maintenanceToggle.isSelected();

            // Call the Admin Service to update the DB
            String result = adminService.toggleMaintenanceMode(newState);

            if (result.startsWith("SUCCESS")) {
                updateMaintenanceBanner(newState);
                JOptionPane.showMessageDialog(this, result, "System Update", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // If the DB update fails, revert the checkbox state
                maintenanceToggle.setSelected(!newState);
                JOptionPane.showMessageDialog(this, result, "System Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
