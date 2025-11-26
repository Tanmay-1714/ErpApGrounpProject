// File: edu.univ.erp.ui.ManageGradesDialog.java

package edu.univ.erp.ui;

import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.service.InstructorService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageGradesDialog extends JDialog {

    private InstructorService instructorService;
    private int sectionId;
    private JTable rosterTable;
    private DefaultTableModel tableModel;

    public ManageGradesDialog(JFrame parent, int sectionId, String courseInfo) {
        super(parent, "Manage Grades: " + courseInfo, true);
        this.instructorService = new InstructorService();
        this.sectionId = sectionId;

        setSize(700, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Class Roster for Section ID: " + sectionId, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);

        // --- Center Panel (Roster Table) ---
        String[] columnNames = {"Enrollment ID", "Roll No", "Student Name", "Program", "Current Grade"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Only the "Current Grade" column is editable (Index 4)
                return column == 4;
            }
        };
        rosterTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(rosterTable);
        add(scrollPane, BorderLayout.CENTER);

        // --- Bottom Panel (Action) ---
        JButton submitButton = new JButton("Submit Grade Changes");
        submitButton.addActionListener(e -> handleSubmitGrades());
        add(submitButton, BorderLayout.SOUTH);

        loadRoster();
        setVisible(true);
    }

    /**
     * Loads the class roster from the service layer.
     */
    private void loadRoster() {
        tableModel.setRowCount(0); // Clear table

        List<Enrollment> roster = instructorService.getEnrolledStudents(sectionId);

        for (Enrollment enrollment : roster) {
            Object[] row = new Object[]{
                    enrollment.getEnrollmentId(),
                    enrollment.student.getRollNo(),
                    enrollment.student.getUsername(), // Using username as name placeholder
                    enrollment.student.getProgram(),
                    enrollment.getGrade() != null ? enrollment.getGrade() : "" // Display existing grade or blank
            };
            tableModel.addRow(row);
        }
    }

    /**
     * Processes changes in the 'Current Grade' column and submits them.
     */
    private void handleSubmitGrades() {
        int rowsUpdated = 0;
        int rowCount = tableModel.getRowCount();

        for (int i = 0; i < rowCount; i++) {
            try {
                int enrollmentId = (int) tableModel.getValueAt(i, 0);
                String newGrade = tableModel.getValueAt(i, 4).toString().trim(); // Grade is in column index 4

                // Only attempt update if grade is not empty and has changed (simple check)
                if (!newGrade.isEmpty()) {
                    String result = instructorService.submitGrade(enrollmentId, newGrade);

                    if (result.startsWith("SUCCESS")) {
                        rowsUpdated++;
                    } else if (!result.contains("Invalid grade format")) {
                        // Log unexpected errors but continue processing
                        System.err.println("Failed to update grade for Enrollment ID " + enrollmentId + ": " + result);
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error processing grade submission on row " + (i+1) + ".", "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }

        if (rowsUpdated > 0) {
            JOptionPane.showMessageDialog(this, rowsUpdated + " grade(s) submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            // Reload the roster to confirm changes (optional, but good practice)
            loadRoster();
        } else {
            JOptionPane.showMessageDialog(this, "No valid grade changes were submitted or found.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }
}
