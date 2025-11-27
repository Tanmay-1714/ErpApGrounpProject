package edu.univ.erp.ui;
import edu.univ.erp.auth.AuthService;
import edu.univ.erp.auth.UserSession;
import edu.univ.erp.data.UserDAO;
import edu.univ.erp.domain.User;
import edu.univ.erp.util.ThemeUtils;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginWindow extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private AuthService authService = new AuthService();
    private UserDAO userDAO = new UserDAO();

    public LoginWindow() {
        setTitle("University ERP - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 550);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(50, 50, 50, 50));

        JLabel title = new JLabel("University ERP");
        title.setFont(ThemeUtils.TITLE_FONT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(title); mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        mainPanel.add(new JLabel("Username"));
        usernameField = new JTextField(); usernameField.setMaximumSize(new Dimension(300, 35));
        mainPanel.add(usernameField); mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        mainPanel.add(new JLabel("Password"));
        passwordField = new JPasswordField(); passwordField.setMaximumSize(new Dimension(300, 35));
        mainPanel.add(passwordField); mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JButton btn = new JButton("LOGIN");
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.addActionListener(e -> perform());
        mainPanel.add(btn);

        add(mainPanel);
        ThemeUtils.applyTheme(this.getContentPane());
        setVisible(true);
    }

    private void perform() {
        String u = usernameField.getText(), p = new String(passwordField.getPassword());
        String role = authService.authenticate(u, p);
        if("LOCKED".equals(role)) { JOptionPane.showMessageDialog(this, "Account Locked (5 mins)."); return; }
        if(role == null) { JOptionPane.showMessageDialog(this, "Invalid Credentials."); return; }

        User user = userDAO.getUserByUsername(u);
        User profile = null;
        if("Student".equals(role)) profile = userDAO.getStudentProfile(user);
        else if("Instructor".equals(role)) profile = userDAO.getInstructorProfile(user);
        else profile = userDAO.getAdminProfile(user);

        if(profile != null) {
            UserSession.getInstance().setCurrentUser(profile);
            dispose();
            if("Admin".equals(role)) new AdminDashboard(u);
            else if("Instructor".equals(role)) new InstructorDashboard(u);
            else new StudentDashboard(u);
        } else JOptionPane.showMessageDialog(this, "Profile Missing.");
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch(Exception e){}
        SwingUtilities.invokeLater(LoginWindow::new);
    }
}
