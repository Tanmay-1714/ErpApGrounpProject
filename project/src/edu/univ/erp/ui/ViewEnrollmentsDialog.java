package edu.univ.erp.ui;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.util.ThemeUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ViewEnrollmentsDialog extends JDialog {
    public ViewEnrollmentsDialog(JFrame p) {
        super(p, "All Enrollments", true);
        setSize(900, 600); setLocationRelativeTo(p); setLayout(new BorderLayout(10,10));

        DefaultTableModel m = new DefaultTableModel(new String[]{"ID", "Student", "Course", "Grade"}, 0);
        add(new JScrollPane(new JTable(m)), BorderLayout.CENTER);

        for(Enrollment e : new AdminService().getAllEnrollments()) {
            String s = e.student != null ? e.student.getUsername() : "ID:"+e.getStudentId();
            String c = (e.section!=null && e.section.course!=null) ? e.section.course.getCode() : "ID:"+e.getSectionId();
            m.addRow(new Object[]{e.getEnrollmentId(), s, c, e.getGrade()});
        }
        ThemeUtils.applyTheme(this.getContentPane()); setVisible(true);
    }
}
