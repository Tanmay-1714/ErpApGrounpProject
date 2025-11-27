package edu.univ.erp.ui;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.service.InstructorService;
import edu.univ.erp.util.ThemeUtils;
import javax.swing.*;
import java.awt.*;
import java.util.DoubleSummaryStatistics;

public class ViewStatsDialog extends JDialog {
    public ViewStatsDialog(JFrame p, int sid, String t) {
        super(p, "Stats - " + t, true);
        setSize(400, 350); setLocationRelativeTo(p); setLayout(new GridLayout(6,1));

        DoubleSummaryStatistics stats = new InstructorService().getEnrolledStudents(sid).stream()
                .mapToDouble(e -> (e.getScoreQuiz()*0.2)+(e.getScoreMidterm()*0.3)+(e.getScoreFinal()*0.5)).summaryStatistics();

        add(row("Count", ""+stats.getCount()));
        if(stats.getCount()>0) {
            add(row("Max", String.format("%.2f", stats.getMax())));
            add(row("Min", String.format("%.2f", stats.getMin())));
            add(row("Avg", String.format("%.2f", stats.getAverage())));
        }
        JButton b = new JButton("Close"); b.addActionListener(e -> dispose());
        JPanel pan = new JPanel(); pan.add(b); add(pan);
        ThemeUtils.applyTheme(this.getContentPane()); setVisible(true);
    }
    private JPanel row(String l, String v) {
        JPanel p = new JPanel(new BorderLayout()); p.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
        p.add(new JLabel(l), BorderLayout.WEST); p.add(new JLabel(v), BorderLayout.EAST); return p;
    }
}
