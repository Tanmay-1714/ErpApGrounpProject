package edu.univ.erp.ui;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.service.InstructorService;
import edu.univ.erp.util.ThemeUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageGradesDialog extends JDialog {
    private InstructorService service = new InstructorService();
    private DefaultTableModel model;
    private int sid;

    public ManageGradesDialog(JFrame p, int sectionId, String t) {
        super(p, "Grades - " + t, true); sid = sectionId;
        setSize(900, 600); setLocationRelativeTo(p); setLayout(new BorderLayout(15,15));
        
        add(new JLabel("Edit Scores (0-100). Click Compute to save.", SwingConstants.CENTER), BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID", "Student", "Quiz (20%)", "Mid (30%)", "Final (50%)", "Grade"}, 0) {
            public boolean isCellEditable(int r, int c) { return c >= 2 && c <= 4; }
        };
        JTable tbl = new JTable(model); add(new JScrollPane(tbl), BorderLayout.CENTER);

        JButton btn = new JButton("Compute & Save");
        btn.addActionListener(e -> {
            if(tbl.isEditing()) tbl.getCellEditor().stopCellEditing();
            int c = 0;
            for(int i=0; i<model.getRowCount(); i++) {
                try {
                    int eid = (int)model.getValueAt(i, 0);
                    double q = Double.parseDouble(model.getValueAt(i, 2).toString());
                    double m = Double.parseDouble(model.getValueAt(i, 3).toString());
                    double f = Double.parseDouble(model.getValueAt(i, 4).toString());
                    if(q<0||q>100||m<0||m>100||f<0||f>100) continue;
                    if(service.updateStudentScore(eid, q, m, f).startsWith("SUCCESS")) c++;
                } catch(Exception ex){}
            }
            JOptionPane.showMessageDialog(this, "Saved " + c); load();
        });
        JPanel s = new JPanel(new FlowLayout(FlowLayout.RIGHT)); s.add(btn); add(s, BorderLayout.SOUTH);

        load();
        ThemeUtils.applyTheme(this.getContentPane());
        setVisible(true);
    }

    private void load() {
        model.setRowCount(0);
        for(Enrollment e : service.getEnrolledStudents(sid))
            model.addRow(new Object[]{e.getEnrollmentId(), e.student.getUsername(), e.getScoreQuiz(), e.getScoreMidterm(), e.getScoreFinal(), e.getGrade()});
    }
}
