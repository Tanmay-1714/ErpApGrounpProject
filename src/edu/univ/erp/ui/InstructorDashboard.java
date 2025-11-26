// File: edu.univ.erp.ui.InstructorDashboard.java (Functional Version)

package edu.univ.erp.ui;

import edu.univ.erp.auth.UserSession; // REQUIRED
import edu.univ.erp.domain.Section; // REQUIRED
import edu.univ.erp.service.InstructorService; // REQUIRED

import javax.swing.*;
import javax.swing.table.DefaultTableModel; // REQUIRED for table
import java.awt.*;
import java.util.List;

public class InstructorDashboard extends JFrame {

    private InstructorService instructorService; // NEW FIELD
    private JTable sectionTable;                // NEW FIELD
    private DefaultTableModel tableModel;       // NEW FIELD
    private JButton gradeButton;                // NEW FIELD
    private JLabel welcomeLabel;
    private JPanel navPanel;                    // Declared to be accessed in addListeners

    public InstructorDashboard(String username) {
        this.instructorService = new InstructorService(); // INITIALIZED

        setTitle("INSTRUCTOR Dashboard - " + username);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(10, 10));

        // --- 1. Top Banner (Welcome Message) ---
        String role = UserSession.getInstance().getRole();
        welcomeLabel = new JLabel("Welcome, " + role + " " + username + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(welcomeLabel, BorderLayout.NORTH);

        // --- 2. Side Panel (Navigation - West) ---
        navPanel = new JPanel();
        navPanel.setLayout(new GridLayout(6, 1, 10, 10));
        navPanel.setBorder(BorderFactory.createTitledBorder("Instructor Tools"));

        // Buttons must be accessible for listeners
        JButton viewSectionsButton = new JButton("View My Sections");
        gradeButton = new JButton("Enter Scores/Grades"); // Set as a field
        JButton computeGradesButton = new JButton("Compute Final Grades");
        JButton viewStatsButton = new JButton("View Simple Statistics");
        JButton changePwdButton = new JButton("Change Password");
        JButton logoutButton = new JButton("Logout");

        // Add buttons
        navPanel.add(viewSectionsButton);
        navPanel.add(gradeButton);
        navPanel.add(computeGradesButton);
        navPanel.add(viewStatsButton);
        navPanel.add(changePwdButton);
        navPanel.add(logoutButton);

        add(navPanel, BorderLayout.WEST);

        // --- 3. Center Panel (Section Table) ---

        // Define the table model columns
        String[] columnNames = {"Section ID", "Course Code", "Course Title", "Credits", "Schedule", "Capacity"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Sections list is view-only
            }
        };
        sectionTable = new JTable(tableModel);
        sectionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(sectionTable);
        add(scrollPane, BorderLayout.CENTER);

        // Initially disable the grading button until a section is loaded and selected
        gradeButton.setEnabled(false);

        // Load data and setup listeners
        loadAssignedSections();
        addListeners();

        setVisible(true);
    }

    /**
     * Loads the assigned sections from the service layer and populates the table.
     */
    private void loadAssignedSections() {
        tableModel.setRowCount(0);

        List<Section> assignedSections = instructorService.getAssignedSections();

        if (assignedSections.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "You have no sections assigned for the current term.",
                    "No Assignments",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (Section section : assignedSections) {
            // Ensure section.course is populated by the DAO
            Object[] row = new Object[]{
                    section.getSectionId(),
                    section.course.getCode(),
                    section.course.getTitle(),
                    section.course.getCredits(),
                    section.getDay() + " " + section.getTime() + " (" + section.getRoom() + ")",
                    section.getCapacity()
            };
            tableModel.addRow(row);
        }
    }

    /**
     * Adds listeners for table selection and action buttons.
     */
    private void addListeners() {
        // --- 1. Table Selection Listener ---
        // Enable grade button only when a row is selected
        sectionTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                gradeButton.setEnabled(sectionTable.getSelectedRow() != -1);
            }
        });

        // --- 2. Button Listeners ---

        // The "View My Sections" button will just call loadAssignedSections again
        JButton viewSectionsButton = (JButton) navPanel.getComponent(0);
        viewSectionsButton.addActionListener(e -> loadAssignedSections());

        // Action to open the Grading Dialog (Index 1)
        gradeButton.addActionListener(e -> openGradingDialog());

        // TODO: Implement other buttons (Index 2-5)

        // Logout Button (Index 5)
        JButton logoutButton = (JButton) navPanel.getComponent(5);
        logoutButton.addActionListener(e -> {
            UserSession.getInstance().clearSession();
            // Assuming your main application entry point (e.g., LoginWindow) is called here
            // You would normally call LoginWindow.main(null); or similar
            JOptionPane.showMessageDialog(this, "Logged out successfully.", "Logout", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
        });
    }

    /**
     * Retrieves the selected section ID and opens the ManageGradesDialog.
     */
    private void openGradingDialog() {
        int selectedRow = sectionTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a section first.", "Selection Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Section ID is in the first column (Index 0)
            int sectionId = (int) tableModel.getValueAt(selectedRow, 0);
            String courseCode = (String) tableModel.getValueAt(selectedRow, 1);
            String courseTitle = (String) tableModel.getValueAt(selectedRow, 2);
            String courseInfo = courseCode + " - " + courseTitle;

            // ManageGradesDialog was implemented in the last step
            ManageGradesDialog dialog = new ManageGradesDialog(this, sectionId, courseInfo);
            dialog.setVisible(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error processing selected section.", "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // Note: If you want to run this directly for testing, ensure the UserSession is mocked or pre-loaded.
}
