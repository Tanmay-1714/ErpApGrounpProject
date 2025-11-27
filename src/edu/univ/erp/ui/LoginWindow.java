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
        setSize(400, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel titleLabel = new JLabel("University ERP");
        titleLabel.setFont(ThemeUtils.TITLE_FONT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        mainPanel.add(new JLabel("Username"));
        usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        mainPanel.add(usernameField);
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        mainPanel.add(new JLabel("Password"));
        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        mainPanel.add(passwordField);
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JButton loginButton = new JButton("LOGIN");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(e -> performLogin());
        mainPanel.add(loginButton);

        add(mainPanel);
        ThemeUtils.applyTheme(this.getContentPane());
        setVisible(true);
    }

    private void performLogin() {
        String u = usernameField.getText().trim();
        String p = new String(passwordField.getPassword());
        String role = authService.authenticate(u, p);

        if (role != null) {
            User baseUser = userDAO.getUserByUsername(u);
            User fullUser = null;
            if ("Student".equals(role)) fullUser = userDAO.getStudentProfile(baseUser);
            else if ("Instructor".equals(role)) fullUser = userDAO.getInstructorProfile(baseUser);
            else fullUser = userDAO.getAdminProfile(baseUser);

            if (fullUser != null) {
                UserSession.getInstance().setCurrentUser(fullUser);
                dispose();
                if ("Admin".equals(role)) new AdminDashboard(u);
                else if ("Instructor".equals(role)) new InstructorDashboard(u);
                else new StudentDashboard(u);
            } else JOptionPane.showMessageDialog(this, "Profile not found.");
        } else JOptionPane.showMessageDialog(this, "Invalid Login.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginWindow::new);
    }
}
