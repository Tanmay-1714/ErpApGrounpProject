// File: edu.univ.erp.ui.ManageUsersDialog.java

package edu.univ.erp.ui;

import edu.univ.erp.service.AdminService;

import javax.swing.*;
import java.awt.*;

public class ManageUsersDialog extends JDialog {

    private AdminService adminService;

    // Common fields for all users
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;

    // Dynamic panels for profile details
    private JPanel dynamicPanel;
    private CardLayout cardLayout;

    // Student-specific fields
    private JTextField rollNoField;
    private JTextField programField;
    private JTextField yearField;
    private JPanel studentPanel;

    // Instructor-specific fields
    private JTextField departmentField;
    private JPanel instructorPanel;

    public ManageUsersDialog(JFrame parent) {
        super(parent, "Admin: Create New User", true);
        this.adminService = new AdminService();

        setSize(550, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // --- 1. Main Input Panel (North) ---
        JPanel mainInputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        mainInputPanel.setBorder(BorderFactory.createTitledBorder("User Credentials"));

        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        roleComboBox = new JComboBox<>(new String[]{"Student", "Instructor"});

        mainInputPanel.add(new JLabel("Username:"));
        mainInputPanel.add(usernameField);
        mainInputPanel.add(new JLabel("Password:"));
        mainInputPanel.add(passwordField);
        mainInputPanel.add(new JLabel("Role:"));
        mainInputPanel.add(roleComboBox);

        add(mainInputPanel, BorderLayout.NORTH);

        // --- 2. Dynamic Profile Panel (Center) ---
        cardLayout = new CardLayout();
        dynamicPanel = new JPanel(cardLayout);
        dynamicPanel.setBorder(BorderFactory.createTitledBorder("Profile Details"));

        setupStudentPanel();
        setupInstructorPanel();

        dynamicPanel.add(studentPanel, "Student");
        dynamicPanel.add(instructorPanel, "Instructor");

        // Listener to switch panels when role changes
        roleComboBox.addActionListener(e -> {
            cardLayout.show(dynamicPanel, (String) roleComboBox.getSelectedItem());
            revalidate(); // Revalidate ensures the UI updates correctly
        });

        add(dynamicPanel, BorderLayout.CENTER);

        // --- 3. Control Panel (South) ---
        JButton createButton = new JButton("Create User");
        createButton.addActionListener(e -> handleCreateUser());

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.add(createButton);
        add(controlPanel, BorderLayout.SOUTH);

        // Show the initial panel
        cardLayout.show(dynamicPanel, "Student");

        setVisible(true);
    }

    private void setupStudentPanel() {
        studentPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        rollNoField = new JTextField(15);
        programField = new JTextField(15);
        yearField = new JTextField(15);

        studentPanel.add(new JLabel("Roll No (e.g., S0001):"));
        studentPanel.add(rollNoField);
        studentPanel.add(new JLabel("Program:"));
        studentPanel.add(programField);
        studentPanel.add(new JLabel("Admission Year:"));
        studentPanel.add(yearField);
    }

    private void setupInstructorPanel() {
        instructorPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        departmentField = new JTextField(15);

        instructorPanel.add(new JLabel("Department:"));
        instructorPanel.add(departmentField);
    }

    private void handleCreateUser() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role = (String) roleComboBox.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and Password fields cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String result;

        if ("Student".equals(role)) {
            String rollNo = rollNoField.getText().trim();
            String program = programField.getText().trim();
            String yearText = yearField.getText().trim();

            if (rollNo.isEmpty() || program.isEmpty() || yearText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All Student profile fields must be filled.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate year is numeric
            try {
                Integer.parseInt(yearText);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Admission Year must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Call Admin Service with required arguments
            result = adminService.createNewUser(username, password, role, rollNo, program, yearText);

        } else if ("Instructor".equals(role)) {
            String department = departmentField.getText().trim();

            if (department.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Department field cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Call Admin Service with required arguments
            result = adminService.createNewUser(username, password, role, department);
        } else {
            result = "FAILURE: Invalid role selected.";
        }

        // Display result
        if (result.startsWith("SUCCESS")) {
            JOptionPane.showMessageDialog(this, result, "User Creation Success", JOptionPane.INFORMATION_MESSAGE);
            // Optionally clear fields after success
            usernameField.setText("");
            passwordField.setText("");
            rollNoField.setText("");
            programField.setText("");
            yearField.setText("");
            departmentField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, result, "User Creation Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
