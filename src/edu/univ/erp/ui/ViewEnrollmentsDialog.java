package edu.univ.erp.ui;

import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.util.ThemeUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ViewEnrollmentsDialog extends JDialog {
    
    public ViewEnrollmentsDialog(JFrame parent) {
        super(parent, "Admin: All Enrollments", true);
        AdminService service = new AdminService();
        setSize(900, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("System-Wide Enrollments", SwingConstants.CENTER);
        titleLabel.setFont(ThemeUtils.TITLE_FONT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Student (Roll)", "Course", "Sec ID", "Status", "Grade"}, 0);
        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        List<Enrollment> list = service.getAllEnrollments();
        for (Enrollment e : list) {
            String studentDisplay = "ID: " + e.getStudentId();
            if (e.student != null) {
                String u = e.student.getUsername();
                if (u != null && !u.equals("Unknown User")) {
                    studentDisplay = u + " (" + (e.student.getRollNo() != null ? e.student.getRollNo() : "N/A") + ")";
                }
            }

            String courseDisplay = "ID: " + e.getSectionId();
            if (e.section != null && e.section.course != null) {
                String c = e.section.course.getCode();
                if (c != null && !c.equals("Unknown")) courseDisplay = c;
            }

            model.addRow(new Object[]{
                e.getEnrollmentId(), 
                studentDisplay, 
                courseDisplay,
                e.getSectionId(), 
                e.getStatus(), 
                e.getGrade() == null ? "-" : e.getGrade()
            });
        }
        
        ThemeUtils.applyTheme(this.getContentPane());
        setVisible(true);
    }
}
