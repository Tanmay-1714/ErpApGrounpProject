package edu.univ.erp.ui;
import edu.univ.erp.auth.UserSession;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.util.ThemeUtils;
import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {
    private AdminService service = new AdminService();
    private JCheckBox maintToggle;
    private JLabel banner;

    public AdminDashboard(String u) {
        setTitle("Admin - " + u);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));

        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
        JLabel l = new JLabel("System Admin"); l.setFont(ThemeUtils.TITLE_FONT);
        top.add(l, BorderLayout.WEST);

        maintToggle = new JCheckBox("Maintenance Mode");
        maintToggle.setSelected(service.isMaintenanceModeEnabled());
        top.add(maintToggle, BorderLayout.EAST);
        
        banner = new JLabel("MAINTENANCE MODE ON", SwingConstants.CENTER);
        banner.setOpaque(true); banner.setBackground(ThemeUtils.ERROR_RED);
        banner.setForeground(Color.WHITE); banner.setVisible(maintToggle.isSelected());
        
        JPanel n = new JPanel(new BorderLayout()); n.add(top, BorderLayout.NORTH); n.add(banner, BorderLayout.SOUTH);
        add(n, BorderLayout.NORTH);

        JPanel nav = new JPanel(new GridLayout(8,1,10,10));
        nav.setPreferredSize(new Dimension(220,0));
        String[] b = {"Users", "Courses", "Sections", "Instructors", "Enrollments", " ", "Password", "Logout"};
        JButton[] btns = new JButton[8];
        for(int i=0; i<8; i++) {
            if(b[i].equals(" ")) nav.add(new JLabel(""));
            else { btns[i] = new JButton(b[i]); nav.add(btns[i]); }
        }
        add(nav, BorderLayout.WEST);
        add(new JLabel("Admin Control Center", SwingConstants.CENTER), BorderLayout.CENTER);

        maintToggle.addActionListener(e -> {
            service.toggleMaintenanceMode(maintToggle.isSelected());
            banner.setVisible(maintToggle.isSelected());
        });

        btns[0].addActionListener(e -> new ManageUsersDialog(this));
        btns[1].addActionListener(e -> new ManageCoursesDialog(this));
        btns[2].addActionListener(e -> new ManageSectionsDialog(this));
        btns[3].addActionListener(e -> new AssignInstructorDialog(this));
        btns[4].addActionListener(e -> new ViewEnrollmentsDialog(this));
        btns[6].addActionListener(e -> new ChangePasswordDialog(this));
        btns[7].addActionListener(e -> { UserSession.getInstance().clearSession(); dispose(); new LoginWindow(); });

        ThemeUtils.applyTheme(this);
        if(maintToggle.isSelected()) banner.setBackground(ThemeUtils.ERROR_RED);
        setVisible(true);
    }
}
