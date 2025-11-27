// File: src/edu/univ/erp/ui/ChangePasswordDialog.java

package edu.univ.erp.ui;

import edu.univ.erp.auth.AuthService;
import edu.univ.erp.auth.UserSession;

import javax.swing.*;
import java.awt.*;

public class ChangePasswordDialog extends JDialog {

    private JPasswordField oldPassField;
    private JPasswordField newPassField;
    private JPasswordField confirmPassField;
    private AuthService authService;

    public ChangePasswordDialog(JFrame parent) {
        super(parent, "Change Password", true);
        this.authService = new AuthService();

        setSize(400, 250);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // --- Input Panel ---
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        oldPassField = new JPasswordField();
        newPassField = new JPasswordField();
        confirmPassField = new JPasswordField();

        inputPanel.add(new JLabel("Current Password:"));
        inputPanel.add(oldPassField);
        inputPanel.add(new JLabel("New Password:"));
        inputPanel.add(newPassField);
        inputPanel.add(new JLabel("Confirm New Password:"));
        inputPanel.add(confirmPassField);

        add(inputPanel, BorderLayout.CENTER);

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Change Password");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> handleChangePassword());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        add(buttonPanel, BorderLayout.SOUTH);
        
        // Show dialog
        setVisible(true);
    }

    private void handleChangePassword() {
        String oldPass = new String(oldPassField.getPassword());
        String newPass = new String(newPassField.getPassword());
        String confirmPass = new String(confirmPassField.getPassword());
        
        // Get the currently logged-in user's username
        String username = UserSession.getInstance().getUsername();

        // 1. Client-Side Validation
        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!newPass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "New passwords do not match.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Verify Identity (Check if old password is correct)
        // We reuse the existing authenticate method for this verification
        if (authService.authenticate(username, oldPass) == null) {
            JOptionPane.showMessageDialog(this, "Incorrect current password.", "Authentication Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. Perform the Update
        if (authService.changePassword(username, newPass)) {
            JOptionPane.showMessageDialog(this, "Password changed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose(); // Close the dialog
        } else {
            JOptionPane.showMessageDialog(this, "Database error. Could not change password.", "System Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
