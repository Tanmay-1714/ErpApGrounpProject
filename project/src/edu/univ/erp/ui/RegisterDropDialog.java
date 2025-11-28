// File: edu.univ.erp.ui.RegisterDropDialog.java

package edu.univ.erp.ui;

import edu.univ.erp.data.SectionDAO;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.StudentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RegisterDropDialog extends JDialog {

    private JTable catalogTable;
    private StudentService studentService;
    private SectionDAO sectionDAO;

    public RegisterDropDialog(JFrame parent) {
        super(parent, "Course Registration / Drop Sections", true);
        setSize(800, 500);
        setLocationRelativeTo(parent);

        this.studentService = new StudentService();
        this.sectionDAO = new SectionDAO();

        // Setup main panel
        setLayout(new BorderLayout(10, 10));

        // --- Catalog Table Setup ---
        catalogTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(catalogTable);

        // Load data into the table
        loadCourseCatalog();

        // --- Buttons Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton registerButton = new JButton("Register Selected Section");
        JButton dropButton = new JButton("Drop Selected Section"); // Placeholder for future feature

        buttonPanel.add(registerButton);
        buttonPanel.add(dropButton);

        // Add components to the dialog
        add(new JLabel("Available Sections (Catalog):", SwingConstants.CENTER), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // --- Action Listener ---
        registerButton.addActionListener(e -> handleRegistration());
    }

    private void loadCourseCatalog() {
        // Define table columns
        String[] columnNames = {"Section ID", "Code", "Title", "Credits", "Capacity", "Semester"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            // Make cells non-editable
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        List<Section> sections = sectionDAO.getAllSections();
        for (Section s : sections) {
            // Use getCourseCode() helper from the Section model
            Object[] row = new Object[]{
                    s.getSectionId(),
                    s.getCourseCode(),
                    s.course.getTitle(),
                    s.course.getCredits(),
                    s.getCapacity(),
                    s.getSemester()
            };
            model.addRow(row);
        }

        catalogTable.setModel(model);
    }

    private void handleRegistration() {
        int selectedRow = catalogTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a section from the table.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Section ID is in the first column (index 0)
            int sectionId = (int) catalogTable.getValueAt(selectedRow, 0);
            String courseCode = (String) catalogTable.getValueAt(selectedRow, 1);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to register for " + courseCode + " (Section ID: " + sectionId + ")?",
                    "Confirm Registration", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Call the Student Service (The business logic!)
                String result = studentService.registerForSection(sectionId);

                // Display result message
                if (result.startsWith("SUCCESS")) {
                    JOptionPane.showMessageDialog(this, result, "Registration Success", JOptionPane.INFORMATION_MESSAGE);
                    // You would typically call loadCourseCatalog() again here to refresh capacities
                } else {
                    JOptionPane.showMessageDialog(this, result, "Registration Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
