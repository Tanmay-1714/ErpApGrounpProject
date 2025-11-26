// File: edu.univ.erp.ui.ManageSectionsDialog.java

package edu.univ.erp.ui;

import edu.univ.erp.service.AdminService;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;

public class ManageSectionsDialog extends JDialog {

    private AdminService adminService;
    private JTextField courseIdField;
    private JTextField capacityField;
    private JTextField roomField;
    private JComboBox<String> semesterComboBox;
    private JComboBox<String> dayComboBox;
    private JTextField timeField;
    private JTextField yearField;

    public ManageSectionsDialog(JFrame parent) {
        super(parent, "Admin: Section Management", true);
        this.adminService = new AdminService();

        setSize(600, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // --- Input Panel ---
        JPanel inputPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Create New Section"));

        // Initialize fields
        courseIdField = new JTextField(10);
        capacityField = new JTextField(10);
        roomField = new JTextField(10);
        timeField = new JTextField(10);
        yearField = new JTextField(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));

        semesterComboBox = new JComboBox<>(new String[]{"Fall", "Spring", "Summer"});
        dayComboBox = new JComboBox<>(new String[]{"M", "Tu", "W", "Th", "F"}); // Simplified days

        // Add components
        inputPanel.add(new JLabel("Course ID (from courses table):"));
        inputPanel.add(courseIdField);
        inputPanel.add(new JLabel("Semester:"));
        inputPanel.add(semesterComboBox);
        inputPanel.add(new JLabel("Year:"));
        inputPanel.add(yearField);
        inputPanel.add(new JLabel("Day(s) (e.g., M/Tu/W):"));
        inputPanel.add(dayComboBox);
        inputPanel.add(new JLabel("Time (e.g., 10:00 AM - 11:30 AM):"));
        inputPanel.add(timeField);
        inputPanel.add(new JLabel("Room/Location:"));
        inputPanel.add(roomField);
        inputPanel.add(new JLabel("Capacity:"));
        inputPanel.add(capacityField);

        JButton createButton = new JButton("Create Section");
        inputPanel.add(new JLabel("")); // Spacer
        inputPanel.add(createButton);

        add(inputPanel, BorderLayout.NORTH);

        // --- Action Listener ---
        createButton.addActionListener(e -> handleCreateSection());

        // TODO: Add a panel/table for viewing and assigning instructors to sections below this.

        setVisible(true);
    }

    private void handleCreateSection() {
        int courseId;
        int capacity;
        int year;
        String day = (String) dayComboBox.getSelectedItem();
        String semester = (String) semesterComboBox.getSelectedItem();
        String room = roomField.getText().trim();
        String time = timeField.getText().trim();

        try {
            courseId = Integer.parseInt(courseIdField.getText().trim());
            capacity = Integer.parseInt(capacityField.getText().trim());
            year = Integer.parseInt(yearField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Course ID, Capacity, and Year must be valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (time.isEmpty() || room.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Time and Room/Location must be filled.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Call the Admin Service
        String result = adminService.createNewSection(courseId, day, time, room, capacity, semester, year);

        // Display result
        if (result.startsWith("SUCCESS")) {
            JOptionPane.showMessageDialog(this, result, "Section Creation Success", JOptionPane.INFORMATION_MESSAGE);
            // Clear fields (except for year/semester/day)
            courseIdField.setText("");
            capacityField.setText("");
            roomField.setText("");
            timeField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, result, "Section Creation Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}