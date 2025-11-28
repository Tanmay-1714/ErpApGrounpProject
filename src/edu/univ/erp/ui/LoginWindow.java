package edu.univ.erp.ui;

import edu.univ.erp.auth.AuthService;
import edu.univ.erp.auth.UserSession;
import edu.univ.erp.data.UserDAO;
import edu.univ.erp.domain.User;
import edu.univ.erp.util.ThemeUtils;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class LoginWindow extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private AuthService authService = new AuthService();
    private UserDAO userDAO = new UserDAO();

    public LoginWindow() {
        setTitle("University ERP System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 600);
        setLocationRelativeTo(null);
        
        // Main container
        JPanel mainContainer = new JPanel(new GridBagLayout());
        mainContainer.setBackground(ThemeUtils.BG_COLOR); // This variable now exists!
        
        // Login Card
        JPanel loginCard = new JPanel();
        loginCard.setLayout(new BoxLayout(loginCard, BoxLayout.Y_AXIS));
        loginCard.setBackground(ThemeUtils.CARD_BG);
        loginCard.setBorder(new CompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1, true),
            new EmptyBorder(40, 40, 40, 40)
        ));

        // Logo / Title
        JLabel title = new JLabel("UNIVERSITY ERP");
        title.setFont(ThemeUtils.TITLE_FONT);
        title.setForeground(ThemeUtils.ACCENT_BLACK); // This variable now exists!
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitle = new JLabel("Secure Login Portal");
        subtitle.setFont(ThemeUtils.REGULAR_FONT);
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Inputs
        usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(300, 45));
        ThemeUtils.styleField(usernameField);
        
        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(300, 45));
        ThemeUtils.styleField(passwordField);

        JButton loginBtn = new JButton("LOGIN");
        loginBtn.setMaximumSize(new Dimension(300, 50));
        ThemeUtils.styleButton(loginBtn);
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.addActionListener(e -> performLogin());

        // Assembly
        loginCard.add(title);
        loginCard.add(subtitle);
        loginCard.add(Box.createRigidArea(new Dimension(0, 40)));
        
        loginCard.add(createLabel("Username"));
        loginCard.add(usernameField);
        loginCard.add(Box.createRigidArea(new Dimension(0, 20)));
        
        loginCard.add(createLabel("Password"));
        loginCard.add(passwordField);
        loginCard.add(Box.createRigidArea(new Dimension(0, 40)));
        
        loginCard.add(loginBtn);

        mainContainer.add(loginCard);
        add(mainContainer);

        setVisible(true);
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        l.setForeground(ThemeUtils.ACCENT_BLACK); // This variable now exists!
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void performLogin() {
        String u = usernameField.getText().trim();
        String p = new String(passwordField.getPassword());
        
        if(u.isEmpty() || p.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter credentials.");
            return;
        }

        String result = authService.authenticate(u, p);

        if ("LOCKED".equals(result)) {
            JOptionPane.showMessageDialog(this, 
                "<html><body><h3>ACCOUNT LOCKED</h3><p>Too many failed attempts.<br>Please wait 5 minutes.</p></body></html>", 
                "Security Alert", JOptionPane.ERROR_MESSAGE);
        } 
        else if (result != null) {
            User baseUser = userDAO.getUserByUsername(u);
            User fullUser = null;
            
            if ("Student".equals(result)) fullUser = userDAO.getStudentProfile(baseUser);
            else if ("Instructor".equals(result)) fullUser = userDAO.getInstructorProfile(baseUser);
            else fullUser = userDAO.getAdminProfile(baseUser);

            if (fullUser != null) {
                UserSession.getInstance().setCurrentUser(fullUser);
                dispose();
                if ("Admin".equals(result)) new AdminDashboard(u);
                else if ("Instructor".equals(result)) new InstructorDashboard(u);
                else new StudentDashboard(u);
            } else {
                JOptionPane.showMessageDialog(this, "Profile not found. Contact Admin.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Username or Password.", "Login Failed", JOptionPane.WARNING_MESSAGE);
            passwordField.setText("");
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch(Exception ignored){}
        SwingUtilities.invokeLater(LoginWindow::new);
    }
}
