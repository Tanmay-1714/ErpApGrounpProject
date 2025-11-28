package edu.univ.erp.ui;

import edu.univ.erp.auth.UserSession;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.util.ThemeUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AdminDashboard extends JFrame {
    private AdminService adminService;
    private JLabel bannerLabel;
    private JPanel contentArea;
    private CardLayout cardLayout;

    public AdminDashboard(String username) {
        this.adminService = new AdminService();

        setTitle("Admin Dashboard | University ERP");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- 1. Sidebar Navigation (Left) ---
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(ThemeUtils.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBorder(new EmptyBorder(20, 0, 20, 0));

        // Title in Sidebar
        JLabel brand = new JLabel("  ADMIN PANEL");
        brand.setFont(ThemeUtils.TITLE_FONT);
        brand.setForeground(ThemeUtils.ACCENT_YELLOW);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(brand);
        sidebar.add(Box.createRigidArea(new Dimension(0, 40)));

        // Nav Buttons
        sidebar.add(createNavButton("Dashboard Home", "HOME"));
        sidebar.add(createNavButton("Manage Users", "USERS"));
        sidebar.add(createNavButton("Manage Courses", "COURSES"));
        sidebar.add(createNavButton("Manage Sections", "SECTIONS"));
        sidebar.add(createNavButton("Assign Instructors", "ASSIGN"));
        sidebar.add(createNavButton("View Enrollments", "ENROLL"));
        sidebar.add(createNavButton("Database Backup", "BACKUP"));
        sidebar.add(Box.createVerticalGlue()); // Push bottom buttons down
        sidebar.add(createNavButton("Change Password", "PASS"));
        sidebar.add(createNavButton("Logout", "LOGOUT"));

        add(sidebar, BorderLayout.WEST);

        // --- 2. Main Content Area (Center) ---
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ThemeUtils.MAIN_BG);

        // Top Bar (Maintenance Toggle)
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel userLbl = new JLabel("Logged in as: " + username);
        userLbl.setFont(ThemeUtils.REGULAR_FONT);
        
        JCheckBox maintToggle = new JCheckBox("Maintenance Mode");
        maintToggle.setFont(ThemeUtils.REGULAR_FONT);
        maintToggle.setBackground(Color.WHITE);
        maintToggle.setSelected(adminService.isMaintenanceModeEnabled());
        
        // Banner
        bannerLabel = new JLabel("⚠ SYSTEM IS IN MAINTENANCE MODE", SwingConstants.CENTER);
        bannerLabel.setOpaque(true);
        bannerLabel.setBackground(ThemeUtils.ERROR_RED);
        bannerLabel.setForeground(Color.WHITE);
        bannerLabel.setFont(ThemeUtils.BUTTON_FONT);
        bannerLabel.setVisible(maintToggle.isSelected());

        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topRight.setBackground(Color.WHITE);
        topRight.add(userLbl);
        topRight.add(Box.createHorizontalStrut(20));
        topRight.add(maintToggle);

        topBar.add(bannerLabel, BorderLayout.NORTH);
        topBar.add(topRight, BorderLayout.CENTER);
        mainPanel.add(topBar, BorderLayout.NORTH);

        // Dynamic Content
        cardLayout = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(ThemeUtils.MAIN_BG);
        contentArea.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Add the Home Dashboard Card
        contentArea.add(createDashboardHome(), "HOME");
        
        mainPanel.add(contentArea, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        // --- Listeners ---
        maintToggle.addActionListener(e -> {
            String res = adminService.toggleMaintenanceMode(maintToggle.isSelected());
            JOptionPane.showMessageDialog(this, res);
            bannerLabel.setVisible(maintToggle.isSelected());
            if (!res.startsWith("SUCCESS")) maintToggle.setSelected(!maintToggle.isSelected());
        });

        setVisible(true);
    }

    private JButton createNavButton(String text, String actionCommand) {
        JButton btn = ThemeUtils.createSidebarButton(text);
        btn.setMaximumSize(new Dimension(250, 50));
        
        btn.addActionListener(e -> {
            switch (actionCommand) {
                case "HOME": cardLayout.show(contentArea, "HOME"); refreshStats(); break;
                case "USERS": new ManageUsersDialog(this); break;
                case "COURSES": new ManageCoursesDialog(this); break;
                case "SECTIONS": new ManageSectionsDialog(this); break;
                case "ASSIGN": new AssignInstructorDialog(this); break;
                case "ENROLL": new ViewEnrollmentsDialog(this); break;
                case "BACKUP": edu.univ.erp.service.DBBackupService.performBackup(this); break;
                case "PASS": new ChangePasswordDialog(this); break;
                case "LOGOUT": 
                    UserSession.getInstance().clearSession();
                    dispose(); 
                    new LoginWindow();
                    break;
            }
        });
        return btn;
    }

    private JPanel createDashboardHome() {
        JPanel p = new JPanel(new BorderLayout(0, 20));
        p.setBackground(ThemeUtils.MAIN_BG);

        JLabel title = new JLabel("Dashboard Overview");
        title.setFont(ThemeUtils.TITLE_FONT);
        p.add(title, BorderLayout.NORTH);

        JPanel statsGrid = new JPanel(new GridLayout(1, 3, 20, 0));
        statsGrid.setBackground(ThemeUtils.MAIN_BG);
        
        // We calculate real stats using the service
        int enrollCount = adminService.getAllEnrollments().size();
        
        statsGrid.add(ThemeUtils.createStatCard("Total Enrollments", String.valueOf(enrollCount), new Color(66, 135, 245)));
        statsGrid.add(ThemeUtils.createStatCard("System Status", "Active", new Color(76, 175, 80)));
        statsGrid.add(ThemeUtils.createStatCard("Pending Tasks", "0", new Color(255, 152, 0)));

        p.add(statsGrid, BorderLayout.CENTER);
        return p;
    }
    
    private void refreshStats() {
        // In a real app, we would rebuild the home panel here to update numbers
        // For now, the structure is static but data loads on init
    }
}
