import edu.univ.erp.ui.LoginWindow;
import edu.univ.erp.util.ThemeUtils;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // Optional: Set System Look and Feel for better integration
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Launch the Login Window on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new LoginWindow();
        });
    }
}
