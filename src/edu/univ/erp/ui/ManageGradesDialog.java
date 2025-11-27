package edu.univ.erp.ui;

import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.service.InstructorService;
import edu.univ.erp.util.ThemeUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageGradesDialog extends JDialog {

    private InstructorService instructorService;
    private JTable rosterTable;
    private DefaultTableModel tableModel;
    private int sectionId;

    public ManageGradesDialog(JFrame parent, int sectionId, String title) {
        super(parent, "Grades: " + title, true);
        this.instructorService = new InstructorService();
        this.sectionId = sectionId;

        setSize(900, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // Instructions
        String info = "<html><center>Edit Scores (0-100) for Quiz, Midterm, Final.<br>" +
                      "Click 'Compute & Save' to calculate final grade (Weights: 20% / 30% / 50%).</center></html>";
        JLabel header = new JLabel(info, SwingConstants.CENTER);
        header.setFont(ThemeUtils.REGULAR_FONT);
        header.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(header, BorderLayout.NORTH);

        // Table with Score Columns
        // Cols: 0=ID, 1=Roll, 2=Name, 3=Quiz(Edit), 4=Mid(Edit), 5=Final(Edit), 6=Grade(Calc)
        String[] cols = {"ID", "Roll No", "Student", "Quiz (20%)", "Midterm (30%)", "Final (50%)", "Final Grade"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 3 || col == 4 || col == 5; // Only scores are editable
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex >= 3 && columnIndex <= 5) return Double.class;
                return String.class;
            }
        };
        
        rosterTable = new JTable(tableModel);
        add(new JScrollPane(rosterTable), BorderLayout.CENTER);

        // Button
        JButton btn = new JButton("Compute & Save All");
        btn.addActionListener(e -> submit());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(btn);
        add(south, BorderLayout.SOUTH);

        loadRoster();
        ThemeUtils.applyTheme(this.getContentPane());
        setVisible(true);
    }

    private void loadRoster() {
        tableModel.setRowCount(0);
        List<Enrollment> list = instructorService.getEnrolledStudents(sectionId);
        for(Enrollment e : list) {
            tableModel.addRow(new Object[]{
                e.getEnrollmentId(), 
                e.student.getRollNo(), 
                e.student.getUsername(),
                e.getScoreQuiz(),     // Col 3
                e.getScoreMidterm(),  // Col 4
                e.getScoreFinal(),    // Col 5
                e.getGrade() == null ? "-" : e.getGrade() // Col 6
            });
        }
    }

    private void submit() {
        int count = 0;
        if (rosterTable.isEditing()) rosterTable.getCellEditor().stopCellEditing();
        
        for(int i=0; i<tableModel.getRowCount(); i++) {
            try {
                int eid = (int)tableModel.getValueAt(i, 0);
                
                double q = parseScore(tableModel.getValueAt(i, 3));
                double m = parseScore(tableModel.getValueAt(i, 4));
                double f = parseScore(tableModel.getValueAt(i, 5));

                String res = instructorService.updateStudentScore(eid, q, m, f);
                if(res.startsWith("SUCCESS")) count++;
                
            } catch (Exception ex) {
                System.err.println("Error processing row " + i);
            }
        }
        JOptionPane.showMessageDialog(this, "Updated " + count + " student records.");
        loadRoster(); // Reload
    }

    private double parseScore(Object obj) {
        if (obj == null) return 0.0;
        if (obj instanceof Number) return ((Number) obj).doubleValue();
        try { return Double.parseDouble(obj.toString()); } 
        catch (Exception e) { return 0.0; }
    }
}
