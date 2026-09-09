import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import view.MainFrame;
import controller.MainController;

public class MainTest {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Impossibile impostare il Look & Feel di sistema: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            new MainController(frame);
            frame.setVisible(true);
        });
    }
}