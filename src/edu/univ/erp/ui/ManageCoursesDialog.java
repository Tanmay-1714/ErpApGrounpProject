package edu.univ.erp.ui;

import edu.univ.erp.service.AdminService;

import javax.swing.*;
import java.awt.*;

public class ManageCoursesDialog extends JDialog {

    private AdminService adminService;
    private JTextField codeField;
    private JTextField titleField;
    private JTextField creditsField;

    public ManageCoursesDialog(JFrame parent) {
        super(parent, "Admin: Manage Courses", true);
        this.adminService = new AdminService();

        setSize(400, 250);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // --- Input Panel ---
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("New Course Details"));

        codeField = new JTextField(10);   // e.g. "CS101"
        titleField = new JTextField(15);  // e.g. "Intro to Java"
        creditsField = new JTextField(5); // e.g. "4.0"

        inputPanel.add(new JLabel("Course Code (e.g., CS101):"));
        inputPanel.add(codeField);
        inputPanel.add(new JLabel("Course Title:"));
        inputPanel.add(titleField);
        inputPanel.add(new JLabel("Credits:"));
        inputPanel.add(creditsField);

        add(inputPanel, BorderLayout.CENTER);

        // --- Action Button ---
        JButton createButton = new JButton("Create Course");
        createButton.addActionListener(e -> handleCreateCourse());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(createButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void handleCreateCourse() {
        String code = codeField.getText().trim();
        String title = titleField.getText().trim();
        String creditsStr = creditsField.getText().trim();

        if (code.isEmpty() || title.isEmpty() || creditsStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double credits = Double.parseDouble(creditsStr);

            // Call the Service
            String result = adminService.createNewCourse(code, title, credits);

            if (result.startsWith("SUCCESS")) {
                JOptionPane.showMessageDialog(this, result, "Success", JOptionPane.INFORMATION_MESSAGE);
                codeField.setText("");
                titleField.setText("");
                creditsField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, result, "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Credits must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}