// File: edu.univ.erp.ui.AssignInstructorDialog.java

package edu.univ.erp.ui;

import edu.univ.erp.service.AdminService;

import javax.swing.*;
import java.awt.*;

public class AssignInstructorDialog extends JDialog {

    private AdminService adminService;
    private JTextField sectionIdField;
    private JTextField instructorIdField;

    public AssignInstructorDialog(JFrame parent) {
        super(parent, "Admin: Assign Instructor to Section", true);
        this.adminService = new AdminService();

        setSize(400, 250);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // --- Input Panel ---
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Assignment Details"));

        sectionIdField = new JTextField(10);
        instructorIdField = new JTextField(10);

        inputPanel.add(new JLabel("Section ID to Assign:"));
        inputPanel.add(sectionIdField);
        inputPanel.add(new JLabel("Instructor ID:"));
        inputPanel.add(instructorIdField);

        JButton assignButton = new JButton("Assign Instructor");
        inputPanel.add(new JLabel("")); // Spacer
        inputPanel.add(assignButton);

        add(inputPanel, BorderLayout.NORTH);

        // --- Action Listener ---
        assignButton.addActionListener(e -> handleAssignInstructor());

        setVisible(true);
    }

    private void handleAssignInstructor() {
        int sectionId;
        int instructorId;

        // Get and validate input
        try {
            sectionId = Integer.parseInt(sectionIdField.getText().trim());
            instructorId = Integer.parseInt(instructorIdField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Both Section ID and Instructor ID must be valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Call the Admin Service
        String result = adminService.assignInstructor(sectionId, instructorId);

        // Display result
        if (result.startsWith("SUCCESS")) {
            JOptionPane.showMessageDialog(this, result, "Assignment Success", JOptionPane.INFORMATION_MESSAGE);
            sectionIdField.setText("");
            instructorIdField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, result, "Assignment Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}