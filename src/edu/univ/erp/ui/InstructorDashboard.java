package edu.univ.erp.ui;

import edu.univ.erp.auth.UserSession;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.InstructorService;
import edu.univ.erp.util.ThemeUtils; // Theme Import

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InstructorDashboard extends JFrame {

    private InstructorService instructorService;
    private JTable sectionTable;
    private DefaultTableModel tableModel;
    private JButton gradeButton, viewSectionsButton, changePwdButton, logoutButton;
    private JPanel navPanel;

    public InstructorDashboard(String username) {
        this.instructorService = new InstructorService();

        setTitle("INSTRUCTOR Dashboard - " + username);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));

        // --- Header ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel welcomeLabel = new JLabel("Welcome, Instructor " + username);
        welcomeLabel.setFont(ThemeUtils.TITLE_FONT);
        
        topPanel.add(welcomeLabel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // --- Side Nav ---
        navPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        navPanel.setBorder(BorderFactory.createTitledBorder("Tools"));
        navPanel.setPreferredSize(new Dimension(200, 0));

        viewSectionsButton = new JButton("View My Sections");
        gradeButton = new JButton("Enter Grades");
        changePwdButton = new JButton("Change Password");
        logoutButton = new JButton("Logout");

        // Simple placeholders for alignment
        navPanel.add(viewSectionsButton);
        navPanel.add(gradeButton);
        navPanel.add(new JLabel("")); // Spacer
        navPanel.add(new JLabel("")); // Spacer
        navPanel.add(changePwdButton);
        navPanel.add(logoutButton);

        add(navPanel, BorderLayout.WEST);

        // --- Table ---
        String[] cols = {"Section ID", "Course Code", "Title", "Credits", "Schedule", "Capacity"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        sectionTable = new JTable(tableModel);
        sectionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(sectionTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        gradeButton.setEnabled(false);

        loadAssignedSections();
        addListeners();

        // *** APPLY THEME ***
        ThemeUtils.applyTheme(this);

        setVisible(true);
    }

    private void loadAssignedSections() {
        tableModel.setRowCount(0);
        List<Section> list = instructorService.getAssignedSections();
        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No sections assigned.");
            return;
        }
        for (Section s : list) {
            tableModel.addRow(new Object[]{
                s.getSectionId(), s.course.getCode(), s.course.getTitle(), s.course.getCredits(),
                s.getDay() + " " + s.getTime() + " (" + s.getRoom() + ")", s.getCapacity()
            });
        }
    }

    private void addListeners() {
        sectionTable.getSelectionModel().addListSelectionListener(e -> gradeButton.setEnabled(sectionTable.getSelectedRow() != -1));
        
        viewSectionsButton.addActionListener(e -> loadAssignedSections());
        
        gradeButton.addActionListener(e -> {
            int row = sectionTable.getSelectedRow();
            if (row == -1) return;
            int secId = (int) tableModel.getValueAt(row, 0);
            String info = (String) tableModel.getValueAt(row, 1) + " - " + tableModel.getValueAt(row, 2);
            
            // Note: The Dialogs won't be themed unless you update them too, or pass 'this' which helps.
            new ManageGradesDialog(this, secId, info); 
        });

        changePwdButton.addActionListener(e -> new ChangePasswordDialog(this));
        
        logoutButton.addActionListener(e -> {
            UserSession.getInstance().clearSession();
            dispose();
            new LoginWindow();
        });
    }
}
