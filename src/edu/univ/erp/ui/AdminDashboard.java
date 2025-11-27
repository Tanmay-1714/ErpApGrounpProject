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
        adminService = new AdminService();
        setTitle("ADMIN Dashboard - " + username);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel welcome = new JLabel("System Administrator");
        welcome.setFont(ThemeUtils.TITLE_FONT);
        topPanel.add(welcome, BorderLayout.WEST);

        maintenanceToggle = new JCheckBox("Maintenance Mode");
        maintenanceToggle.setSelected(adminService.isMaintenanceModeEnabled());
        topPanel.add(maintenanceToggle, BorderLayout.EAST);

        bannerLabel = new JLabel("MAINTENANCE MODE ON", SwingConstants.CENTER);
        bannerLabel.setOpaque(true);
        bannerLabel.setBackground(ThemeUtils.ERROR_RED);
        bannerLabel.setForeground(Color.WHITE);
        bannerLabel.setVisible(maintenanceToggle.isSelected());

        JPanel north = new JPanel(new BorderLayout());
        north.add(topPanel, BorderLayout.NORTH);
        north.add(bannerLabel, BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);

        navPanel = new JPanel(new GridLayout(8, 1, 10, 15));
        navPanel.setPreferredSize(new Dimension(250, 0));
        String[] btns = {"Manage Users", "Create Courses", "Create Sections", "Assign Instructors", "View Enrollments", " ", "Change Password", "Logout"};
        for(String b : btns) {
            if(b.equals(" ")) navPanel.add(new JLabel(""));
            else navPanel.add(new JButton(b));
        }
        add(navPanel, BorderLayout.WEST);
        add(new JLabel("Select an option.", SwingConstants.CENTER), BorderLayout.CENTER);

        addListeners();
        ThemeUtils.applyTheme(this);
        if(maintenanceToggle.isSelected()) bannerLabel.setBackground(ThemeUtils.ERROR_RED);
        setVisible(true);
    }

    private void addListeners() {
        maintenanceToggle.addActionListener(e -> {
            boolean on = maintenanceToggle.isSelected();
            String res = adminService.toggleMaintenanceMode(on);
            JOptionPane.showMessageDialog(this, res);
            if(!res.startsWith("SUCCESS")) maintenanceToggle.setSelected(!on);
            bannerLabel.setVisible(maintenanceToggle.isSelected());
        });

        // Helper to get buttons safely
        JButton[] buttons = new JButton[7];
        int idx = 0;
        for(Component c : navPanel.getComponents()) {
            if(c instanceof JButton) buttons[idx++] = (JButton)c;
        }

        buttons[0].addActionListener(e -> new ManageUsersDialog(this));
        buttons[1].addActionListener(e -> new ManageCoursesDialog(this));
        buttons[2].addActionListener(e -> new ManageSectionsDialog(this));
        buttons[3].addActionListener(e -> new AssignInstructorDialog(this));
        buttons[4].addActionListener(e -> new ViewEnrollmentsDialog(this));
        buttons[5].addActionListener(e -> new ChangePasswordDialog(this));
        buttons[6].addActionListener(e -> {
            UserSession.getInstance().clearSession();
            dispose(); new LoginWindow();
        });
    }
}
