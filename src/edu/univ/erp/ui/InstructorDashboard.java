package edu.univ.erp.ui;

import edu.univ.erp.auth.UserSession;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.InstructorService;
import edu.univ.erp.util.ThemeUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class InstructorDashboard extends JFrame {
    private InstructorService service = new InstructorService();
    private DefaultTableModel model;
    private JTable table;
    private JLabel statsLabel;

    public InstructorDashboard(String username) {
        setTitle("Instructor Portal | " + username);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(ThemeUtils.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        JLabel brand = new JLabel("  INSTRUCTOR");
        brand.setFont(ThemeUtils.TITLE_FONT);
        brand.setForeground(ThemeUtils.ACCENT_YELLOW);
        sidebar.add(brand);
        sidebar.add(Box.createRigidArea(new Dimension(0, 40)));

        sidebar.add(createSideBtn("Refresh Sections", e -> load()));
        sidebar.add(createSideBtn("Enter Grades", e -> openAction(1)));
        sidebar.add(createSideBtn("View Statistics", e -> openAction(2)));
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(createSideBtn("Change Password", e -> new ChangePasswordDialog(this)));
        sidebar.add(createSideBtn("Logout", e -> { UserSession.getInstance().clearSession(); dispose(); new LoginWindow(); }));

        add(sidebar, BorderLayout.WEST);

        // Main Area
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBackground(ThemeUtils.MAIN_BG);
        main.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Top Stats
        statsLabel = new JLabel("Active Sections: 0");
        statsLabel.setFont(ThemeUtils.HEADER_FONT);
        main.add(statsLabel, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID", "Code", "Title", "Credits", "Capacity"}, 0);
        table = new JTable(model);
        ThemeUtils.styleTable(table);
        
        main.add(new JScrollPane(table), BorderLayout.CENTER);
        add(main, BorderLayout.CENTER);

        load();
        setVisible(true);
    }

    private JButton createSideBtn(String txt, java.awt.event.ActionListener l) {
        JButton b = ThemeUtils.createSidebarButton(txt);
        b.addActionListener(l);
        b.setMaximumSize(new Dimension(250, 50));
        return b;
    }

    private void load() {
        model.setRowCount(0);
        java.util.List<Section> list = service.getAssignedSections();
        statsLabel.setText("Active Sections: " + list.size());
        for(Section s : list) {
            model.addRow(new Object[]{s.getSectionId(), s.course.getCode(), s.course.getTitle(), s.course.getCredits(), s.getCapacity()});
        }
    }

    private void openAction(int type) {
        int r = table.getSelectedRow();
        if(r == -1) { JOptionPane.showMessageDialog(this, "Select a section first."); return; }
        int id = (int)model.getValueAt(table.convertRowIndexToModel(r), 0);
        String t = (String)model.getValueAt(table.convertRowIndexToModel(r), 1);
        if(type == 1) new ManageGradesDialog(this, id, t);
        else new ViewStatsDialog(this, id, t);
    }
}
