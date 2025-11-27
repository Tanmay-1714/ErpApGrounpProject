package edu.univ.erp.ui;
import edu.univ.erp.auth.UserSession;
import edu.univ.erp.domain.*;
import edu.univ.erp.service.StudentService;
import edu.univ.erp.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class StudentDashboard extends JFrame {
    private StudentService service = new StudentService();
    private JTable catTable, grdTable;
    private DefaultTableModel catModel, grdModel;

    public StudentDashboard(String u) {
        setTitle("Student - " + u); setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 750); setLocationRelativeTo(null); setLayout(new BorderLayout(15,15));

        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
        JLabel l = new JLabel("Student Portal"); l.setFont(ThemeUtils.TITLE_FONT);
        top.add(l, BorderLayout.WEST); add(top, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        
        catModel = new DefaultTableModel(new String[]{"ID", "Code", "Title", "Credits", "Time", "Seats"}, 0);
        catTable = new JTable(catModel);
        JPanel p1 = new JPanel(new BorderLayout()); p1.add(new JScrollPane(catTable));
        JButton regBtn = new JButton("Register");
        regBtn.addActionListener(e -> {
            int r = catTable.getSelectedRow(); if(r==-1) return;
            int id = (int)catModel.getValueAt(catTable.convertRowIndexToModel(r), 0);
            JOptionPane.showMessageDialog(this, service.registerForSection(id));
            refresh();
        });
        JPanel p1b = new JPanel(new FlowLayout(FlowLayout.RIGHT)); p1b.add(regBtn); p1.add(p1b, BorderLayout.SOUTH);
        tabs.addTab("Catalog", p1);

        grdModel = new DefaultTableModel(new String[]{"ID", "Code", "Title", "Grade"}, 0);
        grdTable = new JTable(grdModel);
        JPanel p2 = new JPanel(new BorderLayout()); p2.add(new JScrollPane(grdTable));
        JButton drpBtn = new JButton("Drop");
        drpBtn.addActionListener(e -> {
            int r = grdTable.getSelectedRow(); if(r==-1) return;
            int id = (int)grdModel.getValueAt(grdTable.convertRowIndexToModel(r), 0);
            JOptionPane.showMessageDialog(this, service.dropSection(id));
            refresh();
        });
        JButton csvBtn = new JButton("Export CSV");
        csvBtn.addActionListener(e -> CsvUtils.exportTableToCSV(grdTable, this));
        JPanel p2b = new JPanel(new FlowLayout(FlowLayout.RIGHT)); p2b.add(drpBtn); p2b.add(csvBtn); p2.add(p2b, BorderLayout.SOUTH);
        tabs.addTab("My Grades", p2);

        add(tabs, BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cp = new JButton("Password"), out = new JButton("Logout");
        cp.addActionListener(e -> new ChangePasswordDialog(this));
        out.addActionListener(e -> { UserSession.getInstance().clearSession(); dispose(); new LoginWindow(); });
        south.add(cp); south.add(out); add(south, BorderLayout.SOUTH);

        refresh();
        ThemeUtils.applyTheme(this);
        setVisible(true);
    }

    private void refresh() {
        catModel.setRowCount(0);
        for(Section s : service.getCourseCatalog()) 
            catModel.addRow(new Object[]{s.getSectionId(), s.course.getCode(), s.course.getTitle(), s.course.getCredits(), s.getDay()+" "+s.getTime(), s.getCapacity()});
        grdModel.setRowCount(0);
        for(Enrollment e : service.getMyRegistrations()) 
            grdModel.addRow(new Object[]{e.getSectionId(), e.section.course.getCode(), e.section.course.getTitle(), e.getGrade() == null ? "-" : e.getGrade()});
    }
}
