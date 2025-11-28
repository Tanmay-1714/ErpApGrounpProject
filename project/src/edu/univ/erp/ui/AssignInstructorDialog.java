package edu.univ.erp.ui;

import edu.univ.erp.service.AdminService;
import edu.univ.erp.util.ThemeUtils;

import javax.swing.*;
import java.awt.*;

public class AssignInstructorDialog extends JDialog {
    private AdminService adminService = new AdminService();
    private JTextField sectionIdField, instructorIdField;

    public AssignInstructorDialog(JFrame parent) {
        super(parent, "Assign Instructor", true);
        setSize(400, 200);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(15, 15));

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        sectionIdField = new JTextField();
        instructorIdField = new JTextField();
        panel.add(new JLabel("Section ID:")); panel.add(sectionIdField);
        panel.add(new JLabel("Instructor ID:")); panel.add(instructorIdField);
        add(panel, BorderLayout.CENTER);

        JButton btn = new JButton("Assign");
        btn.addActionListener(e -> assign());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(btn);
        add(south, BorderLayout.SOUTH);

        ThemeUtils.applyTheme(this.getContentPane());
        setVisible(true);
    }

    private void assign() {
        try {
            int sid = Integer.parseInt(sectionIdField.getText());
            int iid = Integer.parseInt(instructorIdField.getText());
            String res = adminService.assignInstructor(sid, iid);
            JOptionPane.showMessageDialog(this, res);
            if(res.startsWith("SUCCESS")) dispose();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Invalid IDs"); }
    }
}
