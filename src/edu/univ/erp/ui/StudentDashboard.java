package edu.univ.erp.ui;

import edu.univ.erp.auth.UserSession;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.StudentService;
import edu.univ.erp.util.CsvUtils;
import edu.univ.erp.util.ThemeUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentDashboard extends JFrame {
    private StudentService service = new StudentService();
    private JTable catalogTable, gradesTable;
    private DefaultTableModel catalogModel, gradesModel;
    private JLabel creditsLabel;

    public StudentDashboard(String username) {
        setTitle("Student Portal | " + username);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Top Bar ---
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(ThemeUtils.SIDEBAR_BG);
        topBar.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel logo = new JLabel("University Student Portal");
        logo.setFont(ThemeUtils.TITLE_FONT);
        logo.setForeground(ThemeUtils.ACCENT_YELLOW);
        
        JButton logoutBtn = new JButton("Logout");
        ThemeUtils.styleButton(logoutBtn);
        logoutBtn.addActionListener(e -> { UserSession.getInstance().clearSession(); dispose(); new LoginWindow(); });

        topBar.add(logo, BorderLayout.WEST);
        topBar.add(logoutBtn, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // --- Tabs ---
        JTabbedPane tabs = new JTabbedPane();
        
        // 1. Home / Summary
        tabs.addTab("Home", createHomePanel(username));
        
        // 2. Catalog
        tabs.addTab("Course Catalog", createCatalogPanel());
        
        // 3. My Grades
        tabs.addTab("My Grades & Transcript", createGradesPanel());

        add(tabs, BorderLayout.CENTER);
        
        refresh();
        setVisible(true);
    }

    private JPanel createHomePanel(String username) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(ThemeUtils.MAIN_BG);
        
        JPanel card = ThemeUtils.createStatCard("Welcome Back", username, ThemeUtils.ACCENT_YELLOW);
        creditsLabel = new JLabel("Credits: 0"); // Placeholder
        
        p.add(card);
        return p;
    }

    private JPanel createCatalogPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(new EmptyBorder(10,10,10,10));
        
        catalogModel = new DefaultTableModel(new String[]{"ID", "Code", "Title", "Credits", "Schedule", "Seats"}, 0);
        catalogTable = new JTable(catalogModel);
        ThemeUtils.styleTable(catalogTable);
        
        JButton regBtn = new JButton("Register Selected");
        ThemeUtils.styleButton(regBtn);
        regBtn.addActionListener(e -> {
            int r = catalogTable.getSelectedRow();
            if(r == -1) return;
            int id = (int)catalogModel.getValueAt(catalogTable.convertRowIndexToModel(r), 0);
            JOptionPane.showMessageDialog(this, service.registerForSection(id));
            refresh();
        });

        p.add(new JScrollPane(catalogTable), BorderLayout.CENTER);
        p.add(regBtn, BorderLayout.SOUTH);
        return p;
    }

    private JPanel createGradesPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(new EmptyBorder(10,10,10,10));

        gradesModel = new DefaultTableModel(new String[]{"ID", "Code", "Title", "Credits", "Grade"}, 0);
        gradesTable = new JTable(gradesModel);
        ThemeUtils.styleTable(gradesTable);

        JButton dropBtn = new JButton("Drop Course");
        ThemeUtils.styleButton(dropBtn);
        dropBtn.setBackground(ThemeUtils.ERROR_RED);
        dropBtn.setForeground(Color.WHITE);
        
        JButton exportBtn = new JButton("Download Transcript");
        ThemeUtils.styleButton(exportBtn);

        dropBtn.addActionListener(e -> {
            int r = gradesTable.getSelectedRow();
            if(r == -1) return;
            int id = (int)gradesModel.getValueAt(gradesTable.convertRowIndexToModel(r), 0);
            JOptionPane.showMessageDialog(this, service.dropSection(id));
            refresh();
        });
        
        exportBtn.addActionListener(e -> CsvUtils.exportTableToCSV(gradesTable, this));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.add(dropBtn);
        btns.add(exportBtn);
        
        p.add(new JScrollPane(gradesTable), BorderLayout.CENTER);
        p.add(btns, BorderLayout.SOUTH);
        return p;
    }

    private void refresh() {
        catalogModel.setRowCount(0);
        for(Section s : service.getCourseCatalog()) 
            catalogModel.addRow(new Object[]{s.getSectionId(), s.course.getCode(), s.course.getTitle(), s.course.getCredits(), s.getDay()+" "+s.getTime(), s.getCapacity()});
        
        gradesModel.setRowCount(0);
        double totalCredits = 0;
        for(Enrollment e : service.getMyRegistrations()) {
            gradesModel.addRow(new Object[]{e.getSectionId(), e.section.course.getCode(), e.section.course.getTitle(), e.section.course.getCredits(), e.getGrade() == null ? "In Progress" : e.getGrade()});
            totalCredits += e.section.course.getCredits();
        }
        creditsLabel.setText("Total Credits: " + totalCredits);
    }
}
