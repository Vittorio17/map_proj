import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import view.MainFrame;
import controller.MainController;

public class MainTest {

    public static void main(String[] args) {
    	System.setProperty("sun.java2d.uiScale", "1. 5"); 

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            new MainController(frame);
            frame.setVisible(true);
        });
    }
}