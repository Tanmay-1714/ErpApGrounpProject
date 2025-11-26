// File: edu.univ.erp.ui.LoginWindow.java (FINAL VERSION)

package edu.univ.erp.ui;

import edu.univ.erp.auth.AuthService;
import edu.univ.erp.auth.UserSession;
import edu.univ.erp.data.UserDAO;
import edu.univ.erp.domain.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginWindow extends JFrame {

    // --- UI Components ---
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton themeToggleBtn;
    private JPanel mainPanel;
    private JLabel titleLabel;
    private JLabel userLabel;
    private JLabel passLabel;

    // --- Services ---
    private AuthService authService;
    private UserDAO userDAO;

    // --- State ---
    private boolean isDarkMode = false;

    // --- Color Palettes ---
    // Light Mode Colors
    private final Color LIGHT_BG = new Color(245, 245, 250);
    private final Color LIGHT_PANEL_BG = Color.WHITE;
    private final Color LIGHT_TEXT = new Color(50, 50, 50);
    private final Color LIGHT_INPUT_BG = Color.WHITE;
    private final Color LIGHT_BORDER = new Color(200, 200, 200);

    // Dark Mode Colors
    private final Color DARK_BG = new Color(30, 30, 30);
    private final Color DARK_PANEL_BG = new Color(45, 45, 45);
    private final Color DARK_TEXT = new Color(230, 230, 230);
    private final Color DARK_INPUT_BG = new Color(60, 60, 60);
    private final Color DARK_BORDER = new Color(80, 80, 80);

    // Accent Color (University Blue)
    private final Color ACCENT_COLOR = new Color(70, 130, 180);

    public LoginWindow() {
        // Initialize Services
        authService = new AuthService();
        userDAO = new UserDAO();

        // --- 1. Window Setup ---
        setTitle("University ERP");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 350);
        setLocationRelativeTo(null); // Center on screen
        setResizable(false);

        // --- 2. Layout & Components ---
        initUI();

        // --- 3. Apply Default Theme ---
        applyTheme();

        // --- 4. Finalize ---
        setVisible(true);
    }

    private void initUI() {
        // Main wrapper with Card-like look
        mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(20, 40, 20, 40)); // Outer padding

        // Title
        titleLabel = new JLabel("Welcome Back");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Inputs
        userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        usernameField = new JTextField(15);
        styleTextField(usernameField);

        passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        passwordField = new JPasswordField(15);
        styleTextField(passwordField);

        // Login Button
        loginButton = new JButton("LOGIN");
        loginButton.setFocusPainted(false);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBorder(new EmptyBorder(10, 0, 10, 0));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setBackground(ACCENT_COLOR);
        loginButton.setForeground(Color.WHITE);

        // Add simplified hover effect
        loginButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) { loginButton.setBackground(ACCENT_COLOR.darker()); }
            public void mouseExited(MouseEvent evt) { loginButton.setBackground(ACCENT_COLOR); }
        });

        // Theme Toggle Button (Top Right)
        themeToggleBtn = new JButton("☾"); // Moon symbol
        themeToggleBtn.setFocusPainted(false);
        themeToggleBtn.setBorderPainted(false);
        themeToggleBtn.setContentAreaFilled(false);
        themeToggleBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        themeToggleBtn.setToolTipText("Toggle Dark Mode");
        themeToggleBtn.addActionListener(e -> toggleTheme());

        // --- GridBag Layout Assembly ---
        setLayout(new BorderLayout());

        // Header Panel for the Toggle Button
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topBar.setOpaque(false); // Transparent
        topBar.add(themeToggleBtn);

        // Add Top Bar to Frame
        add(topBar, BorderLayout.NORTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // Title
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(titleLabel, gbc);

        // Username
        gbc.gridy = 1; gbc.insets = new Insets(5, 0, 2, 0);
        mainPanel.add(userLabel, gbc);

        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 10, 0);
        gbc.ipady = 10; // Make input taller
        mainPanel.add(usernameField, gbc);

        // Password
        gbc.gridy = 3; gbc.insets = new Insets(5, 0, 2, 0);
        gbc.ipady = 0;
        mainPanel.add(passLabel, gbc);

        gbc.gridy = 4; gbc.insets = new Insets(0, 0, 20, 0);
        gbc.ipady = 10;
        mainPanel.add(passwordField, gbc);

        // Button
        gbc.gridy = 5;
        mainPanel.add(loginButton, gbc);

        // Add main panel to center of frame
        add(mainPanel, BorderLayout.CENTER);

        // Attach Logic
        loginButton.addActionListener(e -> performLogin());
    }

    private void styleTextField(JTextField field) {
        // Add padding inside the text field
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(LIGHT_BORDER, 1),
                new EmptyBorder(5, 10, 5, 10)));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    /**
     * Toggles the boolean flag and reapplies the theme.
     */
    private void toggleTheme() {
        isDarkMode = !isDarkMode;
        applyTheme();
    }

    /**
     * Updates colors of all components based on isDarkMode flag.
     */
    private void applyTheme() {
        Color bg = isDarkMode ? DARK_BG : LIGHT_BG;
        Color panelBg = isDarkMode ? DARK_PANEL_BG : LIGHT_PANEL_BG;
        Color text = isDarkMode ? DARK_TEXT : LIGHT_TEXT;
        Color inputBg = isDarkMode ? DARK_INPUT_BG : LIGHT_INPUT_BG;
        Color border = isDarkMode ? DARK_BORDER : LIGHT_BORDER;
        String toggleText = isDarkMode ? "☀" : "☾";

        // Window Background
        getContentPane().setBackground(bg);

        // Panel Backgrounds
        mainPanel.setBackground(panelBg);

        // Labels
        titleLabel.setForeground(text);
        userLabel.setForeground(text);
        passLabel.setForeground(text);
        themeToggleBtn.setForeground(text);
        themeToggleBtn.setText(toggleText);

        // Inputs
        updateFieldColors(usernameField, inputBg, text, border);
        updateFieldColors(passwordField, inputBg, text, border);
    }

    private void updateFieldColors(JTextField field, Color bg, Color fg, Color borderCol) {
        field.setBackground(bg);
        field.setForeground(fg);
        field.setCaretColor(fg);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(borderCol, 1),
                new EmptyBorder(5, 10, 5, 10)));
    }

    private void performLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
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
                JOptionPane.showMessageDialog(this, "Login Successful! Opening " + userRole + " Dashboard.");
                dispose();
                openDashboard(userRole);
            } else {
                JOptionPane.showMessageDialog(this, "Profile data missing.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginWindow::new);
    }
}
