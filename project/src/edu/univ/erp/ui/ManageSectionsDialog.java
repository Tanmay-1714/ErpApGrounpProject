package edu.univ.erp.ui;

import edu.univ.erp.service.AdminService;
import edu.univ.erp.util.ThemeUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;

public class ManageSectionsDialog extends JDialog {
    private AdminService adminService = new AdminService();
    private JTextField courseIdField, capacityField, roomField, timeField, yearField;
    private JComboBox<String> semesterComboBox, dayComboBox;

    public ManageSectionsDialog(JFrame parent) {
        super(parent, "Create Section", true);
        setSize(500, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(15, 15));

        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        courseIdField = new JTextField();
        capacityField = new JTextField();
        roomField = new JTextField();
        timeField = new JTextField();
        yearField = new JTextField(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        semesterComboBox = new JComboBox<>(new String[]{"Fall", "Spring", "Summer"});
        dayComboBox = new JComboBox<>(new String[]{"Mon", "Tue", "Wed", "Thu", "Fri"});

        panel.add(new JLabel("Course ID:")); panel.add(courseIdField);
        panel.add(new JLabel("Semester:"));  panel.add(semesterComboBox);
        panel.add(new JLabel("Year:"));      panel.add(yearField);
        panel.add(new JLabel("Day:"));       panel.add(dayComboBox);
        panel.add(new JLabel("Time:"));      panel.add(timeField);
        panel.add(new JLabel("Room:"));      panel.add(roomField);
        panel.add(new JLabel("Capacity:"));  panel.add(capacityField);

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
            int cid = Integer.parseInt(courseIdField.getText());
            int cap = Integer.parseInt(capacityField.getText());
            if(cap <= 0) throw new Exception("Capacity must be positive");
            int y = Integer.parseInt(yearField.getText());
            String res = adminService.createNewSection(cid, (String)dayComboBox.getSelectedItem(), timeField.getText(), roomField.getText(), cap, (String)semesterComboBox.getSelectedItem(), y);
            JOptionPane.showMessageDialog(this, res);
            if(res.startsWith("SUCCESS")) dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}
