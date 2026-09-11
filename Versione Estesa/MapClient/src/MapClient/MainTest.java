package MapClient;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import view.MainFrame;
import controller.MainController;

/**
 * Entry point per l'applicazione client.
 * Configura le proprietà di rendering e il Look and Feel del sistema operativo,
 * inizializzando quindi la finestra principale e il relativo controller
 * all'interno dell'Event Dispatch Thread (EDT).
 */
public class MainTest {

	/**
     * Punto di ingresso principale dell'applicazione client.
     * Configura la scala dell'interfaccia, imposta il Look and Feel nativo
     * e schedula l'avvio della GUI tramite SwingUtilities.invokeLater.
     *
     * @param args argomenti passati da riga di comando 
     */
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