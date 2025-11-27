package edu.univ.erp.ui;
import edu.univ.erp.auth.*;
import edu.univ.erp.util.ThemeUtils;
import javax.swing.*;
import java.awt.*;

public class ChangePasswordDialog extends JDialog {
    private JPasswordField o = new JPasswordField(), n = new JPasswordField(), c = new JPasswordField();
    public ChangePasswordDialog(JFrame p) {
        super(p, "Change Password", true); setSize(400, 300); setLocationRelativeTo(p); setLayout(new GridLayout(7,1));
        add(new JLabel("Old:")); add(o); add(new JLabel("New:")); add(n); add(new JLabel("Confirm:")); add(c);
        JButton b = new JButton("Save");
        b.addActionListener(e -> {
            String u = UserSession.getInstance().getUsername();
            if(!new String(n.getPassword()).equals(new String(c.getPassword()))) JOptionPane.showMessageDialog(this, "Mismatch");
            else if(new AuthService().authenticate(u, new String(o.getPassword())) == null) JOptionPane.showMessageDialog(this, "Wrong Old Pass");
            else { new AuthService().changePassword(u, new String(n.getPassword())); dispose(); JOptionPane.showMessageDialog(this, "Done"); }
        });
        JPanel pn = new JPanel(); pn.add(b); add(pn);
        ThemeUtils.applyTheme(this.getContentPane()); setVisible(true);
    }
}
