package edu.univ.erp.ui;

import edu.univ.erp.auth.UserSession;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.StudentService;
import edu.univ.erp.util.ThemeUtils; // Import ThemeUtils

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentDashboard extends JFrame {

    private StudentService studentService;
    private JTabbedPane tabbedPane;
    private JTable catalogTable, gradesTable;
    private DefaultTableModel catalogTableModel, gradesTableModel;
    private JButton registerButton, dropButton, changePwdButton, logoutButton;

    public StudentDashboard(String username) {
        this.studentService = new StudentService();

        setTitle("STUDENT Dashboard - " + username);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));

        // --- Header ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel welcomeLabel = new JLabel("Welcome, " + username);
        welcomeLabel.setFont(ThemeUtils.TITLE_FONT);
        
        JLabel roleLabel = new JLabel("Student Portal");
        roleLabel.setFont(ThemeUtils.HEADER_FONT);
        roleLabel.setForeground(ThemeUtils.ACCENT_YELLOW.darker());

        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(roleLabel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // --- Tabs ---
        tabbedPane = new JTabbedPane();
        setupCatalogTab();
        setupGradesTab();
        
        // Wrap panels in padding
        JPanel p1 = createCatalogPanel();
        p1.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        
        JPanel p2 = createGradesPanel();
        p2.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        tabbedPane.addTab("Course Catalog", p1);
        tabbedPane.addTab("My Grades", p2);
        add(tabbedPane, BorderLayout.CENTER);

        // --- Bottom Panel ---
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        changePwdButton = new JButton("Change Password");
        logoutButton = new JButton("Logout");
        
        southPanel.add(changePwdButton);
        southPanel.add(logoutButton);
        add(southPanel, BorderLayout.SOUTH);

        // --- Logic ---
        addListeners();
        loadCourseCatalog();
        loadMyRegistrations();

        // *** APPLY THEME ***
        ThemeUtils.applyTheme(this);
        
        // Specific fix: TabbedPane background usually needs manual help in some LookAndFeels
        tabbedPane.setBackground(ThemeUtils.BG_COLOR);

        setVisible(true);
    }

    private void setupCatalogTab() {
        String[] cols = {"Section ID", "Code", "Title", "Credits", "Schedule", "Capacity", "Sem", "Year"};
        catalogTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        catalogTable = new JTable(catalogTableModel);
        catalogTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private JPanel createCatalogPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(new JScrollPane(catalogTable), BorderLayout.CENTER);

        registerButton = new JButton("Register Selected");
        registerButton.setEnabled(false);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(registerButton);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void setupGradesTab() {
        String[] cols = {"Section ID", "Code", "Title", "Credits", "Schedule", "Grade"};
        gradesTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        gradesTable = new JTable(gradesTableModel);
        gradesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private JPanel createGradesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(new JScrollPane(gradesTable), BorderLayout.CENTER);

        dropButton = new JButton("Drop Selected");
        dropButton.setEnabled(false);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(dropButton);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void loadCourseCatalog() {
        catalogTableModel.setRowCount(0);
        List<Section> catalog = studentService.getCourseCatalog();
        for (Section s : catalog) {
            catalogTableModel.addRow(new Object[]{
                s.getSectionId(), s.course.getCode(), s.course.getTitle(), s.course.getCredits(),
                s.getDay() + " " + s.getTime() + " (" + s.getRoom() + ")",
                s.getCapacity(), s.getSemester(), s.getYear()
            });
        }
    }

    private void loadMyRegistrations() {
        gradesTableModel.setRowCount(0);
        List<Enrollment> regs = studentService.getMyRegistrations();
        for (Enrollment e : regs) {
            gradesTableModel.addRow(new Object[]{
                e.getSectionId(), e.section.course.getCode(), e.section.course.getTitle(),
                e.section.course.getCredits(), e.section.getSemester() + " " + e.section.getYear(),
                e.getGrade() != null ? e.getGrade() : "In Progress"
            });
        }
    }

    private void addListeners() {
        catalogTable.getSelectionModel().addListSelectionListener(e -> registerButton.setEnabled(catalogTable.getSelectedRow() != -1));
        gradesTable.getSelectionModel().addListSelectionListener(e -> dropButton.setEnabled(gradesTable.getSelectedRow() != -1));

        registerButton.addActionListener(e -> {
            int row = catalogTable.getSelectedRow();
            if (row == -1) return;
            int secId = (int) catalogTableModel.getValueAt(row, 0);
            String code = (String) catalogTableModel.getValueAt(row, 1);
            if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, "Register for " + code + "?", "Confirm", JOptionPane.YES_NO_OPTION)) {
                String res = studentService.registerForSection(secId);
                JOptionPane.showMessageDialog(this, res);
                if (res.startsWith("SUCCESS")) loadMyRegistrations();
            }
        });

        dropButton.addActionListener(e -> {
            int row = gradesTable.getSelectedRow();
            if (row == -1) return;
            int secId = (int) gradesTableModel.getValueAt(row, 0);
            String code = (String) gradesTableModel.getValueAt(row, 1);
            if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, "Drop " + code + "? Cannot undo.", "Confirm", JOptionPane.YES_NO_OPTION)) {
                String res = studentService.dropSection(secId);
                JOptionPane.showMessageDialog(this, res);
                if (res.startsWith("SUCCESS")) loadMyRegistrations();
            }
        });

        changePwdButton.addActionListener(e -> new ChangePasswordDialog(this));
        logoutButton.addActionListener(e -> {
            UserSession.getInstance().clearSession();
            dispose();
            new LoginWindow();
        });
        
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 0) loadCourseCatalog();
            else loadMyRegistrations();
        });
    }
}
