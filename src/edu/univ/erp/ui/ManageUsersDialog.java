package edu.univ.erp.ui;

import edu.univ.erp.service.AdminService;
import edu.univ.erp.util.ThemeUtils; // Import Theme

import javax.swing.*;
import java.awt.*;

public class ManageUsersDialog extends JDialog {

    private AdminService adminService;
    private JTextField usernameField, rollNoField, programField, yearField, departmentField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JPanel dynamicPanel, studentPanel, instructorPanel;
    private CardLayout cardLayout;

    public ManageUsersDialog(JFrame parent) {
        super(parent, "Admin: Create New User", true);
        this.adminService = new AdminService();

        setSize(550, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(15, 15));

        // --- Header ---
        JLabel title = new JLabel("Create New User", SwingConstants.CENTER);
        title.setFont(ThemeUtils.HEADER_FONT);
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // --- Credentials Panel ---
        JPanel mainInputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        mainInputPanel.setBorder(BorderFactory.createTitledBorder("Credentials"));

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        roleComboBox = new JComboBox<>(new String[]{"Student", "Instructor"});

        mainInputPanel.add(new JLabel("Username:")); mainInputPanel.add(usernameField);
        mainInputPanel.add(new JLabel("Password:")); mainInputPanel.add(passwordField);
        mainInputPanel.add(new JLabel("Role:"));     mainInputPanel.add(roleComboBox);

        // --- Dynamic Panels ---
        cardLayout = new CardLayout();
        dynamicPanel = new JPanel(cardLayout);
        dynamicPanel.setBorder(BorderFactory.createTitledBorder("Profile Details"));

        // Student Panel
        studentPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        rollNoField = new JTextField();
        programField = new JTextField();
        yearField = new JTextField();
        studentPanel.add(new JLabel("Roll No:")); studentPanel.add(rollNoField);
        studentPanel.add(new JLabel("Program:")); studentPanel.add(programField);
        studentPanel.add(new JLabel("Year:"));    studentPanel.add(yearField);

        // Instructor Panel
        instructorPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        departmentField = new JTextField();
        instructorPanel.add(new JLabel("Department:")); instructorPanel.add(departmentField);

        dynamicPanel.add(studentPanel, "Student");
        dynamicPanel.add(instructorPanel, "Instructor");

        // Wrapper for inputs
        JPanel centerPanel = new JPanel(new BorderLayout(10,10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        centerPanel.add(mainInputPanel, BorderLayout.NORTH);
        centerPanel.add(dynamicPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // --- Bottom Panel ---
        JButton createButton = new JButton("Create User");
        createButton.addActionListener(e -> handleCreateUser());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(createButton);
        add(south, BorderLayout.SOUTH);

        // Logic
        roleComboBox.addActionListener(e -> cardLayout.show(dynamicPanel, (String) roleComboBox.getSelectedItem()));
        
        // *** APPLY THEME ***
        ThemeUtils.applyTheme(this.getContentPane());
        
        setVisible(true);
    }

    private void handleCreateUser() {
        String u = usernameField.getText();
        String p = new String(passwordField.getPassword());
        String r = (String) roleComboBox.getSelectedItem();
        
        String res;
        if ("Student".equals(r)) {
            res = adminService.createNewUser(u, p, r, rollNoField.getText(), programField.getText(), yearField.getText());
        } else {
            res = adminService.createNewUser(u, p, r, departmentField.getText());
        }
        
        JOptionPane.showMessageDialog(this, res);
        if(res.startsWith("SUCCESS")) dispose();
    }
}
