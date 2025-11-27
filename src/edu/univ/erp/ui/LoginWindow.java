package edu.univ.erp.ui;

import edu.univ.erp.auth.AuthService;
import edu.univ.erp.auth.UserSession;
import edu.univ.erp.data.UserDAO;
import edu.univ.erp.domain.User;
import edu.univ.erp.util.ThemeUtils; // Import the new ThemeUtils

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginWindow extends JFrame {

  private JTextField usernameField;
  private JPasswordField passwordField;
  private JButton loginButton;
  private AuthService authService;
  private UserDAO userDAO;

  public LoginWindow() {
    authService = new AuthService();
    userDAO = new UserDAO();

    setTitle("University ERP - Login");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(400, 500); // Taller, nicer layout
    setLocationRelativeTo(null);
    setResizable(false);

    initUI();

    // Apply our new Yellow/Black theme
    ThemeUtils.applyTheme(this.getContentPane());

    setVisible(true);
  }

  private void initUI() {
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

    // -- Logo / Title --
    JLabel titleLabel = new JLabel("University ERP");
    titleLabel.setFont(ThemeUtils.TITLE_FONT);
    titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel subtitleLabel = new JLabel("Please Login");
    subtitleLabel.setFont(ThemeUtils.REGULAR_FONT);
    subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    // -- Inputs --
    JLabel userLabel = new JLabel("Username");
    userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

    usernameField = new JTextField();
    usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

    JLabel passLabel = new JLabel("Password");
    passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

    passwordField = new JPasswordField();
    passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

    // -- Button --
    loginButton = new JButton("LOGIN");
    loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    loginButton.setMaximumSize(new Dimension(200, 45));

    // Add spacing and components
    mainPanel.add(Box.createVerticalGlue());
    mainPanel.add(titleLabel);
    mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    mainPanel.add(subtitleLabel);
    mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

    mainPanel.add(userLabel);
    mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    mainPanel.add(usernameField);
    mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

    mainPanel.add(passLabel);
    mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    mainPanel.add(passwordField);
    mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

    mainPanel.add(loginButton);
    mainPanel.add(Box.createVerticalGlue());

    add(mainPanel);

    loginButton.addActionListener(e -> performLogin());
  }

  private void performLogin() {
    String username = usernameField.getText().trim();
    String password = new String(passwordField.getPassword());

    if (username.isEmpty() || password.isEmpty()) {
      JOptionPane.showMessageDialog(this, "Please enter both username and password.");
      return;
    }

    String userRole = authService.authenticate(username, password);

    if (userRole != null) {
      User baseUser = userDAO.getUserByUsername(username);
      User completeProfile = null;

      if (baseUser != null) {
        if ("Student".equals(userRole)) {
          completeProfile = userDAO.getStudentProfile(baseUser);
        } else if ("Instructor".equals(userRole)) {
          completeProfile = userDAO.getInstructorProfile(baseUser);
        } else if ("Admin".equals(userRole)) {
          completeProfile = userDAO.getAdminProfile(baseUser);
        }
      }

      if (completeProfile != null) {
        UserSession.getInstance().setCurrentUser(completeProfile);
        dispose();
        openDashboard(userRole);
      } else {
        JOptionPane.showMessageDialog(this, "Profile data missing.", "Error", JOptionPane.ERROR_MESSAGE);
      }
    } else {
      JOptionPane.showMessageDialog(this, "Invalid credentials.", "Login Failed", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void openDashboard(String role) {
    String username = UserSession.getInstance().getUsername();
    switch (role) {
      case "Admin": new AdminDashboard(username); break;
      case "Instructor": new InstructorDashboard(username); break;
      case "Student": new StudentDashboard(username); break;
      default: JOptionPane.showMessageDialog(null, "Unknown role: " + role);
    }
  }
  // Add this method inside the LoginWindow class
  public static void main(String[] args) {
    // This uses the SwingUtilities to ensure the UI is created on the Event Dispatch Thread (EDT)
    SwingUtilities.invokeLater(() -> {
      // You might want to set a cleaner look and feel before creating the window
      try {
        // Use the system default look and feel for better integration, 
        // or a cross-platform one like "Nimbus" if you prefer.
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception e) {
        e.printStackTrace();
      }
      new LoginWindow();
    });
  }
}
