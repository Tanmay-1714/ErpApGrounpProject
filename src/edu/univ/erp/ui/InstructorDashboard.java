package edu.univ.erp.ui;
import edu.univ.erp.auth.UserSession;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.InstructorService;
import edu.univ.erp.util.ThemeUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class InstructorDashboard extends JFrame {
    private InstructorService service = new InstructorService();
    private DefaultTableModel model;
    private JTable table;

    public InstructorDashboard(String u) {
        setTitle("Instructor - " + u); setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 750); setLocationRelativeTo(null); setLayout(new BorderLayout(15,15));

        JPanel top = new JPanel(new BorderLayout()); top.setBorder(BorderFactory.createEmptyBorder(15,20,15,20));
        JLabel l = new JLabel("Instructor Portal"); l.setFont(ThemeUtils.TITLE_FONT); top.add(l);
        add(top, BorderLayout.NORTH);

        JPanel nav = new JPanel(new GridLayout(6,1,10,10)); nav.setPreferredSize(new Dimension(220,0));
        nav.setBorder(BorderFactory.createTitledBorder("Tools"));
        JButton b1 = new JButton("My Sections"), b2 = new JButton("Grades"), b3 = new JButton("Statistics"), b4 = new JButton("Password"), b5 = new JButton("Logout");
        nav.add(b1); nav.add(b2); nav.add(b3); nav.add(new JLabel("")); nav.add(b4); nav.add(b5);
        add(nav, BorderLayout.WEST);

        model = new DefaultTableModel(new String[]{"ID", "Code", "Title", "Credits", "Capacity"}, 0);
        table = new JTable(model); add(new JScrollPane(table), BorderLayout.CENTER);

        b1.addActionListener(e -> load());
        b2.addActionListener(e -> open(1)); // 1 = Grades
        b3.addActionListener(e -> open(2)); // 2 = Stats
        b4.addActionListener(e -> new ChangePasswordDialog(this));
        b5.addActionListener(e -> { UserSession.getInstance().clearSession(); dispose(); new LoginWindow(); });

        load();
        ThemeUtils.applyTheme(this);
        setVisible(true);
    }

    private void load() {
        model.setRowCount(0);
        for(Section s : service.getAssignedSections()) 
            model.addRow(new Object[]{s.getSectionId(), s.course.getCode(), s.course.getTitle(), s.course.getCredits(), s.getCapacity()});
    }

    private void open(int type) {
        int r = table.getSelectedRow();
        if(r == -1) { JOptionPane.showMessageDialog(this, "Select a section"); return; }
        int id = (int)model.getValueAt(table.convertRowIndexToModel(r), 0);
        String t = (String)model.getValueAt(table.convertRowIndexToModel(r), 1);
        if(type == 1) new ManageGradesDialog(this, id, t);
        else new ViewStatsDialog(this, id, t);
    }
}
