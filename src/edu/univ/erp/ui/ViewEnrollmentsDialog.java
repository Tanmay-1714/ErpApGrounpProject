package edu.univ.erp.ui;

import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.util.ThemeUtils; // Uses your new theme

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ViewEnrollmentsDialog extends JDialog {

    private JTable table;
    private DefaultTableModel tableModel;
    private AdminService adminService;

    public ViewEnrollmentsDialog(JFrame parent) {
        super(parent, "Admin: All Enrollments", true);
        this.adminService = new AdminService();

        setSize(800, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // Header
        JLabel titleLabel = new JLabel("System-Wide Enrollments", SwingConstants.CENTER);
        titleLabel.setFont(ThemeUtils.TITLE_FONT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Table Setup
        String[] columns = {"ID", "Student Roll", "Username", "Course Code", "Section ID", "Status", "Grade"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only
            }
        };
        
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("Refresh");
        JButton closeButton = new JButton("Close");

        refreshButton.addActionListener(e -> loadData());
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load Data
        loadData();

        // Apply Theme
        ThemeUtils.applyTheme(this.getContentPane());

        setVisible(true);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Enrollment> list = adminService.getAllEnrollments();

        for (Enrollment e : list) {
            Object[] row = {
                e.getEnrollmentId(),
                (e.student != null) ? e.student.getRollNo() : "N/A",
                (e.student != null) ? e.student.getUsername() : "N/A",
                (e.section != null && e.section.course != null) ? e.section.course.getCode() : "??",
                e.getSectionId(),
                e.getStatus(),
                e.getGrade() == null ? "-" : e.getGrade()
            };
            tableModel.addRow(row);
        }
    }
}
