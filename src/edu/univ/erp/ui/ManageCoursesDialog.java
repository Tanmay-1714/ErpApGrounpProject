package edu.univ.erp.ui;

import edu.univ.erp.service.AdminService;
import edu.univ.erp.util.ThemeUtils; // Import Theme

import javax.swing.*;
import java.awt.*;

public class ManageCoursesDialog extends JDialog {

    private AdminService adminService;
    private JTextField codeField, titleField, creditsField;

    public ManageCoursesDialog(JFrame parent) {
        super(parent, "Admin: Create Course", true);
        adminService = new AdminService();
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(15, 15));

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        codeField = new JTextField();
        titleField = new JTextField();
        creditsField = new JTextField();

        panel.add(new JLabel("Course Code:")); panel.add(codeField);
        panel.add(new JLabel("Title:"));       panel.add(titleField);
        panel.add(new JLabel("Credits:"));     panel.add(creditsField);
        panel.add(new JLabel(""));             panel.add(new JLabel("")); // Spacer

        add(panel, BorderLayout.CENTER);

        JButton btn = new JButton("Create Course");
        btn.addActionListener(e -> create());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(btn);
        add(south, BorderLayout.SOUTH);

        // *** APPLY THEME ***
        ThemeUtils.applyTheme(this.getContentPane());

        setVisible(true);
    }

    private void create() {
        try {
            double c = Double.parseDouble(creditsField.getText());
            String res = adminService.createNewCourse(codeField.getText(), titleField.getText(), c);
            JOptionPane.showMessageDialog(this, res);
            if(res.startsWith("SUCCESS")) dispose();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Credits must be a number");
        }
    }
}
