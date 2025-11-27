package edu.univ.erp.ui;

import edu.univ.erp.service.AdminService;
import edu.univ.erp.util.ThemeUtils;

import javax.swing.*;
import java.awt.*;

public class ManageCoursesDialog extends JDialog {
    private AdminService adminService = new AdminService();
    private JTextField codeField, titleField, creditsField;

    public ManageCoursesDialog(JFrame parent) {
        super(parent, "Create Course", true);
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(15, 15));

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        codeField = new JTextField();
        titleField = new JTextField();
        creditsField = new JTextField();

        panel.add(new JLabel("Code:")); panel.add(codeField);
        panel.add(new JLabel("Title:")); panel.add(titleField);
        panel.add(new JLabel("Credits:")); panel.add(creditsField);
        add(panel, BorderLayout.CENTER);

        JButton btn = new JButton("Create");
        btn.addActionListener(e -> create());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(btn);
        add(south, BorderLayout.SOUTH);

        ThemeUtils.applyTheme(this.getContentPane());
        setVisible(true);
    }

    private void create() {
        try {
            double c = Double.parseDouble(creditsField.getText());
            if(c <= 0) throw new NumberFormatException();
            String res = adminService.createNewCourse(codeField.getText(), titleField.getText(), c);
            JOptionPane.showMessageDialog(this, res);
            if(res.startsWith("SUCCESS")) dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Credits must be > 0");
        }
    }
}
