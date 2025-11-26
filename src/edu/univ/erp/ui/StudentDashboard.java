// File: edu.univ.erp.ui.StudentDashboard.java

package edu.univ.erp.ui;

import edu.univ.erp.auth.UserSession;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.StudentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentDashboard extends JFrame {

    private StudentService studentService;
    private JTabbedPane tabbedPane;

    // Components for the 'Course Catalog' tab
    private JTable catalogTable;
    private DefaultTableModel catalogTableModel;
    private JButton registerButton;

    // Components for the 'My Courses and Grades' tab
    private JTable gradesTable;
    private DefaultTableModel gradesTableModel;
    private JButton dropButton; // NEW BUTTON

    public StudentDashboard(String username) {
        this.studentService = new StudentService();

        setTitle("STUDENT Dashboard - " + username);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- 1. Top Panel Setup ---
        JLabel welcomeLabel = new JLabel("Welcome, Student " + username + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(welcomeLabel, BorderLayout.NORTH);

        // --- 2. Tabbed Pane (Center) ---
        tabbedPane = new JTabbedPane();

        // --- 3. Navigation/Logout (South) ---
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutButton = new JButton("Logout");
        southPanel.add(logoutButton);
        add(southPanel, BorderLayout.SOUTH);

        // --- Initialize Tabs ---
        setupCatalogTab();
        setupGradesTab();

        tabbedPane.addTab("Course Catalog & Registration", createCatalogPanel());
        tabbedPane.addTab("My Courses and Grades", createGradesPanel());

        add(tabbedPane, BorderLayout.CENTER);

        // --- Add Listeners ---
        addListeners();

        // Add Logout Action
        logoutButton.addActionListener(e -> {
            UserSession.getInstance().clearSession();
            JOptionPane.showMessageDialog(this, "Logged out successfully.", "Logout", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
            new LoginWindow(); // Re-open login window
        });

        // Load data on start
        loadCourseCatalog();
        loadMyRegistrations();

        setVisible(true);
    }

    // =================================================================
    //          TAB 1: COURSE CATALOG AND REGISTRATION
    // =================================================================

    private void setupCatalogTab() {
        String[] columnNames = {"Section ID", "Course Code", "Course Title", "Credits", "Schedule", "Capacity", "Semester", "Year"};
        catalogTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        catalogTable = new JTable(catalogTableModel);
        catalogTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private JPanel createCatalogPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(new JScrollPane(catalogTable), BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        registerButton = new JButton("Register for Selected Course");
        registerButton.setEnabled(false);
        actionPanel.add(registerButton);

        panel.add(actionPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void loadCourseCatalog() {
        catalogTableModel.setRowCount(0);
        List<Section> catalog = studentService.getCourseCatalog();

        for (Section section : catalog) {
            Object[] row = new Object[]{
                    section.getSectionId(),
                    section.course.getCode(),
                    section.course.getTitle(),
                    section.course.getCredits(),
                    section.getDay() + " " + section.getTime() + " (" + section.getRoom() + ")",
                    section.getCapacity(),
                    section.getSemester(),
                    section.getYear()
            };
            catalogTableModel.addRow(row);
        }
    }

    // =================================================================
    //          TAB 2: MY COURSES AND GRADES (Includes DROP)
    // =================================================================

    private void setupGradesTab() {
        String[] columnNames = {"Section ID", "Course Code", "Course Title", "Credits", "Schedule", "Grade"};
        gradesTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        gradesTable = new JTable(gradesTableModel);
        gradesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private JPanel createGradesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(gradesTable), BorderLayout.CENTER);

        // Add the Drop Button Panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        dropButton = new JButton("Drop Selected Course");
        dropButton.setEnabled(false);
        dropButton.setBackground(new Color(255, 200, 200)); // Light red to indicate caution
        actionPanel.add(dropButton);

        panel.add(actionPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void loadMyRegistrations() {
        gradesTableModel.setRowCount(0);
        List<Enrollment> registrations = studentService.getMyRegistrations();

        for (Enrollment enrollment : registrations) {
            Object[] row = new Object[]{
                    enrollment.getSectionId(),
                    enrollment.section.course.getCode(),
                    enrollment.section.course.getTitle(),
                    enrollment.section.course.getCredits(),
                    enrollment.section.getSemester() + " " + enrollment.section.getYear(),
                    enrollment.getGrade() != null ? enrollment.getGrade() : "In Progress"
            };
            gradesTableModel.addRow(row);
        }
    }

    // =================================================================
    //          LISTENERS AND ACTIONS
    // =================================================================

    private void addListeners() {
        // 1. Enable buttons only when rows are selected
        catalogTable.getSelectionModel().addListSelectionListener(e ->
                registerButton.setEnabled(catalogTable.getSelectedRow() != -1)
        );
        gradesTable.getSelectionModel().addListSelectionListener(e ->
                dropButton.setEnabled(gradesTable.getSelectedRow() != -1)
        );

        // 2. Register Button Action
        registerButton.addActionListener(e -> handleRegistration());

        // 3. Drop Button Action (NEW)
        dropButton.addActionListener(e -> handleDrop());

        // 4. Refresh data when tabs change
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 0) loadCourseCatalog();
            if (tabbedPane.getSelectedIndex() == 1) loadMyRegistrations();
        });
    }

    private void handleRegistration() {
        int selectedRow = catalogTable.getSelectedRow();
        if (selectedRow == -1) return;

        try {
            int sectionId = (int) catalogTableModel.getValueAt(selectedRow, 0);
            String courseCode = (String) catalogTableModel.getValueAt(selectedRow, 1);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Register for " + courseCode + " (Section ID: " + sectionId + ")?",
                    "Confirm", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // FIX: Changed from registerForCourse to registerForSection
                String result = studentService.registerForSection(sectionId);

                if (result.startsWith("SUCCESS")) {
                    JOptionPane.showMessageDialog(this, result, "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadMyRegistrations(); // Refresh grades tab
                } else {
                    JOptionPane.showMessageDialog(this, result, "Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDrop() {
        int selectedRow = gradesTable.getSelectedRow();
        if (selectedRow == -1) return;

        try {
            int sectionId = (int) gradesTableModel.getValueAt(selectedRow, 0);
            String courseCode = (String) gradesTableModel.getValueAt(selectedRow, 1);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to DROP " + courseCode + "?\nThis action cannot be undone.",
                    "Confirm Drop", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                // Call the Drop method in StudentService
                String result = studentService.dropSection(sectionId);

                if (result.startsWith("SUCCESS")) {
                    JOptionPane.showMessageDialog(this, result, "Dropped", JOptionPane.INFORMATION_MESSAGE);
                    loadMyRegistrations(); // Refresh the list immediately
                } else {
                    JOptionPane.showMessageDialog(this, result, "Drop Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error processing drop request.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
