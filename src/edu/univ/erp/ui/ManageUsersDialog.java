package edu.univ.erp.ui;

import edu.univ.erp.service.AdminService;
import edu.univ.erp.util.ThemeUtils;

import javax.swing.*;
import java.awt.*;

public class ManageUsersDialog extends JDialog {

    private AdminService adminService;
    private JTextField usernameField, rollNoField, programField, yearField, departmentField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JPanel dynamicPanel, studentPanel, instructorPanel;
    private CardLayout cardLayout;

    public ManageUsersDialog(JFrame parent) {
        super(parent, "Admin: Create User", true);
        adminService = new AdminService();
        setSize(550, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(15, 15));

        // Inputs
        JPanel creds = new JPanel(new GridLayout(3, 2, 10, 10));
        creds.setBorder(BorderFactory.createTitledBorder("Credentials"));
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        roleComboBox = new JComboBox<>(new String[]{"Student", "Instructor"});
        creds.add(new JLabel("Username:")); creds.add(usernameField);
        creds.add(new JLabel("Password:")); creds.add(passwordField);
        creds.add(new JLabel("Role:"));     creds.add(roleComboBox);

        cardLayout = new CardLayout();
        dynamicPanel = new JPanel(cardLayout);
        dynamicPanel.setBorder(BorderFactory.createTitledBorder("Profile Details"));

        studentPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        rollNoField = new JTextField();
        programField = new JTextField();
        yearField = new JTextField();
        studentPanel.add(new JLabel("Roll No:")); studentPanel.add(rollNoField);
        studentPanel.add(new JLabel("Program:")); studentPanel.add(programField);
        studentPanel.add(new JLabel("Year:"));    studentPanel.add(yearField);

        instructorPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        departmentField = new JTextField();
        instructorPanel.add(new JLabel("Department:")); instructorPanel.add(departmentField);

        dynamicPanel.add(studentPanel, "Student");
        dynamicPanel.add(instructorPanel, "Instructor");

        JPanel center = new JPanel(new BorderLayout(10,10));
        center.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        center.add(creds, BorderLayout.NORTH);
        center.add(dynamicPanel, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        JButton btn = new JButton("Create User");
        btn.addActionListener(e -> create());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(btn);
        add(south, BorderLayout.SOUTH);

        roleComboBox.addActionListener(e -> cardLayout.show(dynamicPanel, (String)roleComboBox.getSelectedItem()));
        ThemeUtils.applyTheme(this.getContentPane());
        setVisible(true);
    }

    private void create() {
        String u = usernameField.getText();
        String p = new String(passwordField.getPassword());
        if(u.isEmpty() || p.isEmpty()) { JOptionPane.showMessageDialog(this, "Missing Credentials"); return; }
        
        String res;
        if ("Student".equals(roleComboBox.getSelectedItem())) {
             res = adminService.createNewUser(u, p, "Student", rollNoField.getText(), programField.getText(), yearField.getText());
        } else {
             res = adminService.createNewUser(u, p, "Instructor", departmentField.getText());
        }
        JOptionPane.showMessageDialog(this, res);
        if(res.startsWith("SUCCESS")) dispose();
    }
}
